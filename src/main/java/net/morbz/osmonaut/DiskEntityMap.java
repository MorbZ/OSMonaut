package net.morbz.osmonaut;

/*
* The MIT License (MIT)
* 
* Copyright (c) 2016 Merten Peetz
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

import java.util.Map;

import org.mapdb.DB;
import org.mapdb.Serializer;

import net.morbz.osmonaut.osm.Entity;

/**
 * Uses MapDB for storing entities.
 */
public class DiskEntityMap<T extends Entity> implements EntityMap<T> {
	// TODO: Find alternative to MapDB or implement own disk map, as it is 
	// really slow with MapDB

	private Map<Long, T> entities;

	/**
	 * @param db The MapDB database
	 * @param name Unique identifier for this object
	 */
	@SuppressWarnings("unchecked")
	public DiskEntityMap(DB db, String name) {
		entities = (Map<Long, T>)db.treeMap(name)
				.keySerializer(Serializer.LONG)
				.create();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(T entity) {
		entities.put(entity.getId(), entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get(long id) {
		return entities.get(id);
	}

}
