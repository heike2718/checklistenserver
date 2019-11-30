// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

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

import de.egladil.web.checklistenserver.service.SignUpService;
import de.egladil.web.commons_validation.payload.HateoasPayload;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * SignUpResource
 */
@RequestScoped
@Path("signup")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SignUpResource {

	@Inject
	SignUpService signUpService;

	@Context
	SecurityContext securityContext;

	/**
	 * Legt einen Checklistenuser mit der UUID an.
	 *
	 * @param  signUpPayload
	 *                       String
	 * @return               Response
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
