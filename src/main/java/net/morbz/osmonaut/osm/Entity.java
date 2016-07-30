package net.morbz.osmonaut.osm;

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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.morbz.osmonaut.geometry.Bounds;

/**
 * The base class for all OSM entities.
 * 
 * @author MorbZ
 */
public abstract class Entity implements Externalizable {
	protected long id;
	protected Tags tags;

	/**
	 * @param id
	 *            The OSM-ID of this entity
	 * @param tags
	 *            The tags of this entity
	 */
	public Entity(long id, Tags tags) {
		this.id = id;
		this.tags = tags;
	}

	/**
	 * No-arg constructor for Externalizable
	 */
	public Entity() {

	}

	/**
	 * @return The OSM-ID of this entity
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return The tags of this entity
	 */
	public Tags getTags() {
		return tags;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Entity)) {
			return false;
		}
		Entity entity = (Entity) obj;
		if (entity.getEntityType() != getEntityType()) {
			return false;
		}
		if (entity.getId() != id) {
			return false;
		}
		return true;
	}

	/**
	 * @return The type of this entity
	 */
	public abstract EntityType getEntityType();

	/**
	 * Returns the center of this entity. For nodes it's the exact position. For
	 * ways their geometric center is used. For relations the center of a
	 * bounding box defined by the centers of all member entities is used.
	 * 
	 * @return The center of this entity or null if there is no data
	 */
	public abstract LatLon getCenter();

	/**
	 * Returns a bounding box that contains all elements and sub-entities of
	 * this entity.
	 * 
	 * @return The surrounding bounding box
	 */
	public abstract Bounds getBounds();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(id);
		out.writeObject(tags);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		id = in.readLong();
		tags = (Tags)in.readObject();
	}
}
