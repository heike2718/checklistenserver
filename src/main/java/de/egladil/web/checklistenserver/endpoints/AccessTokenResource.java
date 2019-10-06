// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.endpoints;

import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.error.AuthException;
import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;
import de.egladil.web.checklistenserver.error.LogmessagePrefixes;
import de.egladil.web.checklistenserver.restclient.InitAccessTokenRestClient;
import de.egladil.web.checklistenserver.restclient.ReplaceAccessTokenRestClient;
import de.egladil.web.commons_validation.payload.OAuthClientCredentials;

/**
 * AccessTokenResource
 */
@RequestScoped
@Path("accesstoken")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccessTokenResource {

	private static final Logger LOG = LoggerFactory.getLogger(AccessTokenResource.class.getName());

	@ConfigProperty(name = "auth.client-id")
	String clientId;

	@ConfigProperty(name = "auth.client-secret")
	String clientSecret;

	@Inject
	@RestClient
	InitAccessTokenRestClient initAccessTokenService;

	@Inject
	@RestClient
	ReplaceAccessTokenRestClient replaceAccessTokenRestClient;

	@GET
	@Path("/initial")
	public Response getAccessToken() {

		String nonce = UUID.randomUUID().toString();
		OAuthClientCredentials credentials = OAuthClientCredentials.create(clientId,
			clientSecret, nonce);

		return orderTheInitialToken(nonce, credentials);
	}

	private Response orderTheInitialToken(final String nonce, final OAuthClientCredentials credentials) {

		try {

			JsonObject auhtResponse = initAccessTokenService.authenticateClient(credentials);

			return evaluateResponse(nonce, auhtResponse);
		} catch (IllegalStateException | RestClientDefinitionException e) {

			LOG.error(e.getMessage(), e);
			throw new ChecklistenRuntimeException("Unerwarteter Fehler beim Anfordern eines client-accessTokens: " + e.getMessage(),
				e);
		} catch (WebApplicationException e) {

			LOG.error(e.getMessage(), e);

			return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
		}
	}

	private Response evaluateResponse(final String nonce, final JsonObject response) {

		JsonObject message = response.getJsonObject("message");
		String level = message.getString("level");

		if ("INFO".equals(level)) {

			String responseNonce = response.getJsonObject("data").getString("nonce");

			if (!nonce.equals(responseNonce)) {

				LOG.error(LogmessagePrefixes.BOT + "zurückgesendetes nonce stimmt nicht");
				throw new AuthException();
			}
		}

		return Response.ok(response).build();
	}

	@GET
	@Path("/{replacedToken}")
	public Response replaceAccessToken(@PathParam(value = "replacedToken") final String replacedToken) {

		// LOG.debug("Replace the token {}", replacedToken);

		String nonce = UUID.randomUUID().toString();
		OAuthClientCredentials credentials = OAuthClientCredentials.create(clientId,
			clientSecret, nonce);

		if (replacedToken == null || replacedToken.isBlank()) {

			return this.orderTheInitialToken(nonce, credentials);
		}

		try {

			JsonObject response = replaceAccessTokenRestClient.replaceAccessToken(replacedToken, credentials);

			return evaluateResponse(nonce, response);
		} catch (IllegalStateException | RestClientDefinitionException e) {

			LOG.error(e.getMessage(), e);
			throw new ChecklistenRuntimeException(
				"Unerwarteter Fehler beim Austauschen eines client-accessTokens: " + e.getMessage(), e);
		} catch (WebApplicationException e) {

			Response response = e.getResponse();

			if (response.getStatus() == 404) {

				String msg = "404 beim Austauschen eines abgelaufenen Client-AccessTokens. Könnte aus einem Browser-Cache stammen und in der DB gelöscht worden sein. Hole ein frisches AccessToken.";

				LOG.info(msg);

				return getAccessToken();
			}

			LOG.error("Unerwarteter Fehler beim Austauschen des ClientAccessTokens: Status={}", response.getStatus());
			return Response.serverError().build();
		}
	}
}
