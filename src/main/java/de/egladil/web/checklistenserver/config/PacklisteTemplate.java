// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * PacklisteTemplate
 */
@ApplicationScoped
public class PacklisteTemplate extends AbstractListeTemplate {

	private static final String SUFFIX_FILENAME = "_packliste.txt";

	@Inject
	@ConfigProperty(name = "dir.packlisten")
	String pathDirPacklisten;

	@Override
	protected String getPathTemplateDir() {

		return pathDirPacklisten;
	}

	@Override
	protected String getSuffixFilename() {

		return SUFFIX_FILENAME;
	}

}
