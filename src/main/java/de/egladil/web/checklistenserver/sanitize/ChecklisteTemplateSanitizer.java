// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.sanitize;

import java.util.function.Function;

import de.egladil.web.checklistenserver.domain.ChecklisteTemplate;

/**
 * ChecklisteTemplateSanitizer
 */
public class ChecklisteTemplateSanitizer implements Function<ChecklisteTemplate, ChecklisteTemplate> {

	@Override
	public ChecklisteTemplate apply(final ChecklisteTemplate originalTemplate) {

		final ChecklisteTemplateItemSanitizer itemSanitizer = new ChecklisteTemplateItemSanitizer();

		ChecklisteTemplate result = ChecklisteTemplate.create(originalTemplate.getTyp());

		originalTemplate.getItems().forEach(item -> result.addItem(itemSanitizer.apply(item)));

		return result;
	}
}
