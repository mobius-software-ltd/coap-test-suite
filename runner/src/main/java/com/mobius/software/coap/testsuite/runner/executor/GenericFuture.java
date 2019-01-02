package com.mobius.software.coap.testsuite.runner.executor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GenericFuture<T> implements Future<List<T>>
{
	private final CountDownLatch latch;
	private CopyOnWriteArrayList<T> result = new CopyOnWriteArrayList<>();

	private int totalCount;
	private AtomicInteger responseCount = new AtomicInteger(0);
	private AtomicBoolean isCanceled = new AtomicBoolean(false);

	public GenericFuture(int totalCount)
	{
		this.totalCount = totalCount;
		this.latch = new CountDownLatch(totalCount);
	}

	public void addResponse(T response)
	{
		if (isCancelled())
			return;

		if (response != null)
			result.add(response);

		responseCount.incrementAndGet();

		latch.countDown();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning)
	{
		return isCanceled.compareAndSet(false, true);
	}

	@Override
	public boolean isCancelled()
	{
		return isCanceled.get();
	}

	@Override
	public boolean isDone()
	{
		return totalCount == responseCount.get();
	}

	@Override
	public List<T> get() throws InterruptedException, ExecutionException
	{
		latch.await();
		return result;
	}

	@Override
	public List<T> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
	{
		latch.await(timeout, unit);
		return result;
	}
}
