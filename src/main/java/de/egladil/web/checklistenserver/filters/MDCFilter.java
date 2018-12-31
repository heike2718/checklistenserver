//=====================================================
// Projekt: authenticationprovider
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.ThreadContext;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import de.egladil.web.commons.utils.CommonStringUtils;

/**
 * MDCFilter
 */
@Provider
@PreMatching
@Priority(101)
public class MDCFilter implements ContainerRequestFilter {

	private static final Logger LOG = LogManager.getLogger(MDCFilter.class.getName());

	@Context
	private HttpServletRequest servletRequest;

	@Override
	public synchronized void filter(final ContainerRequestContext requestContext) throws IOException {

		MultivaluedMap<String, String> headers = requestContext.getHeaders();

		final String pathInfo = servletRequest.getPathInfo();

		// Das ist die Variante, bei der Custom Context zum Log hinzugefügt wird.
		// parseClientIp(headers);

		String origin = CommonStringUtils.extractOrigin(headers.getFirst("Origin"));
		String referer = headers.getFirst("Referer");

		setLogContext(requestContext);

		LOG.info("{} - {} {} , Origin={}, Referrer={}", servletRequest.getRemoteAddr(), servletRequest.getMethod(), pathInfo,
			origin, referer);
	}

	private void setLogContext(final ContainerRequestContext requestContext) {
		String ip = servletRequest.getRemoteAddr();

		ThreadContext.put("ip", ip);

		Object subject = requestContext.getProperty("USER_ID");

		if (subject != null) {
			ThreadContext.put("username", subject.toString().substring(0, 8));
		}
	}
}
