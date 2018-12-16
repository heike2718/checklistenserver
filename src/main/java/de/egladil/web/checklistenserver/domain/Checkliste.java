//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Checkliste
 */
@Entity
@Table(name = "checklisten")
public class Checkliste implements Checklistenentity {

	/* serialVersionUID */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@NotNull
	@Size(min = 1, max = 36)
	@Column(name = "KUERZEL")
	private String kuerzel;

	@NotNull
	@Size(min = 3, max = 100)
	@Column(name = "NAME")
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "TYP")
	private Checklistentyp typ;

	@NotNull
	@Column(name = "DATEN")
	private String daten;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_MODIFIED")
	private Date datumGeaendert;

	@Version
	@Column(name = "VERSION")
	private int version;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kuerzel == null) ? 0 : kuerzel.hashCode());
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
		Checkliste other = (Checkliste) obj;
		if (kuerzel == null) {
			if (other.kuerzel != null) {
				return false;
			}
		} else if (!kuerzel.equals(other.kuerzel)) {
			return false;
		}
		return true;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
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

	public String getDaten() {
		return daten;
	}

	public void setDaten(final String details) {
		this.daten = details;
	}

	public Date getDatumGeaendert() {
		return datumGeaendert;
	}

	public void setDatumGeaendert(final Date datumGeaendert) {
		this.datumGeaendert = datumGeaendert;
	}

	public int getVersion() {
		return version;
	}

	public String getKuerzel() {
		return kuerzel;
	}

	public void setKuerzel(final String kuerzel) {
		this.kuerzel = kuerzel;
	}
}
