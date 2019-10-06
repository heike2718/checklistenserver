// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.service;

import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.config.SignupSecretProperties;
import de.egladil.web.checklistenserver.dao.IUserDao;
import de.egladil.web.checklistenserver.domain.Checklistenuser;
import de.egladil.web.checklistenserver.error.AuthException;
import de.egladil.web.checklistenserver.payload.SignUpPayload;
import de.egladil.web.commons_crypto.CryptoService;
import de.egladil.web.commons_crypto.PasswordAlgorithm;
import de.egladil.web.commons_crypto.PasswordAlgorithmBuilder;
import de.egladil.web.commons_validation.InvalidProperty;
import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.HateoasPayload;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

@RequestScoped
public class SignUpService {

	private static final Logger LOG = LoggerFactory.getLogger(SignUpService.class.getName());

	@Inject
	SignupSecretProperties signupSecretProperties;

	@Inject
	CryptoService cryptoService;

	@Inject
	IUserDao userDao;

	/**
	 * Validiert das secret.
	 *
	 * @param  signUpPayload
	 *                                            SignUpPayload
	 * @throws ChecklistenAuthenticationException
	 *                                            falls nicht korrekt
	 */
	public void verifySecret(final SignUpPayload signUpPayload) throws AuthException {

		if (StringUtils.isBlank(signUpPayload.getSecret())) {

			ResponsePayload payload = new ResponsePayload(MessagePayload.error("Die Eingaben sind nicht korrekt."),
				Arrays.asList(new InvalidProperty[] { new InvalidProperty("secret", "blank", 0) }));
			throw new InvalidInputException(payload);
		}

		// @formatter:off
		PasswordAlgorithm passwordAlgorithm = PasswordAlgorithmBuilder.instance()
			.withAlgorithmName(signupSecretProperties.getAlgorithm())
			.withNumberIterations(signupSecretProperties.getIterations())
			.withPepper(signupSecretProperties.getPepper())
			.build();
		// @formatter:on

		boolean stimmt = cryptoService.verifyPassword(passwordAlgorithm, signUpPayload.getSecret().toCharArray(),
			signupSecretProperties.getSecret(), signupSecretProperties.getSalt());

		signUpPayload.wipe();

		if (!stimmt) {

			throw new AuthException("Der Fremde kannte das Geheimnis nicht.");
		}
	}

	/**
	 * Gibt den user zurück, falls er existiert, sonst ein leeres Optional.
	 *
	 * @param  uuid
	 * @return
	 */
	public Optional<HateoasPayload> findUser(final String uuid) {

		Optional<Checklistenuser> optUser = userDao.findByUniqueIdentifier(uuid);

		if (!optUser.isPresent()) {

			return Optional.empty();
		}

		HateoasPayload result = createHateoasPayload(uuid);
		return Optional.of(result);
	}

	/**
	 * Erzeugt das Teil, was für jemanden, der REST mit HATEOAS verwenden will, erforderlich ist, um die User-Resource
	 * zu finden.
	 *
	 * @param  uuid
	 * @return
	 */
	HateoasPayload createHateoasPayload(final String uuid) {

		HateoasPayload result = new HateoasPayload(uuid, "/users/" + uuid);
		return result;
	}

	/**
	 * Erzeugt einen neuen Checklistenuser mit der gegebenen uuid.
	 *
	 * @param  uuid
	 *              String
	 * @return      Checklistenuser
	 */
	@Transactional
	public HateoasPayload signUpUser(final String uuid) {

		Optional<Checklistenuser> optUser = userDao.findByUniqueIdentifier(uuid);

		if (optUser.isPresent()) {

			LOG.info("Checklistenuser {} existiert bereits", uuid);
			return createHateoasPayload(uuid);
		}

		Checklistenuser user = new Checklistenuser();
		user.setUuid(uuid);

		Checklistenuser persisted = userDao.save(user);
		LOG.info("Checklistenuser mit uuid={} angelegt: id={}", uuid, persisted.getId());
		return createHateoasPayload(uuid);
	}

}
