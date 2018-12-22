//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.checklistenserver.domain.Checkliste;

/**
* ChecklisteDaoTest
*/
public class ChecklisteDaoTest {

	@Test
	void testUniqueIdentityQuery() {
		// Arrange
		String identifierName = "hühnchen";
		ChecklisteDao dao = new ChecklisteDao();

		// Act
		String stmt = dao.getSubjectQuery(identifierName);

		// Assert
		assertEquals("select c from Checkliste c where c.kuerzel=:hühnchen", stmt);
	}


	void testLoadAll() {
		// Act
		ChecklisteDao dao = new ChecklisteDao();

		// Act
		List<Checkliste> trefferliste = dao.loadChecklisten();

		// Assert
		assertNotNull(trefferliste);
	}
}
