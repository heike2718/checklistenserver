//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.filters;

import java.io.IOException;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.checklistenserver.config.ApplicationConfig;
import de.egladil.web.commons.jwt.JWTVerifierWrapper;

/**
 * JWTProvider
 */
public class JWTProvider {

	/**
	 * Verifiziert das JWT und gibt es zurück, wenn valid.
	 *
	 * @param authorizationHeader
	 * @param applicationConfig ApplicationConfig
	 * @return DecodedJWT oder null
	 * @throws JWTVerificationException wenn das JWT nicht valide ist (man in the middle)
	 * @throws IOException vom Lesen des public keys des AuthProviders
	 */
	public DecodedJWT getJWT(final String authorizationHeader, final ApplicationConfig applicationConfig)
		throws JWTVerificationException, TokenExpiredException, IOException {
		if (authorizationHeader == null) {
			return null;
		}
		final String idToken = authorizationHeader.replace("Bearer ", "");
		DecodedJWT jwt = JWTVerifierWrapper.getInstance().verify(idToken, applicationConfig.getAuthPublicKeyUrl());
		return jwt;
	}
}
