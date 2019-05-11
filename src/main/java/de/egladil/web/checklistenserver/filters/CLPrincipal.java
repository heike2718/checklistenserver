//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.filters;

import java.util.Set;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.kumuluz.ee.jwt.auth.principal.JWTPrincipal;

/**
 * CLPrincipal wrappes a JsonWebToken in order to take the groups from both JWT and application.
 */
public class CLPrincipal implements JsonWebToken {

	private final JWTPrincipal delegate;

	private final Set<String> roles;

	/**
	 * Erzeugt eine Instanz von CLPrincipal
	 */
	public CLPrincipal(final JWTPrincipal delegate, final Set<String> roles) {
		super();
		this.delegate = delegate;
		this.roles = roles;
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public Set<String> getClaimNames() {
		return delegate.getClaimNames();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getClaim(final String claimName) {

		Claims claimType = Claims.UNKNOWN;
		try {
			claimType = Claims.valueOf(claimName);
		} catch (IllegalArgumentException e) {
			// ignore
		}

		if (Claims.groups == claimType) {
			Set<String> groups = delegate.getGroups();
			groups.addAll(roles);
			return (T) groups;
		}

		return delegate.getClaim(claimName);
	}

}
