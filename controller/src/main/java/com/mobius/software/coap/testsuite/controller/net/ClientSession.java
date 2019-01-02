package com.mobius.software.coap.testsuite.controller.net;

import com.mobius.software.coap.testsuite.controller.client.ConnectionListener;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

public class ClientSession
{
	private final Bootstrap bootstrap;
	private final Channel channel;
	private final ConnectionListener listener;

	public ClientSession(Bootstrap bootstrap, Channel channel, ConnectionListener listener)
	{
		this.channel = channel;
		this.listener = listener;
		this.bootstrap = bootstrap;
	}

	public Channel getChannel()
	{
		return channel;
	}

	public ConnectionListener getListener()
	{
		return listener;
	}

	public Bootstrap getBootstrap()
	{
		return bootstrap;
	}
}
