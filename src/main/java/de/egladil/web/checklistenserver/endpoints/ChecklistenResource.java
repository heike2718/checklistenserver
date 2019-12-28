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
import de.egladil.web.commons_validation.ValidationDelegate;
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

	private static final Logger LOG = LoggerFactory.getLogger(ChecklistenResource.class);

	@Inject
	ChecklistenService checklistenService;

	@Inject
	ChecklistenSessionService _sessionService;

	@Context
	UriInfo uriInfo;

	@Context
	SecurityContext securityContext;

	private final ValidationDelegate validationDelegate = new ValidationDelegate();

	@GET
	@PermitAll
	public Response getChecklisten() {

		LOG.debug("entering getChecklisten");

		UserSession userSession = getUserSession();

		LOG.debug("Alles gut: session vorhanden");

		List<ChecklisteDaten> checklisten = checklistenService.loadChecklisten(userSession.getUuid());

		ResponsePayload payload = new ResponsePayload(MessagePayload.info("OK: Anzahl Checklisten: " + checklisten.size()),
			checklisten);

		LOG.info("{}: checklisten geladen", getStringAbbreviated(userSession.getUuid()));

		return Response.ok().entity(payload).build();
		// return Response.status(500).entity(ResponsePayload.messageOnly(MessagePayload.error("Das ist ein Testfehler"))).build();
	}

	@GET
	@Path("/checkliste/{kuerzel}")
	@PermitAll
	public Response getCheckliste(@PathParam(
		value = "kuerzel") final String kuerzel) {

		UserSession userSession = getUserSession();
		ChecklisteDaten checkliste = checklistenService.getCheckliste(kuerzel, userSession.getUuid());
		return Response.ok(checkliste).build();
	}

	@POST
	@PermitAll
	public Response checklisteAnlegen(final ChecklisteDaten daten) {

		this.validationDelegate.check(daten, ChecklisteDaten.class);

		UserSession userSession = getUserSession();

		ChecklisteDaten result = checklistenService.checklisteAnlegen(daten.getTyp(), daten.getName(), userSession.getUuid());

		LOG.info("{}: checkliste angelegt: {}", getStringAbbreviated(userSession.getUuid()),
			getStringAbbreviated(result.getKuerzel()));

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
	@Path("/checkliste/{kuerzel}")
	@PermitAll
	public Response checklisteAendern(@PathParam(
		value = "kuerzel") final String kuerzel, final ChecklisteDaten daten) {

		UserSession userSession = getUserSession();

		if (!kuerzel.equals(daten.getKuerzel())) {

			LOG.error("{}: Konflikt: kuerzel= '{}', daten.kuerzel = '{}'",
				getStringAbbreviated(userSession.getUuid()), kuerzel, daten.getKuerzel());
			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Precondition Failed"));
			return Response.status(412)
				.entity(payload)
				.build();
		}

		this.validationDelegate.check(daten, ChecklisteDaten.class);

		ResponsePayload payload = checklistenService.checklisteAendern(daten, kuerzel, userSession.getUuid());
		LOG.info("{}: checkliste {} geändert", getStringAbbreviated(userSession.getUuid()),
			getStringAbbreviated(kuerzel));
		return Response.ok(payload).build();
	}

	@DELETE
	@Path("/checkliste/{kuerzel}")
	@PermitAll
	public Response checklisteLoeschen(@PathParam(value = "idRef") final String idRef, @PathParam(
		value = "kuerzel") final String kuerzel) {

		UserSession userSession = getUserSession();

		checklistenService.checklisteLoeschen(kuerzel, userSession.getUuid());

		ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.info("erfolgreich gelöscht"));

		LOG.info("{} - {}: checkliste {} gelöscht", getStringAbbreviated(userSession.getUuid()), getStringAbbreviated(kuerzel));
		return Response.ok()
			.entity(payload)
			.build();
	}

	private UserSession getUserSession() {

		Principal userPrincipal = securityContext.getUserPrincipal();

		if (userPrincipal != null) {

			LOG.debug("UserPrincipal gefunden: {}", userPrincipal);

			return (UserSession) userPrincipal;
		}

		LOG.error("keine UserSession für Principal vorhanden");
		throw new AuthException("keine Berechtigung");
	}

	private String getStringAbbreviated(final String string) {

		return StringUtils.abbreviate(string, 11);
	}

}
