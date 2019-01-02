package com.mobius.software.coap.testsuite.runner;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import com.mobius.software.coap.testsuite.common.rest.ControllerRequest;

public class TestRunner extends Thread
{
	private String scenarioFilename;
	private CountDownLatch latch = new CountDownLatch(1);

	public TestRunner(String scenarioFilename)
	{
		this.scenarioFilename = scenarioFilename;
	}

	@Override
	public void run()
	{
		try
		{
			ClassLoader classLoader = getClass().getClassLoader();
			File json = new File(classLoader.getResource(scenarioFilename).getFile());

			ControllerRequest requests = ScenarioRunner.parseRequests(json.getAbsolutePath());
			ScenarioRunner runner = new ScenarioRunner(requests);
			runner.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			latch.countDown();
		}
	}

	public void awaitFinished() throws InterruptedException
	{
		latch.await();
	}
}
