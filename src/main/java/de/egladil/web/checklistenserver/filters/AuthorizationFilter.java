// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.context.ChecklistenSecurityContext;
import de.egladil.web.checklistenserver.domain.UserSession;
import de.egladil.web.checklistenserver.error.AuthException;
import de.egladil.web.checklistenserver.service.ChecklistenSessionService;
import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.commons_net.utils.CommonHttpUtils;

/**
 * AuthorizationFilter
 */
@ApplicationScoped
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationFilter.class);

	@ConfigProperty(name = "stage")
	String stage;

	@Context
	ResourceInfo resourceInfo;

	@Inject
	ChecklistenSessionService sessionService;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		String path = requestContext.getUriInfo().getPath();

		LOG.debug("entering AuthorizationFilter: path={}", path);

		if (needsSession(path)) {

			String sessionId = CommonHttpUtils.getSessionId(requestContext, stage);

			if (sessionId != null) {

				UserSession userSession = sessionService.getSession(sessionId);

				if (userSession == null) {

					LOG.warn("sessionId {} nicht bekannt oder abgelaufen", sessionId);
					throw new SessionExpiredException("keine gültige Session vorhanden");
				}

				sessionService.refresh(sessionId);

				SecurityContext quarkusSecurityContext = requestContext.getSecurityContext();

				ChecklistenSecurityContext securityContext = new ChecklistenSecurityContext(userSession);
				requestContext.setSecurityContext(securityContext);

			} else {

				throw new AuthException("Keine Berechtigung");
			}
		}

	}

	private boolean needsSession(final String path) {

		return path.toLowerCase().startsWith("/checklisten");
	}

}
