//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.endpoints;

import java.security.Principal;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.checklistenserver.payload.SignUpPayload;
import de.egladil.web.checklistenserver.service.SignUpService;
import de.egladil.web.commons.payload.HateoasPayload;
import de.egladil.web.commons.payload.MessagePayload;
import de.egladil.web.commons.payload.ResponsePayload;
import de.egladil.web.commons.validation.ValidationDelegate;

/**
 * SignUpResource
 */
@RequestScoped
@Path("signup")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SignUpResource {

	@Inject
	private SignUpService signUpService;

	@Context
	private SecurityContext securityContext;

	private ValidationDelegate validationDelegate = new ValidationDelegate();

	/**
	 * Prüft, ob derjenige, der diesen Endpoint aufruft, das Geheimnis kennt. Dann wird er auf den AuthProvider
	 * redirected, um ein Konto anzulegen.
	 *
	 * @param signUpPayload String
	 * @return Response
	 */
	@POST
	@Path("/secret")
	public Response checkMaySignUp(SignUpPayload signUpPayload) {

		SignUpPayload trimmedPayload = trimIfNotEmpty(signUpPayload);
		try {
			validationDelegate.check(trimmedPayload, SignUpPayload.class);

			signUpService.verifySecret(trimmedPayload);

			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Tritt ein, Fremder."));
			return Response.status(200).entity(payload).build();
		} finally {
			signUpPayload.wipe();
			signUpPayload = null;
			if (trimmedPayload != null) {
				trimmedPayload.wipe();
				trimmedPayload = null;
			}
		}

	}

	private SignUpPayload trimIfNotEmpty(final SignUpPayload payload) {
		if (payload == null) {
			return null;
		}
		String secret = payload.getSecret();
		if (secret != null && StringUtils.isEmpty(secret)) {
			secret = secret.trim();
		}
		SignUpPayload result = new SignUpPayload(secret, payload.getKleber());
		secret = null;
		payload.wipe();
		return result;
	}

	/**
	 * Legt einen Checklistenuser mit der UUID an.
	 *
	 * @param signUpPayload String
	 * @return Response
	 */
	@POST
	@Path("/user")
	public Response createUser() {

		Principal principal = securityContext.getUserPrincipal();

		String uuid = principal.getName();
		Optional<HateoasPayload> optHateoasPayload = signUpService.findUser(uuid);

		if (optHateoasPayload.isPresent()) {
			return Response.ok().entity(new ResponsePayload(MessagePayload.info("User existiert bereits"), optHateoasPayload.get()))
				.build();
		}

		HateoasPayload hateoasPayload = signUpService.signUpUser(uuid);

		ResponsePayload payload = new ResponsePayload(MessagePayload.info("User angelegt"), hateoasPayload);
		return Response.status(201).entity(payload).build();
	}

}
