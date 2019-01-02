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

package com.mobius.software.coap.testsuite.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import com.mobius.software.coap.testsuite.common.model.Report;
import com.mobius.software.coap.testsuite.common.model.ReportData;
import com.mobius.software.coap.testsuite.common.model.ScenarioReport;
import com.mobius.software.coap.testsuite.controller.client.Client;
import com.mobius.software.coap.testsuite.controller.executor.TaskExecutor;

public class Orchestrator
{
	private static final TaskExecutor taskExecutor = TaskExecutor.getInstance();

	private final UUID id;
	private OrchestratorProperties properties;
	private List<Client> clientList;
	private int totalCommads;

	private AtomicInteger pendingCount = new AtomicInteger(0);
	private ConcurrentLinkedDeque<Client> pendingQueue = new ConcurrentLinkedDeque<>();
	private long startTime;
	private long finishTime;

	public Orchestrator(UUID id, OrchestratorProperties properties)
	{
		this.id = id;
		this.properties = properties;
	}

	public void start(List<Client> clientList, int totalCommads)
	{
		this.clientList = clientList;
		this.totalCommads = totalCommads;
		this.startTime = System.currentTimeMillis() + properties.getScenarioDelay();
		this.pendingQueue.addAll(clientList);
		while (scheduleNext())
			;
	}

	private boolean scheduleNext()
	{
		boolean scheduled = false;
		int oldStarting = pendingCount.get();
		int newStarting = pendingCount.updateAndGet(v -> !pendingQueue.isEmpty() && pendingCount.get() >= properties.getThreashold() ? pendingCount.get() : pendingCount.get() + 1);
		if (newStarting > oldStarting)
		{
			Client nextClient = pendingQueue.poll();
			if (nextClient != null)
			{
				scheduled = true;
				taskExecutor.schedule(nextClient);
			}
			else
				pendingCount.decrementAndGet();
		}
		return scheduled;
	}

	public void notifyOnComplete(Client client)
	{
		pendingCount.decrementAndGet();

		if (client.hasMoreCommands())
			pendingQueue.offer(client);

		if (!pendingQueue.isEmpty())
			scheduleNext();

		if (pendingQueue.isEmpty() && pendingCount.get() == 0)
			this.finishTime = System.currentTimeMillis();
	}

	public void terminate()
	{
		for (Client client : clientList)
			client.stop();
	}

	public ScenarioReport report()
	{
		List<Report> reports = new ArrayList<>();
		for (Client client : clientList)
		{
			ReportData reportData = client.getReport();
			reports.add(reportData.translate());
		}
		return ScenarioReport.create(id, startTime, finishTime, clientList.size(), totalCommads, reports);
	}

	public OrchestratorProperties getProperties()
	{
		return properties;
	}
}
