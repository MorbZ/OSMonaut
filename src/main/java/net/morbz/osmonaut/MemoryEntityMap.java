package net.morbz.osmonaut;

/*
* The MIT License (MIT)
* 
* Copyright (c) 2016 Merten Peetz
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
import java.util.Comparator;
import java.util.List;

import net.morbz.osmonaut.osm.Entity;

/**
 * Uses heap to store entities. As fast as TLongObjectHashMap but has a far 
 * lower memory usage. Uses bucket arrays and does a binary search to find
 * entities. Does not support duplicate handling.
 */
public class MemoryEntityMap<T extends Entity> implements EntityMap<T> {
	private List<List<T>> buckets = new ArrayList<List<T>>();
	private boolean sorted = true;

	private static final int entitiesPerBucket = 1_000_000;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(T entity) {
		// Grow array
		int bucketId = getBucketId(entity.getId());
		while(!arraySpaceAllocated(bucketId)) {
			buckets.add(null);
		}

		// Get/create bucket
		List<T> bucket = buckets.get(bucketId);
		if(bucket == null) {
			bucket = new ArrayList<T>();
			buckets.set(bucketId, bucket);
		}

		// Add entity
		bucket.add(entity);
		sorted = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get(long id) {
		// Ensure that arrays are sorted
		if(!sorted) {
			sort();
			sorted = true;
		}

		// Check array size
		int bucketId = getBucketId(id);
		if(!arraySpaceAllocated(bucketId)) {
			return null;
		}

		// Get bucket
		List<T> bucket = buckets.get(bucketId);
		if(bucket == null) {
			return null;
		}

		// Binary search for entity
		int low = 0;
		int high = bucket.size() - 1;
		while(low <= high) {
			int mid = (low + high) >>> 1;
			long midId = bucket.get(mid).getId();

			if(midId < id)
				low = mid + 1;
			else if(midId > id)
				high = mid - 1;
			else {
				return bucket.get(mid);
			}
		}
		return null;
	}

	private void sort() {
		// Create comparator
		Comparator<T> comp = new Comparator<T>() {
			public int compare(T t1, T t2) {
				return Long.compare(t1.getId(), t2.getId());
			}
		};

		// Sort all buckets
		for(List<T> bucket : buckets) {
			if(bucket == null) {
				continue;
			}

			Collections.sort(bucket, comp);
		}
	}

	private int getBucketId(long id) {
		return (int)(id / entitiesPerBucket);
	}

	private boolean arraySpaceAllocated(int bucketId) {
		return buckets.size() >= bucketId + 1;
	}
}
