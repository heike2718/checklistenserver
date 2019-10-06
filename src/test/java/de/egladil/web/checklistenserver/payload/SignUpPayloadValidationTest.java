// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.payload;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.ValidationDelegate;

/**
 * SignUpPayloadValidationTest
 */
public class SignUpPayloadValidationTest {

	@Test
	void testValid01() {

		// Arrange
		SignUpPayload payload = new SignUpPayload("hsjah ahso fhaohs", null);

		// Act
		new ValidationDelegate().check(payload, SignUpPayload.class);
	}

}
