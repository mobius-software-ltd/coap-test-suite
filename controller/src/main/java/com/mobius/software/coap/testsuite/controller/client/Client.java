package com.mobius.software.coap.testsuite.controller.client;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.mobius.software.coap.parser.message.options.CoapOptionType;
import com.mobius.software.coap.parser.tlv.CoapCode;
import com.mobius.software.coap.parser.tlv.CoapMessage;
import com.mobius.software.coap.parser.tlv.CoapType;
import com.mobius.software.coap.testsuite.common.model.Command;
import com.mobius.software.coap.testsuite.common.model.ErrorType;
import com.mobius.software.coap.testsuite.common.model.MessageType;
import com.mobius.software.coap.testsuite.common.model.ReportData;
import com.mobius.software.coap.testsuite.common.util.CommandParser;
import com.mobius.software.coap.testsuite.controller.Orchestrator;
import com.mobius.software.coap.testsuite.controller.executor.TaskExecutor;
import com.mobius.software.coap.testsuite.controller.executor.TimedTask;
import com.mobius.software.coap.testsuite.controller.net.NetworkHandlerException;
import com.mobius.software.coap.testsuite.controller.net.UDPClient;

public class Client implements TimedTask, ConnectionListener
{
	private final String clientID;
	private final String localHostname;
	private final InetSocketAddress serverAddress;
	private final ConcurrentLinkedQueue<Command> commands;
	private final Orchestrator orchestrator;
	private boolean continueOnError;

	private InetSocketAddress localAddress;
	private ReportData report;
	private AtomicLong timestamp;

	private TokenStorage tokenStorage = new TokenStorage();
	private AtomicReference<Command> pendingCommand = new AtomicReference<>();

	private AtomicBoolean firstMessageSent = new AtomicBoolean();
	private ResendTimer pingTimer;

	private Client(String clientID, String localHostname, InetSocketAddress serverAddress, ConcurrentLinkedQueue<Command> commands, Orchestrator orchestrator, boolean continueOnError)
	{
		this.clientID = clientID;
		this.localHostname = localHostname;
		this.serverAddress = serverAddress;
		this.commands = commands;
		this.report = new ReportData(clientID, commands.size());
		this.orchestrator = orchestrator;
		this.continueOnError = continueOnError;
		this.timestamp = new AtomicLong(System.currentTimeMillis() + orchestrator.getProperties().getInitialDelay() + orchestrator.getProperties().getScenarioDelay());
	}

