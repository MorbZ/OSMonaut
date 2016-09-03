package net.morbz.osmonaut;

/*
* The MIT License (MIT)
* 
* Copyright (c) 2015 Merten Peetz
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

import org.mapdb.DB;

import net.morbz.osmonaut.osm.Entity;

/**
 * Holds the IDs for entities that are needed until provided with the full
 * entity.
 * 
 * @author MorbZ
 */
public class EntityCache<T extends Entity> {
	private IdTracker idTracker = new IdTracker();
	private EntityMap<T> entityMap;

	/**
	 * @param entityMap The map to store the full entities
	 */
	public EntityCache(EntityMap<T> entityMap) {
		this.entityMap = entityMap;
	}

	/**
	 * Factory method to create a memory based entity cache.
	 */
	public static <T extends Entity> EntityCache<T> getMemoryEntityCache() {
		EntityMap<T> entityMap = new MemoryEntityMap<T>();
		return new EntityCache<T>(entityMap);
	}

	/**
	 * Factory method to create a disk based entity cache.
	 * @param db The MapDB database
	 * @param name Unique identifier for this object
	 */
	public static <T extends Entity> EntityCache<T> getDiskEntityCache(DB db, String name) {
		EntityMap<T> entityMap = new DiskEntityMap<T>(db, name);
		return new EntityCache<T>(entityMap);
	}

	/**
	 * Adds the ID to the list of needed IDs.
	 * 
	 * @param id
	 *            The needed ID
	 */
	public void addNeeded(long id) {
		idTracker.set(id);
	}

	/**
	 * @param id
	 *            The needed ID
	 * @return true if the entity with this ID is needed
	 */
	public boolean isNeeded(Long id) {
		return idTracker.get(id);
	}

	/**
	 * Adds a full entity. This will drop the needed ID.
	 * 
	 * @param entity
	 *            The full entity
	 */
	public void addEntity(T entity) {
		// Remove from needed
		idTracker.unset(entity.getId());

		// Add to entities
		entityMap.add(entity);
	}

	/**
	 * @param id
	 *            The entity ID
	 * @return The full entity with that ID or null if there is no full entity
	 */
	public T getEntity(long id) {
		return entityMap.get(id);
	}

	/**
	 * @return true if there is at least one needed entity
	 */
	public boolean needsEntities() {
		return !idTracker.isEmpty();
	}
}
