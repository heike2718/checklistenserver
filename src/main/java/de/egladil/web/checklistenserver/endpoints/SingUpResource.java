//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.endpoints;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;

import de.egladil.web.checklistenserver.payload.SignUpPayload;
import de.egladil.web.checklistenserver.service.SignUpService;
import de.egladil.web.commons.payload.MessagePayload;
import de.egladil.web.commons.payload.ResponsePayload;
import de.egladil.web.commons.validation.ValidationDelegate;

/**
 * SingUpResource
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Log(LogParams.METRICS)
@RequestScoped
@Path("signup")
public class SingUpResource {

	@Inject
	private SignUpService signUpService;

	private ValidationDelegate validationDelegate = new ValidationDelegate();

	/**
	 * Prüft, ob derjenige, der diesen Endpoint aufruft, das Geheimnis kennt.
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
	 * Prüft, ob derjenige, der diesen Endpoint aufruft, das Geheimnis kennt.
	 *
	 * @param signUpPayload String
	 * @return Response
	 */
	@POST
	@Path("/user")
	public Response createUser(final String jwt) {

		ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Tritt ein, Fremder."));
		return Response.status(201).entity(payload).build();

	}

}
