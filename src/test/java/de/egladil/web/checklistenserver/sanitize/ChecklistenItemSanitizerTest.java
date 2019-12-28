// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.sanitize;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import de.egladil.web.checklistenserver.domain.ChecklistenItem;

/**
 * ChecklistenItemSanitizerTest
 */
public class ChecklistenItemSanitizerTest {

	@Test
	void testEscapesScriptTag() {

		// Arrange
		ChecklistenItem item = ChecklistenItem.fromName("<script>alert(\"Hello! I am an alert box!!\")</script>");

		// Act
		ChecklistenItem sanitizedItem = new ChecklistenItemSanitizer().apply(item);

		// Assert
		assertEquals("&lt;script&gt;alert(&#34;Hello! I am an alert box!!&#34;)&lt;/script&gt;", sanitizedItem.getName());

		// Act 2
		ChecklistenItem nochmals = new ChecklistenItemSanitizer().apply(sanitizedItem);

		// Assert 2
		assertEquals("&amp;lt;script&amp;gt;alert(&amp;#34;Hello! I am an alert box!!&amp;#34;)&amp;lt;/script&amp;gt;",
			nochmals.getName());

	}

}
