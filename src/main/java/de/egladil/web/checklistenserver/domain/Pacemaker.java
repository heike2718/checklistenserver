//=====================================================
// Projekt: checklisten
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import de.egladil.web.commons.payload.HateoasPayload;

/**
 * Pacemaker (Herzschrittmacher) wird vom eigengebauten Monitor verwendet, um regelmäßige Zugriffe auf die DB zu machen.
 */
@Entity
@Table(name = "PACEMAKERS")
public class Pacemaker implements Checklistenentity {

	/* serialVersionUID */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "MONITOR_ID")
	@NotBlank
	@Size(max = 50)
	/** Die monitorId dient als fachlicher schlüssel für den einzigen Eintrag in der Tabelle */
	private String monitorId;

	@Column(name = "WERT")
	@NotBlank
	@Size(max = 36)
	/** wert wird bei jedem Zugriff geändert */
	private String wert;

	@Version
	@Column(name = "VERSION")
	private int version;

	@Transient
	private HateoasPayload hateoasPayload;

	@Override
	public Long getId() {
		return this.id;
	}

	public String getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(final String monitorId) {
		this.monitorId = monitorId;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getWert() {
		return wert;
	}

	public void setWert(final String wert) {
		this.wert = wert;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Pacemaker [monitorId=");
		builder.append(monitorId);
		builder.append(", wert=");
		builder.append(wert);
		builder.append(", version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void setHateoasPayload(final HateoasPayload hateoasPayload) {
		this.hateoasPayload = hateoasPayload;

	}

	@Override
	public HateoasPayload getHateoasPayload() {
		return hateoasPayload;
	}

	@Override
	public int hashCode() {
		return Objects.hash(monitorId);
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
		Pacemaker other = (Pacemaker) obj;
		return Objects.equals(monitorId, other.monitorId);
	}
}
