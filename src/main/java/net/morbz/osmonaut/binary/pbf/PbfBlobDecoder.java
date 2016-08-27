// This software is released into the Public Domain.  See copying.txt for details.

package net.morbz.osmonaut.binary.pbf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import com.google.protobuf.InvalidProtocolBufferException;

import net.morbz.osmonaut.EntityFilter;
import net.morbz.osmonaut.binary.pbf.proto.Fileformat.Blob;
import net.morbz.osmonaut.binary.pbf.proto.Osmformat;
import net.morbz.osmonaut.binary.pbf.proto.Osmformat.PrimitiveBlock;
import net.morbz.osmonaut.binary.pbf.proto.Osmformat.PrimitiveBlock.PrimitiveGroup;
import net.morbz.osmonaut.binary.pbf.proto.Osmformat.PrimitiveBlock.PrimitiveGroup.DenseNodes;
import net.morbz.osmonaut.binary.pbf.proto.Osmformat.PrimitiveBlock.PrimitiveGroup.Node;
import net.morbz.osmonaut.binary.pbf.proto.Osmformat.PrimitiveBlock.PrimitiveGroup.Relation;
import net.morbz.osmonaut.binary.pbf.proto.Osmformat.PrimitiveBlock.PrimitiveGroup.Relation.MemberType;
import net.morbz.osmonaut.binary.pbf.proto.Osmformat.PrimitiveBlock.PrimitiveGroup.Way;
import net.morbz.osmonaut.osm.Entity;
import net.morbz.osmonaut.osm.LatLon;
import net.morbz.osmonaut.osm.RelationMember;
import net.morbz.osmonaut.osm.Tags;

/**
 * Converts PBF block data into decoded entities ready to be passed into a 
 * pipeline. This class is designed to be passed into a pool of worker threads
 * to allow multi-threaded decoding.
 * 
 * @author Brett Henderson
 * @author Merten Peetz
 */
public class PbfBlobDecoder implements Runnable {
	private String blobType;
	private byte[] rawBlob;
	private PbfBlobDecoderListener listener;
	private List<Entity> decodedEntities;
	private EntityFilter filter;
	private PbfFieldDecoder fieldDecoder;

	/**
	 * Creates a new instance.
	 * 
	 * @param blobType
	 *            The type of blob.
	 * @param rawBlob
	 *            The raw data of the blob.
	 * @param listener
	 *            The listener for receiving decoding results.
	 * @param filter
	 *            The entity filter that tells which entities should be 
	 *            scanned.
	 */
	public PbfBlobDecoder(String blobType, byte[] rawBlob, PbfBlobDecoderListener listener, EntityFilter filter) {
		this.blobType = blobType;
		this.rawBlob = rawBlob;
		this.listener = listener;
		this.filter = filter;
	}

	private byte[] readBlobContent() throws IOException {
		Blob blob = Blob.parseFrom(rawBlob);
		byte[] blobData;

		if (blob.hasRaw()) {
			blobData = blob.getRaw().toByteArray();
		} else if (blob.hasZlibData()) {
			Inflater inflater = new Inflater();
			inflater.setInput(blob.getZlibData().toByteArray());
			blobData = new byte[blob.getRawSize()];
			try {
				inflater.inflate(blobData);
			} catch (DataFormatException e) {
				throw new RuntimeException("Unable to decompress PBF blob.", e);
			}
			if (!inflater.finished()) {
				throw new RuntimeException("PBF blob contains incomplete compressed data.");
			}
		} else {
			throw new RuntimeException("PBF blob uses unsupported compression, only raw or zlib may be used.");
		}

		return blobData;
	}

