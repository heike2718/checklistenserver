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

import de.egladil.web.checklistenserver.domain.ChecklistenItem;
import de.egladil.web.commons_validation.StringLatinValidator;

/**
 * ChecklistenItemValidator
 */
public class ChecklistenItemValidator implements ConstraintValidator<ValidChecklistenItem, ChecklistenItem> {

	private static final Logger LOG = LoggerFactory.getLogger(ChecklistenItemValidator.class);

	private StringLatinValidator stringLatinValidator = new StringLatinValidator();

	@Override
	public boolean isValid(final ChecklistenItem value, final ConstraintValidatorContext context) {

		if (value == null) {

			return true;
		}

		boolean valid = stringLatinValidator.isValid(value.getName(), context);

		List<String> validationErrors = new ArrayList<>();

		if (!valid) {

			validationErrors.add("invalid property: name");
		}

		valid = stringLatinValidator.isValid(value.getKommentar(), context);

		if (!valid) {

			validationErrors.add("invalid property: kommentar");
		}

		if (validationErrors.size() > 0) {

			if (validationErrors.size() == 2) {

				context.buildConstraintViolationWithTemplate("ChecklistenItem.invalid");

			} else {

				if ("invalid property: name".equals(validationErrors.get(0))) {

					context.buildConstraintViolationWithTemplate("ChecklistenItem.invalidName");
				} else {

					context.buildConstraintViolationWithTemplate("ChecklistenItem.invalidKommentar");
				}

			}

			LOG.error("Validierungsfehler: {}, {}", StringUtils.join(validationErrors, ","), value.toLog());
			return false;
		}

		return true;
	}

}
