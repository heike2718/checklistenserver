// =====================================================
// Projekt: checklisten
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.checklistenserver.service;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.checklistenserver.dao.IPacemakerDao;
import de.egladil.web.checklistenserver.domain.Pacemaker;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * HeartbeatService
 */
@RequestScoped
public class HeartbeatService {

	private static final String MONITOR_ID = "checklisten-database";

	private static final Logger LOG = LoggerFactory.getLogger(HeartbeatService.class);

	@Inject
	IPacemakerDao pacemakerDao;

	/**
	 * Erzeugt eine Instanz von HeartbeatService
	 */
	public HeartbeatService() {

	}

	/**
	 * Erzeugt eine Instanz von HeartbeatService zum Testen ohne DB.
	 */
	public HeartbeatService(final IPacemakerDao iPacemakerDao) {

		super();
		this.pacemakerDao = iPacemakerDao;
	}

	public ResponsePayload update() {

		try {

			Pacemaker pacemaker = pacemakerDao.findByMonitorId(HeartbeatService.MONITOR_ID);
			pacemaker.setWert("wert-" + System.currentTimeMillis());
			Pacemaker result = pacemakerDao.save(pacemaker);
			LOG.debug("pacemaker aktualisiert: {}", result);
			return ResponsePayload.messageOnly(MessagePayload.info(HeartbeatService.MONITOR_ID + " lebt"));
		} catch (Exception e) {

			String msg = "Fehler beim Speichern des pacemakers " + HeartbeatService.MONITOR_ID + ": " + e.getMessage();
			LOG.error("Fehler beim updaten des pacemakers: {}", e.getMessage(), e);
			return ResponsePayload.messageOnly(MessagePayload.error(msg));
		}
	}
}
