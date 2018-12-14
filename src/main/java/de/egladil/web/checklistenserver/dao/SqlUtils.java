//=====================================================
// Projekt: de.egladil.web.checklistenserver.dao
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao;

import java.math.BigInteger;

import javax.persistence.Query;

import de.egladil.web.checklistenserver.error.ChecklistenRuntimeException;

/**
 * SqlUtils
 */
public class SqlUtils {

	public static BigInteger getCount(final Query query) {
		final Object res = query.getSingleResult();

		if (!(res instanceof BigInteger)) {
			throw new ChecklistenRuntimeException("result ist kein BigInteger, sondern " + res.getClass());
		}

		return (BigInteger) res;
	}
}
