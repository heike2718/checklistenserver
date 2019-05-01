//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.filters;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import de.egladil.web.checklistenserver.config.ApplicationConfig;
import de.egladil.web.checklistenserver.dao.impl.UserDao;
import de.egladil.web.checklistenserver.domain.Checklistenuser;
import de.egladil.web.commons.access.PrincipalImpl;
import de.egladil.web.commons.error.AuthException;
import de.egladil.web.commons.error.SessionExpiredException;
import de.egladil.web.commons.utils.CommonHttpUtils;
import de.egladil.web.commons.utils.CommonStringUtils;

/**
 * AuthenticationFilter liest den authorization-Header, wenn erforderlich, verifiziert das JWT und setzt das subject aus
 * dem JWT in den SecurityContext als Principal. Von dort kann es in den API-Endpoints über den Parameter @Context
 * SercurityContext ausgelesen werden.
 */
@ApplicationScoped
@Provider
@PreMatching
@Priority(100)
public class AuthenticationFilter implements ContainerRequestFilter {

	private static final Logger LOG = LogManager.getLogger(AuthenticationFilter.class.getName());

	private static final List<String> NO_CONTENT_PATHS = Arrays.asList(new String[] { "/favicon.ico" });

	private static final List<String> PUBLIC_API_PATHS = Arrays.asList(new String[] { "/signup/secret" });

	private static final String SIGN_UP_PATH = "/signup/user";

	@Context
	private HttpServletRequest servletRequest;

	@Inject
	private ApplicationConfig applicationConfig;

	@Inject
	private UserDao userDao;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException, AuthException, SessionExpiredException {

		final String pathInfo = servletRequest.getPathInfo();
		if (NO_CONTENT_PATHS.contains(pathInfo) || "OPTIONS".equals(this.servletRequest.getMethod())) {
			throw new NoContentException(pathInfo);
		}

		this.validateOriginAndRefererHeader();

		if (PUBLIC_API_PATHS.contains(pathInfo)) {
			return;
		}

		authorizeRequest(requestContext, pathInfo);
	}

	private void authorizeRequest(final ContainerRequestContext requestContext, final String pathInfo) throws IOException {

		final String authorizationHeader = servletRequest.getHeader("Authorization");

		if (authorizationHeader == null) {
			throw new AuthException("authorization- Header missing");
		}

		try {
			DecodedJWT jwt = new JWTProvider().getJWT(authorizationHeader, applicationConfig);
			final String subject = jwt.getSubject();
			this.initSecurityContext(requestContext, subject);
			if (mustCheckUser(pathInfo)) {
				boolean isUser = this.isAuthenticated(subject);
				if (!isUser) {
					LOG.warn("Das JWT subj {} ist der Checklistenanwendung nicht bekannt", subject);
					throw new AuthException();
				}
			}
		} catch (TokenExpiredException e) {
			if (mustCheckJWTExpired(pathInfo)) {
				throw e;
			}
		} catch (JWTVerificationException e) {
			LOG.warn("Das JWT wurde unterwegs manipuliert: {}", e.getMessage());
			throw new AuthException();
		}
	}

	private boolean mustCheckUser(final String pathInfo) {
		return !SIGN_UP_PATH.equals(pathInfo);
	}

	private boolean mustCheckJWTExpired(final String pathInfo) {
		return !SIGN_UP_PATH.equals(pathInfo);
	}

	private boolean isAuthenticated(final String subject) {
		Optional<Checklistenuser> user = userDao.findByUniqueIdentifier(subject);
		return user.isPresent();
	}

	/**
	 * Holt das subject aus dem JWT und setzt es als Principal in den SecurityContext. Von dort kann es in den
	 * Endpoint-Methoden mittels eines Parameters @Context final SecurityContext securityContext ausgelesen werden.
	 *
	 * @param requestContext
	 * @param subject String das subject aus dem JWT
	 */
	private void initSecurityContext(final ContainerRequestContext requestContext, final String subject) {

		final SecurityContext securityContext = requestContext.getSecurityContext();
		final boolean secure = securityContext != null && securityContext.isSecure();

		requestContext.setSecurityContext(new SecurityContext() {

			@Override
			public Principal getUserPrincipal() {
				return new PrincipalImpl(subject);
			}

			@Override
			public boolean isUserInRole(final String role) {
				// jeder User
				return StringUtils.isNotBlank(subject);
			}

			@Override
			public boolean isSecure() {
				return secure;
			}

			@Override
			public String getAuthenticationScheme() {
				return SecurityContext.FORM_AUTH;
			}
		});

	}

	/**
	 * Validiert die Header-Parameter 'Origin' und 'Referer'.
	 *
	 * @param response
	 * @param req
	 * @throws IOException
	 */
	private void validateOriginAndRefererHeader() throws IOException {
		final String origin = servletRequest.getHeader("Origin");
		final String referer = servletRequest.getHeader("Referer");

		LOG.debug("Origin = [{}], Referer = [{]}", origin, referer);

		if (StringUtils.isBlank(origin) && StringUtils.isBlank(referer)) {
			final String details = "Header Origin UND Referer fehlen";
			if (applicationConfig.isBlockOnMissingOriginReferer()) {
				logErrorAndThrow(details);
			}
		}

		if (!StringUtils.isBlank(origin)) {
			checkHeaderTarget(origin);
		}
		if (!StringUtils.isBlank(referer)) {
			checkHeaderTarget(referer);
		}
	}

	private void checkHeaderTarget(final String headerValue) throws IOException {
		final String extractedValue = CommonStringUtils.extractOrigin(headerValue);
		if (extractedValue == null) {
			return;
		}

		String targetOrigin = applicationConfig.getTargetOrigin();
		if (!targetOrigin.equals(extractedValue)) {
			final String details = "targetOrigin != extractedOrigin: [targetOrigin=" + targetOrigin + ", extractedOriginOrReferer="
				+ extractedValue + "]";
			logErrorAndThrow(details);
		}
	}

	/**
	 * Der Authentisierungsfehler wird geloggt und ein entsprechender Response erzeugt.
	 *
	 * @param request
	 * @param res
	 * @throws IOException
	 */
	private void logErrorAndThrow(final String details) throws IOException {
		final String dump = CommonHttpUtils.getRequesInfos(servletRequest);
		LOG.warn("Possible CSRF-Attack: {} - {}", details, dump);
		throw new AuthException();
	}
}
