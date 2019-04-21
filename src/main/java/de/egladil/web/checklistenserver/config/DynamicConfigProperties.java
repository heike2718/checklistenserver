//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.config;

import de.egladil.web.commons.config.DynamicConfiguration;

/**
 * DynamicConfigProperties
 */
public class DynamicConfigProperties implements DynamicConfiguration {

	private String checklistenserverVersion;

	private String salt;

	private String pepper;

	private String algorithm;

	private Integer iterations;

	private String secret;


	public String getChecklistenserverVersion() {
		return checklistenserverVersion;
	}

	public void setChecklistenserverVersion(final String checklistenappVersion) {
		this.checklistenserverVersion = checklistenappVersion;
	}

	public String getSalt() {
		return salt;
	}

	public String getPepper() {
		return pepper;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public Integer getIterations() {
		return iterations;
	}

	public String getSecret() {
		return secret;
	}

}
