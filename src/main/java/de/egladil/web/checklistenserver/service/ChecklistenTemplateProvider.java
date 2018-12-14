//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import de.egladil.web.checklistenserver.config.ChecklistenTemplateConfiguration;
import de.egladil.web.checklistenserver.domain.ChecklisteDaten;
import de.egladil.web.checklistenserver.domain.ChecklistenItem;
import de.egladil.web.checklistenserver.domain.Checklistentyp;

/**
 * ChecklistenTemplateProvider
 */
@ApplicationScoped
public class ChecklistenTemplateProvider {

	private static final Logger LOG = LogManager.getLogger(ChecklistenTemplateProvider.class.getName());

	@Inject
	private ChecklistenTemplateConfiguration templateConfiguration;

	private static final String[] EINKAUFSLISTE = new String[] { "Äpfel", "Brot", "Brötchen", "Butter", "Eier", "Erdnüsse", "Fisch",
		"Forellenfilet", "Frischkäse", "Grapefruit", "Haselnüsse", "Joghurt", "Kartoffeln", "Klopapier", "Mandeln", "Milch fettarm",
		"Milch voll", "Obst", "Quark", "Salate", "Schnittkäse", "Schokolade", "Toast", "Tomaten", "Wurst Winkel" };

	private static final String[] PACKLISTE = new String[] { "Abwaschlappen", "Ausweise", "Brotpapier", "Bücher", "Bürste",
		"feste Schuhe", "Filme", "Gemüsebrühe", "Geschirrtücher", "Handschuhe", "Handtücher", "Hausschuhe", "Hörbücher",
		"Hüfttasche", "Klopapier", "Küchenmesser", "Küchenschwamm", "kurze Hosen", "lange Hosen", "Ladekabel", "Laptop",
		"Lesebrille", "Mülltüten", "Mütze", "Ohrstöpsel", "Olivenöl", "Pullover", "Regenjacke", "Sandalen", "Salz", "Schal",
		"Schlafanzug", "Schampoo", "Schnur", "Schreibzeug", "Schwimmsachen", "Socken", "Sonnencreme", "Spiele", "Sportklamotten",
		"Spüli", "Stranddecke", "Tee + Zubehör", "T-Shirts", "Unterwäsche", "Vouchers", "Wäscheklammern", "Zahnputzzeug" };

	/**
	 * Gibt eine Standardvorauswahl für eine bestimmte Checkliste zurück.
	 *
	 * @param typ Checklistentyp
	 * @return List
	 */
	public ChecklisteDaten getTemplateMitTyp(final Checklistentyp typ) {

		ChecklisteDaten result = new ChecklisteDaten();
		result.setTyp(typ);
		List<ChecklistenItem> items = readFromFile(typ);
		result.setItems(items);

		return result;
	}

	private List<ChecklistenItem> readInMemory(final Checklistentyp typ) {

		String[] vorgaben = new String[] {};
		switch (typ) {
		case EINKAUFSLISTE:
			vorgaben = EINKAUFSLISTE;
			break;
		case PACKLISTE:
			vorgaben = PACKLISTE;
			break;
		default:
			break;
		}

		List<ChecklistenItem> result = Stream.of(vorgaben).filter(s -> StringUtils.isNotBlank(s)).map(ChecklistenItem::fromName)
			.collect(Collectors.toList());
		return result;
	}

	private List<ChecklistenItem> readFromFile(final Checklistentyp typ) {
		String path = null;
		switch (typ) {
		case EINKAUFSLISTE:
			path = templateConfiguration.getEingaufsliste();
			break;
		case PACKLISTE:
			path = templateConfiguration.getPackliste();
		default:
			break;
		}

		List<ChecklistenItem> result = new ArrayList<>();

		if (path != null) {
			File file = new File(path);

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				String readLine = "";
				while ((readLine = br.readLine()) != null) {
					result.add(ChecklistenItem.fromName(readLine));
				}
			} catch (IOException e) {
				LOG.error("Konnte Daten nicht lesen, verwende hiesige: {} ", e.getMessage());
				return readInMemory(typ);
			}
		}
		return result;
	}
}
