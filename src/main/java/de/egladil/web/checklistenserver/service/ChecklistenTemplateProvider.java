// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.Collator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.config.EinkaufslisteTemplate;
import de.egladil.web.checklistenserver.config.PacklisteTemplate;
import de.egladil.web.checklistenserver.dao.impl.UserDao;
import de.egladil.web.checklistenserver.domain.ChecklisteDaten;
import de.egladil.web.checklistenserver.domain.ChecklisteTemplate;
import de.egladil.web.checklistenserver.domain.ChecklisteTemplateItem;
import de.egladil.web.checklistenserver.domain.ChecklistenItem;
import de.egladil.web.checklistenserver.domain.Checklistentyp;
import de.egladil.web.checklistenserver.domain.Checklistenuser;
import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;
import de.egladil.web.checklistenserver.error.ConcurrentUpdateException;
import de.egladil.web.commons_net.time.CommonTimeUtils;

/**
 * ChecklistenTemplateProvider
 */
@ApplicationScoped
public class ChecklistenTemplateProvider {

	private static final Logger LOG = LoggerFactory.getLogger(ChecklistenTemplateProvider.class);

	@Inject
	EinkaufslisteTemplate einkaufslisteTemplate;

	@Inject
	UserDao userDao;

	@Inject
	PacklisteTemplate packlisteTemplate;

	public List<ChecklisteTemplate> getTemplates(final String userUuid) {

		Optional<Checklistenuser> optUser = userDao.findByUniqueIdentifier(userUuid);

		if (optUser.isEmpty()) {

			throw new ChecklistenRuntimeException("An dieser Stelle müsste ein User mit uuid=" + userUuid + " vorhanden sein");
		}

		final String gruppe = optUser.get().getGruppe();

		List<ChecklisteTemplate> result = new ArrayList<>();

		Arrays.stream(Checklistentyp.values()).filter(typ -> typ.hasTemplate()).forEach(typ -> {

			List<ChecklistenItem> items = readFromFile(typ, gruppe);

			ChecklisteTemplate template = ChecklisteTemplate.create(typ);

			items.forEach(item -> template.addItem(ChecklisteTemplateItem.create(item.getName(), typ)));

			template.sortItems();

			result.add(template);
		});

		return result;

	}

	/**
	 * Gibt das ChecklisteTemplate des gegebenen Typs zurück.
	 *
	 * @param  typ
	 *                  Checklistentyp
	 * @param  userUuid
	 *                  String Name der Gruppe
	 * @return          ChecklisteTemplate
	 */
	public ChecklisteTemplate getTemplateMitTypFuerGruppe(final Checklistentyp typ, final String userUuid) {

		Optional<Checklistenuser> optUser = userDao.findByUniqueIdentifier(userUuid);

		if (optUser.isEmpty()) {

			throw new ChecklistenRuntimeException("An dieser Stelle müsste ein User mit uuid=" + userUuid + " vorhanden sein");
		}

		ChecklisteTemplate result = ChecklisteTemplate.create(typ);
		List<ChecklistenItem> items = readFromFile(typ, optUser.get().getGruppe());
		items.forEach(item -> result.addItem(ChecklisteTemplateItem.create(item.getName(), typ)));
		result.setReadTime(System.currentTimeMillis());

		result.sortItems();
		return result;
	}

	/**
	 * Gibt eine Standardvorauswahl für eine bestimmte Checkliste zurück. Diese Vorauswahl ist mit der Gruppe personalisiert (gruppe
	 * = präfix). Falls es kein personalisiertes Template gibt, wird ein default zurückgegeben.
	 *
	 * @param  typ
	 *                Checklistentyp
	 * @param  gruppe
	 *                String Name der Gruppe
	 * @return        ChecklisteDaten
	 */
	public ChecklisteDaten getChecklisteMitTypFuerGruppe(final Checklistentyp typ, final String gruppe) {

		ChecklisteDaten result = new ChecklisteDaten();
		result.setTyp(typ);
		result.setKuerzel(UUID.randomUUID().toString());
		List<ChecklistenItem> items = readFromFile(typ, gruppe);
		result.setItems(items);

		return result;
	}

