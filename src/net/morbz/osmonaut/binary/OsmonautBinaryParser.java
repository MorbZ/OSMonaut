package net.morbz.osmonaut.binary;

//This software is released into the Public Domain.  See copying.txt for details.

import java.util.ArrayList;
import java.util.List;

import net.morbz.osmonaut.EntityFilter;
import net.morbz.osmonaut.osm.LatLon;
import net.morbz.osmonaut.osm.Entity;
import net.morbz.osmonaut.osm.Node;
import net.morbz.osmonaut.osm.Relation;
import net.morbz.osmonaut.osm.RelationMember;
import net.morbz.osmonaut.osm.Tags;
import net.morbz.osmonaut.osm.Way;

import org.openstreetmap.osmosis.osmbinary.BinaryParser;
import org.openstreetmap.osmosis.osmbinary.Osmformat;

/** 
 * Class that reads and parses binary files and sends the contained entities to the sink.
 * @author crosby
 * @author MorbZ
 */
public class OsmonautBinaryParser extends BinaryParser {
	private OsmonautSink sink;
	private EntityFilter filter;
	
	public OsmonautBinaryParser(OsmonautSink sink) {
		this.sink = sink;
		this.filter = sink.getEntityFilter();
	}

	@Override
	public void complete() {
		
	}

	@Override
	protected void parseNodes(List<Osmformat.Node> nodes) {
		// Check entity filter
		if(!filter.getEntityAllowed(net.morbz.osmonaut.osm.EntityType.NODE)) {
			return;
		}
				
		for(Osmformat.Node i : nodes) {
			// Get tags
			Tags tags = new Tags();
			for(int j = 0; j < i.getKeysCount(); j++) {
				tags.put(getStringById(i.getKeys(j)), getStringById(i.getVals(j)));
			}
			
			// Get properties
			double latf = parseLat(i.getLat()), lonf = parseLon(i.getLon());
			LatLon latlon = new LatLon(latf, lonf);
			long id = i.getId();
			
			// Create entity
			Node node = new Node(id, tags, latlon);
			sink.foundNode(node);
		}
	}

	@Override
	protected void parseDense(Osmformat.DenseNodes nodes) {
		// Check entity filter
		if(!filter.getEntityAllowed(net.morbz.osmonaut.osm.EntityType.NODE)) {
			return;
		}
				
		long lastId = 0, lastLat = 0, lastLon = 0;
		int j = 0; // Index into the keysvals array.

		for(int i = 0; i < nodes.getIdCount(); i++) {
			// Get tags
			Tags tags = new Tags();
			if(nodes.getKeysValsCount() > 0) {
				while(nodes.getKeysVals(j) != 0) {
					int keyid = nodes.getKeysVals(j++);
					int valid = nodes.getKeysVals(j++);
					tags.put(getStringById(keyid), getStringById(valid));
				}
				j++; // Skip over the '0' delimiter.
			}
			
			// Get properties
			long lat = nodes.getLat(i) + lastLat;
			lastLat = lat;
			long lon = nodes.getLon(i) + lastLon;
			lastLon = lon;
			long id = nodes.getId(i) + lastId;
			lastId = id;
			double latf = parseLat(lat), lonf = parseLon(lon);
			LatLon latlon = new LatLon(latf, lonf);
			
			// Create entity
			Node node = new Node(id, tags, latlon);
			sink.foundNode(node);
		}
	}

	@Override
	protected void parseWays(List<Osmformat.Way> ways) {
		// Check entity filter
		if(!filter.getEntityAllowed(net.morbz.osmonaut.osm.EntityType.WAY)) {
			return;
		}
		
		for(Osmformat.Way i : ways) {
			// Get tags
			Tags tags = new Tags();
			for(int j = 0; j < i.getKeysCount(); j++) {
				tags.put(getStringById(i.getKeys(j)), getStringById(i.getVals(j)));
			}

			// Get nodes
			List<Node> nodes = new ArrayList<Node>();
			long lastId = 0;
			for(long j : i.getRefsList()) {
				long nodeRef = j + lastId;
				Node node = new Node(nodeRef, null, null);
				nodes.add(node);
				lastId = nodeRef;
			}
			
			// Create entity
			long id = i.getId();
			Way way = new Way(id, tags, nodes);
			sink.foundWay(way);
		}
	}

	@Override
	protected void parseRelations(List<Osmformat.Relation> rels) {
		// Check entity filter
		if(!filter.getEntityAllowed(net.morbz.osmonaut.osm.EntityType.RELATION)) {
			return;
		}
				
		for(Osmformat.Relation i : rels) {
			// Get tags
			Tags tags = new Tags();
			for(int j = 0; j < i.getKeysCount(); j++) {
				tags.put(getStringById(i.getKeys(j)), getStringById(i.getVals(j)));
			}

			// Get members
			long lastMid = 0;
			List<RelationMember> members = new ArrayList<RelationMember>();
			for(int j = 0; j < i.getMemidsCount(); j++) {
				// Get properties
				long mid = lastMid + i.getMemids(j);
				lastMid = mid;
				String role = getStringById(i.getRolesSid(j));
				
				// Get type
				Entity entity = null;
				if(i.getTypes(j) == Osmformat.Relation.MemberType.NODE) {
					entity = new Node(mid, null, null);
				} else if(i.getTypes(j) == Osmformat.Relation.MemberType.WAY) {
					entity = new Way(mid, null, null);
				} else {
					// We don't handle super-relation and instead just ignore sub-relations
					// TODO: Handle super-relations
					continue;
				}
				
				// Add member
				members.add(new RelationMember(entity, role));
			}
			
			// Create entity
			long id = i.getId();
			Relation relation = new Relation(id, tags, members);
			sink.foundRelation(relation);
		}
	}

	@Override
	public void parse(Osmformat.HeaderBlock block) {
		for(String s : block.getRequiredFeaturesList()) {
			if(s.equals("OsmSchema-V0.6")) {
				continue; // We can parse this.
			}
			if(s.equals("DenseNodes")) {
				continue; // We can parse this.
			}
			System.out.println("E: File requires unknown feature: " + s);
		}
	}
}
