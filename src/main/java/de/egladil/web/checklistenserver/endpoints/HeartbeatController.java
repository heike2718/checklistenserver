//=====================================================
// Projekt: checklisten
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.endpoints;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;

import de.egladil.web.checklistenserver.config.ApplicationConfig;
import de.egladil.web.checklistenserver.service.HeartbeatService;
import de.egladil.web.commons.error.LogmessagePrefixes;
import de.egladil.web.commons.payload.MessagePayload;
import de.egladil.web.commons.payload.ResponsePayload;

/**
 * HeartbeatController
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Log(LogParams.METRICS)
@RequestScoped
@Path("heartbeats")
public class HeartbeatController {

	private static final Logger LOG = LoggerFactory.getLogger(HeartbeatController.class);

	@Inject
	private HeartbeatService heartbeatService;

	@Inject
	private ApplicationConfig applicationConfig;

	@GET
	public Response check(@QueryParam("heartbeatId")
	final String heartbeatId) {

		if (!applicationConfig.getHeartbeatId().equals(heartbeatId)) {
			LOG.warn("{}Aufruf mit fehlerhaftem QueryParam {}", LogmessagePrefixes.BOT, heartbeatId);
			return Response.status(401)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("keine Berechtigung für diese Resource"))).build();
		}
		ResponsePayload responsePayload = heartbeatService.update();
		if ("INFO".equals(responsePayload.getMessage().getLevel())) {
			return Response.ok().entity(responsePayload).build();
		}
		return Response.serverError().entity(responsePayload).build();
	}
}
