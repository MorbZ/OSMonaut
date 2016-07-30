package net.morbz.osmonaut.util;

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

import java.util.Locale;

/**
 * Some custom string functions.
 * 
 * @author MorbZ
 */
public class StringUtil {
	/**
	 * Indents every line of the String by 1 tab.
	 * 
	 * @param str
	 *            The string to indent
	 * @return The indented string
	 */
	public static String indent(String str) {
		String[] lines = str.split("\\n");
		String newStr = "";
		for (String line : lines) {
			newStr += "\t" + line + "\n";
		}
		return newStr;
	}

	/**
	 * Formats a coordinate for pretty output.
	 * 
	 * @param coord
	 *            The coordinate to format
	 * @return The formatted coordinate
	 */
	public static String formatCoordinate(double coord) {
		// 7 decimals is the OSM default
		return String.format(Locale.ENGLISH, "%.7f", coord);
	}
}
