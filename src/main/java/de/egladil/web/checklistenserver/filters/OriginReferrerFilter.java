// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.error.AuthException;
import de.egladil.web.commons_net.utils.CommonHttpUtils;

/**
 * OriginReferrerFilter
 */
@ApplicationScoped
@Provider
@Priority(900)
public class OriginReferrerFilter implements ContainerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(OriginReferrerFilter.class.getName());

	private static final List<String> NO_CONTENT_PATHS = Arrays.asList(new String[] { "/favicon.ico" });

	@ConfigProperty(name = "block.on.missing.origin.referer")
	boolean blockOnMissingOriginReferer;

	@ConfigProperty(name = "target.origin")
	String targetOrigin;

	@Context
	HttpServletRequest servletRequest;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		final String pathInfo = servletRequest.getPathInfo();

		if (NO_CONTENT_PATHS.contains(pathInfo) || "OPTIONS".equals(this.servletRequest.getMethod())) {

			throw new NoContentException(pathInfo);
		}

		this.validateOriginAndRefererHeader();

	}

	private void validateOriginAndRefererHeader() throws IOException {

		final String origin = servletRequest.getHeader("Origin");
		final String referer = servletRequest.getHeader("Referer");

		LOG.debug("Origin = [{}], Referer = [{]}", origin, referer);

		if (StringUtils.isBlank(origin) && StringUtils.isBlank(referer)) {

			final String details = "Header Origin UND Referer fehlen";

			if (blockOnMissingOriginReferer) {

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

		final String extractedValue = CommonHttpUtils.extractOrigin(headerValue);

		if (extractedValue == null) {

			return;
		}

		if (!targetOrigin.equals(extractedValue)) {

			final String details = "targetOrigin != extractedOrigin: [targetOrigin=" + targetOrigin + ", extractedOriginOrReferer="
				+ extractedValue + "]";
			logErrorAndThrow(details);
		}
	}

	private void logErrorAndThrow(final String details) throws IOException {

		final String dump = CommonHttpUtils.getRequesInfos(servletRequest);
		LOG.warn("Possible CSRF-Attack: {} - {}", details, dump);
		throw new AuthException();
	}

}
