//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import de.egladil.web.checklistenserver.payload.MessagePayload;
import de.egladil.web.checklistenserver.payload.ResponsePayload;

/**
 * ChecklistenExceptionMapper
 */
@Provider
public class ChecklistenExceptionMapper implements ExceptionMapper<Exception> {

	private static final Logger LOG = LogManager.getLogger(ChecklistenExceptionMapper.class.getName());

	@Override
	public Response toResponse(final Exception exception) {
		if (exception instanceof NoContentException) {
			return Response.status(204).build();
		}
		if (exception instanceof ResourceNotFoundException || exception instanceof ConcurrentUpdateException) {
			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.warn(exception.getMessage()));
			return Response.status(409).entity(payload).build();
		}
		if (exception instanceof ChecklistenRuntimeException) {
			// wurde schon gelogged
		} else {
			LOG.error(exception.getMessage(), exception);
		}

		ResponsePayload payload = ResponsePayload
			.messageOnly(MessagePayload.error("OMG +++ Divide By Cucumber Error. Please Reinstall Universe And Reboot +++"));

		return Response.status(500).entity(payload).build();
	}

}
