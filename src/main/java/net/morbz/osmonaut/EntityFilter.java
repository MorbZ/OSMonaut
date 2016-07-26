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

/**
 * This class is used to select which OSM entities are required. By default all entities are 
 * allowed.
 * @author MorbZ
 */
public class EntityFilter {
	private boolean nodesAllowed, waysAllowed, relationsAllowed;
	
	public EntityFilter() {
		nodesAllowed = true;
		waysAllowed = true;
		relationsAllowed = true;
	}
	
	/**
	 * @param nodesAllowed True if nodes are allowed
	 * @param waysAllowed True if ways are allowed
	 * @param relationsAllowed True if relations are allowed
	 */
	public EntityFilter(boolean nodesAllowed, boolean waysAllowed, boolean relationsAllowed) {
		this.nodesAllowed = nodesAllowed;
		this.waysAllowed = waysAllowed;
		this.relationsAllowed = relationsAllowed;
	}
	
	/**
	 * @param type The entity type
	 * @param allowed True if the entity type is allowed
	 */
	public void setEntityAllowed(EntityType type, boolean allowed) {
		switch(type) {
		case NODE:
			nodesAllowed = allowed;
			break;
		case WAY:
			waysAllowed = allowed;
			break;
		case RELATION:
			relationsAllowed = allowed;
			break;
		}
	}
	
	/**
	 * @param type The entity type
	 * @return true if the entity type is allowed
	 */
	public boolean getEntityAllowed(EntityType type) {
		switch(type) {
		case NODE:
			return nodesAllowed;
		case WAY:
			return waysAllowed;
		case RELATION:
			return relationsAllowed;
		}
		return false;
	}
}
