// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.endpoints;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import javax.annotation.security.RolesAllowed;
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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.domain.ChecklisteDaten;
import de.egladil.web.checklistenserver.service.ChecklistenService;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * ChecklistenResource
 */
@RequestScoped
@Path("checklisten")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "STANDARD" })
public class ChecklistenResource {

	private static final Logger LOG = LoggerFactory.getLogger(ChecklistenResource.class.getSimpleName());

	@Inject
	ChecklistenService checklistenService;

	@Context
	UriInfo uriInfo;

	@Context
	SecurityContext securityContext;

	@GET
	public Response getChecklisten() {

		List<ChecklisteDaten> checklisten = checklistenService.loadChecklisten();

		ResponsePayload payload = new ResponsePayload(MessagePayload.info("OK: Anzahl Checklisten: " + checklisten.size()),
			checklisten);

		LOG.info("{}: checklisten geladen", getPrincipalAbbreviated());

		return Response.ok().entity(payload).build();
		// return Response.status(500).entity(ResponsePayload.messageOnly(MessagePayload.error("Das ist ein
		// Testfehler"))).build();
	}

	@GET
	@Path("/{kuerzel}")
	public Response getCheckliste(@Context final ContainerRequestContext crc, @PathParam(value = "kuerzel") final String kuerzel) {

		ChecklisteDaten checkliste = checklistenService.getCheckliste(kuerzel);
		return Response.ok(checkliste).build();
	}

	@POST
	public Response checklisteAnlegen(final ChecklisteDaten daten) {

		ChecklisteDaten result = checklistenService.checklisteAnlegen(daten.getTyp(), daten.getName());

		LOG.info("{}: checkliste anglegt", getPrincipalAbbreviated());

		URI uri = uriInfo.getBaseUriBuilder()
			.path(ChecklistenResource.class)
			.path(ChecklistenResource.class, "getCheckliste")
			.build(result.getKuerzel());

		ResponsePayload payload = new ResponsePayload(MessagePayload.info("erfolgreich angelegt"), result);
		return Response.created(uri)
			.entity(payload)
			.build();
	}

	@PUT
	@Path("/{kuerzel}")
	public Response checklisteAendern(@PathParam(value = "kuerzel") final String kuerzel, final ChecklisteDaten daten) {

		if (!kuerzel.equals(daten.getKuerzel())) {

			LOG.error("Konflikt: kuerzel= '{}', daten.kuerzel = '{}'", kuerzel, daten.getKuerzel());
			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Precondition Failed"));
			return Response.status(412)
				.entity(payload)
				.build();
		}

		ResponsePayload payload = checklistenService.checklisteAendern(daten, kuerzel);
		LOG.info("{}: checkliste geändert", getPrincipalAbbreviated());
		return Response.ok().entity(payload).build();

	}

	@DELETE
	@Path("/{kuerzel}")
	public Response checklisteLoeschen(@PathParam(value = "kuerzel") final String kuerzel) {

		checklistenService.checklisteLoeschen(kuerzel);

		ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.info("erfolgreich gelöscht"));

		LOG.info("{}: checkliste {} gelöscht", getPrincipalAbbreviated(), kuerzel);
		return Response.ok()
			.entity(payload)
			.build();
	}

	private String getPrincipalAbbreviated() {

		Principal userPrincipal = securityContext.getUserPrincipal();
		return userPrincipal != null ? userPrincipal.getName().substring(0, 8) : null;
	}

}
