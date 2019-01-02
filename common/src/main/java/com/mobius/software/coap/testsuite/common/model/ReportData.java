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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ReportData
{
	private final String clientID;

	private ConcurrentHashMap<MessageType, AtomicInteger> inPacketCounters = new ConcurrentHashMap<>();
	private ConcurrentHashMap<MessageType, AtomicInteger> outPacketCounters = new ConcurrentHashMap<>();

	private AtomicInteger inDuplicates = new AtomicInteger();

	private List<ClientError> errors = new ArrayList<>();

	private AtomicInteger finishedComands = new AtomicInteger();
	private AtomicInteger unfinishedCommands = new AtomicInteger();

	public ReportData(String clientID, int totalCommands)
	{
		this.clientID = clientID;
		for (MessageType type : MessageType.values())
		{
			inPacketCounters.put(type, new AtomicInteger(0));
			outPacketCounters.put(type, new AtomicInteger(0));
		}
		unfinishedCommands.set(totalCommands);
	}

	public Report translate()
	{
		return new Report(clientID, retrieveCommandCounters(), inDuplicates.get(), finishedComands.get(), unfinishedCommands.get(), errors);
	}

	public List<Counters> retrieveCommandCounters()
	{
		Map<MessageType, Counters> map = new HashMap<>();
		for (Entry<MessageType, AtomicInteger> entry : inPacketCounters.entrySet())
		{
			if (entry.getValue().get() == 0)
				continue;

			Counters curr = map.get(entry.getKey());
			if (curr == null)
			{
				curr = new Counters(new CommandCounter(entry.getKey(), 0, Direction.INCOMING), new CommandCounter(entry.getKey(), 0, Direction.OUTGOING));
				map.put(entry.getKey(), curr);
			}
			curr.getIn().add(entry.getValue().get());
		}
		for (Entry<MessageType, AtomicInteger> entry : outPacketCounters.entrySet())
		{
			if (entry.getValue().get() == 0)
				continue;

			Counters curr = map.get(entry.getKey());
			if (curr == null)
			{
				curr = new Counters(new CommandCounter(entry.getKey(), 0, Direction.INCOMING), new CommandCounter(entry.getKey(), 0, Direction.OUTGOING));
				map.put(entry.getKey(), curr);
			}
			curr.getOut().add(entry.getValue().get());
		}

		return new ArrayList<>(map.values());
	}

	public void countFinishedCommand()
	{
		finishedComands.incrementAndGet();
		unfinishedCommands.decrementAndGet();
	}

	public int getUnfinishedCommands()
	{
		return unfinishedCommands.get();
	}

	public void countIn(MessageType type)
	{
		inPacketCounters.get(type).incrementAndGet();
	}

	public void countOut(MessageType type)
	{
		outPacketCounters.get(type).incrementAndGet();
	}

	public void countDuplicateIn()
	{
		inDuplicates.incrementAndGet();
	}

	public String getClientID()
	{
		return clientID;
	}

	public ConcurrentHashMap<MessageType, AtomicInteger> getInPacketCounters()
	{
		return inPacketCounters;
	}

	public ConcurrentHashMap<MessageType, AtomicInteger> getOutPacketCounters()
	{
		return outPacketCounters;
	}

	public int getInDuplicates()
	{
		return inDuplicates.get();
	}

	public List<ClientError> getErrors()
	{
		return errors;
	}

	public void error(ErrorType type, String message)
	{
		errors.add(new ClientError(type, message));
	}
}
