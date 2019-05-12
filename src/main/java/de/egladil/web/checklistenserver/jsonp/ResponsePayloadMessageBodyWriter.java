//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.jsonp;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons.payload.ResponsePayload;

/**
 * ResponsePayloadMessageBodyWriter
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ResponsePayloadMessageBodyWriter implements MessageBodyWriter<ResponsePayload> {

	@Override
	public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations,
		final MediaType mediaType) {
		return ResponsePayload.class.equals(type);
	}

	@Override
	public void writeTo(final ResponsePayload t, final Class<?> type, final Type genericType, final Annotation[] annotations,
		final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream)
		throws IOException, WebApplicationException {

		entityStream.write(new ObjectMapper().writeValueAsBytes(t));
	}

}
