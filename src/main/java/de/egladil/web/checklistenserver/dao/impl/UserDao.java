//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao.impl;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;

import de.egladil.web.checklistenserver.dao.IUserDao;
import de.egladil.web.checklistenserver.domain.Checklistenentity;
import de.egladil.web.checklistenserver.domain.Checklistenuser;

/**
 * UserDao
 */
@RequestScoped
public class UserDao extends BaseDao implements IUserDao {

	/**
	 * Erzeugt eine Instanz von UserDao
	 */
	public UserDao() {
	}

	/**
	 * Erzeugt eine Instanz von UserDao
	 */
	public UserDao(final EntityManager em) {
		super(em);
	}

	@Override
	protected String getFindEntityByUniqueIdentifierQuery(final String queryParameterName) {
		return "select u from Checklistenuser u where u.uuid=:" + queryParameterName;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends Checklistenentity> Class<T> getEntityClass() {
		return (Class<T>) Checklistenuser.class;
	}

	@Override
	protected String getCountStatement() {
		return "select count(*) from USER";
	}
}
