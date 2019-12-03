// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.endpoints;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.LogEntry;
import de.egladil.web.commons_validation.payload.TSLogLevel;

/**
 * LogResource
 */
@RequestScoped
@Path("/log")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LogResource {

	private static final Logger LOG = LoggerFactory.getLogger(LogResource.class);

	@ConfigProperty(name = "auth.client-id")
	String clientId;

	@POST
	public Response logError(final LogEntry logEntry) {

		TSLogLevel level = logEntry.getLevel();

		String clientIdAbbr = StringUtils.abbreviate(clientId, 11);

		switch (level) {

		case All:
		case Debug:
			LOG.debug("BrowserLog: {} - Client-ID={}", logEntry, clientIdAbbr);
			break;

		case Info:
			LOG.info("BrowserLog: {} - Client-ID={}", logEntry, clientIdAbbr);
			break;

		case Warn:
			LOG.warn("BrowserLog: {} - Client-ID={}", logEntry, clientIdAbbr);
			break;

		case Error:
		case Fatal:
			LOG.error("BrowserLog: {} - Client-ID={}", logEntry, clientIdAbbr);
			break;

		default:
			break;
		}

		return Response.ok().build();

	}

}
