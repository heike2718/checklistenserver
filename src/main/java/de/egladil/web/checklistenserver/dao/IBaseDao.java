//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.checklistenserver.dao;

import java.util.List;
import java.util.Optional;

import de.egladil.web.checklistenserver.domain.Checklistenentity;

/**
 *
 * IBaseDao
 */
public interface IBaseDao {

	/**
	 * Tja, was wohl.
	 *
	 * @param entity Checklistenentity
	 * @return Checklistenentity
	 */
	<T extends Checklistenentity> T save(T entity);

	/**
	 * Sucht die Entity anhand ihres eindeutigen fachlichen Schlüssels.
	 *
	 * @param identifier String
	 * @return Optional
	 */
	<T extends Checklistenentity> Optional<T> findByUniqueIdentifier(String identifier);

	/**
	 * Läd die Entity anhand der technischen Id.
	 *
	 * @param id
	 * @return T oder null.
	 */
	<T extends Checklistenentity> T findById(Long id);

	/**
	 * Läd alle Entities.
	 *
	 * @return
	 */
	<T extends Checklistenentity> List<T> load();

	/**
	 *
	 * @return
	 */
	Integer getAnzahl();

}