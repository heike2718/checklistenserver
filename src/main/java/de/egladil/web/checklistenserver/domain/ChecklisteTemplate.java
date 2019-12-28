// =====================================================
// Project: checklistenserver
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.checklistenserver.validation.ValidTemplate;

/**
 * ChecklisteTemplate
 */
@ValidTemplate
public class ChecklisteTemplate {

	@NotNull
	private Checklistentyp typ;

	private long readTime;

	@Size(max = 999)
	private List<ChecklisteTemplateItem> items = new ArrayList<>();

	public static final ChecklisteTemplate create(final Checklistentyp typ) {

		ChecklisteTemplate template = new ChecklisteTemplate();
		template.typ = typ;
		return template;

	}

	public static final ChecklisteTemplate create(final Checklistentyp typ, final List<ChecklisteTemplateItem> items, final long timestamp) {

		ChecklisteTemplate template = new ChecklisteTemplate();
		template.typ = typ;
		template.items = items;
		template.readTime = timestamp;
		return template;

	}

	public Checklistentyp getTyp() {

		return typ;
	}

	public List<ChecklisteTemplateItem> getItems() {

		return items;
	}

	public void addItem(final ChecklisteTemplateItem item) {

		if (!items.contains(item)) {

			items.add(item);
		}

	}

	public void sortItems() {

		Collections.sort(items, new ChecklisteTemplateItemComparator());

	}

	public long getReadTime() {

		return readTime;
	}

	public void setReadTime(final long readTime) {

		this.readTime = readTime;
	}

}