	/**
	 * Überschreibt die Template-Datei mit den Item-Namen für diese Gruppe.
	 *
	 * @param template
	 * @param userSession
	 */
	public ChecklisteTemplate templateSpeichern(final ChecklisteTemplate template, final String userUuid) throws ConcurrentUpdateException {

		Optional<Checklistenuser> optUser = userDao.findByUniqueIdentifier(userUuid);

		if (optUser.isEmpty()) {

			throw new ChecklistenRuntimeException("An dieser Stelle müsste ein User mit uuid=" + userUuid + " vorhanden sein");
		}

		switch (template.getTyp()) {

		case EINKAUFSLISTE:

			break;

		case PACKLISTE:
			break;

		default:
			break;
		}

		try {

			ChecklisteTemplate persisted = this.writeToFile(template.getTyp(), optUser.get(), template.getItems());
			return persisted;

		} catch (IOException e) {

			LOG.error("Fehler beim Speichern des Templates: {}", e.getMessage());

			throw new ChecklistenRuntimeException("Konnte Template " + template.getTyp() + " nicht speichern");
		}
	}

	/**
	 * Zu Testzwecken Sichtbarkeit package
	 *
	 * @param  namen
	 *               String[]
	 * @return       List
	 */
	List<ChecklistenItem> mapToChecklistenItems(final String[] namen) {

		Set<String> gefilterteNamen = Stream.of(namen).filter(name -> StringUtils.isNotBlank(name)).map(name -> name.trim())
			.map(name -> Encode.forHtml(name))
			.collect(Collectors.toSet());

		ArrayList<String> namenliste = new ArrayList<>(gefilterteNamen);

		Collator coll = Collator.getInstance(Locale.GERMAN);
		coll.setStrength(Collator.PRIMARY);
		Collections.sort(namenliste, coll);

		List<ChecklistenItem> result = namenliste.stream().map(ChecklistenItem::fromName).collect(Collectors.toList());
		return result;
	}

	private List<ChecklistenItem> readFromFile(final Checklistentyp typ, final String gruppe) {

		switch (typ) {

		case EINKAUFSLISTE:
			return mapToChecklistenItems(einkaufslisteTemplate.getListeTemplate(gruppe));

		case PACKLISTE:
			return mapToChecklistenItems(packlisteTemplate.getListeTemplate(gruppe));

		default:
			return new ArrayList<>();
		}
	}

	private ChecklisteTemplate writeToFile(final Checklistentyp typ, final Checklistenuser user, final List<ChecklisteTemplateItem> items) throws IOException, ConcurrentUpdateException {

		String pathTemplateFile = null;

		switch (typ) {

		case EINKAUFSLISTE:
			pathTemplateFile = einkaufslisteTemplate.getPathTemplateFile(user.getGruppe());
			break;

		case PACKLISTE:
			pathTemplateFile = packlisteTemplate.getPathTemplateFile(user.getGruppe());
			break;

		default:
			break;
		}

		if (pathTemplateFile != null) {

			long lastModified = java.nio.file.Files.getLastModifiedTime(Paths.get(pathTemplateFile)).toMillis();
			LocalDateTime timeLastModified = CommonTimeUtils.transformFromDate(new Date(lastModified));

			if (LocalDateTime.now().isBefore(timeLastModified)) {

				ChecklisteTemplate neuesTemplate = getTemplateMitTypFuerGruppe(typ, user.getUuid());

				ConcurrentUpdateException concurrentUpdateException = new ConcurrentUpdateException(
					"Listenvorlage " + typ + " wurde kürzlich durch jemand anderen geändert. Anbei die neue Version.");
				concurrentUpdateException.setActualData(neuesTemplate);
				throw concurrentUpdateException;
			}

			// String pathBackupFile = pathTemplateFile + "-" + System.currentTimeMillis();
			//
			// java.nio.file.Files.move(Paths.get(pathTemplateFile), Paths.get(pathBackupFile),
			// StandardCopyOption.REPLACE_EXISTING);

			try (FileWriter fw = new FileWriter(new File(pathTemplateFile))) {

				for (int i = 0; i < items.size(); i++) {

					String name = items.get(i).getName();
					fw.write(name);

					if (i < items.size() - 1) {

						fw.write(System.lineSeparator());
					}
				}
				fw.flush();
			}

			return ChecklisteTemplate.create(typ, items, System.currentTimeMillis());
		}

		return null;
	}
}
