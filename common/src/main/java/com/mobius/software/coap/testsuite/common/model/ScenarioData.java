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
import java.util.List;
import java.util.UUID;

public class ScenarioData
{
	private UUID scenarioID;
	private Boolean status;
	private long startTime;
	private long finishTime;
	private int totalClients;
	private int totalCommands;
	private int finishedClients;
	private int failedClients;
	private int finishedCommands;
	private int failedCommands;
	private int totalErrors = 0;
	private List<Counters> counters = new ArrayList<>();
	private Counter duplicatesIn = new Counter(0, Direction.INCOMING);
	private Counter duplicatesOut = new Counter(0, Direction.OUTGOING);

	public ScenarioData(UUID scenarioID, Boolean status, long startTime, long finishTime, int totalClients, int totalCommands, int finishedClients, int failedClients, int finishedCommands, int failedCommands, int totalErrors, List<Counters> counters, Counter duplicatesIn, Counter duplicatesOut)
	{
		this.scenarioID = scenarioID;
		this.status = status;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.totalClients = totalClients;
		this.totalCommands = totalCommands;
		this.finishedClients = finishedClients;
		this.failedClients = failedClients;
		this.finishedCommands = finishedCommands;
		this.failedCommands = failedCommands;
		this.totalErrors = totalErrors;
		this.counters = counters;
		this.duplicatesIn = duplicatesIn;
		this.duplicatesOut = duplicatesOut;
	}

	/*public static ScenarioData translate(Scenario scenario, ReportResponse report)
	{
		UUID scenarioID = scenario.getId();
		int totalClients = scenario.getCount();
		int repeatCount = 1;
		long repeatInterval = 0L;
		Repeat repeat = scenario.getProperties().getRepeat();
		if (repeat != null)
		{
			repeatCount = repeat.getCount();
			repeatInterval = repeat.getInterval();
		}
		int totalCommands = totalClients * CommandParser.retrieveCommands(scenario.getCommands(), repeatCount, repeatInterval).size();

		long startTime = report.getStartTime();
		long finishTime = report.getFinishTime();
		boolean status = true;
		int finishedClients = totalClients;
		int finishedCommands = totalCommands;
		int failedClients = 0;
		int failedCommands = 0;
		int totalErrors = 0;

		Counter duplicatesIn = new Counter(0, Direction.INCOMING);
		Counter duplicatesOut = new Counter(0, Direction.OUTGOING);

		List<Counters> counters = new ArrayList<>();

		Map<SNType, AtomicInteger> incomingCounters = new LinkedHashMap<>();
		Map<SNType, AtomicInteger> outgoingCounters = new LinkedHashMap<>();
		for (SNType type : SNType.values())
		{
			incomingCounters.put(type, new AtomicInteger(0));
			outgoingCounters.put(type, new AtomicInteger(0));
		}

		List<ClientRe> clientReports = report.getReports();
		for (ClientReport clientReport : clientReports)
		{
			boolean clientSuccessStatus = true;
			int unfinishedCommands = clientReport.getUnfinishedCommands();
			if (unfinishedCommands > 0)
			{
				clientSuccessStatus = false;
				finishedCommands -= unfinishedCommands;
				failedCommands += unfinishedCommands;
			}

			if (!clientReport.getErrors().isEmpty())
			{
				totalErrors += clientReport.getErrors().size();
				clientSuccessStatus = false;
			}

			for (CommandCounter counter : clientReport.getCommandCounters())
			{
				switch (counter.getDirection())
				{
				case INCOMING:
					incomingCounters.get(counter.getCommand()).getAndAdd(counter.getCount());
					break;

				case OUTGOING:
					outgoingCounters.get(counter.getCommand()).getAndAdd(counter.getCount());
					break;
				}
			}

			for (Counter counter : clientReport.getDuplicateCounters())
			{
				if (counter.getDirection() == Direction.INCOMING)
					duplicatesIn.setCount(duplicatesIn.getCount() + counter.getCount());
				else
					duplicatesOut.setCount(duplicatesOut.getCount() + counter.getCount());
			}

			if (!clientSuccessStatus)
			{
				status = false;
				finishedClients -= 1;
				failedClients += 1;
			}
		}

		counters = translateCommandCounters(outgoingCounters, incomingCounters);

		return new ScenarioData(scenarioID, status, startTime, finishTime, totalClients, totalCommands, finishedClients, failedClients, finishedCommands, failedCommands, totalErrors, counters, duplicatesIn, duplicatesOut);
	}*/

