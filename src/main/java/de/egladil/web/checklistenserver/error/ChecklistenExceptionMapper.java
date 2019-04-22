//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.error;

import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import de.egladil.web.commons.error.AuthException;
import de.egladil.web.commons.error.ConcurrentUpdateException;
import de.egladil.web.commons.error.InvalidInputException;
import de.egladil.web.commons.error.ResourceNotFoundException;
import de.egladil.web.commons.error.SessionExpiredException;
import de.egladil.web.commons.payload.MessagePayload;
import de.egladil.web.commons.payload.ResponsePayload;

/**
 * ChecklistenExceptionMapper wird durchjax.rs aufgerufen und behandelt alle Exceptions sinnvoll. Dadurch muss kein
 * Endpoint mehr Exceptions fangen.
 */
@Provider
public class ChecklistenExceptionMapper implements ExceptionMapper<Exception> {

	private static final Logger LOG = LogManager.getLogger(ChecklistenExceptionMapper.class.getName());

	@Override
	public Response toResponse(final Exception exception) {
		if (exception instanceof NoContentException) {
			return Response.status(204).build();
		}
		if (exception instanceof InvalidInputException) {
			InvalidInputException e = (InvalidInputException) exception;
			return Response.status(400).entity(e.getResponsePayload()).build();
		}
		if (exception instanceof ChecklistenAuthenticationException || exception instanceof AuthException) {
			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Du kommst nicht vorbei!"));
			return Response.status(401).entity(payload).build();
		}
		if (exception instanceof SessionExpiredException) {
			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Deine Session ist abgelaufen."));
			return Response.status(901).entity(payload).build();
		}
		if (exception instanceof ResourceNotFoundException) {
			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Hamwer nich"));
			return Response.status(404).entity(payload).build();
		}
		if (exception instanceof ConcurrentUpdateException) {
			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.warn(exception.getMessage()));
			return Response.status(409).entity(payload).build();
		}
		if (exception instanceof ChecklistenRuntimeException) {
			// wurde schon gelogged
		} else {
			LOG.error(exception.getMessage(), exception);
		}

		ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error(
			"OMG +++ Divide By Cucumber Error. Please Reinstall Universe And Reboot +++ (und schau besser auch mal ins Server-Log...)"));

		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(payload).build();
	}

}