	@Override
	public Boolean execute()
	{
		Command previousCommand = pendingCommand.getAndSet(null);
		if (previousCommand != null)
		{
			report.error(ErrorType.PREVIOUS_COMMAND_FAILED, "previous failed: clientAddress:" + localAddress);
			if (!continueOnError)
				return false;
		}

		Command currCommand = commands.poll();
		if (currCommand != null)
		{
			try
			{
				if (!currCommand.checkIfIsDisconnect())
				{
					if (this.localAddress == null)
					{
						if (report.getErrors().isEmpty())
						{
							this.localAddress = UDPClient.getInstance().createSession(serverAddress, localHostname, this);
						}
						else
						{
							stop();
							return false;
						}
					}

					CoapMessage message = CommandParser.toMessage(clientID, currCommand);
					sendMessage(message);
					pendingCommand.set(currCommand);
					setNextTimestamp();
				}
				else
				{
					stopPingTimer();
					report.countFinishedCommand();
					pendingCommand.set(null);
					orchestrator.notifyOnComplete(this);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				report.error(ErrorType.MESSAGE_SEND, "an error occured while sending message:" + e.getMessage() + ". clientAddress:" + localAddress);
				if (!continueOnError)
					return false;
			}
		}
		else
			orchestrator.notifyOnComplete(this);

		return !commands.isEmpty();
	}

	private void setNextTimestamp()
	{
		Command next = commands.peek();
		if (next != null)
			timestamp.set(System.currentTimeMillis() + next.getSendTime());
	}

	@Override
	public void packetReceived(CoapMessage message)
	{
		report.countIn(MessageType.translate(message));
		try
		{
			processReceived(message);
		}
		catch (NetworkHandlerException e)
		{
			report.error(ErrorType.MESSAGE_SEND, "an error occure while sending message " + message.getType() + "," + e.getMessage());
		}
	}

	private void processReceived(CoapMessage message) throws NetworkHandlerException
	{
		MessageType messageType = MessageType.translate(message);
		if (messageType != MessageType.INVALID)
		{
			switch (messageType)
			{
			case PUBLISH:
				processReceivedPublish(message);
				break;
			case PINGRESP:
				processReceivedPingresp();
				break;
			default:
				processReceivedMessage(messageType, message);
				break;
			}
		}
		else
			report.error(ErrorType.MESSAGE_RECEIVE, "received invalid message:" + message);
	}

	private void processReceivedPublish(CoapMessage message) throws NetworkHandlerException
	{
		sendMessage(CoapMessage.builder()//
				.code(CoapCode.PUT)//
				.type(CoapType.ACKNOWLEDGEMENT)//
				.token(message.getToken())//
				.messageID(message.getMessageID())//
				.option(CoapOptionType.NODE_ID, message.options().fetchNodeID())//
				.build());
	}

	private void processReceivedMessage(MessageType messageType, CoapMessage message)
	{
		Command pending = pendingCommand.get();
		if (pending != null)
		{
			CoapMessage pendingMessage = CommandParser.toMessage(clientID, pending);
			if (pendingMessage != null)
			{
				MessageType expected = MessageType.translate(pendingMessage).invert();
				if (messageType == expected)
				{
					tokenStorage.release(message.getToken());
					pendingCommand.set(null);
					report.countFinishedCommand();
				}
				else
					report.error(ErrorType.MESSAGE_RECEIVE, "Unexpected received. Invalid type. Expected: " + expected + ". Message:" + message);

				orchestrator.notifyOnComplete(this);
			}
			else
				report.error(ErrorType.MESSAGE_RECEIVE, "Unexpected received. No pending command. Message:" + message);
		}
		else
			report.error(ErrorType.MESSAGE_RECEIVE, "Unexpected received. No pending command. Message:" + message);

	}

	private void processReceivedPingresp()
	{
		Command pending = pendingCommand.get();
		if (pending != null)
		{
			CoapMessage pendingMessage = CommandParser.toMessage(clientID, pending);
			if (pendingMessage != null)
			{
				MessageType expected = MessageType.translate(pendingMessage).invert();
				if (expected == MessageType.PINGRESP)
				{
					pendingCommand.set(null);
					report.countFinishedCommand();
					orchestrator.notifyOnComplete(this);
				}
			}
		}
	}

	private void sendMessage(CoapMessage message) throws NetworkHandlerException
	{
		MessageType messageType = MessageType.translate(message);
		if (messageType != MessageType.PUBACK)
		{
			if (messageType != MessageType.PINGREQ)
				tokenStorage.generateAndSetToken(message);
			else
				message.setMessageID(0);
		}
		UDPClient.getInstance().send(localAddress, message);
		report.countOut(MessageType.translate(message));

		if (firstMessageSent.compareAndSet(false, true))
		{
			pingTimer = new ResendTimer(CoapMessage.builder()//
					.code(CoapCode.PUT)//
					.type(CoapType.CONFIRMABLE)//
					.messageID(0)//
					.option(CoapOptionType.NODE_ID, clientID)//
					.build(), localAddress, 3000, report);
			TaskExecutor.getInstance().schedule(pingTimer);
		}
	}

	@Override
	public Long getRealTimestamp()
	{
		return timestamp.get();
	}

	@Override
	public void stop()
	{
		stopPingTimer();
		if (localAddress != null)
			UDPClient.getInstance().stopSession(localAddress);
	}

	private void stopPingTimer()
	{
		if (pingTimer != null)
		{
			pingTimer.stop();
			pingTimer = null;
		}
	}

	public ReportData getReport()
	{
		return report;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public boolean hasMoreCommands()
	{
		return !commands.isEmpty();
	}

	public static class Builder
	{
		private String clientID;
		private String localHostname;
		private InetSocketAddress serverAddress;
		private ConcurrentLinkedQueue<Command> commands;
		private Orchestrator orchestrator;
		private boolean continueOnError;

		public Builder clientID(String clientID)
		{
			this.clientID = clientID;
			return this;
		}

		public Builder localHostname(String localHostname)
		{
			this.localHostname = localHostname;
			return this;
		}

		public Builder serverAddress(InetSocketAddress serverAddress)
		{
			this.serverAddress = serverAddress;
			return this;
		}

		public Builder commands(ConcurrentLinkedQueue<Command> commands)
		{
			this.commands = commands;
			return this;
		}

		public Builder orchestrator(Orchestrator orchestrator)
		{
			this.orchestrator = orchestrator;
			return this;
		}

		public Builder continueOnError(boolean continueOnError)
		{
			this.continueOnError = continueOnError;
			return this;
		}

		public Client build()
		{
			return new Client(clientID, localHostname, serverAddress, commands, orchestrator, continueOnError);
		}
	}

	@Override
	public long getDelay(TimeUnit unit)
	{
		long diff = timestamp.get() - System.currentTimeMillis();
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
