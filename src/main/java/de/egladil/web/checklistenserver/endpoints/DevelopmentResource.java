//=====================================================
// Projekt: authprovider
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.endpoints;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons.utils.CommonTimeUtils;

/**
 * DevelopmentResource stellt REST-Endpoints zum Spielen und Dinge ausprobieren zur Verfügung. Die werden irgendwann
 * umziehen.
 */
@RequestScoped
@Path("dev")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DevelopmentResource {

	private static final Logger LOG = LoggerFactory.getLogger(DevelopmentResource.class.getSimpleName());

	@GET
	@Path("/hello")
	public Response test() {
		final Map<String, String> json = new HashMap<>();
		json.put("greetings",
			"Also Hallochen am  "
				+ DateTimeFormatter.ofPattern(CommonTimeUtils.DEFAULT_DATE_TIME_FORMAT).format(CommonTimeUtils.now()));

		LOG.debug("Fast fertig");

		return Response.ok(json).build();
	}

}
