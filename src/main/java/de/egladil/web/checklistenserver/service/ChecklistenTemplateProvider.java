// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.service;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.checklistenserver.config.EinkaufslisteTemplate;
import de.egladil.web.checklistenserver.config.PacklisteTemplate;
import de.egladil.web.checklistenserver.dao.impl.UserDao;
import de.egladil.web.checklistenserver.domain.ChecklisteDaten;
import de.egladil.web.checklistenserver.domain.ChecklistenItem;
import de.egladil.web.checklistenserver.domain.Checklistentyp;

/**
 * ChecklistenTemplateProvider
 */
@ApplicationScoped
public class ChecklistenTemplateProvider {

	@Inject
	EinkaufslisteTemplate einkaufslisteTemplate;

	@Inject
	UserDao userDao;

	@Inject
	PacklisteTemplate packlisteTemplate;

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
	public ChecklisteDaten getTemplateMitTypFuerGruppe(final Checklistentyp typ, final String gruppe) {

		ChecklisteDaten result = new ChecklisteDaten();
		result.setTyp(typ);
		result.setKuerzel(UUID.randomUUID().toString());
		List<ChecklistenItem> items = readFromFile(typ, gruppe);
		result.setItems(items);

		return result;
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
}
