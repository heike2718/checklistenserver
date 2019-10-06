// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;
import de.egladil.web.commons_net.time.CommonTimeUtils;

/**
 * PacklisteTemplate
 */
@ApplicationScoped
public class PacklisteTemplate {

	private static final Logger LOG = LoggerFactory.getLogger(PacklisteTemplate.class);

	private LocalDateTime lastReadTime = LocalDateTime.now();

	@ConfigProperty(name = "packliste")
	String pathPackliste;

	private String[] packliste;

	public String[] getPackliste() {

		if (einkaufslisteChanged()) {

			LOG.info("packliste null or changed and is going to be loaded");
			loadEinkaufsliste();
		}

		return packliste;
	}

	private boolean einkaufslisteChanged() {

		if (packliste == null) {

			return true;
		}

		File file = new File(pathPackliste);

		if (file.isFile()) {

			long lastModified = file.lastModified();
			return CommonTimeUtils.transformFromMilliseconds(lastModified).isAfter(lastReadTime);
		}

		return false;

	}

	private void loadEinkaufsliste() {

		Path path = Paths.get(pathPackliste);

		try (Stream<String> lines = Files.lines(path)) {

			List<String> alle = lines.filter(s -> !StringUtils.isBlank(s)).collect(Collectors.toList());

			this.packliste = alle.toArray(new String[0]);
		} catch (IOException e) {

			throw new ChecklistenRuntimeException(e.getMessage(), e);
		}

	}

}
