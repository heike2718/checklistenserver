//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.endpoints;

import java.security.Principal;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.commons.jwt.JwtAuthz;

/**
 * UserResource
 */
@RequestScoped
@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@JwtAuthz
public class UserResource {

	@Context
	private SecurityContext securityContext;

	/**
	 * Erzeugt eine Instanz von UserResource
	 */
	public UserResource() {
	}

	@GET
	@Path("/user")
	public Response getUser() {

		Principal principal = securityContext.getUserPrincipal();

		// Erstmal no content
		return Response.status(204).build();
	}
}
