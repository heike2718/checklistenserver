//=====================================================
// Projekt: authenticationprovider
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.error;

/**
 * AuthException
 */
public class AuthException extends RuntimeException {

	/* serialVersionUID */
	private static final long serialVersionUID = 1L;

	public AuthException(final String arg0) {
		super(arg0);
	}

	public AuthException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
}
