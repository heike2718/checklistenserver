// =====================================================
// Project: commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.payload;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons_validation.payload.OAuthClientCredentials;

/**
 * OAuthClientCredentialsTest
 */
public class OAuthClientCredentialsTest {

	@Test
	void serialize() throws JsonProcessingException {

		OAuthClientCredentials creds = OAuthClientCredentials.create("WLJLH4vsldWapZrMZi2U5HKRBVpgyUiRTWwX7aiJd8nX", "start123",
			"horst");

		System.out.println(new ObjectMapper().writeValueAsString(creds));

	}

}
