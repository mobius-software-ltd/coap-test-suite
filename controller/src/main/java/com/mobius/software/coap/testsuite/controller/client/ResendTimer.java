package com.mobius.software.coap.testsuite.controller.client;

import java.net.SocketAddress;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mobius.software.coap.parser.tlv.CoapMessage;
import com.mobius.software.coap.testsuite.common.model.ErrorType;
import com.mobius.software.coap.testsuite.common.model.MessageType;
import com.mobius.software.coap.testsuite.common.model.ReportData;
import com.mobius.software.coap.testsuite.controller.executor.TaskExecutor;
import com.mobius.software.coap.testsuite.controller.executor.TimedTask;
import com.mobius.software.coap.testsuite.controller.net.NetworkHandlerException;
import com.mobius.software.coap.testsuite.controller.net.UDPClient;

public class ResendTimer implements TimedTask
{
	private CoapMessage message;
	private SocketAddress clientAddress;
	private long interval;
	private ReportData report;

	private long timestamp;

	private AtomicBoolean terminated = new AtomicBoolean();

	public ResendTimer(CoapMessage message, SocketAddress clientAddress, int interval, ReportData report)
	{
		this.message = message;
		this.clientAddress = clientAddress;
		this.interval = interval;
		this.report = report;
		resetTimestamp();
	}

	@Override
	public Boolean execute()
	{
		if (terminated.get())
			return false;

		try
		{
			report.countOut(MessageType.translate(message));
			UDPClient.getInstance().send(clientAddress, message);
			resetTimestamp();
			TaskExecutor.getInstance().schedule(this);
		}
		catch (NetworkHandlerException e)
		{
			report.error(ErrorType.MESSAGE_SEND, "An error occured while sending message");
		}
		return true;
	}

	@Override
	public Long getRealTimestamp()
	{
		return timestamp;
	}

	@Override
	public void stop()
	{
		terminated.compareAndSet(false, true);
	}

	private void resetTimestamp()
	{
		this.timestamp = System.currentTimeMillis() + interval;
	}

	@Override
	public long getDelay(TimeUnit unit)
	{
		long diff = timestamp - System.currentTimeMillis();
		return unit.convert(diff, TimeUnit.MILLISECONDS);
	}

	@Override
	public int compareTo(Delayed o)
	{
		if (o == null)
			return 1;
		if (o == this)
			return 0;
		long diff = getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
		return diff > 0 ? 1 : diff == 0 ? 0 : -1;
	}
}
