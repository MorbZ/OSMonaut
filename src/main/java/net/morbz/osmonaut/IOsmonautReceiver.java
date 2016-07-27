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

import net.morbz.osmonaut.osm.EntityType;
import net.morbz.osmonaut.osm.Entity;
import net.morbz.osmonaut.osm.Tags;

/**
 * The receiver for entities that Osmonaut has found.
 * 
 * @author MorbZ
 */
public interface IOsmonautReceiver {
	/**
	 * Checks if that entity is needed. This prevents loading unnecessary
	 * entities into the memory.
	 * 
	 * @param type
	 *            The type of the entity
	 * @param tags
	 *            The tags of the entity
	 * @return true if entity is needed
	 */
	public boolean needsEntity(EntityType type, Tags tags);

	/**
	 * Called when Osmonaut has found an entity. This is only called if
	 * needsEntity() returned true for this entity.
	 * 
	 * @param entity
	 *            The entity
	 */
	public void foundEntity(Entity entity);
}
