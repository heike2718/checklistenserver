// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * EinkaufslisteTemplate
 */
@ApplicationScoped
public class EinkaufslisteTemplate extends AbstractListeTemplate {

	private static final String SUFFIX_FILENAME = "_einkaufsliste.txt";

	@ConfigProperty(name = "dir.einkaufslisten")
	String pathDirEinkaufslisten;

	@Override
	protected String getPathTemplateDir() {

		return pathDirEinkaufslisten;
	}

	@Override
	protected String getSuffixFilename() {

		return SUFFIX_FILENAME;
	}
}
