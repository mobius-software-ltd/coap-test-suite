package com.mobius.software.coap.testsuite.runner;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mobius.software.coap.testsuite.runner.util.TemplateParser;

public class RunnerTests
{
	private static final String MULTI_SUBSCRIBERS_QOS0 = "json/subscribers_qos0.json";
	private static final String MULTI_SUBSCRIBERS_QOS1 = "json/subscribers_qos1.json";

	private static final String MULTI_PUBLISHERS_QOS0 = "json/publishers_qos0.json";
	private static final String MULTI_PUBLISHERS_QOS1 = "json/publishers_qos1.json";

	private static final String PINGERS = "json/pingers.json";

	@BeforeClass
	public static void beforeAll()
	{
		try
		{
			//TemplateParser.initLocal();
			TemplateParser.initRemote();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test1000Subscribers_qos0()
	{
		checkScenario(MULTI_SUBSCRIBERS_QOS0);
	}

	@Test
	public void test1000Subscribers_qos1()
	{
		checkScenario(MULTI_SUBSCRIBERS_QOS1);
	}

	@Test
	public void test1000Publishers_qos0()
	{
		checkScenario(MULTI_PUBLISHERS_QOS0);
	}

	@Test
	public void test1000Publishers_qos1()
	{
		checkScenario(MULTI_PUBLISHERS_QOS1);
	}

	@Test
	public void test100kConnections()
	{
		checkScenario(PINGERS);
	}

	@Test
	public void testAll()
	{
		checkScenario(MULTI_SUBSCRIBERS_QOS0);
		checkScenario(MULTI_SUBSCRIBERS_QOS1);
		checkScenario(MULTI_PUBLISHERS_QOS0);
		checkScenario(MULTI_PUBLISHERS_QOS1);
		//checkScenario(PINGERS);
	}

	private void checkScenario(String filename)
	{
		try
		{
			runScenario(filename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	private TestRunner runScenario(String scenarioFilename) throws InterruptedException
	{
		TestRunner runner = new TestRunner(scenarioFilename);
		runner.start();
		runner.awaitFinished();
		return runner;
	}
}
