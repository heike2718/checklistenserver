//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.checklistenserver.dao;

import de.egladil.web.checklistenserver.domain.Checkliste;

/**
 * IChecklisteDao
 */
public interface IChecklisteDao extends IBaseDao {

	/**
	 * delete.
	 *
	 * @param checkliste Checkliste
	 */
	void delete(Checkliste checkliste);

}