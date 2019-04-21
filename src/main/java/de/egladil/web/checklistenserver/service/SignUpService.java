//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.service;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import de.egladil.web.checklistenserver.config.DynamicConfigProperties;
import de.egladil.web.checklistenserver.error.ChecklistenAuthenticationException;
import de.egladil.web.checklistenserver.payload.SignUpPayload;
import de.egladil.web.commons.config.DynamicConfigReader;
import de.egladil.web.commons.crypto.CryptoService;

/**
 * SignUpService stellt die Funktionalität zum Anlegen eines Kontos in der Checklistenapp zur Verfügung.
 */
@RequestScoped
public class SignUpService {

	@Inject
	private DynamicConfigReader dynamicConfigReader;

	@Inject
	private CryptoService cryptoService;

	/**
	 * Validiert das secret.
	 *
	 * @param signUpPayload SignUpPayload
	 * @throws ChecklistenAuthenticationException falls nicht korrekt
	 */
	public void verifySecret(final SignUpPayload signUpPayload) throws ChecklistenAuthenticationException {

		DynamicConfigProperties dynamicConfigProperties = (DynamicConfigProperties) dynamicConfigReader
			.getConfig(DynamicConfigProperties.class);

		boolean stimmt = cryptoService.verifyPassword(signUpPayload.getSecret().toCharArray(), dynamicConfigProperties.getSecret(),
			dynamicConfigProperties.getSalt(), dynamicConfigProperties.getPepper(), dynamicConfigProperties.getAlgorithm(),
			dynamicConfigProperties.getIterations());

		if (!stimmt) {
			throw new ChecklistenAuthenticationException("Der Fremde kannte das Geheimnis nicht.");
		}

	}

}
