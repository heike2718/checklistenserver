//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.filters;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kumuluz.ee.jwt.auth.cdi.JWTContextInfo;
import com.kumuluz.ee.jwt.auth.context.JWTSecurityContext;
import com.kumuluz.ee.jwt.auth.principal.JWTPrincipal;
import com.kumuluz.ee.jwt.auth.validator.JWTValidationException;
import com.kumuluz.ee.jwt.auth.validator.JWTValidator;

import de.egladil.web.checklistenserver.dao.impl.UserDao;
import de.egladil.web.checklistenserver.domain.Checklistenuser;
import de.egladil.web.commons.error.AuthException;
import de.egladil.web.commons.error.SessionExpiredException;
import de.egladil.web.commons.jwt.JwtAuthz;

/**
 * AuthorizationFilter liest den authorization-Header, wenn erforderlich, verifiziert das JWT und setzt das subject aus
 * dem JWT in den SecurityContext als Principal. Von dort kann es in den API-Endpoints über den Parameter @Context
 * SercurityContext ausgelesen werden.
 */
@ApplicationScoped
@JwtAuthz
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthorizationFilter implements ContainerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationFilter.class.getName());

	private static final String SIGN_UP_PATH = "/signup/user";

	@Context
	private HttpServletRequest servletRequest;

	@Inject
	private JWTContextInfo jwtContextInfo;

	@Inject
	private UserDao userDao;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException, AuthException, SessionExpiredException {

		final String pathInfo = servletRequest.getPathInfo();
		authorizeRequest(requestContext, pathInfo);
	}

	private void authorizeRequest(final ContainerRequestContext requestContext, final String pathInfo) throws IOException {

		final String authorizationHeader = servletRequest.getHeader("Authorization");

		if (authorizationHeader == null) {
			LOG.error("authorization- Header missing");
			// requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
			// .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"MP-JWT\"").build());
			throw new AuthException();
		}

		try {

			JWTPrincipal jwtPrincipal = validateToken(authorizationHeader.substring(7));

			CLPrincipal egladilPrincipal = null;

			if (mustCheckUser(pathInfo)) {
				final String subject = jwtPrincipal.getSubject();
				Optional<Checklistenuser> optUser = this.getChecklistenUser(subject);
				if (!optUser.isPresent()) {
					LOG.warn("Das JWT subj {} ist der Checklistenanwendung nicht bekannt", subject);
					throw new AuthException();
				} else {
					egladilPrincipal = new CLPrincipal(jwtPrincipal, optUser.get().getRoles());
					final SecurityContext securityContext = requestContext.getSecurityContext();
					JWTSecurityContext jwtSecurityContext = new JWTSecurityContext(securityContext, egladilPrincipal);
					requestContext.setSecurityContext(jwtSecurityContext);
				}
			}
		} catch (JWTValidationException e) {
			LOG.warn("Das JWT wurde unterwegs manipuliert: {}", e.getMessage());
			throw new AuthException();
		}
	}

	private boolean mustCheckUser(final String pathInfo) {
		return !SIGN_UP_PATH.equals(pathInfo);
	}

	private Optional<Checklistenuser> getChecklistenUser(final String subject) {
		Optional<Checklistenuser> user = userDao.findByUniqueIdentifier(subject);
		return user;
	}

	private JWTPrincipal validateToken(final String token) throws JWTValidationException {
		return JWTValidator.validateToken(token, jwtContextInfo);
	}
}
