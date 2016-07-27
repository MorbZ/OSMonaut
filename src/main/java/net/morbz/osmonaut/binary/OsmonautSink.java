package net.morbz.osmonaut.binary;

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

import net.morbz.osmonaut.EntityFilter;
import net.morbz.osmonaut.osm.Node;
import net.morbz.osmonaut.osm.Relation;
import net.morbz.osmonaut.osm.Way;

/**
 * The sink that connects the Osmosis binary parser with Osmonaut.
 * 
 * @author MorbZ
 */
public abstract class OsmonautSink {
	/**
	 * The parser found a node.
	 * 
	 * @param node
	 *            The full node
	 */
	public void foundNode(Node node) {
	}

	/**
	 * The parser found a way.
	 * 
	 * @param way
	 *            The way with placeholder nodes in it that just have an ID.
	 */
	public void foundWay(Way way) {
	}

	/**
	 * The parser found a relation.
	 * 
	 * @param relation
	 *            The relation with placeholder members that just have a role
	 *            and an ID.
	 */
	public void foundRelation(Relation relation) {
	}

	/**
	 * @return The entity filter that is used to optimize the parser.
	 */
	public abstract EntityFilter getEntityFilter();
}
