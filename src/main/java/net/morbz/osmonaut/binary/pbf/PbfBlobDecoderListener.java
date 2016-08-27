// This software is released into the Public Domain.  See copying.txt for details.

package net.morbz.osmonaut.binary.pbf;

import java.util.List;

import net.morbz.osmonaut.osm.Entity;

/**
 * Instances of this interface are used to receive results from PBFBlobDecoder.
 * 
 * @author Brett Henderson
 */
public interface PbfBlobDecoderListener {
	/**
	 * Provides the listener with the list of decoded entities.
	 * 
	 * @param decodedEntities
	 *            The decoded entities.
	 */
	void complete(List<Entity> decodedEntities);

	/**
	 * Notifies the listener that an error occurred during processing.
	 */
	void error();
}
