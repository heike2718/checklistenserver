//=====================================================
// Projekt: de.egladil.mkv.adminservice
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * ContentSecurityPolicyFilter
 */
@Provider
public class ContentSecurityPolicyFilter implements ContainerResponseFilter {

	private static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";

	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
		throws IOException {
		responseContext.getHeaders().add(CONTENT_SECURITY_POLICY, "default-src 'self'; ");
	}
}
