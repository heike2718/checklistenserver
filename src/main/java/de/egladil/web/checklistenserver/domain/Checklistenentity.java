// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.domain;

import java.io.Serializable;

import de.egladil.web.commons_validation.payload.HateoasPayload;

/**
 * Checklistenentity
 */
public interface Checklistenentity extends Serializable {

	Long getId();

	/**
	 * Für HATEOAS..
	 *
	 * @param hateoasPayload
	 */
	void setHateoasPayload(HateoasPayload hateoasPayload);

	/**
	 * Zu Testzwecken kann man das abfragen.
	 *
	 * @return HateoasPayload
	 */
	HateoasPayload getHateoasPayload();
}
