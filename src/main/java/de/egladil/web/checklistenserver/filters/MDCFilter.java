//=====================================================
// Projekt: authenticationprovider
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

/**
 * MDCFilter *
 */
@Provider
@PreMatching
public class MDCFilter implements ContainerRequestFilter {

	private static final Logger LOG = LogManager.getLogger(MDCFilter.class.getName());

	private static final List<String> NO_CONTENT_PATHS = Arrays.asList(new String[] { "/favicon.ico" });

	@Context
	private HttpServletRequest servletRequest;

	@Override
	public synchronized void filter(final ContainerRequestContext requestContext) throws IOException {
		MultivaluedMap<String, String> headers = requestContext.getHeaders();

		final String pathInfo = servletRequest.getPathInfo();
		if (NO_CONTENT_PATHS.contains(pathInfo)) {
			throw new NoContentException(pathInfo);
		}

		// Das ist die Variante, bei der Custom Context zum Log hinzugefügt wird.
		// parseClientIp(headers);

		String origin = extractOriginOrReferer(headers.getFirst("Origin"));
		String referer = headers.getFirst("Referer");

		LOG.info("{} - {} {} , Origin={}, Referrer={}", servletRequest.getRemoteAddr(), servletRequest.getMethod(), pathInfo,
			origin, referer);
	}

	private String extractOriginOrReferer(final String headerValue) {
		if (StringUtils.isBlank(headerValue)) {
			return null;
		}
		final String value = headerValue.replaceAll("http://", "").replaceAll("https://", "");
		final String[] token = StringUtils.split(value, "/");
		final String extractedOrigin = token == null ? value : token[0];
		return extractedOrigin;
	}

	@SuppressWarnings("unused")
	private void parseClientIp(final MultivaluedMap<String, String> headers) {
		String ip = servletRequest.getRemoteAddr();

		ThreadContext.put("ip", ip);
		ThreadContext.put("username", UUID.randomUUID().toString().substring(0, 8));
	}
}
