package com.mobius.software.coap.testsuite.controller.executor;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mobius.software.coap.testsuite.controller.Config;

public class TaskExecutor
{
	private static final int TERMINATION_TIMEOUT = 2000;
	private static final Log logger = LogFactory.getLog(TaskExecutor.class);

	private ExecutorService service;
	private DelayQueue<TimedTask> mainQueue;

	private AtomicBoolean isStopping;

	private static TaskExecutor INSTANCE;

	private TaskExecutor(ExecutorService service, DelayQueue<TimedTask> mainQueue, AtomicBoolean isStopping)
	{
		this.service = service;
		this.mainQueue = mainQueue;
		this.isStopping = isStopping;
	}

	public static TaskExecutor getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = init();
		return INSTANCE;
	}

	private static TaskExecutor init()
	{
		Config config = Config.getInstance();
		if (config == null)
			throw new IllegalStateException("app config was not initialized!");

		final AtomicBoolean isStopping = new AtomicBoolean(false);
		final DelayQueue<TimedTask> mainQueue = new DelayQueue<>();

		ExecutorService service = Executors.newFixedThreadPool(config.getWorkers());
		for (int i = 0; i < config.getWorkers(); i++)
		{
			service.submit(new Runnable()
			{
				@Override
				public void run()
				{
					while (!isStopping.get())
					{
						try
						{
							TimedTask timer = mainQueue.poll(100L, TimeUnit.MILLISECONDS);
							if (timer != null)
							{
								timer.execute();
							}
						}
						catch (Exception ex)
						{
							logger.error("An error occured in worker thread while processing task:" + ex.getMessage(), ex);
						}
					}
				}
			});
		}

		return new TaskExecutor(service, mainQueue, isStopping);
	}

	public void schedule(TimedTask task)
	{
		mainQueue.offer(task);
	}

	public void terminate()
	{
		isStopping.set(true);
		stopExecutor(this.service);
		mainQueue.clear();
		mainQueue = null;
		INSTANCE = null;
	}

	private void stopExecutor(ExecutorService service)
	{
		if (service != null)
		{
			service.shutdown();
			service.shutdownNow();
			try
			{
				service.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				logger.error(e.getMessage(), e);
			}
			service = null;
		}
	}
}
