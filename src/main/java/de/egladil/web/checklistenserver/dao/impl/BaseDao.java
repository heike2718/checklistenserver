//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import de.egladil.web.checklistenserver.dao.IBaseDao;
import de.egladil.web.checklistenserver.domain.Checklistenentity;
import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;

/**
 * BaseDao
 */
public abstract class BaseDao implements IBaseDao {

	private static final Logger LOG = LogManager.getLogger(BaseDao.class.getName());

	@PersistenceContext
	private EntityManager em;

	/**
	 * Erzeugt eine Instanz von BaseDao
	 */
	public BaseDao() {
	}

	/**
	 * Erzeugt eine Instanz von BaseDao
	 */
	public BaseDao(final EntityManager em) {
		super();
		this.em = em;
	}

	@Override
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

	@Override
	public <T extends Checklistenentity> Optional<T> findByUniqueIdentifier(final String identifier) {

		String stmt = getFindEntityByUniqueIdentifierQuery("identifier");
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

	@Override
	public <T extends Checklistenentity> List<T> load() {
		String stmt = "select e from " + getEntityClass().getSimpleName() + " e";

		TypedQuery<T> query = getEm().createQuery(stmt, getEntityClass());

		List<T> trefferliste = query.getResultList();

		LOG.debug("Anzahl Treffer: {}", trefferliste.size());

		return trefferliste;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Checklistenentity> T findById(final Long id) {
		return (T) getEm().find(getEntityClass(), id);
	}

	@Override
	public Integer getAnzahl() {
		final String stmt = getCountStatement();

		final Query query = getEm().createNativeQuery(stmt);

		return getCount(query).intValue();
	}

	/**
	 * Gibt die jql zurück, die eine Entity anhand des fachlichen Schlüssels sucht.
	 *
	 * @return String
	 */
	protected abstract String getFindEntityByUniqueIdentifierQuery(String queryParameterName);

	/**
	 * Gib sas SQL zum ermitten der Anzahl zurüch.
	 *
	 * @return
	 */
	protected abstract String getCountStatement();

	/**
	 * Gibt die Klasse zurück, die die gesuchte Entity ist.
	 *
	 * @return Class<T>
	 */
	protected abstract <T extends Checklistenentity> Class<T> getEntityClass();

	protected EntityManager getEm() {
		return em;
	}

	private BigInteger getCount(final Query query) {
		final Object res = query.getSingleResult();

		if (!(res instanceof BigInteger)) {
			throw new ChecklistenRuntimeException("result ist kein BigInteger, sondern " + res.getClass());
		}

		return (BigInteger) res;
	}

}
