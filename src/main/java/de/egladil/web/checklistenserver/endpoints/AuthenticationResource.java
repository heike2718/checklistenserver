// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.endpoints;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.checklistenserver.service.ClientAccessTokenService;

/**
 * AuthenticationResource authentisiert sich beim authprovider und sendet ein redirect zur auth-app
 */
@RequestScoped
@Path("auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

	@Inject
	private ClientAccessTokenService clientAccessTokenService;

	public Response redirectToAuthprovider() {

		JsonObject accessTokenPayload = clientAccessTokenService.orderAccessToken();

		if (accessTokenPayload == null) {

			return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
		}

		// authUrl + '#/login?accessToken=' + accessToken + '&state=' + state + '&nonce=' + nonce + '&redirectUrl=' + redirectUrl;
		return null;
	}

}
