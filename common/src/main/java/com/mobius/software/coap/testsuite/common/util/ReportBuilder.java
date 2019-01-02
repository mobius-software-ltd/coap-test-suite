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

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import com.mobius.software.coap.testsuite.common.model.Counters;
import com.mobius.software.coap.testsuite.common.model.ScenarioReport;
import com.mobius.software.coap.testsuite.common.util.table.Cell;
import com.mobius.software.coap.testsuite.common.util.table.TableBuilder;

import CountersComparator.CountersComparator;

public class ReportBuilder
{
	private static final CountersComparator COUNTERS_COMPARATOR = new CountersComparator();
	
	private static final int TABLE_WIDTH = 100;

	private static final String ID = "Scenario-ID:";
	private static final String RESULT_SUCCESS = "Result: SUCCESS";
	private static final String RESULT_FAILED = "Result: FAILED";
	private static final String START_TIME = "Start Time";
	private static final String FINISH_TIME = "Finish Time";
	private static final String CURRENT_TIME = "Current Time";
	private static final String TOTAL_CLIENTS = "Total clients";
	private static final String TOTAL_COMMANDS = "Total commands";
	private static final String TOTAL_ERRORS = "Errors occured";
	private static final String SUCCESSFULY_FINISHED = "Successfuly finished";
	private static final String FAILED = "Failed ";
	private static final String DUPLICATES_IN = "Duplicates received";
	private static final String DUPLICATES_OUT = "Duplicates sent";
	private static final String OUTGOING_COUNTERS = "Outgoing counters";
	private static final String INCOMING_COUNTERS = "Incoming counters";
	private static final String COUNTER_NAME = "Counter Name";
	private static final String COUNTER_VALUE = "Counter Value";

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	public static String buildSummary(ScenarioReport data)
	{
		long currTime = System.currentTimeMillis();
		TableBuilder builder = new TableBuilder().width(TABLE_WIDTH);

		builder.addHeader(
				Cell.center(ID)
				.addValue(0, data.getScenarioID())
				.addValue(10, data.getStatus() ? RESULT_SUCCESS : RESULT_FAILED));

		builder.addRow(
				Cell.left(START_TIME), 
				Cell.left(timestampToDateTime(data.getStartTime())), 
				Cell.left(data.getStartTime()));
		builder.addRow(
				Cell.left(FINISH_TIME), 
				Cell.left(timestampToDateTime(data.getFinishTime())), 
				Cell.left(data.getFinishTime()));
		builder.addRow(
				Cell.left(CURRENT_TIME), 
				Cell.left(timestampToDateTime(currTime)), 
				Cell.left(currTime));
		builder.addFooter(3);

		builder.addRow(
				Cell.left(TOTAL_CLIENTS).addLast(data.getTotalClients()), 
				Cell.left(TOTAL_COMMANDS).addLast(data.getTotalCommands()),
				Cell.left(TOTAL_ERRORS).addLast(data.getTotalErrors()));
		builder.addRow(
				Cell.left(SUCCESSFULY_FINISHED).addLast(data.getFinishedClients()),
				Cell.left(SUCCESSFULY_FINISHED).addLast(data.getFinishedCommands()),
				Cell.left(DUPLICATES_IN).addLast(data.getDuplicatesIn().getCount()));
		builder.addRow(
				Cell.left(FAILED).addLast(data.getFailedClients()), 
				Cell.left(FAILED).addLast(data.getFailedCommands()), 
				Cell.left(DUPLICATES_OUT).addLast(data.getDuplicatesOut().getCount()));

		builder.addHeader(
				Cell.center(OUTGOING_COUNTERS), 
				Cell.center(INCOMING_COUNTERS));
		builder.addRow(
				Cell.center(COUNTER_NAME), 
				Cell.center(COUNTER_VALUE), 
				Cell.center(COUNTER_NAME), 
				Cell.center(COUNTER_VALUE));
		Collections.sort(data.getCounters(), COUNTERS_COMPARATOR);
		for (Counters counters : data.getCounters())
		{
			builder.addRow(Cell.center(counters.getOut().getCommand()), 
					Cell.center(counters.getOut().getCount()), 
					Cell.center(counters.getIn().getCommand()), 
					Cell.center(counters.getIn().getCount()));
		}
		builder.addFooter(4);

		return builder.build();
	}

	private static String timestampToDateTime(long timestamp)
	{
		SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_TIME_FORMAT);
		Date now = new Date(timestamp);
		String strDate = sdfDate.format(now);
		return strDate;
	}
}
