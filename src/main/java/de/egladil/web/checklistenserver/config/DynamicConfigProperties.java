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

	public String getChecklistenserverVersion() {
		return checklistenserverVersion;
	}

	public void setChecklistenserverVersion(final String checklistenappVersion) {
		this.checklistenserverVersion = checklistenappVersion;
	}

}
