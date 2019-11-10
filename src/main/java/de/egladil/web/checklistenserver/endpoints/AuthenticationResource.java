// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.endpoints;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.service.ClientAccessTokenService;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * AuthenticationResource authentisiert sich beim authprovider und sendet ein redirect zur auth-app
 */
@RequestScoped
@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationResource.class);

	@ConfigProperty(name = "auth-app.url")
	String authAppUrl;

	@ConfigProperty(name = "auth.redirect-url.login")
	String loginRedirectUrl;

	@ConfigProperty(name = "auth.redirect-url.signup")
	String signupRedirectUrl;

	@Inject
	private ClientAccessTokenService clientAccessTokenService;

	@GET
	@Path("/login")
	public Response getLoginUrl() {

		String accessToken = this.getAccessToken();

		if (StringUtils.isBlank(accessToken)) {

			return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
		}

		String redirectUrl = authAppUrl + "#/login?accessToken=" + accessToken + "&state=login&nonce=null&redirectUrl="
			+ loginRedirectUrl;

		LOG.debug(redirectUrl);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(redirectUrl))).build();
	}

	@GET
	@Path("/signup")
	public Response getSignupUrl() {

		String accessToken = this.getAccessToken();

		if (StringUtils.isBlank(accessToken)) {

			return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
		}

		String redirectUrl = authAppUrl + "#/signup?accessToken=" + accessToken + "&state=login&nonce=null&redirectUrl="
			+ signupRedirectUrl;

		LOG.debug(redirectUrl);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(redirectUrl))).build();
	}

	private String getAccessToken() {

		JsonObject accessTokenPayload = clientAccessTokenService.orderAccessToken();

		if (accessTokenPayload == null) {

			return null;
		}

		try {

			JsonObject data = accessTokenPayload.getJsonObject("data");
			String accessToken = data.getString("accessToken");

			return accessToken;

		} catch (Exception e) {

			LOG.error(e.getMessage());
			return null;
		}

	}

}
