//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.endpoints;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.domain.ChecklisteDaten;
import de.egladil.web.checklistenserver.domain.Checklistentyp;
import de.egladil.web.checklistenserver.filters.JwtAuthz;
import de.egladil.web.checklistenserver.service.ChecklistenTemplateProvider;
import de.egladil.web.commons.payload.MessagePayload;
import de.egladil.web.commons.payload.ResponsePayload;

/**
 * ChecklistenTemplateController gibt Vorgabedetails für Checklisten zurück.
 */
@RequestScoped
@Path("templates")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@JwtAuthz
public class ChecklistenTemplateController {

	private static final Logger LOG = LoggerFactory.getLogger(ChecklistenTemplateController.class.getSimpleName());

	@Inject
	private ChecklistenTemplateProvider templateProvider;

	@GET
	@Path("/{typ}")
	public Response getTemplateMitTyp(@PathParam("typ")
	final String value) {

		try {
			Checklistentyp typ = Checklistentyp.valueOf(value.trim().toUpperCase());
			ChecklisteDaten template = templateProvider.getTemplateMitTyp(typ);

			ResponsePayload payload = new ResponsePayload(MessagePayload.info("Bitteschön"), template);
			return Response.ok().entity(payload).build();
		} catch (IllegalArgumentException e) {
			LOG.error("Falscher Parameter [typ={}]", value);
			return Response.status(404)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("Gib einen korrekten Checklistentyp an"))).build();
		}
	}
}
