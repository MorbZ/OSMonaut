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

import net.morbz.osmonaut.util.StringUtil;

/**
 * A class that represents a member of an OSM relation.
 * 
 * @author MorbZ
 */
public class RelationMember {
	private final Entity entity;
	private final String role;

	/**
	 * @param entity
	 *            The entity of this member
	 * @param role
	 *            The role of this member
	 */
	public RelationMember(Entity entity, String role) {
		this.entity = entity;
		this.role = role;
	}

	/**
	 * @return The entity of this member
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * @return The role of this member
	 */
	public String getRole() {
		return role;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String str = "";
		str += "{" + "\n";
		str += "\t" + "role: \"" + role + "\"" + "\n";
		str += StringUtil.indent(entity.toString()) + "\n";
		str += "}";
		return str;
	}
}