	private void processOsmHeader(byte[] data) throws InvalidProtocolBufferException {
		Osmformat.HeaderBlock header = Osmformat.HeaderBlock.parseFrom(data);

		// Build the list of active and unsupported features in the file.
		List<String> supportedFeatures = Arrays.asList("OsmSchema-V0.6", "DenseNodes");
		List<String> activeFeatures = new ArrayList<String>();
		List<String> unsupportedFeatures = new ArrayList<String>();
		for (String feature : header.getRequiredFeaturesList()) {
			if (supportedFeatures.contains(feature)) {
				activeFeatures.add(feature);
			} else {
				unsupportedFeatures.add(feature);
			}
		}

		// We can't continue if there are any unsupported features. We wait
		// until now so that we can display all unsupported features instead of
		// just the first one we encounter.
		if (unsupportedFeatures.size() > 0) {
			throw new RuntimeException("PBF file contains unsupported features " + unsupportedFeatures);
		}
	}

	private Tags buildTags(List<Integer> keys, List<Integer> values) {
		// Ensure parallel lists are of equal size.
		if (keys.size() != values.size()) {
			throw new RuntimeException("Number of tag keys (" + keys.size() + ") and tag values ("
					+ values.size() + ") don't match");
		}

		Tags tags = new Tags();
		Iterator<Integer> keyIterator = keys.iterator();
		Iterator<Integer> valueIterator = values.iterator();
		while (keyIterator.hasNext()) {
			String key = fieldDecoder.decodeString(keyIterator.next());
			String value = fieldDecoder.decodeString(valueIterator.next());
			tags.set(key, value);
		}
		return tags;
	}

	private void processNodes(List<Node> nodes) {
		for (Node node : nodes) {
			// Create node
			long id = node.getId();
			Tags tags = buildTags(node.getKeysList(), node.getValsList());
			LatLon latlon = new LatLon(node.getLat(), node.getLon());
			net.morbz.osmonaut.osm.Node osmNode = new net.morbz.osmonaut.osm.Node(id, tags, latlon);

			// Add to results
			decodedEntities.add(osmNode);
		}
	}

	private void processNodes(DenseNodes nodes) {
		List<Long> idList = nodes.getIdList();
		List<Long> latList = nodes.getLatList();
		List<Long> lonList = nodes.getLonList();

		// Ensure parallel lists are of equal size.
		if ((idList.size() != latList.size()) || (idList.size() != lonList.size())) {
			throw new RuntimeException("Number of ids (" + idList.size() + "), latitudes (" + latList.size()
			+ "), and longitudes (" + lonList.size() + ") don't match");
		}

		Iterator<Integer> keysValuesIterator = nodes.getKeysValsList().iterator();
		long nodeId = 0;
		long latitude = 0;
		long longitude = 0;
		for (int i = 0; i < idList.size(); i++) {
			// Delta decode node fields.
			nodeId += idList.get(i);
			latitude += latList.get(i);
			longitude += lonList.get(i);

			// Build the tags. The key and value string indexes are sequential
			// in the same PBF array. Each set of tags is delimited by an index
			// with a value of 0.
			Tags tags = new Tags();
			while (keysValuesIterator.hasNext()) {
				int keyIndex = keysValuesIterator.next();
				if (keyIndex == 0) {
					break;
				}
				if (!keysValuesIterator.hasNext()) {
					throw new RuntimeException(
							"The PBF DenseInfo keys/values list contains a key with no corresponding value.");
				}
				int valueIndex = keysValuesIterator.next();

				tags.set(fieldDecoder.decodeString(keyIndex), fieldDecoder.decodeString(valueIndex));
			}

			// Create node
			LatLon latlon = new LatLon(
					fieldDecoder.decodeLatitude(latitude), 
					fieldDecoder.decodeLongitude(longitude));
			net.morbz.osmonaut.osm.Node osmNode = new net.morbz.osmonaut.osm.Node(nodeId, tags, latlon);

			// Add to results
			decodedEntities.add(osmNode);
		}
	}

	private void processWays(List<Way> ways) {
		for (Way way : ways) {
			// Build up the list of way nodes for the way. The node ids are
			// delta encoded meaning that each id is stored as a delta against
			// the previous one.
			long nodeId = 0;
			List<net.morbz.osmonaut.osm.Node> wayNodes = new ArrayList<net.morbz.osmonaut.osm.Node>();
			for (long nodeIdOffset : way.getRefsList()) {
				nodeId += nodeIdOffset;
				wayNodes.add(new net.morbz.osmonaut.osm.Node(nodeId, null, null));
			}

			// Create way
			long id = way.getId();
			Tags tags = buildTags(way.getKeysList(), way.getValsList());
			net.morbz.osmonaut.osm.Way osmWay = new net.morbz.osmonaut.osm.Way(id, tags, wayNodes);

			// Add to results
			decodedEntities.add(osmWay);
		}
	}

