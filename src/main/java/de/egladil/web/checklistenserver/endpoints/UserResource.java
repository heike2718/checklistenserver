//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.endpoints;

import java.security.Principal;

import javax.enterprise.context.RequestScoped;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;

import de.egladil.web.commons.payload.MessagePayload;
import de.egladil.web.commons.payload.ResponsePayload;
import de.egladil.web.commons.validation.annotations.UuidString;

/**
 * UserResource
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Log(LogParams.METRICS)
@RequestScoped
@Path("users")
public class UserResource {

	/**
	 * Erzeugt eine Instanz von UserResource
	 */
	public UserResource() {
	}

	@Path("/{uuid}")
	public Response getUser(@Context
	final SecurityContext securityContext, @UuidString @PathParam("uuid")
	final String uuid) {

		Principal principal = securityContext.getUserPrincipal();
		if (principal == null || uuid == null || !uuid.equals(principal.getName())) {
			return Response.status(403).entity(ResponsePayload.messageOnly(MessagePayload.error("Keine Berechtigung"))).build();
		}

		// Erstmal no content
		return Response.status(204).build();
	}

}
