// =====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.service;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.dao.IUserDao;
import de.egladil.web.checklistenserver.domain.Checklistenuser;
import de.egladil.web.commons_validation.payload.HateoasPayload;

@RequestScoped
public class SignUpService {

	private static final Logger LOG = LoggerFactory.getLogger(SignUpService.class.getName());

	@Inject
	IUserDao userDao;

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
	 * Erzeugt einen neuen Checklistenuser mit der gegebenen uuid und der uuid als Gruppe.
	 *
	 * @param  uuid
	 *              String
	 * @return      Checklistenuser
	 */
	public HateoasPayload signUpUser(final String uuid) {

		Checklistenuser user = new Checklistenuser();
		user.setUuid(uuid);
		user.setGruppe(uuid);

		Checklistenuser persisted = userDao.save(user);
		LOG.info("Checklistenuser mit uuid={} angelegt: id={}", uuid, persisted.getId());
		return createHateoasPayload(uuid);
	}

}
