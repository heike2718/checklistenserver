// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.endpoints;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.LogDelegate;
import de.egladil.web.commons_validation.payload.LogEntry;

/**
 * LogResource
 */
@RequestScoped
@Path("/log")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LogResource {

	private static final Logger LOG = LoggerFactory.getLogger(LogResource.class);

	@Inject
	@ConfigProperty(name = "auth.client-id")
	String clientId;

	@POST
	public Response log(final LogEntry logEntry) {

		new LogDelegate().log(logEntry, LOG, clientId);

		return Response.ok().build();

	}

}
