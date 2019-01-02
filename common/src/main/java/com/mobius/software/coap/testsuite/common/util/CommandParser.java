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

package com.mobius.software.coap.testsuite.common.util;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;

import com.mobius.software.coap.parser.message.options.CoapOptionType;
import com.mobius.software.coap.parser.tlv.CoapCode;
import com.mobius.software.coap.parser.tlv.CoapMessage;
import com.mobius.software.coap.parser.tlv.CoapType;
import com.mobius.software.coap.testsuite.common.model.Command;
import com.mobius.software.coap.testsuite.common.model.Property;
import com.mobius.software.coap.testsuite.common.model.PropertyType;

public class CommandParser
{
	public static boolean validate(Command command)
	{
		if (command == null)
			return false;

		for (Property property : command.getCommandProperties())
		{
			switch (property.getType())
			{
			case CODE:
				CoapCode coapCode = CoapCode.valueOf(property.getValue());
				if (coapCode == null)
					return false;
				break;
			case COUNT:
			case MESSAGE_SIZE:
			case RESEND_TIME:
				try
				{
					Integer val = Integer.parseInt(property.getValue());
					if (val == null || val < 0)
						return false;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
				break;
			case URI_PATH:
				if (StringUtils.isEmpty(property.getValue()))
					return false;
				break;
			case ACCEPT:
				try
				{
					Integer val = Integer.parseInt(property.getValue());
					if (val == null || (val > 2 || val < 0))
						return false;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
				break;
			case OBSERVE:
				try
				{
					Integer val = Integer.parseInt(property.getValue());
					if (val == null || val < 0)
						return false;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
				break;
			case TYPE:
				CoapType coapType = CoapType.valueOf(property.getValue());
				if (coapType == null)
					return false;
				break;
			case DISCONNECT:
			default:
				break;
			}
		}
		return true;
	}

	public static CoapMessage toMessage(String clientID, Command command)
	{
		CoapMessage.Builder builder = CoapMessage.builder();
		builder.option(CoapOptionType.NODE_ID, clientID);
		for (Property property : command.getCommandProperties())
		{
			switch (property.getType())
			{
			case CODE:
				builder.code(CoapCode.valueOf(property.getValue()));
				break;
			case URI_PATH:
				builder.option(CoapOptionType.URI_PATH, property.getValue());
				break;
			case ACCEPT:
				Integer qos = Integer.parseInt(property.getValue());
				builder.option(CoapOptionType.ACCEPT, qos);
				break;
			case OBSERVE:
				Integer observe = Integer.parseInt(property.getValue());
				builder.option(CoapOptionType.OBSERVE, observe);
				break;
			case TYPE:
				builder.type(CoapType.valueOf(property.getValue()));
				break;
			case MESSAGE_SIZE:
				Integer messageSize = Integer.parseInt(property.getValue());
				builder.payload(MessageGenerator.generateContent(messageSize));
				break;
			case COUNT:
			case RESEND_TIME:
			case DISCONNECT:
			default:
				break;
			}
		}
		return builder.build();
	}

	public static ConcurrentLinkedQueue<Command> retrieveCommands(List<Command> commands, int repeatCount, long repeatInterval)
	{
		ConcurrentLinkedQueue<Command> queue = new ConcurrentLinkedQueue<>();
		long currInterval = 0L;
		while (repeatCount-- > 0)
		{
			for (int i = 0; i < commands.size(); i++)
			{
				Command command = commands.get(i);
				if (i == 0)
					command = new Command(command.getSendTime() + currInterval, command.getCommandProperties());
				queue.offer(command);
				Integer resendTime = retrieveIntegerProperty(command, PropertyType.RESEND_TIME);
				if (resendTime != null)
				{
					Integer count = retrieveIntegerProperty(command, PropertyType.COUNT);
					for (int j = 1; j < count; j++)
					{
						Command publish = new Command(resendTime.longValue(), command.getCommandProperties());
						queue.offer(publish);
					}
				}
			}
			currInterval = repeatInterval;
		}
		return queue;
	}

	public static int count(Command command)
	{
		Integer count = retrieveIntegerProperty(command, PropertyType.COUNT);
		return count != null ? count : 0;
	}

	private static Integer retrieveIntegerProperty(Command command, PropertyType type)
	{
		Integer value = null;
		for (Property property : command.getCommandProperties())
		{
			if (property.getType() == type)
			{
				value = Integer.parseInt(property.getValue());
				break;
			}
		}
		return value;
	}
}
