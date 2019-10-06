// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.HateoasPayload;

/**
 * SignUpServiceTest
 */
public class SignUpServiceTest {

	@Test
	void testCreateHateoasPayload() {

		// Arrange
		String uuid = "qwgdigil";
		String expectedUrl = "/users/qwgdigil";

		// Act
		HateoasPayload payload = new SignUpService().createHateoasPayload(uuid);

		// Assert
		assertEquals(expectedUrl, payload.getUrl());
		assertEquals(uuid, payload.getId());
		assertTrue(payload.getLinks().isEmpty());
	}

}
