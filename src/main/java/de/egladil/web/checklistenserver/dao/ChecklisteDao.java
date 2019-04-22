//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao;

import java.math.BigInteger;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import de.egladil.web.checklistenserver.domain.Checkliste;
import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;

/**
 * ChecklisteDao
 */
@RequestScoped
public class ChecklisteDao extends BaseDao {

	private static final Logger LOG = LogManager.getLogger(ChecklisteDao.class.getName());

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

	public Integer getAnzahlChecklisten() {
		final String stmt = "select count(*) from checklisten c";

		final Query query = getEm().createNativeQuery(stmt);

		return getCount(query).intValue();
	}

	private BigInteger getCount(final Query query) {
		final Object res = query.getSingleResult();

		if (!(res instanceof BigInteger)) {
			throw new ChecklistenRuntimeException("result ist kein BigInteger, sondern " + res.getClass());
		}

		return (BigInteger) res;
	}

	/**
	 * Läd alles Checklisten ohne deren Details.
	 *
	 * @return List
	 */
	public List<Checkliste> loadChecklisten() {

		String stmt = "select c from Checkliste c";
		TypedQuery<Checkliste> query = getEm().createQuery(stmt, Checkliste.class);

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
		return getEm().find(Checkliste.class, id);
	}

	/**
	 * delete.
	 *
	 * @param checkliste Checkliste
	 */
	@Transactional
	public void delete(final Checkliste checkliste) {
		getEm().remove(checkliste);
	}

	@Override
	protected String getFindEntityByUniqueIdentifierQuery(final String queryParameterName) {
		return "select c from Checkliste c where c.kuerzel=:" + queryParameterName;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getEntityClass() {
		return Checkliste.class;
	}
}
