// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import de.egladil.web.commons_validation.annotations.StringLatin;

/**
 * ChecklisteTemplateItem
 */
public class ChecklisteTemplateItem {

	@NotNull
	private Checklistentyp typ;

	@StringLatin
	@NotBlank
	private String name;

	public static ChecklisteTemplateItem create(final String name, final Checklistentyp typ) {

		ChecklisteTemplateItem result = new ChecklisteTemplateItem();
		result.name = name;
		result.typ = typ;
		return result;
	}

	public Checklistentyp getTyp() {

		return typ;
	}

	public String getName() {

		return name;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		ChecklisteTemplateItem other = (ChecklisteTemplateItem) obj;

		if (name == null) {

			if (other.name != null) {

				return false;
			}
		} else if (!name.equals(other.name)) {

			return false;
		}
		return true;
	}

	@Override
	public String toString() {

		return name;
	}

}