	/*public ScenarioData parseReport()
	{
		return this;
	}

	private static List<Counters> translateCommandCounters(Map<CommandType, AtomicInteger> out, Map<CommandType, AtomicInteger> in)
	{
		List<Counters> rows = new ArrayList<>();
		Counters row = translateCountersRow(CommandType.CONNECT, CommandType.CONNACK, out, in);
		rows.add(row);
		row = translateCountersRow(CommandType.SUBSCRIBE, CommandType.SUBACK, out, in);
		rows.add(row);
		row = translateCountersRow(CommandType.UNSUBSCRIBE, CommandType.UNSUBACK, out, in);
		rows.add(row);
		row = translateCountersRow(CommandType.PINGREQ, CommandType.PINGRESP, out, in);
		rows.add(row);
		row = translateCountersRow(CommandType.PUBLISH, CommandType.PUBLISH, out, in);
		rows.add(row);
		row = translateCountersRow(CommandType.PUBACK, CommandType.PUBACK, out, in);
		rows.add(row);
		row = translateCountersRow(CommandType.PUBREC, CommandType.PUBREC, out, in);
		rows.add(row);
		row = translateCountersRow(CommandType.PUBREL, CommandType.PUBREL, out, in);
		rows.add(row);
		row = translateCountersRow(CommandType.PUBCOMP, CommandType.PUBCOMP, out, in);
		rows.add(row);
		row = translateCountersRow(CommandType.DISCONNECT, CommandType.DISCONNECT, out, in);
		rows.add(row);
		return rows;
	}

	private static Counters translateCountersRow(CommandType out, CommandType in, Map<CommandType, AtomicInteger> outMap, Map<CommandType, AtomicInteger> inMap)
	{
		Counters row = null;
		AtomicInteger outCount = outMap.remove(out);
		if (outCount != null)
		{
			String outValue = String.valueOf(outCount.get());
			AtomicInteger inCount = inMap.remove(in);
			String inValue = "0";
			if (inCount != null)
				inValue = String.valueOf(inCount.get());
			row = new Counters(new CommandCounter(in, Integer.valueOf(inValue), Direction.INCOMING), new CommandCounter(out, Integer.valueOf(outValue), Direction.OUTGOING));
		}
		else
		{
			AtomicInteger inCount = inMap.remove(out);
			if (inCount != null)
			{
				String inValue = String.valueOf(inCount.get());
				row = new Counters(new CommandCounter(in, Integer.valueOf(inValue), Direction.INCOMING), new CommandCounter(out, 0, Direction.OUTGOING));
			}
		}
		return row;
	}*/

	public UUID getScenarioID()
	{
		return scenarioID;
	}

	public void setScenarioID(UUID scenarioID)
	{
		this.scenarioID = scenarioID;
	}

	public boolean getStatus()
	{
		return status;
	}

	public void setStatus(Boolean status)
	{
		this.status = status;
	}

	public long getStartTime()
	{
		return startTime;
	}

	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}

	public long getFinishTime()
	{
		return finishTime;
	}

	public void setFinishTime(long finishTime)
	{
		this.finishTime = finishTime;
	}

	public int getTotalClients()
	{
		return totalClients;
	}

	public void setTotalClients(int totalClients)
	{
		this.totalClients = totalClients;
	}

	public int getTotalCommands()
	{
		return totalCommands;
	}

	public void setTotalCommands(int totalCommands)
	{
		this.totalCommands = totalCommands;
	}

	public int getFinishedClients()
	{
		return finishedClients;
	}

	public void setFinishedClients(int finishedClients)
	{
		this.finishedClients = finishedClients;
	}

	public int getFailedClients()
	{
		return failedClients;
	}

	public void setFailedClients(int failedClients)
	{
		this.failedClients = failedClients;
	}

	public int getFinishedCommands()
	{
		return finishedCommands;
	}

	public void setFinishedCommands(int finishedCommands)
	{
		this.finishedCommands = finishedCommands;
	}

	public int getFailedCommands()
	{
		return failedCommands;
	}

	public void setFailedCommands(int failedCommands)
	{
		this.failedCommands = failedCommands;
	}

	public List<Counters> getCounters()
	{
		return counters;
	}

	public void setCounters(List<Counters> counters)
	{
		this.counters = counters;
	}

	public Counter getDuplicatesIn()
	{
		return duplicatesIn;
	}

	public void setDuplicatesIn(Counter duplicatesIn)
	{
		this.duplicatesIn = duplicatesIn;
	}

	public Counter getDuplicatesOut()
	{
		return duplicatesOut;
	}

	public void setDuplicatesOut(Counter duplicatesOut)
	{
		this.duplicatesOut = duplicatesOut;
	}

	public int getTotalErrors()
	{
		return totalErrors;
	}

	public void setTotalErrors(int totalErrors)
	{
		this.totalErrors = totalErrors;
	}
}
