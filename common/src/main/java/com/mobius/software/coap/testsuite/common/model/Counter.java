/**
 * Mobius Software LTD
 * Copyright 2015-2016, Mobius Software LTD
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.mobius.software.coap.testsuite.common.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Counter
{
	protected Integer count;
	protected Direction direction;

	public Counter()
	{
		super();
	}

	public Counter(Integer count, Direction direction)
	{
		super();
		this.count = count;
		this.direction = direction;
	}

	public void increment()
	{
		if (count == null)
			count = 0;
		count++;
	}

	public void add(int count)
	{
		if (this.count == null)
			this.count = 0;
		this.count += count;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((count == null) ? 0 : count.hashCode());
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Counter other = (Counter) obj;
		if (count == null)
		{
			if (other.count != null)
				return false;
		}
		else if (!count.equals(other.count))
			return false;
		if (direction != other.direction)
			return false;
		return true;
	}

	public Integer getCount()
	{
		return count != null ? count : 0;
	}

	public void setCount(Integer count)
	{
		this.count = count;
	}

	public Direction getDirection()
	{
		return direction;
	}

	public void setDirection(Direction direction)
	{
		this.direction = direction;
	}

	public boolean messageIsIncoming()
	{
		return this.direction == Direction.INCOMING;
	}
}
