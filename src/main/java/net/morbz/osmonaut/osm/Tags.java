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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class that holds the tags of an OSM entity. Iteration on this class is
 * based on the keys.
 * 
 * @author MorbZ
 */
public class Tags implements Iterable<String>, Externalizable {
	private List<String> keys;
	private List<String> values;

	/**
	 * Lazy creation of the arrays.
	 */
	private void createArrays() {
		if (!hasArrays()) {
			keys = new ArrayList<String>();
			values = new ArrayList<String>();
		}
	}

	/**
	 * @return True if the arrays have already been created
	 */
	private boolean hasArrays() {
		return keys != null;
	}

	/**
	 * @param key
	 *            The key of the tag
	 * @return The index of the tag or -1 if the tag doesn't exist
	 */
	private int indexForKey(String key) {
		// Has arrays?
		if (!hasArrays()) {
			return -1;
		}

		// Iterate keys
		for (int i = 0; i < keys.size(); i++) {
			if (keys.get(i).equals(key)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param key
	 *            The key of the tag
	 * @return True if there is a tag with this key
	 */
	public boolean hasKey(String key) {
		return indexForKey(key) != -1;
	}

	/**
	 * 
	 * @param keys
	 *            The keys to check
	 * @return True if there is at least 1 of the given keys
	 */
	public boolean hasOneOfKeys(String[] keys) {
		for (String key : keys) {
			if (indexForKey(key) != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param key
	 *            The key of the tag
	 * @param value
	 *            The value of the tag
	 * @return True if there is a tag with this key and value
	 */
	public boolean hasKeyValue(String key, String value) {
		int index = indexForKey(key);
		if (index == -1) {
			return false;
		}
		return values.get(index).equals(value);
	}

	/**
	 * @param key
	 *            The key of the tag
	 * @return The value for the tag with this key or null of the key doesn't
	 *         exist
	 */
	public String get(String key) {
		int index = indexForKey(key);
		if (index != -1) {
			return values.get(index);
		}
		return null;
	}

	/**
	 * Adds a tags with the given key and value or updates the tag if the key
	 * already exists.
	 * 
	 * @param key
	 *            The key of the tag
	 * @param value
	 *            The value of the tag
	 */
	public void set(String key, String value) {
		// Create the arrays
		createArrays();

		// Check if key is present
		int index = indexForKey(key);
		if (index == -1) {
			// Add new tag
			keys.add(key);
			values.add(value);
		} else {
			// Update tag
			values.set(index, value);
		}
	}

	/**
	 * @return The number of tags
	 */
	public int size() {
		if (!hasArrays()) {
			return 0;
		}
		return keys.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<String> iterator() {
		if (!hasArrays()) {
			return new ArrayList<String>().iterator();
		} else {
			return keys.iterator();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if (size() == 0) {
			return "[]";
		}

		String str = "";
		str += "[" + "\n";
		for (int i = 0; i < size(); i++) {
			str += "\t" + "{ key: \"" + keys.get(i) + "\", value: \"" + values.get(i) + "\" }" + "\n";
		}
		str += "]";
		return str;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(keys);
		out.writeObject(values);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		keys = (List<String>)in.readObject();
		values = (List<String>)in.readObject();
	}
}
