// =====================================================
// Projekt: authenticationprovider
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.error;

/**
 * ChecklistenRuntimeException. Muss gelogged werden, bevor sie geworfen wird. Wird anschließend nur noch vom
 * ExceptionMapper behandelt.
 */
public class ChecklistenRuntimeException extends RuntimeException {

	/* serialVersionUID */
	private static final long serialVersionUID = 1L;

	public ChecklistenRuntimeException(final String arg0) {

		super(arg0);
	}

	public ChecklistenRuntimeException(final String arg0, final Throwable arg1) {

		super(arg0, arg1);
	}
}
