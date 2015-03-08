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
import java.util.Collections;
import java.util.List;

import net.morbz.osmonaut.geometry.MultiPolygonMember.Type;
import net.morbz.osmonaut.osm.EntityType;
import net.morbz.osmonaut.osm.LatLon;
import net.morbz.osmonaut.osm.Node;
import net.morbz.osmonaut.osm.Relation;
import net.morbz.osmonaut.osm.RelationMember;
import net.morbz.osmonaut.osm.Way;

/**
 * This class defines a compound polygon. It consists of multiple polygons that can be either inner
 * or outer parts.
 * @author MorbZ
 */
public class MultiPolygon extends IPolygon {
	private ArrayList<MultiPolygonMember> members = new ArrayList<MultiPolygonMember>();
	private Bounds bounds = new Bounds();
	
	/**
	 * Creates a multipolygon that consists of the ways of the relation.
	 * @param relation The relation
	 */
	public MultiPolygon(Relation relation) {
		List<List<Node>> innerParts = new ArrayList<List<Node>>();
		List<List<Node>> outerParts = new ArrayList<List<Node>>();
		
		// Iterate ways
		for(RelationMember member : relation.getMembers()) {
			if(member.getEntity().getEntityType() == EntityType.WAY) {
				// Check node count
				Way way = (Way)member.getEntity();
				if(way.getNodes().size() <= 1) {
					continue;
				}
				
				// Check role
				MultiPolygonMember.Type type;
				switch(member.getRole()) {
				case "inner":
					type = MultiPolygonMember.Type.INNER;
					break;
				case "outer":
					type = MultiPolygonMember.Type.OUTER;
					break;
				default:
					continue;
				}

				// If it's closed we can add the polygon directly
				if(way.isClosed()) {
					add(type, new Polygon(way));
					continue;
				}
				
				// Otherwise we have to add it the the polygon parts so that we can assemble 
				// multiple parts to a complete polygon.
				List<Node> nodes = new ArrayList<Node>(way.getNodes());
				if(type == MultiPolygonMember.Type.INNER) {
					innerParts.add(nodes);
				} else {
					outerParts.add(nodes);
				}
			}
		}
		
		// Assemble the polygons
		for(Polygon poly : assemblePolygons(innerParts)) {
			add(MultiPolygonMember.Type.INNER, poly);
		}
		for(Polygon poly : assemblePolygons(outerParts)) {
			add(MultiPolygonMember.Type.OUTER, poly);
		}
	}
	
	/**
	 * Connects the given polygon-parts to as few complete polygons as possible.
	 */
	private List<Polygon> assemblePolygons(List<List<Node>> parts) {
		// Combine parts
		ArrayList<List<Node>> polyParts = new ArrayList<List<Node>>();
		for(List<Node> part : parts) {
			// Get the connecting nodes
			long firstNode = part.get(0).getId();
			long lastNode = part.get(part.size()-1).getId();
			
			// Find parts that can be joined with the end nodes
			List<Node> newPart = null;
			for(long node : new long[]{firstNode, lastNode}) {
				// Find an existing part that can be connected
				List<Node> polyPart = getConnectingPart(polyParts, node);
				
				// Connect the existing with the new part
				if(polyPart != null) {
					// Since it's possible that both the first and the last node can be connected
					// to existing parts we use newPart.
					polyParts.remove(polyPart);
					newPart = joinParts(newPart != null ? newPart : part, polyPart);
				}
			}
			
			// Add either the connected part or the new part if it couldn't be connected
			polyParts.add(newPart != null ? newPart : part);			
		}
		
		// Create polygons
		List<Polygon> polys = new ArrayList<Polygon>();
		for(List<Node> polyPart : polyParts) {
			// Extract coords from nodes
			List<LatLon> coords = new ArrayList<LatLon>();
			for(Node node : polyPart) {
				coords.add(node.getLatlon());
			}
			polys.add(new Polygon(coords));
		}
		return polys;
	}
	
	/**
	 * Finds the part that can be connected to the given node.
	 */
	private List<Node> getConnectingPart(List<List<Node>> polyParts, long node) {
		for(List<Node> tmpPolyPart : polyParts) {
			if(tmpPolyPart.get(0).getId() == node) {
				return tmpPolyPart;
			}
			if(tmpPolyPart.get(tmpPolyPart.size()-1).getId() == node) {
				return tmpPolyPart;
			}
		}
		return null;
	}
	
	/**
	 * Joins 2 polygon parts based on which node they have in common and creates a new part.
	 */
	private List<Node> joinParts(List<Node> part1, List<Node> part2) {
		// Get the first and last nodes of both parts
		long p1First = part1.get(0).getId();
		long p1Last = part1.get(part1.size()-1).getId();
		long p2First = part2.get(0).getId();
		long p2Last = part2.get(part2.size()-1).getId();
		
		// Join the parts based on the connecting nodes
		List<Node> nodes = new ArrayList<Node>();
		if(p1Last == p2First) {
			nodes.addAll(part1);
			nodes.remove(nodes.size() - 1);
			nodes.addAll(part2);
		} else if(p2Last == p1First) {
			nodes.addAll(part2);
			nodes.remove(nodes.size() - 1);
			nodes.addAll(part1);
		} else {
			// One part has to be reversed
			List<Node> reversed2 = new ArrayList<Node>(part2);
			Collections.reverse(reversed2);
			
			if(p1First == p2First) {
				nodes.addAll(reversed2);
				nodes.remove(nodes.size() - 1);
				nodes.addAll(part1);
			} else if(p1Last == p2Last) {
				nodes.addAll(part1);
				nodes.remove(nodes.size() - 1);
				nodes.addAll(reversed2);
			}
		}
		return nodes;
	}
	
	/**
	 * Creates and adds a new polygon member and extends the bounds.
	 */
	private void add(MultiPolygonMember.Type type, Polygon polygon) {
		members.add(new MultiPolygonMember(type, polygon));
		bounds.extend(polygon.getBounds());
	}
	
	/**
	 * @return The members of this polygon
	 */
	public ArrayList<MultiPolygonMember> getMembers() {
		return members;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LatLon> getCoords() {
		List<LatLon> coords = new ArrayList<LatLon>();
		for(MultiPolygonMember member : members) {
			coords.addAll(member.getPolygon().getCoords());
		}
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
		
		// Check members, the outer count has to be higher than the inner, for nested outer 
		// polygons.
		int innerCount = 0, outerCount = 0;
		for(MultiPolygonMember member : members) {
			if(member.getPolygon().contains(latlon)) {
				if(member.getType() == Type.INNER) {
					innerCount++;
				} else {
					outerCount++;
				}
			}
		}
		return outerCount > innerCount;
	}
}
