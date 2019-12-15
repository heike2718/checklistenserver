// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * ChecklistenServerApp
 */
// @LoginConfig(authMethod = "MP-JWT")
@ApplicationPath("/checklisten-api")
public class ChecklistenServerApp extends Application {

	public static final String CLIENT_COOKIE_PREFIX = "CHK";

	public static final String STAGE_DEV = "dev";

}
