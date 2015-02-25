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
 * A class that represents an OSM node element.
 * @author MorbZ
 */
public class Node extends Entity {
	private LatLon latlon;
	
	/**
	 * @param id The OSM-ID of this node
	 * @param tags The tags of this node
	 * @param latlon The coordinates of this node
	 */
	public Node(long id, Tags tags, LatLon latlon) {
		super(id, tags);
		this.latlon = latlon;
	}
	
	/**
	 * @return The coordinates of this node
	 */
	public LatLon getLatlon() {
		return latlon;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityType getEntityType() {
		return EntityType.NODE;
	}
}
