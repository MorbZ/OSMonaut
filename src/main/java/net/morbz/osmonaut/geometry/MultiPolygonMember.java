package net.morbz.osmonaut.geometry;

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
 * Defines a member of a multipolygon that can be either an inner or an outer
 * polygon. The area of inner polygon is not part of the multipolygon.
 * 
 * @author MorbZ
 */
public class MultiPolygonMember {
	private Type type;
	private Polygon polygon;

	/**
	 * @param type
	 *            The type of the polygon
	 * @param polygon
	 *            The polygon
	 */
	public MultiPolygonMember(Type type, Polygon polygon) {
		this.type = type;
		this.polygon = polygon;
	}

	/**
	 * @return The type of the polygon
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return The polygon
	 */
	public Polygon getPolygon() {
		return polygon;
	}

	/**
	 * Enum for inner and outer types
	 */
	public enum Type {
		INNER, OUTER
	}
}
