// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * ConfigService
 */
@ApplicationScoped
public class ConfigService {

	@ConfigProperty(name = "block.on.missing.origin.referer", defaultValue = "false")
	boolean blockOnMissingOriginReferer;

	@ConfigProperty(name = "target.origin")
	String targetOrigin;

	@ConfigProperty(name = "stage")
	String stage;

	@ConfigProperty(name = "allowedOrigin", defaultValue = "https://opa-wetterwachs.de")
	String allowedOrigin;

	public boolean isBlockOnMissingOriginReferer() {

		return blockOnMissingOriginReferer;
	}

	public String getTargetOrigin() {

		return targetOrigin;
	}

	public String getStage() {

		return stage;
	}

	public String getAllowedOrigin() {

		return allowedOrigin;
	}

}
