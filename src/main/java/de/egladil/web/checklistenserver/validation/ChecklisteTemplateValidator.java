// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.domain.ChecklisteTemplate;
import de.egladil.web.checklistenserver.domain.ChecklisteTemplateItem;
import de.egladil.web.commons_validation.StringLatinValidator;

/**
 * ChecklisteTemplateValidator
 */
public class ChecklisteTemplateValidator implements ConstraintValidator<ValidTemplate, ChecklisteTemplate> {

	private static final Logger LOG = LoggerFactory.getLogger(ChecklisteTemplateValidator.class);

	private StringLatinValidator stringLatinValidator = new StringLatinValidator();

	@Override
	public boolean isValid(final ChecklisteTemplate value, final ConstraintValidatorContext context) {

		if (value == null) {

			return true;
		}

		List<ChecklisteTemplateItem> invalidItems = new ArrayList<>();

		for (ChecklisteTemplateItem item : value.getItems()) {

			boolean valid = stringLatinValidator.isValid(item.getName(), context);

			if (!valid) {

				invalidItems.add(item);
			}
		}

		if (invalidItems.size() > 0) {

			if (invalidItems.size() == 1) {

				context.buildConstraintViolationWithTemplate("ChecklisteTemplate.invalidItem");
			} else {

				context.buildConstraintViolationWithTemplate("ChecklisteTemplate.invalidItems");
			}

			LOG.error("Validierungsfehler: ungültige Einträge = {}", StringUtils.join(invalidItems, ","));

			return false;
		}

		return true;

	}

}
