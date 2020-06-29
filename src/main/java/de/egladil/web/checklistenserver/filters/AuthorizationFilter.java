// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.ChecklistenServerApp;
import de.egladil.web.checklistenserver.config.ConfigService;
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

	private static final List<String> AUTHORIZED_PATHS = Arrays.asList(new String[] { "/checklisten", "/templates", "/signup" });

	@Inject
	ConfigService config;

	@Context
	ResourceInfo resourceInfo;

	@Inject
	ChecklistenSessionService sessionService;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		String path = requestContext.getUriInfo().getPath();

		LOG.debug("entering AuthorizationFilter: path={}", path);

		if (needsSession(path)) {

			if (LOG.isDebugEnabled()) {

				logCookies(requestContext);
			}

			String sessionId = CommonHttpUtils.getSessionId(requestContext, config.getStage(),
				ChecklistenServerApp.CLIENT_COOKIE_PREFIX);

			LOG.debug("sessionId={}", sessionId);

			if (sessionId != null) {

				UserSession userSession = sessionService.getSession(sessionId);

				if (userSession == null) {

					LOG.warn("sessionId {} nicht bekannt oder abgelaufen", sessionId);
					throw new SessionExpiredException("keine gültige Session vorhanden");
				}

				UserSession refrehedSession = sessionService.refresh(sessionId);
				ChecklistenSecurityContext securityContext = new ChecklistenSecurityContext(refrehedSession);
				requestContext.setSecurityContext(securityContext);

				LOG.debug("UserSession in SecurityContext gesetzt.");

			} else {

				throw new AuthException("Keine Berechtigung");
			}
		}

	}

	private void logCookies(final ContainerRequestContext requestContext) {

		final Map<String, Cookie> cookies = requestContext.getCookies();

		System.out.println("==== Start read cookies ====");
		cookies.keySet().stream().forEach(key -> {

			Cookie cookie = cookies.get(key);
			System.out.println("[k=" + key + ", value=" + cookie.getValue() + "]");
		});
		System.out.println("==== End read cookies ====");
	}

	private boolean needsSession(final String path) {

		Optional<String> optPath = AUTHORIZED_PATHS.stream().filter(p -> path.toLowerCase().startsWith(p)).findFirst();

		return optPath.isPresent();
	}

}
