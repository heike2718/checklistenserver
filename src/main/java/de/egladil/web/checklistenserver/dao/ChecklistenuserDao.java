//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;

import de.egladil.web.checklistenserver.domain.Checklistenuser;

/**
 * ChecklistenuserDao
 */
@RequestScoped
public class ChecklistenuserDao extends BaseDao {

	/**
	* Erzeugt eine Instanz von ChecklistenuserDao
	*/
	public ChecklistenuserDao() {
	}

	/**
	* Erzeugt eine Instanz von ChecklistenuserDao
	*/
	public ChecklistenuserDao(final EntityManager em) {
		super(em);
	}

	@Override
	protected String getSubjectQuery(final String queryParameterName) {
		return "select u from Checklistenuser u where u.uuid=:" + queryParameterName;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getEntityClass() {
		return Checklistenuser.class;
	}
}
