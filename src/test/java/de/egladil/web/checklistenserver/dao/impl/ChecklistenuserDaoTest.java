//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.dao.impl;

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
		UserDao dao = new UserDao();

		// Act
		String stmt = dao.getFindEntityByUniqueIdentifierQuery(identifierName);

		// Assert
		assertEquals("select u from Checklistenuser u where u.uuid=:hühnchen", stmt);
	}

	@Test
	void testCountStatement()  {

		// Act
		String stmt = new UserDao().getCountStatement();

		// Assert
		assertEquals("select count(*) from USER", stmt);

	}
}
