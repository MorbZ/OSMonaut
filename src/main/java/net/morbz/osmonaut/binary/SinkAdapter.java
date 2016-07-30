package net.morbz.osmonaut.binary;

import static org.openstreetmap.osmosis.core.domain.v0_6.EntityType.Node;
import static org.openstreetmap.osmosis.core.domain.v0_6.EntityType.Way;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import net.morbz.osmonaut.EntityFilter;
import net.morbz.osmonaut.osm.LatLon;
import net.morbz.osmonaut.osm.Node;
import net.morbz.osmonaut.osm.Relation;
import net.morbz.osmonaut.osm.RelationMember;
import net.morbz.osmonaut.osm.Tags;
import net.morbz.osmonaut.osm.Way;

public class SinkAdapter {
	public static Sink adapt(final OsmonautSink sink) {
		final EntityFilter filter = sink.getEntityFilter();
		return new Sink() {
			@Override
			public void release() {
			}

			@Override
			public void complete() {
			}

			@Override
			public void initialize(Map<String, Object> metaData) {
			}

			@Override
			public void process(EntityContainer container) {
				Entity entity = container.getEntity();
				EntityType type = entity.getType();
				switch (type) {
				case Node:
					if (filter.getEntityAllowed(net.morbz.osmonaut.osm.EntityType.NODE)) {
						sink.foundNode(
								nodeFor((org.openstreetmap.osmosis.core.domain.v0_6.Node) container.getEntity()));
					}
					break;
				case Way:
					if (filter.getEntityAllowed(net.morbz.osmonaut.osm.EntityType.WAY)) {
						sink.foundWay(wayFor((org.openstreetmap.osmosis.core.domain.v0_6.Way) container.getEntity()));
					}
					break;
				case Relation:
					if (filter.getEntityAllowed(net.morbz.osmonaut.osm.EntityType.RELATION)) {
						sink.foundRelation(relationFor(
								(org.openstreetmap.osmosis.core.domain.v0_6.Relation) container.getEntity()));
					}
					break;
				default:
					break;
				}
			}

			private Relation relationFor(org.openstreetmap.osmosis.core.domain.v0_6.Relation entity) {
				List<RelationMember> members = new ArrayList<>();
				for (org.openstreetmap.osmosis.core.domain.v0_6.RelationMember member : entity.getMembers()) {
					if (member.getMemberType().equals(Node)) {
						members.add(
								new RelationMember(new Node(member.getMemberId(), null, null), member.getMemberRole()));
					} else if (member.getMemberType().equals(Way)) {
						members.add(
								new RelationMember(new Way(member.getMemberId(), null, null), member.getMemberRole()));
					}
				}
				return new Relation(entity.getId(), convertTags(entity.getTags()), members, false);
			}

			private Way wayFor(org.openstreetmap.osmosis.core.domain.v0_6.Way entity) {
				List<Node> nodes = new ArrayList<>();
				for (WayNode node : entity.getWayNodes()) {
					nodes.add(new Node(node.getNodeId(), null, null));
				}
				return new Way(entity.getId(), convertTags(entity.getTags()), nodes);
			}

			private Node nodeFor(org.openstreetmap.osmosis.core.domain.v0_6.Node entity) {
				return new Node(entity.getId(), convertTags(entity.getTags()),
						new LatLon(entity.getLatitude(), entity.getLongitude()));
			}

			private Tags convertTags(Collection<Tag> tags) {
				Tags result = new Tags();
				for (Tag tag : tags) {
					result.set(tag.getKey(), tag.getValue());
				}
				return result;
			}
		};
	}
}
