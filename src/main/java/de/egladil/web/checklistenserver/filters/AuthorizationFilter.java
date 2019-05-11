//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

import com.kumuluz.ee.jwt.auth.cdi.JWTContextInfo;
import com.kumuluz.ee.jwt.auth.context.JWTSecurityContext;
import com.kumuluz.ee.jwt.auth.principal.JWTPrincipal;
import com.kumuluz.ee.jwt.auth.validator.JWTValidationException;
import com.kumuluz.ee.jwt.auth.validator.JWTValidator;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import de.egladil.web.checklistenserver.config.ApplicationConfig;
import de.egladil.web.checklistenserver.dao.impl.UserDao;
import de.egladil.web.checklistenserver.domain.Checklistenuser;
import de.egladil.web.commons.error.AuthException;
import de.egladil.web.commons.error.SessionExpiredException;
import de.egladil.web.commons.utils.CommonHttpUtils;
import de.egladil.web.commons.utils.CommonStringUtils;

/**
 * AuthorizationFilter liest den authorization-Header, wenn erforderlich, verifiziert das JWT und setzt das subject aus
 * dem JWT in den SecurityContext als Principal. Von dort kann es in den API-Endpoints über den Parameter @Context
 * SercurityContext ausgelesen werden.
 */
@ApplicationScoped
@Provider
@Priority(Priorities.AUTHENTICATION)
@PreMatching
public class AuthorizationFilter implements ContainerRequestFilter {

	private static final Logger LOG = LogManager.getLogger(AuthorizationFilter.class.getName());

	private static final List<String> NO_CONTENT_PATHS = Arrays.asList(new String[] { "/favicon.ico" });

	private static final List<String> PUBLIC_API_PATHS = Arrays.asList(new String[] { "/dev/root", "/signup/secret" });

	private static final String SIGN_UP_PATH = "/signup/user";

	@Context
	private HttpServletRequest servletRequest;

	@Inject
	private ApplicationConfig applicationConfig;

	@Inject
	private JWTContextInfo jwtContextInfo;

	@Inject
	private UserDao userDao;

	// @Override
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
			JWTPrincipal jwtPrincipal = validateToken(authorizationHeader.substring(7));

			CLPrincipal egladilPrincipal = null;

			if (mustCheckUser(pathInfo)) {
				final String subject = jwtPrincipal.getSubject();
				Optional<Checklistenuser> optUser = this.getChecklistenUser(subject);
				if (!optUser.isPresent()) {
					LOG.warn("Das JWT subj {} ist der Checklistenanwendung nicht bekannt", subject);
					requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
						.header(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"MP-JWT\"").build());
				}
				egladilPrincipal = new CLPrincipal(jwtPrincipal, optUser.get().getRoles());
			}

			final SecurityContext securityContext = requestContext.getSecurityContext();
			JWTSecurityContext jwtSecurityContext = new JWTSecurityContext(securityContext, egladilPrincipal);
			requestContext.setSecurityContext(jwtSecurityContext);
		} catch (JWTValidationException e) {
			LOG.warn("Das JWT wurde unterwegs manipuliert: {}", e.getMessage());
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
				.header(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"MP-JWT\"").build());
		}
	}

	private boolean mustCheckUser(final String pathInfo) {
		return !SIGN_UP_PATH.equals(pathInfo);
	}

	private Optional<Checklistenuser> getChecklistenUser(final String subject) {
		Optional<Checklistenuser> user = userDao.findByUniqueIdentifier(subject);
		return user;
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

	private JWTPrincipal validateToken(final String token) throws JWTValidationException {
		return JWTValidator.validateToken(token, jwtContextInfo);
	}
}
