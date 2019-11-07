// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.endpoints;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.checklistenserver.service.ClientAccessTokenService;

/**
 * AccessTokenResource
 */
@RequestScoped
@Path("accesstoken")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Deprecated
public class AccessTokenResource {

	@Inject
	ClientAccessTokenService clientAccessTokenService;

	@GET
	@Path("/initial")
	public Response getAccessToken() {

		return orderTheInitialToken();
	}

	private Response orderTheInitialToken() {

		JsonObject authResult = clientAccessTokenService.orderAccessToken();

		if (authResult != null) {

			return Response.ok(authResult).build();
		}

		return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
	}

	@GET
	@Path("/{replacedToken}")
	public Response replaceAccessToken(@PathParam(value = "replacedToken") final String replacedToken) {

		// LOG.debug("Replace the token {}", replacedToken);

		if (replacedToken == null || replacedToken.isBlank()) {

			return this.orderTheInitialToken();
		}

		JsonObject authResult = clientAccessTokenService.replaceAccessToken(replacedToken);

		if (authResult != null) {

			return Response.ok(authResult).build();
		}

		return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
	}
}
