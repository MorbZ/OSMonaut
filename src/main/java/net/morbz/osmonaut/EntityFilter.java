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
 * This class is used to select OSM entity types. By default all types are 
 * enabled.
 * 
 * @author MorbZ
 */
public class EntityFilter {
	private boolean nodesEnabled;
	private boolean waysEnabled;
	private boolean relationsEnabled;

	public EntityFilter() {
		nodesEnabled = true;
		waysEnabled = true;
		relationsEnabled = true;
	}

	/**
	 * @param nodesEnabled
	 *            Whether nodes are enabled
	 * @param waysEnabled
	 *            Whether ways are enabled
	 * @param relationsEnabled
	 *            Whether relations are enabled
	 */
	public EntityFilter(boolean nodesEnabled, boolean waysEnabled, boolean relationsEnabled) {
		this.nodesEnabled = nodesEnabled;
		this.waysEnabled = waysEnabled;
		this.relationsEnabled = relationsEnabled;
	}

	/**
	 * @param type
	 *            The entity type
	 * @param allowed
	 *            True if the entity type is allowed
	 *            
	 * @deprecated Use setEntityEnabled() instead.
	 */
	@Deprecated
	public void setEntityAllowed(EntityType type, boolean allowed) {
		setEntityEnabled(type, allowed);
	}

	/**
	 * @param type
	 *            The entity type
	 * @param enabled
	 *            Whether the entity type is enabled
	 */
	public void setEntityEnabled(EntityType type, boolean enabled) {
		switch (type) {
		case NODE:
			nodesEnabled = enabled;
			break;
		case WAY:
			waysEnabled = enabled;
			break;
		case RELATION:
			relationsEnabled = enabled;
			break;
		}
	}

	/**
	 * @param type
	 *            The entity type
	 * @return true if the entity type is allowed
	 * 
	 * @deprecated Use getEntityEnabled() instead.
	 */
	@Deprecated
	public boolean getEntityAllowed(EntityType type) {
		return getEntityEnabled(type);
	}

	/**
	 * @param type
	 *            The entity type
	 * @return true if the entity type is enabled
	 */
	public boolean getEntityEnabled(EntityType type) {
		switch (type) {
		case NODE:
			return nodesEnabled;
		case WAY:
			return waysEnabled;
		case RELATION:
			return relationsEnabled;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String str = 
				"N: " + (nodesEnabled ? "1" : "0") + 
				", W: " + (waysEnabled ? "1" : "0") +
				", R: " + (relationsEnabled ? "1" : "0");
		return str;
	}
}
