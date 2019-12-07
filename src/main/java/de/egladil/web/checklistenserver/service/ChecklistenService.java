// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.dao.impl.ChecklisteDao;
import de.egladil.web.checklistenserver.dao.impl.UserDao;
import de.egladil.web.checklistenserver.domain.Checkliste;
import de.egladil.web.checklistenserver.domain.ChecklisteDaten;
import de.egladil.web.checklistenserver.domain.Checklistentyp;
import de.egladil.web.checklistenserver.domain.Checklistenuser;
import de.egladil.web.checklistenserver.error.AuthException;
import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;
import de.egladil.web.checklistenserver.error.LogmessagePrefixes;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * ChecklistenService
 */
@RequestScoped
public class ChecklistenService {

	private static final Logger LOG = LoggerFactory.getLogger(ChecklistenService.class);

	@Inject
	ChecklisteDao checklisteDao;

	@Inject
	UserDao userDao;

	@Inject
	ChecklistenTemplateProvider checklistenTemplateProvider;

	/**
	 * @param  userUUID
	 *                  String die UUID des Users.
	 * @return          List
	 */
	public List<ChecklisteDaten> loadChecklisten(final String userUUID) {

		Optional<Checklistenuser> optUser = userDao.findByUniqueIdentifier(userUUID);

		if (!optUser.isPresent()) {

			LOG.warn("Kein user mit UUID={} vorhanden. Gebe leere Liste zurück.", userUUID);
			return new ArrayList<>();
		}

		Checklistenuser user = optUser.get();

		final List<Checkliste> checklisten = checklisteDao.load(user.getGruppe());

		List<ChecklisteDaten> result = checklisten.stream().map(chl -> ChecklisteDatenMapper.deserialize(chl))
			.filter(daten -> daten != null).collect(Collectors.toList());
		return result;
	}

	public ChecklisteDaten getCheckliste(final String kuerzel, final String userUUID) {

		if (kuerzel == null) {

			throw new ChecklistenRuntimeException("Lesen gescheitert: kein kuerzel");
		}

		Optional<Checklistenuser> optUser = userDao.findByUniqueIdentifier(userUUID);

		if (!optUser.isPresent()) {

			LOG.warn("Kein user mit UUID={} vorhanden. AuthException", userUUID);
			throw new AuthException("Nö, keine Berechtigung.");
		}

		Optional<Checkliste> opt = checklisteDao.findByUniqueIdentifier(kuerzel);

		if (!opt.isPresent()) {

			LOG.error("Checkliste mit kuerzel '{}' nicht gefunden", kuerzel);
			throw new NotFoundException();
		}

		Checklistenuser user = optUser.get();

		Checkliste checkliste = opt.get();

		authorizeUserForCheckliste(user, checkliste, "sehen");

		ChecklisteDaten daten = ChecklisteDatenMapper.deserialize(opt.get());

		if (daten == null) {

			// die Message wurde bereits gelogged
			throw new ChecklistenRuntimeException("");
		}
		return daten;
	}

	/**
	 * Legt für die Gruppe des Users eine neue Checkliste an.
	 *
	 * @param  typ
	 *                  Checklistentyp darf nicht null sein.
	 * @param  name
	 *                  String name darf nicht blank sein
	 * @param  userUUID
	 *                  String darf nicht blank sein.
	 * @return
	 */
	public ChecklisteDaten checklisteAnlegen(final Checklistentyp typ, final String name, final String userUUID) {

		Optional<Checklistenuser> optUser = userDao.findByUniqueIdentifier(userUUID);

		if (!optUser.isPresent()) {

			LOG.warn("Kein user mit UUID={} vorhanden. AuthException", userUUID);
			throw new AuthException("Nö, keine Berechtigung.");
		}

		try {

			Checklistenuser user = optUser.get();

			ChecklisteDaten daten = checklistenTemplateProvider.getTemplateMitTypFuerGruppe(typ, user.getGruppe());
			daten.setName(name);
			daten.setGruppe(user.getGruppe());

			Checkliste checkliste = Checkliste.create(typ, name, daten.getKuerzel());
			checkliste.setDaten(ChecklisteDatenMapper.serialize(daten, "Anlegen gescheitert"));
			checkliste.setGruppe(user.getGruppe());

			Checkliste persisted = checklisteDao.save(checkliste);

			daten.setVersion(persisted.getVersion());

			// LOG.info("Checkliste mit kuerzel '{}' angelegt", daten.getKuerzel());
			return daten;
		} catch (PersistenceException e) {

			String msg = "Anlegen gescheitert (Fehler beim Speichern)";
			LOG.error("{}: {}", e.getMessage(), e);
			throw new ChecklistenRuntimeException(msg);
		}
	}

