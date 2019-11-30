// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.dao.impl;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.dao.IChecklisteDao;
import de.egladil.web.checklistenserver.domain.Checkliste;
import de.egladil.web.checklistenserver.domain.Checklistenentity;
import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;

/**
 * ChecklisteDao
 */
@RequestScoped
public class ChecklisteDao extends BaseDao implements IChecklisteDao {

	private static final Logger LOG = LoggerFactory.getLogger(ChecklisteDao.class);

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
	public void delete(final Checkliste checkliste) {

		// BalusC: falls die Transaktion nicht schon mit dem Suchen der Checkliste beginnt (siehe ChecklistenServivce), muss man es
		// so machen. Es schadet aber nichts, wenn man es immer so macht.
		EntityManager entityManager = getEm();
		entityManager.remove(entityManager.contains(checkliste) ? checkliste : entityManager.merge(checkliste));
		// getEm().remove(checkliste);
		LOG.debug("deleted: {}", checkliste);
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

		return "select count(*) from CHECKLISTEN";
	}

	@Override
	public <T extends Checklistenentity> List<T> load() {

		final String msg = "Methode load() ohne Argument ist für Checklisten verboten. Verwende Methode load(gruppe)";
		LOG.error(msg);
		throw new ChecklistenRuntimeException(msg);
	}

	@Override
	public int getAnzahl() {

		final String msg = "Methode getAnzahl() ohne Argument ist für Checklisten verboten. Verwende Methode getAnzahl(gruppe)";
		LOG.error(msg);
		throw new ChecklistenRuntimeException(msg);
	}

	@Override
	public List<Checkliste> load(final String gruppe) {

		if (StringUtils.isBlank(gruppe)) {

			throw new IllegalArgumentException("gruppe blank");
		}

		String stmt = "select c from Checkliste c where gruppe = :gruppe";
		TypedQuery<Checkliste> query = getEm().createQuery(stmt, Checkliste.class);
		query.setParameter("gruppe", gruppe);

		List<Checkliste> trefferliste = query.getResultList();

		LOG.debug("Checkliste - Anzahl Treffer: {}", trefferliste.size());

		return trefferliste;
	}

	@Override
	public int getAnzahl(final String gruppe) {

		if (StringUtils.isBlank(gruppe)) {

			throw new IllegalArgumentException("gruppe blank");
		}

		String stmt = "select count(*) from CHECKLISTEN where GRUPPE = :gruppe";
		final Query query = getEm().createNativeQuery(stmt);
		query.setParameter("gruppe", gruppe);

		return super.getCount(query).intValue();
	}
}
