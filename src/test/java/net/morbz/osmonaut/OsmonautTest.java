package net.morbz.osmonaut;

import static net.morbz.osmonaut.osm.EntityType.NODE;
import static net.morbz.osmonaut.osm.EntityType.WAY;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;

import net.morbz.osmonaut.osm.Entity;
import net.morbz.osmonaut.osm.EntityType;
import net.morbz.osmonaut.osm.LatLon;
import net.morbz.osmonaut.osm.Node;
import net.morbz.osmonaut.osm.Relation;
import net.morbz.osmonaut.osm.RelationMember;
import net.morbz.osmonaut.osm.Tags;
import net.morbz.osmonaut.osm.Way;

public class OsmonautTest {
    @Test
    public void should_find_nodes() throws Exception {
        List<Node> nodes = scan(
                new EntityFilter(true, false, false),
                new Predicate<Tags>() {
                    @Override
                    public boolean test(Tags tags) {
                        return tags.hasKeyValue("railway", "subway_entrance");
                    }
                });

        assertThat(nodes.get(1)).isEqualToComparingFieldByFieldRecursively(
                new Node(1986875861, entranceTags(), new LatLon(48.867002500000005, 2.3217243)));
        assertThat(nodes).extracting("latlon").containsOnly(
                new LatLon(48.867002500000005, 2.3217243),
                new LatLon(48.8667336, 2.3225672),
                new LatLon(48.866254600000005, 2.32355),
                new LatLon(48.8653454, 2.3226649000000004),
                new LatLon(48.866504500000005, 2.3237284000000002),
                new LatLon(48.8662246, 2.3234453));
    }

    @Test
    public void should_find_ways() throws Exception {
        List<Way> ways = scan(
                new EntityFilter(false, true, false),
                new Predicate<Tags>() {
                    @Override
                    public boolean test(Tags tags) {
                        return tags.hasKeyValue("bridge", "yes");
                    }
                });

        assertThat(ways).hasSize(2);
        assertThat(ways.get(0)).isEqualToComparingFieldByFieldRecursively(
                new Way(28302023, bridgeTags(), nodes()));
    }

    @Test
    public void should_find_relations() throws Exception {
        List<Relation> relations = scan(
                new EntityFilter(false, false, true),
                new Predicate<Tags>() {
                    @Override
                    public boolean test(Tags tags) {
                        return tags.hasKeyValue("public_transport", "stop_area") && tags.hasKeyValue("name", "Concorde");
                    }
                });
        assertThat(relations).hasSize(1);

        Relation concorde = relations.get(0);
        assertThat(concorde.getId()).isEqualTo(379422);
        assertThat(concorde.getMembers()).filteredOn(only(NODE)).hasSize(13);
        assertThat(concorde.getMembers()).filteredOn(only(WAY)).hasSize(4);
    }

    private Predicate<RelationMember> only(final EntityType type) {
        return new Predicate<RelationMember>() {
            @Override
            public boolean test(RelationMember t) {
                return t.getEntity().getEntityType().equals(type);
            }
        };
    }

    private <T> List<T> scan(EntityFilter filter, final Predicate<Tags> predicate) {
        String file = OsmonautTest.class.getResource("/concorde-paris.osm.pbf").getPath();
        final List<T> acc = new ArrayList<>();
        Osmonaut osmonaut = new Osmonaut(file, filter);
        osmonaut.scan(new IOsmonautReceiver() {
            @Override
            public boolean needsEntity(EntityType type, Tags tags) {
                return predicate.test(tags);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void foundEntity(Entity entity) {
                acc.add((T) entity);
            }
        });
        return acc;
    }

    private ArrayList<Node> nodes() {
        ArrayList<Node> list = new ArrayList<Node>();
        list.add(new Node(310795674, new Tags(), new LatLon(48.887131700000005, 2.252968)));
        list.add(new Node(417635644, new Tags(), new LatLon(48.8861514, 2.2561025000000003)));
        return list;
    }

    private Tags entranceTags() {
        Tags tags = new Tags();
        tags.set("name", "Concorde");
        tags.set("railway", "subway_entrance");
        tags.set("wheelchair", "no");
        return tags;
    }

    private Tags bridgeTags() {
        Tags tags = new Tags();
        tags.set("bridge", "yes");
        tags.set("name", "Métro 1");
        tags.set("railway", "subway");
        return tags;
    }
}
