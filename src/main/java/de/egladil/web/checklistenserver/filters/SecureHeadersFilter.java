//=====================================================
// Projekt: de.egladil.mkv.adminservice
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * SecureHeadersFilter packt die SecureHeaders in den Response.
 */
@Provider
public class SecureHeadersFilter implements ContainerResponseFilter {

	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
		throws IOException {

		final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		headers.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		headers.add("X-Content-Type-Options", "nosniff");
		headers.add("X-Frame-Options", "DENY");
		headers.add("X-Powered-By", "Ponder Stibbons");
		headers.add("Server", "Hex");
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Vary", "Origin");
		headers.add("Access-Control-Allow-Credentials", "true");
		headers.add("Access-Control-Allow-Methods", "POST, PUT, GET, HEADERS, OPTIONS, DELETE");
		headers.add("Access-Control-Max-Age", "3600");
		headers.add("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, Authorization");
	}
}
