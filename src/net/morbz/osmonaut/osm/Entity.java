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

/**
 * The base class for all OSM entities.
 * @author MorbZ
 */
public abstract class Entity {
	protected long id;
	protected Tags tags;
	
	/**
	 * @param id The OSM-ID of this entity
	 * @param tags The tags of this entity
	 */
	public Entity(long id, Tags tags) {
		this.id = id;
		this.tags = tags;
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
	 * @return The type of this entity
	 */
	public abstract EntityType getEntityType();
}
