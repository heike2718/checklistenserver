//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import de.egladil.web.checklistenserver.domain.Checklistenentity;
import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;

/**
 * BaseDao
 */
public abstract class BaseDao {

	private static final Logger LOG = LogManager.getLogger(BaseDao.class.getName());

	@PersistenceContext
	private EntityManager em;

	/**
	 * Tja, was wohl.
	 *
	 * @param entity Checklistenentity
	 * @return Checklistenentity
	 */
	public <T extends Checklistenentity> T save(final T entity) {
		T persisted;

		if (entity.getId() == null) {
			em.persist(entity);
			persisted = entity;
		} else {
			persisted = em.merge(entity);
		}
		return persisted;
	}

	/**
	 * Sucht die Entity anhand ihres eindeutigen fachlichen Schlüssels.
	 *
	 * @param identifier String
	 * @return Optional
	 */
	public <T extends Checklistenentity> Optional<T> findByUniqueIdentifier(final String identifier) {
		String stmt = getSubjectQuery("identifier");
		@SuppressWarnings("unchecked")
		TypedQuery<T> query = getEm().createQuery(stmt, getEntityClass());
		query.setParameter("identifier", identifier);

		try {
			return Optional.of(query.getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		} catch (NonUniqueResultException e) {
			String msg = getEntityClass().getSimpleName() + ": Trefferliste zu '" + identifier + "' nicht eindeutig";
			throw new ChecklistenRuntimeException(msg);
		} catch (PersistenceException e) {
			String msg = "Unerwarteter Fehler beim Suchen der Entity " + getEntityClass().getSimpleName();
			LOG.error("{}: {}", e.getMessage(), e);
			throw new ChecklistenRuntimeException(msg);
		}
	}

	/**
	 *
	 * @return String
	 */
	protected abstract String getSubjectQuery(String queryParameterName);

	@SuppressWarnings("rawtypes")
	protected abstract Class getEntityClass();

	protected EntityManager getEm() {
		return em;
	}

}
