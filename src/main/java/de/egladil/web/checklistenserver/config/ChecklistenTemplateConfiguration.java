//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.config;

import javax.enterprise.context.ApplicationScoped;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;

/**
 * ChecklistenTemplateConfiguration
 */
@ApplicationScoped
@ConfigBundle("checklistentemplate-config")
public class ChecklistenTemplateConfiguration {

	private String eingaufsliste;

	private String packliste;

	public String getEingaufsliste() {
		return eingaufsliste;
	}

	public void setEingaufsliste(final String eingaufsliste) {
		this.eingaufsliste = eingaufsliste;
	}

	public String getPackliste() {
		return packliste;
	}

	public void setPackliste(final String packliste) {
		this.packliste = packliste;
	}

}
