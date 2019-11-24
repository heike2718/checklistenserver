// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.NewCookie;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.checklistenserver.dao.IUserDao;
import de.egladil.web.checklistenserver.domain.Checklistenuser;
import de.egladil.web.checklistenserver.domain.UserSession;
import de.egladil.web.checklistenserver.error.AuthException;
import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;
import de.egladil.web.checklistenserver.error.LogmessagePrefixes;
import de.egladil.web.commons_crypto.CryptoService;
import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.commons_net.utils.CommonHttpUtils;

/**
 * ChecklistenSessionService
 */
@ApplicationScoped
public class ChecklistenSessionService {

	private static final Logger LOG = LoggerFactory.getLogger(ChecklistenSessionService.class);

	private static final int SESSION_IDLE_TIMEOUT_MINUTES = 15;

	private static final String STAGE_DEV = "dev";

	@ConfigProperty(name = "stage")
	String stage;

	@ConfigProperty(name = "sessioncookie.secure")
	boolean sessioncookieSecure;

	@ConfigProperty(name = "sessioncookie.httpOnly")
	boolean sessionCookieHttpOnly;

	@ConfigProperty(name = "sessioncookie.domain")
	String domain;

	// TODO: das muss in die Datenbank
	private ConcurrentHashMap<String, UserSession> sessions = new ConcurrentHashMap<>();

	@Inject
	CryptoService cryptoService;

	@Inject
	JWTService jwtService;

	@Inject
	IUserDao userDao;

	public UserSession createUserSession(final String jwt) {

		try {

			DecodedJWT decodedJWT = jwtService.verify(jwt, getPublicKey());

			String uuid = decodedJWT.getSubject();

			Claim groupsClaim = decodedJWT.getClaim(Claims.groups.name());
			String[] rolesArr = groupsClaim.asArray(String.class);

			String roles = null;

			if (rolesArr != null) {

				roles = StringUtils.join(rolesArr, ",");
			}

			Optional<Checklistenuser> optUser = userDao.findByUniqueIdentifier(uuid);

			if (optUser.isPresent()) {

				byte[] sessionIdBase64 = Base64.getEncoder().encode(cryptoService.generateSessionId().getBytes());
				String sesionId = new String(sessionIdBase64);

				UserSession userSession = null;

				if (STAGE_DEV.equals(stage)) {

					userSession = UserSession.create(sesionId, roles, CommonHttpUtils.createUserIdReference());
				} else {

					userSession = UserSession.create(null, roles, CommonHttpUtils.createUserIdReference());
				}

				userSession.setExpiresAt(getSessionTimeout());
				userSession.setUuid(uuid);

				sessions.put(sesionId, userSession);

				return userSession;

			} else {

				throw new AuthException("Du kommst nicht vorbei!");
			}
		} catch (TokenExpiredException e) {

			LOG.error("JWT expired");
			throw new AuthException("JWT has expired");
		} catch (JWTVerificationException e) {

			LOG.warn(LogmessagePrefixes.BOT + "JWT invalid: {}", e.getMessage());
			throw new AuthException("invalid JWT");
		}

	}

	public void refresh(final String sessionId) {

		UserSession userSession = sessions.get(sessionId);

		if (userSession != null) {

			userSession.setExpiresAt(getSessionTimeout());
		} else {

			throw new SessionExpiredException("keine Session mehr vorhanden");
		}

	}

	public void invalidate(final String sessionId) {

		UserSession userSession = sessions.remove(sessionId);

		if (userSession != null) {

			LOG.info("Session invalidated: {} - {}", sessionId, userSession.getUuid().substring(0, 8));
		}

	}

	public NewCookie createSessionCookie(final String sessionId) {

		// @formatter:off
		NewCookie sessionCookie = new NewCookie(CommonHttpUtils.NAME_SESSIONID_COOKIE,
			sessionId,
			null,
			domain,
			1,
			null,
			7200,
			null,
			sessioncookieSecure,
			sessionCookieHttpOnly);
//		 @formatter:on
		// NewCookie sessionCookie = new NewCookie("JSESSIONID", userSession.getSessionId());

		return sessionCookie;
	}

	private byte[] getPublicKey() {

		try (InputStream in = getClass().getResourceAsStream("/META-INF/authprov_public_key.pem");
			StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName("UTF-8"));

			return sw.toString().getBytes();
		} catch (IOException e) {

			throw new ChecklistenRuntimeException("Konnte jwt-public-key nicht lesen: " + e.getMessage());
		}

	}

	/**
	 * Gibt die Session mit der gegebenen sessionId zurück.
	 *
	 * @param  sessionId
	 *                   String
	 * @return           UserSession oder null.
	 */
	public UserSession getSession(final String sessionId) throws SessionExpiredException {

		UserSession userSession = sessions.get(sessionId);

		if (userSession != null) {

			LocalDateTime expireDateTime = CommonTimeUtils.transformFromDate(new Date(userSession.getExpiresAt()));
			LocalDateTime now = CommonTimeUtils.now();

			if (now.isAfter(expireDateTime)) {

				sessions.remove(sessionId);
				throw new SessionExpiredException("Ihre Session ist abgelaufen. Bitte loggen Sie sich erneut ein.");
			}

		}
		return userSession;
	}

	/**
	 * Sucht die UserSession anhand des Principals
	 *
	 * @param  principal
	 * @return           Optional
	 */
	private Optional<UserSession> findSessionByUuid(final String uuid) {

		return this.sessions.values().stream().filter(s -> uuid != null && uuid.equals(s.getUuid())).findFirst();

	}

	private long getSessionTimeout() {

		return CommonTimeUtils.getInterval(CommonTimeUtils.now(), SESSION_IDLE_TIMEOUT_MINUTES,
			ChronoUnit.MINUTES).getEndTime().getTime();
	}
}
