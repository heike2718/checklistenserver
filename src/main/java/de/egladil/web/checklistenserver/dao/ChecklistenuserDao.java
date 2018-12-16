//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao;

import javax.enterprise.context.RequestScoped;

import de.egladil.web.checklistenserver.domain.Checklistenuser;

/**
 * ChecklistenuserDao
 */
@RequestScoped
public class ChecklistenuserDao extends BaseDao {

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
