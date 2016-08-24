package net.morbz.osmonaut;

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
import java.util.BitSet;
import java.util.List;

/**
 * Keeps track of which IDs are needed. Uses chunks of BitSet for storing IDs.
 * Every ID requires at least 1 bit of memory + BitSet overhead. Doesn't 
 * support negative ID values.
 */
public class IdTracker {
	/* TODO: Can be optimized to use 20-30% less RAM if long-arrays are used
	 * instead of BitSet */
	private List<BitSet> segments = new ArrayList<BitSet>();
	private static final int idsPerSegment = 512; // Should be a multiple of 64

	/**
	 * Marks the entity with the given ID as needed.
	 * @param id The needed ID
	 */
	public void set(long id) {
		// Grow array
		int segmentId = getSegmentId(id);
		while(!arraySpaceAllocated(segmentId))  {
			segments.add(null);
		}

		// Get/create segment
		BitSet segment = segments.get(segmentId);
		if(segment == null) {
			segment = new BitSet(idsPerSegment);
			segments.set(segmentId, segment);
		}

		// Set bit
		int segmentPosition = getSegmentPosition(id);
		segment.set(segmentPosition);	
	}

	/**
	 * @param id The entity ID
	 * @return true if the entity with ID is needed
	 */
	public boolean get(long id) {
		// Check array size
		int segmentId = getSegmentId(id);
		if(!arraySpaceAllocated(segmentId))  {
			return false;
		}

		// Get segment
		BitSet segment = segments.get(segmentId);
		if(segment == null) {
			return false;
		}

		// Get bit
		int segmentPosition = getSegmentPosition(id);
		return segment.get(segmentPosition);
	}

	/**
	 * Marks the entity with this ID as not needed anymore.
	 * @param id The entity ID
	 */
	public void unset(long id) {
		// Check array size
		int segmentId = getSegmentId(id);
		if(!arraySpaceAllocated(segmentId))  {
			return;
		}

		// Get segment
		BitSet segment = segments.get(segmentId);
		if(segment == null) {
			return;
		}

		// Unset bit
		int segmentPosition = getSegmentPosition(id);
		segment.clear(segmentPosition);

		// Remove segment if empty
		if(segment.isEmpty()) {
			segments.set(segmentId, null);
		}
	}

	/**
	 * @return true if there are no needed entities
	 */
	public boolean isEmpty() {
		// Iterate through all segments
		for(BitSet segment : segments) {
			if(segment == null) {
				continue;
			}
			if(!segment.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private int getSegmentId(long id) {
		return (int)(id / idsPerSegment);
	}

	private int getSegmentPosition(long id) {
		return (int)(id % idsPerSegment);
	}

	private boolean arraySpaceAllocated(int segmentId) {
		return segments.size() >= segmentId + 1;
	}
}
