//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.domain;

import java.io.Serializable;

/**
 * ChecklistenItem
 */
public class ChecklistenItem implements Serializable {

	/* serialVersionUID */
	private static final long serialVersionUID = 1L;

	private String name;

	private boolean markiert;

	private boolean optional;

	private boolean erledigt;

	private String kommentar;

	public static ChecklistenItem fromName(final String name) {
		return new ChecklistenItem(name.trim());
	}

	/**
	 * Erzeugt eine Instanz von ChecklistenItem
	 */
	public ChecklistenItem() {
	}

	/**
	 * Erzeugt eine Instanz von ChecklistenItem
	 */
	private ChecklistenItem(final String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public boolean isMarkiert() {
		return markiert;
	}

	public void setMarkiert(final boolean markiert) {
		this.markiert = markiert;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(final boolean optional) {
		this.optional = optional;
	}

	public boolean isErledigt() {
		return erledigt;
	}

	public void setErledigt(final boolean erledigt) {
		this.erledigt = erledigt;
	}

	public String getKommentar() {
		return kommentar;
	}

	public void setKommentar(final String kommentar) {
		this.kommentar = kommentar;
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
		ChecklistenItem other = (ChecklistenItem) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
