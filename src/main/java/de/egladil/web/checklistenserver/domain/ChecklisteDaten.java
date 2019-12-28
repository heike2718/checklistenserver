// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.domain;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.commons_validation.annotations.UuidString;

/**
 * ChecklisteDaten
 */
public class ChecklisteDaten {

	@UuidString
	@NotBlank
	private String kuerzel;

	@StringLatin
	@NotBlank
	@Size(max = 100)
	private String name;

	@UuidString
	private String gruppe;

	@NotNull
	private Checklistentyp typ;

	private int version;

	private int anzahlErledigt;

	@Size(max = 999)
	private List<ChecklistenItem> items = new ArrayList<>();

	/**
	 * @param  baseUri
	 *                 String der Teil bis zu den Checklisten mit endendem /
	 * @return         String die URI dieser Checkliste.
	 */
	public String getLocation(final String baseUri) {

		return baseUri + kuerzel;
	}

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	public Checklistentyp getTyp() {

		return typ;
	}

	public void setTyp(final Checklistentyp typ) {

		this.typ = typ;
	}

	public List<ChecklistenItem> getItems() {

		return items;
	}

	public void setItems(final List<ChecklistenItem> items) {

		this.items = items;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public void setKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public int getVersion() {

		return version;
	}

	public void setVersion(final int version) {

		this.version = version;
	}

	public int getAnzahlErledigt() {

		return anzahlErledigt;
	}

	public void setAnzahlErledigt(final int anzahlErledigt) {

		this.anzahlErledigt = anzahlErledigt;
	}

	public String getGruppe() {

		return gruppe;
	}

	public void setGruppe(final String gruppe) {

		this.gruppe = gruppe;
	}
}
