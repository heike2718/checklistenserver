// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.payload;

import javax.validation.constraints.NotNull;

import de.egladil.web.commons_validation.annotations.Honeypot;
import de.egladil.web.commons_validation.annotations.Passphrase;

/**
 * SignUpPayload
 */
public class SignUpPayload {

	@Passphrase
	private String secret;

	@Honeypot(message = "")
	private String kleber;

	/**
	 * Erzeugt eine Instanz von SignUpPayload
	 */
	public SignUpPayload() {

	}

	/**
	 * Erzeugt eine Instanz von SignUpPayload
	 */
	public SignUpPayload(@NotNull final String secret, final String kleber) {

		this.secret = secret;
		this.kleber = kleber;
	}

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

		if (this.secret != null) {

			final char[] chars = this.secret.toCharArray();

			for (int i = 0; i < chars.length; i++) {

				chars[i] = (char) 0;
			}
		}
		this.secret = null;
	}
}
