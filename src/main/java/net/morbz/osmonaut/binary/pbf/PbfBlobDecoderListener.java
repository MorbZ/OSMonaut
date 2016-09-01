// This software is released into the Public Domain.  See copying.txt for details.

package net.morbz.osmonaut.binary.pbf;

import java.util.List;

import net.morbz.osmonaut.EntityFilter;
import net.morbz.osmonaut.osm.Entity;

/**
 * Instances of this interface are used to receive results from PBFBlobDecoder.
 * 
 * @author Brett Henderson
 * @author Merten Peetz
 */
public interface PbfBlobDecoderListener {
	/**
	 * Provides the listener with the list of decoded entities and the 
	 * contained OSM types of the blob.
	 * 
	 * @param decodedEntities
	 *            The decoded entities.
	 * @param containedTypes
	 *            The OSM entity types that the given blob contains.
	 */
	void complete(List<Entity> decodedEntities, EntityFilter containedTypes);

	/**
	 * Notifies the listener that an error occurred during processing.
	 */
	void error();
}
