//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.endpoints;

import java.security.Principal;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.commons.jwt.JwtAuthz;
import de.egladil.web.commons.payload.MessagePayload;
import de.egladil.web.commons.payload.ResponsePayload;
import de.egladil.web.commons.validation.ValidationDelegate;
import de.egladil.web.commons.validation.annotations.UuidString;
import de.egladil.web.commons.validation.beans.UuidPayload;

/**
 * UserResource
 */
@RequestScoped
@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@JwtAuthz
public class UserResource {

	private final ValidationDelegate validationDelegate = new ValidationDelegate();

	@Context
	private SecurityContext securityContext;

	/**
	 * Erzeugt eine Instanz von UserResource
	 */
	public UserResource() {
	}

	@Path("/{uuid}")
	public Response getUser(@UuidString
	@PathParam("uuid")
	final String uuid) {

		validationDelegate.check(new UuidPayload(uuid), UuidPayload.class);

		Principal principal = securityContext.getUserPrincipal();
		if (principal == null || uuid == null || !uuid.equals(principal.getName())) {
			return Response.status(403).entity(ResponsePayload.messageOnly(MessagePayload.error("Keine Berechtigung"))).build();
		}

		// Erstmal no content
		return Response.status(204).build();
	}
}
