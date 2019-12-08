// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.checklistenserver.domain.Checkliste;
import de.egladil.web.checklistenserver.domain.ChecklisteDaten;
import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;

/**
 * ChecklisteDatenMapper
 */
public class ChecklisteDatenMapper {

	private static final Logger LOG = LoggerFactory.getLogger(ChecklisteDatenMapper.class);

	/**
	 * @param  checkliste
	 * @return            ChecklisteDaten oder null
	 */
	public static ChecklisteDaten deserialize(final Checkliste checkliste, final String... errmContext) {

		ObjectMapper objectMapper = new ObjectMapper();

		try {

			ChecklisteDaten daten = objectMapper.readValue(checkliste.getDaten().getBytes(), ChecklisteDaten.class);
			return daten;
		} catch (IOException e) {

			String context = errmContext != null ? errmContext[0] : "";
			LOG.error(context + " Checkliste mit kuerzel '{}' hat korrupte Daten", checkliste.getKuerzel());
			return null;
		}
	}

	/**
	 * @param  daten
	 *                     ChecklisteDaten
	 * @param  errmContext
	 *                     String
	 * @return             String
	 */
	public static String serialize(final ChecklisteDaten daten, final String errmContext) {

		ObjectMapper objectMapper = new ObjectMapper();

		try {

			return objectMapper.writeValueAsString(daten);
		} catch (JsonProcessingException e) {

			String msg = errmContext + " (Fehler beim JSONisieren)";
			LOG.error("{}: {}", e.getMessage(), e);
			throw new ChecklistenRuntimeException(msg);
		}
	}
}