	/**
	 * Ändert die Daten oder den Namen.
	 *
	 * @param  daten
	 * @return       ChecklisteDaten
	 */
	public ResponsePayload checklisteAendern(final ChecklisteDaten daten, final String kuerzel, final String userUUID) throws AuthException {

		if (daten == null) {

			throw new ChecklistenRuntimeException("Ändern gescheitert: keine daten");
		}

		if (kuerzel == null) {

			throw new ChecklistenRuntimeException("Ändern gescheitert: kein kuerzel");
		}

		Optional<Checklistenuser> optUser = userDao.findByUniqueIdentifier(userUUID);

		if (!optUser.isPresent()) {

			LOG.warn("Kein user mit UUID={} vorhanden. AuthException", userUUID);
			throw new AuthException("Nö, keine Berechtigung.");
		}

		Optional<Checkliste> opt = checklisteDao.findByUniqueIdentifier(kuerzel);

		if (!opt.isPresent()) {

			LOG.error("Chcekliste mit kuerzel '{}' nicht gefunden", kuerzel);
			throw new NotFoundException();
		}

		Checkliste checkliste = opt.get();
		Checklistenuser user = optUser.get();
		authorizeUserForCheckliste(user, checkliste, "ändern");

		try {

			if (daten.getVersion() < checkliste.getVersion()) {

				return handleConcurrentUpdate(checkliste);
			}
			// persist erhöht Version um 1, das muss auch in die Daten.
			daten.setVersion(checkliste.getVersion() + 1);
			checkliste.setName(daten.getName());
			checkliste.setDaten(ChecklisteDatenMapper.serialize(daten, "Ändern gescheitert"));
			checklisteDao.save(checkliste);
			return new ResponsePayload(MessagePayload.info("erfolgreich geändert"), daten);
		} catch (PersistenceException e) {

			String msg = "Ändern gescheitert (Fehler beim Speichern)";
			LOG.error("{}: {}", e.getMessage(), e);
			throw new ChecklistenRuntimeException(msg);
		}

	}

	private ResponsePayload handleConcurrentUpdate(final Checkliste checkliste) {

		LOG.debug("konkurrierendes Update: erzeuge neues Payload mit geänderten Daten");

		ChecklisteDaten geaenderteDaten = ChecklisteDatenMapper.deserialize(checkliste, new String[] {
			"Ändern gescheitert (konkurrierendes Update konnte nicht verarbeitet werden: Fehler beim deJSONisieren)" });
		// nur zur Sicherheit.
		geaenderteDaten.setVersion(checkliste.getVersion());
		return new ResponsePayload(MessagePayload.warn("Jemand anderes hat die Daten geändert. Anbei die neue Version"),
			geaenderteDaten);
	}

	/**
	 * Tja, löscht 'se halt.
	 *
	 * @param kuerzel
	 *                String
	 */
	@Transactional
	public void checklisteLoeschen(final String kuerzel, final String userUUID) {

		Optional<Checklistenuser> optUser = userDao.findByUniqueIdentifier(userUUID);

		if (!optUser.isPresent()) {

			LOG.warn("Kein user mit UUID={} vorhanden. AuthException", userUUID);
			throw new AuthException("Nö, keine Berechtigung.");
		}

		try {

			Optional<Checkliste> opt = checklisteDao.findByUniqueIdentifier(kuerzel);

			if (opt.isPresent()) {

				Checkliste checkliste = opt.get();

				Checklistenuser user = optUser.get();

				authorizeUserForCheckliste(user, checkliste, "löschen");

				checklisteDao.delete(checkliste);
				LOG.debug("gelöscht: {}", opt.get());
			} else {

				LOG.debug("Checkliste mit kuerzel '{}' war bereits gelöscht", kuerzel);
			}
		} catch (PersistenceException e) {

			String msg = "Löschen Checkliste " + StringUtils.abbreviate(kuerzel, 11) + " gescheitert (Fehler beim persistieren)";
			LOG.error("{}: {}", msg, e.getMessage(), e);
			throw new ChecklistenRuntimeException(msg);
		}
	}

	private String abbreviate(final String longString) {

		return StringUtils.abbreviate(longString, 11);
	}

	private void authorizeUserForCheckliste(final Checklistenuser user, final Checkliste checkliste, final String kontext) {

		if (!user.getGruppe().equals(checkliste.getGruppe())) {

			LOG.warn(LogmessagePrefixes.BOT + "user " + abbreviate(user.getUuid()) + ", gruppe=" + abbreviate(user.getGruppe())
				+ " versucht Checkliste "
				+ abbreviate(checkliste.getKuerzel() + ", gruppe=" + abbreviate(checkliste.getGruppe()) + " zu " + kontext));
			throw new AuthException("Nö: diese Checkliste darfst Du nicht " + kontext);
		}
	}
}
