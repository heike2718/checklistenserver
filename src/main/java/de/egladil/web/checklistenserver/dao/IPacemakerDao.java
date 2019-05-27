//=====================================================
// Projekt: checklisten
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao;

import de.egladil.web.checklistenserver.domain.Pacemaker;

/**
 * IPacemakerDao
 */
public interface IPacemakerDao extends IBaseDao {

	/**
	 * Sucht den Pacemaker mit dem gegebenen fachlichen Schlüssel
	 *
	 * @param monitorId String
	 * @return Pacemaker oder exception
	 */
	Pacemaker findByMonitorId(String monitorId);

}
