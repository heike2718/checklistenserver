// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.service;

import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.WebApplicationException;

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
 * ClientAccessTokenService
 */
@RequestScoped
public class ClientAccessTokenService {

	private static final Logger LOG = LoggerFactory.getLogger(ClientAccessTokenService.class);

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

	/**
	 * Holt sich ein clientAccessToken beim authprovider.
	 *
	 * @return JsonObject oder null
	 */
	public JsonObject orderAccessToken() {

		String nonce = UUID.randomUUID().toString();
		OAuthClientCredentials credentials = OAuthClientCredentials.create(clientId,
			clientSecret, nonce);

		try {

			JsonObject authResponse = initAccessTokenService.authenticateClient(credentials);

			evaluateResponse(nonce, authResponse);

			return authResponse;
		} catch (IllegalStateException | RestClientDefinitionException e) {

			LOG.error(e.getMessage(), e);
			throw new ChecklistenRuntimeException("Unerwarteter Fehler beim Anfordern eines client-accessTokens: " + e.getMessage(),
				e);
		} catch (AuthException e) {

			// ist schon geloggt
			return null;

		} catch (WebApplicationException e) {

			LOG.error(e.getMessage(), e);

			return null;
		}
	}

	/**
	 * Ersetzt das accessToken oder orderd ein neues.
	 *
	 * @param  replacedToken
	 * @return               JsonObject oder null
	 */
	public JsonObject replaceAccessToken(final String replacedToken) {

		String nonce = UUID.randomUUID().toString();
		OAuthClientCredentials credentials = OAuthClientCredentials.create(clientId,
			clientSecret, nonce);

		try {

			JsonObject authResponse = replaceAccessTokenRestClient.replaceAccessToken(replacedToken, credentials);

			this.evaluateResponse(nonce, authResponse);

			return authResponse;
		} catch (IllegalStateException | RestClientDefinitionException e) {

			LOG.error(e.getMessage(), e);
			throw new ChecklistenRuntimeException("Unerwarteter Fehler beim Anfordern eines client-accessTokens: " + e.getMessage(),
				e);
		} catch (AuthException e) {

			// ist schon geloggt
			return null;

		} catch (WebApplicationException e) {

			LOG.error(e.getMessage(), e);

			return null;
		}

	}

	private void evaluateResponse(final String nonce, final JsonObject authResponse) throws AuthException {

		JsonObject message = authResponse.getJsonObject("message");
		String level = message.getString("level");
		String theMessage = message.getString("message");

		if ("INFO".equals(level)) {

			String responseNonce = authResponse.getJsonObject("data").getString("nonce");

			if (!nonce.equals(responseNonce)) {

				LOG.warn(LogmessagePrefixes.BOT + "zurückgesendetes nonce stimmt nicht");
				throw new AuthException();
			}
		} else {

			LOG.warn("Authentisierung des Clients hat nicht geklappt: {} - {}", level, theMessage);
			throw new AuthException();
		}
	}
}
