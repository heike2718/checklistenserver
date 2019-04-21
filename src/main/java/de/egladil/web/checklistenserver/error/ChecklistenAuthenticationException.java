//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.error;

/**
 * ChecklistenAuthenticationException
 */
public class ChecklistenAuthenticationException extends RuntimeException {

	/* serialVersionUID */
	private static final long serialVersionUID = 1L;

	/**
	 * Erzeugt eine Instanz von ChecklistenAuthenticationException
	 */
	public ChecklistenAuthenticationException(final String arg0) {
		super(arg0);
	}

	/**
	 * Erzeugt eine Instanz von ChecklistenAuthenticationException
	 */
	public ChecklistenAuthenticationException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
}
