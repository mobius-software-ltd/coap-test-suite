package com.mobius.software.coap.testsuite.controller.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.mobius.software.coap.parser.tlv.CoapMessage;

public class TokenStorage
{
	private static final int MAX_VALUE = 65535;
	private static final int FIRST_ID = 1;

	private ConcurrentHashMap<String, CoapMessage> tokenMessages = new ConcurrentHashMap<>();
	private AtomicInteger counter = new AtomicInteger();

	public void generateAndSetToken(CoapMessage message)
	{
		Integer messageID = null;
		do
		{
			if (tokenMessages.size() == MAX_VALUE)
				throw new IllegalStateException("OUTGOING IDENTIFIER OVERFLOW");

			messageID = counter.incrementAndGet();
			if (counter.compareAndSet(MAX_VALUE, messageID))
				messageID = FIRST_ID;
		}
		while (tokenMessages.putIfAbsent(String.valueOf(messageID), message) != null);

		message.setToken(String.valueOf(messageID).getBytes());
		message.setMessageID(messageID);
	}

	public CoapMessage release(byte[] token)
	{
		return tokenMessages.remove(new String(token));
	}
}
