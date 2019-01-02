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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Properties;

public class ControllerRunner
{
	public static String configFile;

	public static void main(String[] args)
	{
		try
		{
			String userDir = System.getProperty("user.dir");
			System.out.println("user.dir: " + userDir);
			setConfigFilePath(userDir + "/config.properties");
			Properties prop = new Properties();
			prop.load(new FileInputStream(configFile));

			String hostname = prop.getProperty("hostname");
			Integer port = Integer.parseInt(prop.getProperty("port"));
			URI baseURI = new URI(null, null, hostname, port, null, null, null);
			JerseyServer server = new JerseyServer(baseURI);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("press any key to stop:\n");
			br.readLine();

			server.terminate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println(e);
		}
		finally
		{
			System.exit(0);
		}
	}

	public static void setConfigFilePath(String path)
	{
		configFile = path;
	}
}
