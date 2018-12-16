//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

/**
* ChecklistenuserDaoTest
*/
public class ChecklistenuserDaoTest {

	@Test
	void testUniqueIdentityQuery() {
		// Arrange
		String identifierName = "hühnchen";
		ChecklistenuserDao dao = new ChecklistenuserDao();

		// Act
		String stmt = dao.getSubjectQuery(identifierName);

		// Assert
		assertEquals("select u from Checklistenuser u where u.uuid=:hühnchen", stmt);
	}
}
