//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import de.egladil.web.checklistenserver.domain.Checkliste;

/**
 * ChecklisteDao
 */
@RequestScoped
public class ChecklisteDao {

	private static final Logger LOG = LogManager.getLogger(ChecklisteDao.class.getName());

	@PersistenceContext
	EntityManager em;

	public Integer getAnzahlChecklisten() {
		final String stmt = "select count(*) from checklisten c";

		final Query query = em.createNativeQuery(stmt);

		return SqlUtils.getCount(query).intValue();
	}

	/**
	 * Läd alles Checklisten ohne deren Details.
	 *
	 * @return List
	 */
	public List<Checkliste> loadChecklisten() {

		String stmt = "c.kuerzel, c.name, c.typ, c.datumGeaendert, c.version from Checkliste c";
		TypedQuery<Checkliste> query = em.createQuery(stmt, Checkliste.class);

		List<Checkliste> trefferliste = query.getResultList();

		LOG.debug("Anzahl Treffer: {}", trefferliste.size());

		return trefferliste;
	}

	/**
	 * Läd alles von der Checkliste.
	 *
	 * @param id
	 * @return Checkliste oder null.
	 */
	public Checkliste findById(final Long id) {
		return em.find(Checkliste.class, id);
	}

	/**
	 * Läd alles von der Checkliste.
	 *
	 * @param id
	 * @return Checkliste oder null.
	 */
	public Optional<Checkliste> findByKuerzel(final String kuerzel) {
		String stmt = "select c from Checkliste c where c.kuerzel=:kuerzel";
		TypedQuery<Checkliste> query = em.createQuery(stmt, Checkliste.class);
		query.setParameter("kuerzel", kuerzel);

		List<Checkliste> trefferliste = query.getResultList();
		return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));
	}

	/**
	 * Insert oder update.
	 *
	 * @param checkliste
	 * @return Checkliste
	 */
	public Checkliste save(final Checkliste checkliste) {

		Checkliste persisted;

		if (checkliste.getId() == null) {
			em.persist(checkliste);
			persisted = checkliste;

		} else {
			persisted = em.merge(checkliste);
		}

		return persisted;

	}

	/**
	 * delete.
	 *
	 * @param checkliste Checkliste
	 */
	@Transactional
	public void delete(final Checkliste checkliste) {
		em.remove(checkliste);
	}

}
