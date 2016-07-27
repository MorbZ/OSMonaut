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

import net.morbz.osmonaut.osm.LatLon;
import net.morbz.osmonaut.util.StringUtil;

/**
 * This class defines a bounding box of latitude and longitude coordinates.
 * 
 * @author MorbZ
 */
public class Bounds {
	private double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
	private double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;
	private boolean initialized = false;

	/**
	 * Creates an empty, uninitialized bounding box.
	 */
	public Bounds() {
	}

	/**
	 * Creates a bounding box with the given bounds. The order of the
	 * coordinates doesn't matter.
	 * 
	 * @param lat1
	 *            The first latitude
	 * @param lat2
	 *            The second latitude
	 * @param lon1
	 *            The first longitude
	 * @param lon2
	 *            The second longitude
	 */
	public Bounds(double lat1, double lat2, double lon1, double lon2) {
		initialized = true;
		minLat = Math.min(lat1, lat2);
		maxLat = Math.max(lat1, lat2);
		minLon = Math.min(lon1, lon2);
		maxLon = Math.max(lon1, lon2);
	}

	/**
	 * Extends the bounding box to include the given coordinate.
	 * 
	 * @param latlon
	 *            The coordinate
	 */
	public void extend(LatLon latlon) {
		initialized = true;
		minLat = Math.min(minLat, latlon.getLat());
		minLon = Math.min(minLon, latlon.getLon());
		maxLat = Math.max(maxLat, latlon.getLat());
		maxLon = Math.max(maxLon, latlon.getLon());
	}

	/**
	 * Extends the bounding box to include the given bounding box.
	 * 
	 * @param bounds
	 *            The bounding box
	 */
	public void extend(Bounds bounds) {
		initialized = true;
		minLat = Math.min(minLat, bounds.getMinLat());
		minLon = Math.min(minLon, bounds.getMinLon());
		maxLat = Math.max(maxLat, bounds.getMaxLat());
		maxLon = Math.max(maxLon, bounds.getMaxLon());
	}

	/**
	 * @return The center of this bounding box or null if the bounding box has
	 *         not been initialized
	 */
	public LatLon getCenter() {
		if (!initialized) {
			return null;
		}
		return new LatLon((minLat + maxLat) / 2, (minLon + maxLon) / 2);
	}

	/**
	 * @param latlon
	 *            The coordinate
	 * @return True if the given coordinate is within the bounds
	 */
	public boolean contains(LatLon latlon) {
		if (!initialized) {
			return false;
		}
		if (latlon.getLat() < minLat) {
			return false;
		}
		if (latlon.getLat() > maxLat) {
			return false;
		}
		if (latlon.getLon() < minLon) {
			return false;
		}
		if (latlon.getLon() > maxLon) {
			return false;
		}
		return true;
	}

	/**
	 * @return The minimal latitude or 0 if the bounding box has not been
	 *         initialized
	 */
	public double getMinLat() {
		if (!initialized) {
			return 0;
		}
		return minLat;
	}

	/**
	 * @return The maximal latitude or 0 if the bounding box has not been
	 *         initialized
	 */
	public double getMaxLat() {
		if (!initialized) {
			return 0;
		}
		return maxLat;
	}

	/**
	 * @return The minimal longitude or 0 if the bounding box has not been
	 *         initialized
	 */
	public double getMinLon() {
		if (!initialized) {
			return 0;
		}
		return minLon;
	}

	/**
	 * @return The maximal longitude or 0 if the bounding box has not been
	 *         initialized
	 */
	public double getMaxLon() {
		if (!initialized) {
			return 0;
		}
		return maxLon;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "{ minLat: " + StringUtil.formatCoordinate(getMinLat()) + ", minLon: "
				+ StringUtil.formatCoordinate(getMinLon()) + ", maxLat: " + StringUtil.formatCoordinate(getMaxLat())
				+ ", maxLon: " + StringUtil.formatCoordinate(getMaxLon()) + " }";
	}
}
