//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import de.egladil.web.checklistenserver.dao.ChecklisteDao;
import de.egladil.web.checklistenserver.domain.Checkliste;
import de.egladil.web.checklistenserver.domain.ChecklisteDaten;
import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;
import de.egladil.web.checklistenserver.error.ResourceNotFoundException;
import de.egladil.web.checklistenserver.payload.MessagePayload;
import de.egladil.web.checklistenserver.payload.ResponsePayload;

/**
 * ChecklistenService
 */
@RequestScoped
public class ChecklistenService {

	private static final Logger LOG = LogManager.getLogger(ChecklistenService.class.getName());

	@Inject
	private ChecklisteDao checklisteDao;

	public List<Checkliste> loadChecklisten() {
		return checklisteDao.loadChecklisten();
	}

	@Transactional
	public ChecklisteDaten checklisteAnlegen(final ChecklisteDaten daten) {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			String kuerzel = UUID.randomUUID().toString();
			daten.setKuerzel(kuerzel);
			Checkliste checkliste = new Checkliste();
			checkliste.setKuerzel(daten.getKuerzel());
			checkliste.setName(daten.getName());
			checkliste.setTyp(daten.getTyp());

			checkliste.setDaten(objectMapper.writeValueAsString(daten));

			Checkliste persisted = checklisteDao.save(checkliste);

			daten.setVersion(persisted.getVersion());

			LOG.info("Checkliste mit kuerzel [{}] angelegt", kuerzel);
			return daten;
		} catch (JsonProcessingException e) {
			String msg = "Anlegen gescheitert (Fehler beim JSONisieren)";
			LOG.error("{}: {}", e.getMessage(), e);
			throw new ChecklistenRuntimeException(msg);
		} catch (PersistenceException e) {
			String msg = "Anlegen gescheitert (Fehler beim Speichern)";
			LOG.error("{}: {}", e.getMessage(), e);
			throw new ChecklistenRuntimeException(msg);
		}
	}

	/**
	 * Ändert die Daten oder den Namen.
	 *
	 * @param daten
	 * @return ChecklisteDaten
	 */
	@Transactional
	public ResponsePayload checklisteAendern(final ChecklisteDaten daten) {

		if (daten == null || daten.getKuerzel() == null) {
			throw new ChecklistenRuntimeException("Ändern gescheitert: keine daten oder noch kein kuerzel");
		}

		Optional<Checkliste> opt = checklisteDao.findByKuerzel(daten.getKuerzel());
		if (!opt.isPresent()) {
			throw new ResourceNotFoundException();
		}

		Checkliste checkliste = opt.get();
		try {
			if (daten.getVersion() < checkliste.getVersion()) {
				return handleConcurrentUpdate(checkliste);
			}
			// persist erhöht Version um 1, das muss auch in die Daten.
			daten.setVersion(checkliste.getVersion() + 1);
			checkliste.setName(daten.getName());
			checkliste.setDaten(new ObjectMapper().writeValueAsString(daten));
			checklisteDao.save(checkliste);
			return new ResponsePayload(MessagePayload.info("erfolgreich geändert"), daten);
		} catch (JsonProcessingException e) {
			String msg = "Ändern gescheitert (Fehler beim JSONisieren)";
			LOG.error("{}: {}", e.getMessage(), e);
			throw new ChecklistenRuntimeException(msg);
		} catch (PersistenceException e) {
			String msg = "Ändern gescheitert (Fehler beim Speichern)";
			LOG.error("{}: {}", e.getMessage(), e);
			throw new ChecklistenRuntimeException(msg);
		}

	}

	private ResponsePayload handleConcurrentUpdate(final Checkliste checkliste) {
		ObjectMapper objectMapper = new ObjectMapper();
		LOG.debug("konkurrierendes Update: erzeuge neues Payload mit geänderten Daten");

		ChecklisteDaten geaenderteDaten;
		try {
			geaenderteDaten = objectMapper.readValue(checkliste.getDaten(), ChecklisteDaten.class);

			// nur zur Sicherheit.
			geaenderteDaten.setVersion(checkliste.getVersion());

			return new ResponsePayload(MessagePayload.warn("Jemand anderes hat die Daten geändert. Anbei die neue Version"),
				geaenderteDaten);
		} catch (IOException e) {
			String msg = "Ändern gescheitert (konkurrierendes Update konnte nicht verarbeitet werden: Fehler beim deJSONisieren)";
			LOG.error("{}: {}", e.getMessage(), e);
			throw new ChecklistenRuntimeException(msg);
		}
	}

	/**
	 * Tja, löscht 'se halt.
	 *
	 * @param kuerzel String
	 */
	@Transactional
	public void checklisteLoeschen(final String kuerzel) {
		try {

			Optional<Checkliste> opt = checklisteDao.findByKuerzel(kuerzel);
			if (opt.isPresent()) {
				checklisteDao.delete(opt.get());
				LOG.info("Checkliste mit kuerzel [{}] gelöscht", kuerzel);
			} else {
				LOG.debug("Checkliste mit kuerzel [{}] war bereits gelöscht", kuerzel);
			}
		} catch (PersistenceException e) {
			String msg = "Löschen gescheitert (Fehler beim Speichern)";
			LOG.error("{}: {}", e.getMessage(), e);
			throw new ChecklistenRuntimeException(msg);
		}
	}
}
