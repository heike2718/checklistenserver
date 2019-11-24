// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.endpoints;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import javax.annotation.security.PermitAll;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.domain.ChecklisteDaten;
import de.egladil.web.checklistenserver.domain.UserSession;
import de.egladil.web.checklistenserver.error.AuthException;
import de.egladil.web.checklistenserver.service.ChecklistenService;
import de.egladil.web.checklistenserver.service.ChecklistenSessionService;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * ChecklistenResource
 */
@RequestScoped
@Path("checklisten")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChecklistenResource {

	private static final Logger LOG = LoggerFactory.getLogger(ChecklistenResource.class.getSimpleName());

	@Inject
	ChecklistenService checklistenService;

	@Inject
	ChecklistenSessionService sessionService;

	@Context
	UriInfo uriInfo;

	@Context
	SecurityContext securityContext;

	@GET
	@PermitAll
	public Response getChecklisten() {

		UserSession userSession = getUserSession();

		List<ChecklisteDaten> checklisten = checklistenService.loadChecklisten();

		NewCookie sessionCookie = sessionService.createSessionCookie(userSession.getSessionId());

		ResponsePayload payload = new ResponsePayload(MessagePayload.info("OK: Anzahl Checklisten: " + checklisten.size()),
			checklisten);

		LOG.info("{}: checklisten geladen", getStringAbbreviated(userSession.getUuid()));

		// TODO: neues XSRF-Token?
		return Response.ok().entity(payload).cookie(sessionCookie).build();
		// return Response.status(500).entity(ResponsePayload.messageOnly(MessagePayload.error("Das ist ein
		// Testfehler"))).build();
	}

	@GET
	@Path("/checkliste/{kuerzel}")
	@PermitAll
	public Response getCheckliste(@PathParam(
		value = "kuerzel") final String kuerzel) {

		UserSession userSession = getUserSession();
		NewCookie sessionCookie = sessionService.createSessionCookie(userSession.getSessionId());

		ChecklisteDaten checkliste = checklistenService.getCheckliste(kuerzel);
		return Response.ok(checkliste).cookie(sessionCookie).build();
	}

	@POST
	@PermitAll
	public Response checklisteAnlegen(final ChecklisteDaten daten) {

		UserSession userSession = getUserSession();
		ChecklisteDaten result = checklistenService.checklisteAnlegen(daten.getTyp(), daten.getName());

		LOG.info("{}: checkliste angelegt: {}", getStringAbbreviated(userSession.getUuid()),
			getStringAbbreviated(result.getKuerzel()));

		URI uri = uriInfo.getBaseUriBuilder()
			.path(ChecklistenResource.class)
			.path(ChecklistenResource.class, "getCheckliste")
			.build(result.getKuerzel());

		NewCookie sessionCookie = sessionService.createSessionCookie(userSession.getSessionId());
		ResponsePayload payload = new ResponsePayload(MessagePayload.info("erfolgreich angelegt"), result);
		return Response.created(uri)
			.entity(payload)
			.cookie(sessionCookie)
			.build();
	}

	@PUT
	@Path("/checkliste/{kuerzel}")
	@PermitAll
	public Response checklisteAendern(@PathParam(
		value = "kuerzel") final String kuerzel, final ChecklisteDaten daten) {

		UserSession userSession = getUserSession();

		NewCookie sessionCookie = sessionService.createSessionCookie(userSession.getSessionId());

		if (!kuerzel.equals(daten.getKuerzel())) {

			LOG.error("{}: Konflikt: kuerzel= '{}', daten.kuerzel = '{}'",
				getStringAbbreviated(userSession.getUuid()), kuerzel, daten.getKuerzel());
			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Precondition Failed"));
			return Response.status(412)
				.entity(payload)
				.cookie(sessionCookie)
				.build();
		}

		ResponsePayload payload = checklistenService.checklisteAendern(daten, kuerzel);
		LOG.info("{}: checkliste {} geändert", getStringAbbreviated(userSession.getUuid()),
			getStringAbbreviated(kuerzel));
		return Response.ok().entity(payload).cookie(sessionCookie).build();

	}

	@DELETE
	@Path("/checkliste/{kuerzel}")
	@PermitAll
	public Response checklisteLoeschen(@PathParam(value = "idRef") final String idRef, @PathParam(
		value = "kuerzel") final String kuerzel) {

		UserSession userSession = getUserSession();

		checklistenService.checklisteLoeschen(kuerzel);

		NewCookie sessionCookie = sessionService.createSessionCookie(userSession.getSessionId());
		ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.info("erfolgreich gelöscht"));

		LOG.info("{} - {}: checkliste {} gelöscht", getStringAbbreviated(userSession.getUuid()), getStringAbbreviated(kuerzel));
		return Response.ok()
			.entity(payload)
			.cookie(sessionCookie)
			.build();
	}

	private UserSession getUserSession() {

		Principal userPrincipal = securityContext.getUserPrincipal();

		if (userPrincipal != null) {

			return (UserSession) userPrincipal;
		}

		LOG.error("keine UserSession für Principal vorhanden");
		throw new AuthException("keine Berechtigung");

	}

	private String getStringAbbreviated(final String string) {

		return StringUtils.abbreviate(string, 11);
	}

}
