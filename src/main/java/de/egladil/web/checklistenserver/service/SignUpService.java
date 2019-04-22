//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver.service;

import java.util.Arrays;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.checklistenserver.config.DynamicConfigProperties;
import de.egladil.web.checklistenserver.error.ChecklistenAuthenticationException;
import de.egladil.web.checklistenserver.payload.SignUpPayload;
import de.egladil.web.commons.config.DynamicConfigReader;
import de.egladil.web.commons.crypto.CryptoService;
import de.egladil.web.commons.error.InvalidInputException;
import de.egladil.web.commons.payload.MessagePayload;
import de.egladil.web.commons.payload.ResponsePayload;
import de.egladil.web.commons.validation.InvalidProperty;

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

		if (StringUtils.isBlank(signUpPayload.getSecret())) {
			ResponsePayload payload = new ResponsePayload(MessagePayload.error("Die Eingaben sind nicht korrekt."),
				Arrays.asList(new InvalidProperty[] { new InvalidProperty("secret", "blank", 0) }));
			throw new InvalidInputException(payload);
		}

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
