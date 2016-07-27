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

import java.util.List;

import net.morbz.osmonaut.geometry.Bounds;
import net.morbz.osmonaut.util.StringUtil;

/**
 * A class that represents an OSM relation element.
 * 
 * @author MorbZ
 */
public class Relation extends Entity {
	private List<RelationMember> members;
	private boolean isIncomplete;

	/**
	 * @return Whether this relation is incomplete. Incomplete means that not
	 *         all relation members are present in the data set
	 */
	public boolean isIncomplete() {
		return isIncomplete;
	}

	/**
	 * @param id
	 *            The OSM-ID of this relation
	 * @param tags
	 *            The tags of this relation
	 * @param members
	 *            The members of this relation
	 */
	public Relation(long id, Tags tags, List<RelationMember> members, boolean isIncomplete) {
		super(id, tags);
		this.members = members;
		this.isIncomplete = isIncomplete;
	}

	/**
	 * @return The members of this relation
	 */
	public List<RelationMember> getMembers() {
		return members;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityType getEntityType() {
		return EntityType.RELATION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LatLon getCenter() {
		Bounds bounds = new Bounds();
		for (RelationMember member : members) {
			LatLon center = member.getEntity().getCenter();
			if (center != null) {
				bounds.extend(center);
			}
		}
		return bounds.getCenter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bounds getBounds() {
		Bounds bounds = new Bounds();
		for (RelationMember member : members) {
			Bounds memberBounds = member.getEntity().getBounds();
			if (memberBounds != null) {
				bounds.extend(memberBounds);
			}
		}
		return bounds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String str = "";
		str += "{" + "\t" + "RELATION" + "\n";
		str += "\t" + "id: " + id + "\n";
		str += "\t" + "tags: " + StringUtil.indent(tags.toString());
		str += "\t" + "members: [" + "\n";
		for (RelationMember member : members) {
			str += StringUtil.indent(StringUtil.indent(member.toString()));
		}
		str += "\t" + "]" + "\n";
		str += "}";
		return str;
	}
}
