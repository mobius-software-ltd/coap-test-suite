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

public class Counters
{
	private CommandCounter in;
	private CommandCounter out;

	public Counters()
	{
	}

	public Counters(CommandCounter in, CommandCounter out)
	{
		this.in = in;
		this.out = out;
	}

	public void incrementIn()
	{
		in.increment();
	}

	public void incrementOut()
	{
		out.increment();
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
		result = prime * result + ((in == null) ? 0 : in.hashCode());
		result = prime * result + ((out == null) ? 0 : out.hashCode());
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
		Counters other = (Counters) obj;
		if (in == null)
		{
			if (other.in != null)
				return false;
		}
		else if (!in.equals(other.in))
			return false;
		if (out == null)
		{
			if (other.out != null)
				return false;
		}
		else if (!out.equals(other.out))
			return false;
		return true;
	}

	public CommandCounter getIn()
	{
		return in;
	}

	public void setIn(CommandCounter in)
	{
		this.in = in;
	}

	public CommandCounter getOut()
	{
		return out;
	}

	public void setOut(CommandCounter out)
	{
		this.out = out;
	}
}
