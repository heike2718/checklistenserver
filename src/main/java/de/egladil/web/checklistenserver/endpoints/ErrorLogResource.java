//=====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.checklistenserver.endpoints;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.config.ApplicationConfig;
import de.egladil.web.commons.payload.LogEntry;
import de.egladil.web.commons.payload.TSLogLevel;

/**
 * ErrorLogResource
 */
@RequestScoped
@Path("error")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ErrorLogResource {

	private static final Logger LOG = LoggerFactory.getLogger(ErrorLogResource.class.getSimpleName());

	@Inject
	private ApplicationConfig applicationConfig;

	@POST
	public Response logError(final LogEntry logEntry) {

		TSLogLevel level = logEntry.getLevel();

		String clientId = StringUtils.abbreviate(applicationConfig.getClientId(), 11);

		switch (level) {
		case All:
		case Debug:
			LOG.debug("BrowserLog: {} - Client-ID={}", logEntry, clientId);
			break;
		case Info:
			LOG.info("BrowserLog: {} - Client-ID={}", logEntry, clientId);
			break;
		case Warn:
			LOG.warn("BrowserLog: {} - Client-ID={}", logEntry, clientId);
			break;
		case Error:
		case Fatal:
			LOG.error("BrowserLog: {} - Client-ID={}", logEntry, clientId);
			break;
		default:
			break;
		}

		return Response.ok().build();

	}

}
