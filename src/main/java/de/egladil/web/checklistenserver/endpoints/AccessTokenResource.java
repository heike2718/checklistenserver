//=====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.checklistenserver.endpoints;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.config.ApplicationConfig;
import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;
import de.egladil.web.checklistenserver.restclient.OAuthRestClient;
import de.egladil.web.commons.error.AuthException;
import de.egladil.web.commons.error.LogmessagePrefixes;
import de.egladil.web.commons.payload.OAuthClientCredentials;

/**
 * AccessTokenResource
 */
@RequestScoped
@Path("accesstoken")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccessTokenResource {

	private static final Logger LOG = LoggerFactory.getLogger(AccessTokenResource.class.getSimpleName());

	@Inject
	private ApplicationConfig applicationConfig;

	@GET
	public Response getAccessToken() {

		String nonce = UUID.randomUUID().toString();
		OAuthClientCredentials credentials = OAuthClientCredentials.create(applicationConfig.getClientId(),
			applicationConfig.getClientSecret(), nonce);

		try {
			OAuthRestClient restClient = RestClientBuilder.newBuilder()
				.baseUri(new URI(applicationConfig.getAuthBaseUri()))
				.connectTimeout(1000, TimeUnit.MILLISECONDS)
				.readTimeout(5000, TimeUnit.MILLISECONDS)
				.build(OAuthRestClient.class);

			JsonObject response = restClient.orderAccessToken(credentials);

			LOG.info("{}", response);

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
		} catch (IllegalStateException | RestClientDefinitionException | URISyntaxException e) {
			LOG.error(e.getMessage(), e);
			throw new ChecklistenRuntimeException("Unerwarteter Fehler beim Anfordern eines accessTokens: " + e.getMessage(), e);
		}
	}
}
