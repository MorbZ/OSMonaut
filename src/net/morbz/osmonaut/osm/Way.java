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

import java.util.ArrayList;
import java.util.List;

import net.morbz.osmonaut.util.Geometry;
import net.morbz.osmonaut.util.StringUtil;

/**
 * A class that represents an OSM way element.
 * @author MorbZ
 */
public class Way extends Entity {
	private List<Node> nodes;
	
	/**
	 * @param id The OSM-ID of this way
	 * @param tags The tags of this way
	 * @param nodes The nodes of this way
	 */
	public Way(long id, Tags tags, List<Node> nodes) {
		super(id, tags);
		this.nodes = nodes;
	}
	
	/**
	 * @return The nodes of this way
	 */
	public List<Node> getNodes() {
		return nodes;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityType getEntityType() {
		return EntityType.WAY;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LatLon getCenter() {
		// Check number of nodes
		switch(nodes.size()) {
		case 0:
			return null;
		case 1:
			return nodes.get(0).getLatlon();
		}
		
		// Make point array
		List<LatLon> coords = new ArrayList<LatLon>();
		for(Node node : nodes) {
			coords.add(node.getLatlon());
		}
		
		// Close polygon
		if(!coords.get(0).equals(coords.get(coords.size()-1))) {
			coords.add(coords.get(0));
		}
		
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
	    
	    // Otherwise we have to use a bounding box (e.g. when all node are in one line)
	    return Geometry.getBoundingCenter(coords);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String str = "";
		str += "{" + "\t" + "WAY" + "\n";
		str += "\t" + "id: " + id + "\n";
		str += "\t" + "tags: " + StringUtil.indent(tags.toString());
		str += "\t" + "nodes: [" + "\n";
		for(Node node : nodes) {
			str += StringUtil.indent(StringUtil.indent(node.toString()));
		}
		str += "\t" + "]" + "\n";
		str += "}";
		return str;		
	}
}
