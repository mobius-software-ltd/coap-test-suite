package com.mobius.software.coap.testsuite.controller.net;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

import com.mobius.software.coap.parser.CoapParser;
import com.mobius.software.coap.parser.tlv.CoapMessage;
import com.mobius.software.coap.testsuite.controller.client.ConnectionListener;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;

public class UDPClient
{
	private static UDPClient INSTANCE;

	private EventLoopGroup group;
	private ConcurrentHashMap<SocketAddress, ClientSession> sessions = new ConcurrentHashMap<>();
	private ConcurrentSkipListMap<Integer, Boolean> usedPorts = new ConcurrentSkipListMap<>();

	private Bootstrap bootstrap = new Bootstrap();

	public static UDPClient reInit(int workerThreads) throws InterruptedException
	{
		if (INSTANCE != null)
			INSTANCE.terminate();
		INSTANCE = new UDPClient(workerThreads);
		return INSTANCE;
	}

	public static UDPClient getInstance()
	{
		return INSTANCE;
	}

	public UDPClient(int workerThreads) throws InterruptedException
	{
		group = new EpollEventLoopGroup(workerThreads);
		bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
		bootstrap.channel(EpollDatagramChannel.class);
		bootstrap.group(group);
		bootstrap.handler(new ClientHandler(sessions));
	}

	public InetSocketAddress createSession(InetSocketAddress serverAddress, String localHostname, ConnectionListener listener) throws InterruptedException
	{
		InetSocketAddress localAddress = null;
		int port = 0;
		do
		{
			port = !usedPorts.isEmpty() ? usedPorts.lastKey() + 1 : 10000;
			if (port == 65536)
				throw new IllegalStateException("port number reached limit!");
			localAddress = new InetSocketAddress(localHostname, port);
		}
		while (usedPorts.putIfAbsent(port, true) != null);

		Channel channel = bootstrap.connect(serverAddress, localAddress).sync().channel();
		ClientSession session = new ClientSession(bootstrap, channel, listener);
		sessions.put(localAddress, session);

		return localAddress;
	}

	public void send(SocketAddress address, CoapMessage message) throws NetworkHandlerException
	{
		ClientSession session = sessions.get(address);
		if (session == null)
			throw new NetworkHandlerException("no client session!");
		ByteBuf buf = CoapParser.encode(message);
		DatagramPacket packet = new DatagramPacket(buf, (InetSocketAddress) session.getChannel().remoteAddress());
		session.getChannel().writeAndFlush(packet);
	}

	public void terminate()
	{
		if (group != null)
		{
			group.shutdownGracefully(0, 100, TimeUnit.MILLISECONDS);
			group = null;
		}
		clearSessions();
		usedPorts.clear();
	}

	private void clearSessions()
	{
		Iterator<Entry<SocketAddress, ClientSession>> iterator = sessions.entrySet().iterator();
		while (iterator.hasNext())
		{
			stopSession(iterator.next().getKey());
			iterator.remove();
		}
	}

	public void stopSession(SocketAddress address)
	{
		ClientSession session = sessions.remove(address);
		if (session != null)
		{
			usedPorts.remove(((InetSocketAddress) session.getChannel().localAddress()).getPort());
			session.getChannel().close();
		}
	}
}
