// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.sanitize;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import de.egladil.web.checklistenserver.domain.ChecklisteTemplateItem;
import de.egladil.web.checklistenserver.domain.Checklistentyp;

/**
 * ChecklisteTemplateItemSanitizerTest
 */
public class ChecklisteTemplateItemSanitizerTest {

	@Test
	void testEscapesScriptTag() {

		// Arrange
		ChecklisteTemplateItem item = ChecklisteTemplateItem.create("<script>alert(\"Hello! I am an alert box!!\")</script>",
			Checklistentyp.EINKAUFSLISTE);

		// Act
		ChecklisteTemplateItem sanitizedItem = new ChecklisteTemplateItemSanitizer().apply(item);

		// Assert
		assertEquals("&lt;script&gt;alert(&#34;Hello! I am an alert box!!&#34;)&lt;/script&gt;", sanitizedItem.getName());

	}

	@Test
	void testEscapesNull() {

		// Arrange
		ChecklisteTemplateItem item = ChecklisteTemplateItem.create(null,
			Checklistentyp.EINKAUFSLISTE);

		// Act
		ChecklisteTemplateItem sanitizedItem = new ChecklisteTemplateItemSanitizer().apply(item);

		// Assert
		assertEquals("null", sanitizedItem.getName());

	}

}
