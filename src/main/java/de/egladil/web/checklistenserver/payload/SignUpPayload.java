//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.payload;

import de.egladil.web.commons.validation.annotations.Honeypot;
import de.egladil.web.commons.validation.annotations.Passwort;

/**
 * SignUpPayload
 */
public class SignUpPayload {

	@Passwort
	private String secret;

	@Honeypot(message = "")
	private String kleber;

	public String getSecret() {
		return secret;
	}

	public void setSecret(final String secret) {
		this.secret = secret;
	}

	public String getKleber() {
		return kleber;
	}

	public void setKleber(final String kleber) {
		this.kleber = kleber;
	}

	public void wipe() {
		this.secret = null;
	}

}
