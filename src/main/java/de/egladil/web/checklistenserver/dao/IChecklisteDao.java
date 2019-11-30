// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.dao;

import java.util.List;

import de.egladil.web.checklistenserver.domain.Checkliste;

/**
 * IChecklisteDao
 */
public interface IChecklisteDao extends IBaseDao {

	/**
	 * delete.
	 *
	 * @param checkliste
	 *                   Checkliste
	 */
	void delete(Checkliste checkliste);

	/**
	 * Läd alle Checklisten mit der gegebenen Gruppe.
	 *
	 * @param  gruppe
	 *                String darf nicht blank sein.
	 * @return        List
	 */
	List<Checkliste> load(String gruppe);

	/**
	 * Gibt die Anzahl aller Checklisten für die gegebene Gruppe zurück.
	 *
	 * @param  gruppe
	 *                String darf nicht blank sein.
	 * @return        Integer
	 */
	int getAnzahl(String gruppe);

}
