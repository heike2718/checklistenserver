// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.endpoints;

import java.security.Principal;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.domain.ChecklisteDaten;
import de.egladil.web.checklistenserver.domain.Checklistentyp;
import de.egladil.web.checklistenserver.domain.UserSession;
import de.egladil.web.checklistenserver.error.AuthException;
import de.egladil.web.checklistenserver.service.ChecklistenTemplateProvider;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * ChecklistenTemplateResource gibt Vorgabedetails für Checklisten zurück.
 */
@RequestScoped
@Path("templates")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChecklistenTemplateResource {

	private static final Logger LOG = LoggerFactory.getLogger(ChecklistenTemplateResource.class);

	@Context
	SecurityContext securityContext;

	@Inject
	ChecklistenTemplateProvider templateProvider;

	@GET
	@Path("/{typ}")
	public Response getTemplateMitTypFuerGruppe(@PathParam("typ") final String typValue) {

		try {

			UserSession userSession = getUserSession();

			Checklistentyp typ = Checklistentyp.valueOf(typValue.trim().toUpperCase());
			ChecklisteDaten template = templateProvider.getTemplateMitTypFuerGruppe(typ, userSession.getUuid());

			ResponsePayload payload = new ResponsePayload(MessagePayload.info("Bitteschön"), template);
			return Response.ok().entity(payload).build();
		} catch (IllegalArgumentException e) {

			LOG.error("Falscher Parameter [typ={}]", typValue);
			return Response.status(404)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("Gib einen korrekten Checklistentyp an"))).build();
		}
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
}
