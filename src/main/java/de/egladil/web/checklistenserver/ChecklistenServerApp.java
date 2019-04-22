//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver;

import java.security.Security;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


/**
* ChecklistenServerApp
*/
@ApplicationPath("/checklisten-api")
public class ChecklistenServerApp extends Application {

	/**
	* Erzeugt eine Instanz von ChecklistenServerApp
	*/
	public ChecklistenServerApp() {
		Security.addProvider(new BouncyCastleProvider());
	}
}
