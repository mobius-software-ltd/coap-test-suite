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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.mobius.software.coap.testsuite.common.util.CommandParser;

public class Command
{
	private Long sendTime;
	private List<Property> commandProperties;

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
	}

	public Command()
	{

	}

	public Command(Long sendTime, List<Property> commandProperties)
	{
		this.sendTime = sendTime;
		this.commandProperties = commandProperties;
	}

	public Long getSendTime()
	{
		return sendTime;
	}

	public void setSendTime(Long sendTime)
	{
		this.sendTime = sendTime;
	}

	public List<Property> getCommandProperties()
	{
		return commandProperties != null ? commandProperties : Collections.<Property> emptyList();
	}

	public void setCommandProperties(List<Property> commandProperties)
	{
		this.commandProperties = commandProperties;
	}

	public boolean validate()
	{
		return sendTime != null && CommandParser.validate(this);
	}

	public boolean checkIfIsDisconnect()
	{
		return getCommandProperties().stream().map(p -> p.getType()).collect(Collectors.toSet()).contains(PropertyType.DISCONNECT);
	}
}
