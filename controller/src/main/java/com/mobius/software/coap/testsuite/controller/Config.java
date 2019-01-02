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

import java.util.Properties;

public class Config
{
	private static final String WORKERS = "workers";
	private static final String TIMERS_INTERVAL = "timersInterval";
	private static final String INITIAL_DELAY = "delay";
	private static final String LOCAL_HOSTNAME = "localHostname";

	private Integer workers;
	private Integer timersInterval;
	private Integer initialDelay;
	private String localHostname;

	public Config(Integer workers, Integer timersInterval, Integer initialDelay, String localHostname)
	{
		this.workers = workers;
		this.timersInterval = timersInterval;
		this.initialDelay = initialDelay;
		this.localHostname = localHostname;
	}

	private static Config INSTANCE;

	public static Config getInstance()
	{
		return INSTANCE;
	}

	public static Config parse(Properties properties)
	{
		try
		{
			Integer workers = Integer.parseInt(properties.getProperty(WORKERS));
			Integer timersInterval = Integer.parseInt(properties.getProperty(TIMERS_INTERVAL));
			Integer initialDelay = Integer.parseInt(properties.getProperty(INITIAL_DELAY));
			String localHostname = properties.getProperty(LOCAL_HOSTNAME);
			INSTANCE = new Config(workers, timersInterval, initialDelay, localHostname);
			return INSTANCE;
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException("invalid arguments format: " + e.getMessage());
		}
	}

	public Integer getWorkers()
	{
		return workers;
	}

	public void setWorkers(Integer workers)
	{
		this.workers = workers;
	}

	public Integer getTimersInterval()
	{
		return timersInterval;
	}

	public void setTimersInterval(Integer timersInterval)
	{
		this.timersInterval = timersInterval;
	}

	public Integer getInitialDelay()
	{
		return initialDelay;
	}

	public void setInitialDelay(Integer initialDelay)
	{
		this.initialDelay = initialDelay;
	}

	public String getLocalHostname()
	{
		return localHostname;
	}

	public void setLocalHostname(String localHostname)
	{
		this.localHostname = localHostname;
	}

}
