// =====================================================
// Project: authprovider
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.checklistenserver.cdi;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import de.egladil.web.commons_crypto.CryptoService;
import de.egladil.web.commons_crypto.impl.CryptoServiceImpl;

/**
 * CryptoServiceProducer
 */
@Singleton
public class CryptoServiceProducer {

	@Produces
	public CryptoService produceCryptoService() {

		return new CryptoServiceImpl();
	}

}
