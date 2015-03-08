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

import java.util.ArrayList;
import java.util.List;

import net.morbz.osmonaut.osm.LatLon;
import net.morbz.osmonaut.osm.Node;
import net.morbz.osmonaut.osm.Way;

/**
 * This class defines a Polygon of Latitude/Longitude coordinates. A polygon is always closed.
 * @author MorbZ
 */
public class Polygon extends IPolygon {
	private List<LatLon> coords = new ArrayList<LatLon>();
	private Bounds bounds = new Bounds();
	
	/**
	 * Creates a polygon that contains all coordinates of the given list.
	 * @param coords The list of coordinates
	 */
	public Polygon(List<LatLon> coords) {
		// Check size
		if(coords.size() == 0) {
			return;
		}
		
		// Add coords
		for(LatLon latlon : coords) {
			add(latlon);
		}
		
		// Close polygon
		if(!coords.get(0).equals(coords.get(coords.size() - 1))) {
			add(coords.get(0));
		}
	}
	
	/**
	 * Creates a polygon that contains the coordinates of the nodes of the given way.
	 * @param way The way
	 */
	public Polygon(Way way) {
		// Check size
		if(way.getNodes().size() == 0) {
			return;
		}
				
		// Add way nodes
		for(Node node : way.getNodes()) {
			add(node.getLatlon());
		}
		
		// Close polygon
		if(!way.isClosed()) {
			add(way.getNodes().get(0).getLatlon());
		}
	}
	
	/**
	 * Adds a coordinate to this polygon and extend bounds.
	 */
	private void add(LatLon latlon) {
		this.coords.add(latlon);
		this.bounds.extend(latlon);
	}
	
	/**
	 * Returns the geometric centroid of this polygon.
	 * @return The center of this polygon or null if there are no coordinates
	 */
	public LatLon getCenter() {
		// Calculate centroid 
		double centerX = 0, centerY = 0;
		double signedArea = 0.0;
	    double x0 = 0.0; // Current vertex X
	    double y0 = 0.0; // Current vertex Y
	    double x1 = 0.0; // Next vertex X
	    double y1 = 0.0; // Next vertex Y
	    double a = 0.0;  // Partial signed area
	    
	    // For all vertices
	    for(int i = 0; i < coords.size() - 1; i++) {
	        x0 = coords.get(i).getLon();
	        y0 = coords.get(i).getLat();
	        x1 = coords.get(i + 1).getLon();
	        y1 = coords.get(i + 1).getLat();
	        a = x0 * y1 - x1 * y0;
	        signedArea += a;
	        centerX += (x0 + x1) * a;
	        centerY += (y0 + y1) * a;
	    }
	    
	    // If there is an area we have found the centroid
	    if(signedArea != 0) {
	    	signedArea *= 0.5;
	    	centerX /= (6.0 * signedArea);
	    	centerY /= (6.0 * signedArea);
	    	return new LatLon(centerY, centerX);
	    }
	    
	    // Otherwise we have to use the bounding box (e.g. when all coords are in one line)
	    return bounds.getCenter();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LatLon> getCoords() {
		return coords;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bounds getBounds() {
		return bounds;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(LatLon latlon) {
		// Check bounds
		if(!bounds.contains(latlon)) {
			return false;
		}
		
		// Iterate vertices
		boolean isIn = false;
		double lat = latlon.getLat();
		double lon = latlon.getLon();
		for(int i = 0, j = coords.size() - 1; i < coords.size(); j = i++) {
			double iLon = coords.get(i).getLon();
			double iLat = coords.get(i).getLat();
			double jLon = coords.get(j).getLon();
			double jLat = coords.get(j).getLat();
			if(((iLon > lon) != (jLon > lon)) && 
				(lat < (jLat - iLat) * (lon - iLon) / (jLon - iLon) + iLat)) {
				isIn = !isIn;
			}
		}
		return isIn;
	}
}
