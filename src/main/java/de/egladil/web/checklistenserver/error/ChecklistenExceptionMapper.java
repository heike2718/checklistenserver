// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.error;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.ChecklistenServerApp;
import de.egladil.web.checklistenserver.domain.UserSession;
import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * ChecklistenExceptionMapper wird durchjax.rs aufgerufen und behandelt alle Exceptions sinnvoll. Dadurch muss kein
 * Endpoint mehr Exceptions fangen.
 */
@Provider
public class ChecklistenExceptionMapper implements ExceptionMapper<Exception> {

	private static final Logger LOG = LoggerFactory.getLogger(ChecklistenExceptionMapper.class);

	@Context
	SecurityContext securityContext;

	@Override
	public Response toResponse(final Exception exception) {

		if (exception instanceof NoContentException) {

			return Response.status(204).build();
		}

		if (exception instanceof InvalidInputException) {

			InvalidInputException e = (InvalidInputException) exception;
			return Response.status(400).entity(e.getResponsePayload()).build();
		}

		if (exception instanceof AuthException) {

			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Du kommst nicht vorbei!"));
			return Response.status(401)
				.cookie(CommonHttpUtils.createSessionInvalidatedCookie(ChecklistenServerApp.CLIENT_COOKIE_PREFIX)).entity(payload)
				.build();
		}

		if (exception instanceof SessionExpiredException) {

			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Deine Session ist abgelaufen."));
			return Response.status(908).entity(payload)
				.cookie(CommonHttpUtils.createSessionInvalidatedCookie(ChecklistenServerApp.CLIENT_COOKIE_PREFIX)).build();
		}

		if (exception instanceof NotFoundException) {

			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Hamwer nich"));
			return Response.status(404).entity(payload).build();
		}

		if (exception instanceof ConcurrentUpdateException) {

			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.warn(exception.getMessage()));
			return Response.status(409).entity(payload).build();
		}

		if (exception instanceof ChecklistenRuntimeException || exception instanceof ClientAuthException) {

			// wurde schon gelogged
		} else {

			if (securityContext != null && securityContext.getUserPrincipal() instanceof UserSession) {

				UserSession userSession = (UserSession) securityContext.getUserPrincipal();
				LOG.error("{} - {}: {}", StringUtils.abbreviate(userSession.getIdReference(), 11),
					StringUtils.abbreviate(userSession.getUuid(), 11), exception.getMessage(), exception);
			} else {

				LOG.error(exception.getMessage(), exception);
			}
		}

		ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error(
			"OMG +++ Divide By Cucumber Error. Please Reinstall Universe And Reboot +++ (und schau besser auch mal ins Server-Log...)"));

		return Response.status(Status.INTERNAL_SERVER_ERROR).header("X-Checklisten-Error", payload.getMessage().getMessage())
			.entity(payload).build();
	}

}
