// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * SignupSecretProperties
 */
@ApplicationScoped
public class SignupSecretProperties {

	@ConfigProperty(name = "signup.salt")
	String salt;

	@ConfigProperty(name = "signup.pepper")
	String pepper;

	@ConfigProperty(name = "signup.algorithm")
	String algorithm;

	@ConfigProperty(name = "signup.iterations")
	int iterations;

	@ConfigProperty(name = "signup.secret")
	String secret;

	public String getSalt() {

		return salt;
	}

	public String getPepper() {

		return pepper;
	}

	public String getAlgorithm() {

		return algorithm;
	}

	public int getIterations() {

		return iterations;
	}

	public String getSecret() {

		return secret;
	}

}
