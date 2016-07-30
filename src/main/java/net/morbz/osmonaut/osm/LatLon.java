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

import net.morbz.osmonaut.util.StringUtil;

/**
 * A class that represents the latitude and longitude.
 * 
 * @author MorbZ
 */
public class LatLon implements Externalizable {
	private double lat;
	private double lon;

	/**
	 * @param lat
	 *            The latitude
	 * @param lon
	 *            The longitude
	 */
	public LatLon(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	/**
	 * No-arg constructor for Externalizable
	 */
	public LatLon() {

	}

	/**
	 * @return The latitude
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * @return The longitude
	 */
	public double getLon() {
		return lon;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LatLon)) {
			return false;
		}
		LatLon latlon = (LatLon) obj;
		if (latlon.getLat() != lat) {
			return false;
		}
		if (latlon.getLon() != lon) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "{ lat: " + StringUtil.formatCoordinate(lat) + ", lon: " + StringUtil.formatCoordinate(lon) + " }";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeDouble(lat);
		out.writeDouble(lon);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.lat = in.readDouble();
		this.lon = in.readDouble();
	}
}
