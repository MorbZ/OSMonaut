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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.morbz.osmonaut.osm.Entity;

/**
 * Holds the IDs for entities that are needed until provided with the full entity.
 * @author MorbZ
 */
public class EntityCache {
	private Set<Long> neededIds;
	private Map<Long, Entity> entities;
	
	public EntityCache() {
		neededIds = new HashSet<Long>();
		entities = new HashMap<Long, Entity>();
	}
	
	/**
	 * Adds the ID to the list of needed IDs.
	 * @param id The needed ID
	 */
	public void addNeeded(long id) {
		neededIds.add(id);
	}
	
	/**
	 * @param id The needed ID
	 * @return true if the entity with this ID is needed
	 */
	public boolean isNeeded(Long id) {
		return neededIds.contains(id);
	}
	
	/** 
	 * Adds a full entity. This will drop the needed ID.
	 * @param entity The full entity
	 */
	public void addEntity(Entity entity) {
		// Remove from needed
		neededIds.remove(entity.getId());
		
		// Add to entities
		entities.put(entity.getId(), entity);
	}
	
	/**
	 * @param id The entity ID
	 * @return The full entity with that ID or null if there is no full entity
	 */
	public Entity getEntity(long id) {
		return entities.get(id);
	}
	
	/**
	 * @return The combined number of needed and full entities
	 */
	public int size() {
		return neededIds.size() + entities.size();
	}
}
