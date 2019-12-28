// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.sanitize;

import java.util.function.Function;

import org.owasp.encoder.Encode;

import de.egladil.web.checklistenserver.domain.ChecklistenItem;

/**
 * ChecklistenItemSanitizer
 */
public class ChecklistenItemSanitizer implements Function<ChecklistenItem, ChecklistenItem> {

	@Override
	public ChecklistenItem apply(final ChecklistenItem item) {

		item.setKommentar(Encode.forHtml(item.getKommentar()));
		item.setName(Encode.forHtml(item.getName()));
		return item;
	}

}
