//=====================================================
// Projekt: authprovider
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.endpoints;

import java.time.LocalDateTime;
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

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;

/**
 * DevelopmentController stellt REST-Endpoints zum Spielen und Dinge ausprobieren zur Verfügung. Die werden irgendwann
 * umziehen.
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Log(LogParams.METRICS)
@RequestScoped
@Path("dev")
public class DevelopmentController {

	private static final Logger LOG = LogManager.getLogger(DevelopmentController.class.getName());

	@GET
	@Path("/hello")
	public Response test() {
		final Map<String, String> json = new HashMap<>();
		json.put("greetings",
			"Also Hallochen am  " + DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss").format(LocalDateTime.now()));

		LOG.debug("Fast fertig");

		return Response.ok(json).build();
	}

}
