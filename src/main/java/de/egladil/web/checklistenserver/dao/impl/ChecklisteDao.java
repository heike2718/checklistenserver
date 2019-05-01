//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao.impl;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import de.egladil.web.checklistenserver.dao.IChecklisteDao;
import de.egladil.web.checklistenserver.domain.Checkliste;
import de.egladil.web.checklistenserver.domain.Checklistenentity;

/**
 * ChecklisteDao
 */
@RequestScoped
public class ChecklisteDao extends BaseDao implements IChecklisteDao {

	/**
	 * Erzeugt eine Instanz von ChecklisteDao
	 */
	public ChecklisteDao() {
	}

	/**
	 * Erzeugt eine Instanz von ChecklisteDao
	 */
	public ChecklisteDao(final EntityManager em) {
		super(em);
	}

	@Override
	@Transactional
	public void delete(final Checkliste checkliste) {
		getEm().remove(checkliste);
	}

	@Override
	protected String getFindEntityByUniqueIdentifierQuery(final String queryParameterName) {
		return "select c from Checkliste c where c.kuerzel=:" + queryParameterName;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends Checklistenentity> Class<T> getEntityClass() {
		return (Class<T>) Checkliste.class;
	}

	@Override
	protected String getCountStatement() {
		return "select count(*) from checklisten";
	}
}
