// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.service;

import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;
import de.egladil.web.checklistenserver.error.ClientAuthException;
import de.egladil.web.checklistenserver.error.LogmessagePrefixes;
import de.egladil.web.checklistenserver.restclient.InitAccessTokenRestClient;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.OAuthClientCredentials;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * ClientAccessTokenService
 */
@RequestScoped
public class ClientAccessTokenService {

	private static final Logger LOG = LoggerFactory.getLogger(ClientAccessTokenService.class);

	@Inject
	@ConfigProperty(name = "auth.client-id")
	String clientId;

	@Inject
	@ConfigProperty(name = "auth.client-secret")
	String clientSecret;

	@Inject
	@RestClient
	InitAccessTokenRestClient initAccessTokenClient;

	/**
	 * Holt sich ein clientAccessToken beim authprovider.
	 *
	 * @return JsonObject oder null
	 */
	public String orderAccessToken() {

		String nonce = UUID.randomUUID().toString();
		OAuthClientCredentials credentials = OAuthClientCredentials.create(clientId,
			clientSecret, nonce);

		try {

			Response authResponse = initAccessTokenClient.authenticateClient(credentials);

			ResponsePayload responsePayload = authResponse.readEntity(ResponsePayload.class);

			evaluateResponse(nonce, responsePayload);

			@SuppressWarnings("unchecked")
			Map<String, String> dataMap = (Map<String, String>) responsePayload.getData();
			String accessToken = dataMap.get("accessToken");

			return accessToken;
		} catch (IllegalStateException | RestClientDefinitionException e) {

			LOG.error(e.getMessage(), e);
			throw new ChecklistenRuntimeException("Unerwarteter Fehler beim Anfordern eines client-accessTokens: " + e.getMessage(),
				e);
		} catch (ClientAuthException e) {

			// ist schon geloggt
			return null;

		} catch (WebApplicationException e) {

			LOG.error(e.getMessage(), e);

			return null;
		} finally {

			credentials.clean();
		}
	}

	private void evaluateResponse(final String nonce, final ResponsePayload responsePayload) throws ClientAuthException {

		MessagePayload messagePayload = responsePayload.getMessage();

		if (messagePayload.isOk()) {

			@SuppressWarnings("unchecked")
			Map<String, String> dataMap = (Map<String, String>) responsePayload.getData();
			String responseNonce = dataMap.get("nonce");

			if (!nonce.equals(responseNonce)) {

				LOG.warn(LogmessagePrefixes.BOT + "zurückgesendetes nonce stimmt nicht");
				throw new ClientAuthException();
			}
		} else {

			LOG.error("Authentisierung des Clients hat nicht geklappt: {} - {}", messagePayload.getLevel(),
				messagePayload.getMessage());
			throw new ClientAuthException();
		}
	}
}
