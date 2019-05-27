//=====================================================
// Projekt: checklisten
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao.impl;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.dao.IPacemakerDao;
import de.egladil.web.checklistenserver.domain.Checklistenentity;
import de.egladil.web.checklistenserver.domain.Pacemaker;

/**
 * PacemakerDao
 */
@RequestScoped
public class PacemakerDao extends BaseDao implements IPacemakerDao {

	private static final Logger LOG = LoggerFactory.getLogger(PacemakerDao.class);

	/**
	 * Erzeugt eine Instanz von PacemakerDao
	 */
	public PacemakerDao() {
	}

	/**
	 * Erzeugt eine Instanz von PacemakerDao
	 */
	public PacemakerDao(final EntityManager em) {
		super(em);
	}

	@Override
	public Pacemaker findByMonitorId(final String monitorId) {
		LOG.debug("monitorId='{}'", monitorId);

		String stmt = "select p from Pacemaker p where monitorId = :monitorId";
		TypedQuery<Pacemaker> query = getEm().createQuery(stmt, Pacemaker.class);
		query.setParameter("monitorId", monitorId);

		return query.getSingleResult();
	}

	@Override
	protected String getFindEntityByUniqueIdentifierQuery(final String queryParameterName) {
		return "select p from Pacemaker p where monitorId = :monitorId";
	}

	@Override
	protected String getCountStatement() {
		return "select count(*) from PACEMAKERS";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends Checklistenentity> Class<T> getEntityClass() {
		return (Class<T>) Pacemaker.class;
	}

}
