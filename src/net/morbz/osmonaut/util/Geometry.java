package net.morbz.osmonaut.util;

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

import java.util.List;

import net.morbz.osmonaut.osm.LatLon;

/**
 * A class for geometric functions.
 * @author MorbZ
 */
public class Geometry {
	/**
	 * Takes a list of coords and returns the center of the bounding box that is defined by all the
	 * coords.
	 * @param coords The coords that define the bounding box
	 * @return The center of the bounding box or null if there are no coords
	 */
	public static LatLon getBoundingCenter(List<LatLon> coords) {
		// Check number of coords
		if(coords.size() == 0) {
			return null;
		}
		
		// Make bounding box
		double minLat = Double.MAX_VALUE, minLon = Double.MAX_VALUE;
	    double maxLat = Double.MIN_VALUE, maxLon = Double.MIN_VALUE;
	    for(LatLon latlon : coords) {
	    	minLat = Math.min(minLat, latlon.getLat());
	    	minLon = Math.min(minLon, latlon.getLon());
	    	maxLat = Math.max(maxLat, latlon.getLat());
	    	maxLon = Math.max(maxLon, latlon.getLon());
	    }
	    return new LatLon((minLat + maxLat) / 2, (minLon + maxLon) / 2);
	}
}
