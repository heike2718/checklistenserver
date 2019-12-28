// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.domain;

/**
 * Checklistentyp
 */
public enum Checklistentyp {

	EINKAUFSLISTE,
	PACKLISTE,
	TODOS {

		@Override
		public boolean hasTemplate() {

			return false;
		}

	};

	public boolean hasTemplate() {

		return true;
	}
}