	private void processRelations(List<Relation> relations) {
		for (Relation relation : relations) {
			List<Long> memberIds = relation.getMemidsList();
			List<Integer> memberRoles = relation.getRolesSidList();
			List<MemberType> memberTypes = relation.getTypesList();

			// Ensure parallel lists are of equal size.
			if ((memberIds.size() != memberRoles.size()) || (memberIds.size() != memberTypes.size())) {
				throw new RuntimeException("Number of member ids (" + memberIds.size() + "), member roles ("
						+ memberRoles.size() + "), and member types (" + memberTypes.size() + ") don't match");
			}

			Iterator<Long> memberIdIterator = memberIds.iterator();
			Iterator<Integer> memberRoleIterator = memberRoles.iterator();
			Iterator<MemberType> memberTypeIterator = memberTypes.iterator();

			// Build up the list of relation members for the way. The member ids are
			// delta encoded meaning that each id is stored as a delta against
			// the previous one.
			long memberId = 0;
			List<RelationMember> members = new ArrayList<RelationMember>();
			boolean isIncomplete = false;
			while (memberIdIterator.hasNext()) {
				memberId += memberIdIterator.next();
				String memberRole = fieldDecoder.decodeString(memberRoleIterator.next());

				// Get member type
				Entity entity = null;
				MemberType memberType = memberTypeIterator.next();
				switch(memberType) {
				case NODE:
					entity = new net.morbz.osmonaut.osm.Node(memberId, null, null);
					break;
				case WAY:
					entity = new net.morbz.osmonaut.osm.Way(memberId, null, null);
					break;
				case RELATION:
					// We don't handle super-relation and instead just ignore
					// sub-relations
					// TODO: Handle super-relations
					isIncomplete = true;
					continue;
				}

				// Create member
				RelationMember member = new RelationMember(entity, memberRole);
				members.add(member);
			}

			// Create relation
			long id = relation.getId();
			Tags tags = buildTags(relation.getKeysList(), relation.getValsList());
			net.morbz.osmonaut.osm.Relation osmRelation = 
					new net.morbz.osmonaut.osm.Relation(id, tags, members, isIncomplete);

			// Add to results
			decodedEntities.add(osmRelation);
		}
	}

	private void processOsmPrimitives(byte[] data) throws InvalidProtocolBufferException {
		PrimitiveBlock block = PrimitiveBlock.parseFrom(data);
		fieldDecoder = new PbfFieldDecoder(block);

		for (PrimitiveGroup primitiveGroup : block.getPrimitivegroupList()) {
			// Nodes
			if(filter.getEntityAllowed(net.morbz.osmonaut.osm.EntityType.NODE)) {
				processNodes(primitiveGroup.getDense());
				processNodes(primitiveGroup.getNodesList());
			}

			// Ways
			if(filter.getEntityAllowed(net.morbz.osmonaut.osm.EntityType.WAY)) {
				processWays(primitiveGroup.getWaysList());
			}

			// Relations
			if(filter.getEntityAllowed(net.morbz.osmonaut.osm.EntityType.RELATION)) {
				processRelations(primitiveGroup.getRelationsList());
			}
		}
	}

	private void runAndTrapExceptions() {
		try {
			decodedEntities = new ArrayList<Entity>();

			if ("OSMHeader".equals(blobType)) {
				processOsmHeader(readBlobContent());
			} else if ("OSMData".equals(blobType)) {
				processOsmPrimitives(readBlobContent());
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to process PBF blob", e);
		}
	}

	@Override
	public void run() {
		try {
			runAndTrapExceptions();

			listener.complete(decodedEntities);
		} catch (RuntimeException e) {
			listener.error();
		}
	}
}
