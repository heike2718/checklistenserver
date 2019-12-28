// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.sanitize;

import java.util.function.Function;

import org.owasp.encoder.Encode;

import de.egladil.web.checklistenserver.domain.ChecklisteDaten;

/**
 * ChecklisteDatenSanitizer
 */
public class ChecklisteDatenSanitizer implements Function<ChecklisteDaten, ChecklisteDaten> {

	@Override
	public ChecklisteDaten apply(final ChecklisteDaten daten) {

		daten.setName(Encode.forHtml(daten.getName()));
		daten.getItems().stream().forEach(item -> {

			item.setName(Encode.forHtml(item.getName()));
			item.setKommentar(Encode.forHtml(item.getKommentar()));
		});

		return daten;
	}

}
