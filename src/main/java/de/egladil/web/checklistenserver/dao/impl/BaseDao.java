// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.dao.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.dao.IBaseDao;
import de.egladil.web.checklistenserver.domain.Checklistenentity;
import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;
import de.egladil.web.commons_validation.ValidationDelegate;

/**
 * BaseDao
 */
public abstract class BaseDao implements IBaseDao {

	private static final Logger LOG = LoggerFactory.getLogger(BaseDao.class.getName());

	private ValidationDelegate validationDelegate = new ValidationDelegate();

	@Inject
	EntityManager em;

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
	@Transactional(value = TxType.REQUIRED)
	public <T extends Checklistenentity> T save(final T entity) {

		@SuppressWarnings("unchecked")
		final Class<T> clazz = (Class<T>) entity.getClass();

		validationDelegate.check(entity, clazz);

		T persisted;

		if (entity.getId() == null) {

			em.persist(entity);
			persisted = entity;
			LOG.debug("created: {}, ID={}", persisted, persisted.getId());
		} else {

			persisted = em.merge(entity);
			LOG.debug("updated: {}", persisted);
		}

		return persisted;
	}

	@Override
	public <T extends Checklistenentity> Optional<T> findByUniqueIdentifier(final String identifier) {

		String stmt = getFindEntityByUniqueIdentifierQuery("identifier");
		TypedQuery<T> query = getEm().createQuery(stmt, getEntityClass());
		query.setParameter("identifier", identifier);

		try {

			final T singleResult = query.getSingleResult();
			LOG.debug("gefunden: {} - {}", getEntityClass().getSimpleName(), identifier);
			return Optional.of(singleResult);
		} catch (NoResultException e) {

			LOG.debug("nicht gefunden: {} - {}", getEntityClass().getSimpleName(), identifier);
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

		final String entityName = getEntityClass().getSimpleName();
		String stmt = "select e from " + entityName + " e";

		TypedQuery<T> query = getEm().createQuery(stmt, getEntityClass());

		List<T> trefferliste = query.getResultList();

		LOG.debug("{} - Anzahl Treffer: {}", entityName, trefferliste.size());

		return trefferliste;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Checklistenentity> T findById(final Long id) {

		return (T) getEm().find(getEntityClass(), id);
	}

	@Override
	public int getAnzahl() {

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

	protected BigInteger getCount(final Query query) {

		final Object res = query.getSingleResult();

		if (!(res instanceof BigInteger)) {

			throw new ChecklistenRuntimeException("result ist kein BigInteger, sondern " + res.getClass());
		}

		return (BigInteger) res;
	}

}
