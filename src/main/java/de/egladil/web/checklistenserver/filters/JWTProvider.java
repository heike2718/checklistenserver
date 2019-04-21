//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import de.egladil.web.checklistenserver.config.ApplicationConfig;
import de.egladil.web.commons.error.AuthException;
import de.egladil.web.commons.jwt.JWTVerifierWrapper;

/**
 * JWTProvider
 */
public class JWTProvider {

	private static final Logger LOG = LogManager.getLogger(JWTProvider.class.getName());

	/**
	 * Verifiziert das JWT und gibt es zurück, wenn valid.
	 *
	 * @param authorizationHeader
	 * @param applicationConfig ApplicationConfig
	 * @return DecodedJWT oder null
	 */
	public DecodedJWT getJWT(final String authorizationHeader, final ApplicationConfig applicationConfig) {
		if (authorizationHeader == null) {
			return null;
		}
		try {

			final String idToken = authorizationHeader.replace("Bearer ", "");
			DecodedJWT jwt = JWTVerifierWrapper.getInstance().verify(idToken, applicationConfig.getAuthPublicKeyUrl());
			return jwt;
		} catch (JWTVerificationException e) {
			LOG.warn("Das JWT wurde unterwegs manipuliert: {}", e.getMessage());
			throw new AuthException();
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new AuthException();
		}
	}

}
