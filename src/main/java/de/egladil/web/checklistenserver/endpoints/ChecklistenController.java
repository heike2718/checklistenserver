//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.endpoints;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;

import de.egladil.web.checklistenserver.domain.ChecklisteDaten;
import de.egladil.web.checklistenserver.service.ChecklistenService;
import de.egladil.web.commons.payload.MessagePayload;
import de.egladil.web.commons.payload.ResponsePayload;

/**
 * ChecklistenController
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Log(LogParams.METRICS)
@RequestScoped
@Path("checklisten")
public class ChecklistenController {

	private static final String RESOURCE_BASE_URL = "/cl/checklisten/";

	private static final Logger LOG = LogManager.getLogger(ChecklistenController.class.getName());

	@Inject
	private ChecklistenService checklistenService;

	@GET
	public Response getChecklisten(@Context final ContainerRequestContext crc) {

		List<ChecklisteDaten> checklisten = checklistenService.loadChecklisten();

		ResponsePayload payload = new ResponsePayload(MessagePayload.info("OK: Anzahl Checklisten: " + checklisten.size()),
			checklisten);

		return Response.ok().entity(payload).build();
	}

	@GET
	@Path("/{kuerzel}")
	public Response getCheckliste(@Context final ContainerRequestContext crc, @PathParam(value = "kuerzel")
	final String kuerzel) {
		ChecklisteDaten checkliste = checklistenService.getCheckliste(kuerzel);
		return Response.ok().entity(checkliste).build();
	}

	@POST
	public Response checklisteAnlegen(@Context final ContainerRequestContext crc, final ChecklisteDaten daten) {

		ChecklisteDaten result = checklistenService.checklisteAnlegen(daten.getTyp(), daten.getName());
		ResponsePayload payload = new ResponsePayload(MessagePayload.info("erfolgreich angelegt"), result);
		// TODO: hier die BaseUrl vom Server lesen. und davorklatschen
		return Response.status(201).entity(payload).header("Location", result.getLocation(RESOURCE_BASE_URL)).build();
	}

	@PUT
	@Path("/{kuerzel}")
	public Response checklisteAendern(@PathParam(value = "kuerzel")
	final String kuerzel, final ChecklisteDaten daten) {

		if (!kuerzel.equals(daten.getKuerzel())) {
			LOG.error("Konflikt: kuerzel= '{}', daten.kuerzel = '{}'", kuerzel, daten.getKuerzel());
			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Precondition Failed"));
			return Response.status(412).entity(payload).build();
		}

		ResponsePayload payload = checklistenService.checklisteAendern(daten, kuerzel);
		return Response.ok().entity(payload).build();

	}

	@DELETE
	@Path("/{kuerzel}")
	public Response checklisteLoeschen(@PathParam(value = "kuerzel")
	final String kuerzel) {

		checklistenService.checklisteLoeschen(kuerzel);
		ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.info("erfolgreich gelöscht"));

		return Response.ok().entity(payload).build();
	}

}
