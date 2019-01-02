package com.mobius.software.coap.testsuite.controller.net;

public class NetworkHandlerException extends Exception
{
	private static final long serialVersionUID = -7184187116409173612L;

	public NetworkHandlerException(String message)
	{
		super(message);
	}

	public NetworkHandlerException(String message, Throwable e)
	{
		super(message, e);
	}
}
