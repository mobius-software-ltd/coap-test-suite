package com.mobius.software.coap.testsuite.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mobius.software.coap.testsuite.common.model.Command;
import com.mobius.software.coap.testsuite.common.model.Repeat;
import com.mobius.software.coap.testsuite.common.model.Scenario;
import com.mobius.software.coap.testsuite.common.model.ScenarioReport;
import com.mobius.software.coap.testsuite.common.rest.GenericJsonObject;
import com.mobius.software.coap.testsuite.common.rest.GenericRequest;
import com.mobius.software.coap.testsuite.common.rest.GenericResponse;
import com.mobius.software.coap.testsuite.common.rest.ReportResponse;
import com.mobius.software.coap.testsuite.common.rest.ResponseData;
import com.mobius.software.coap.testsuite.common.util.CommandParser;
import com.mobius.software.coap.testsuite.common.util.IdentifierParser;
import com.mobius.software.coap.testsuite.controller.client.Client;
import com.mobius.software.coap.testsuite.controller.executor.TaskExecutor;
import com.mobius.software.coap.testsuite.controller.net.UDPClient;

@Path("controller")
@Singleton
public class Controller
{
	private IdentifierStorage identifierStorage = new IdentifierStorage();
	private ConcurrentHashMap<UUID, Orchestrator> scenarioMap = new ConcurrentHashMap<>();

	public Controller() throws Exception
	{
		start();
	}

	public void start() throws Exception
	{
		initConfig();
		initTaskExecutor();
		initNetworkHandler();
	}

	private Config initConfig() throws IOException
	{
		Properties properties = new Properties();
		properties.load(new FileInputStream(ControllerRunner.configFile));
		return Config.parse(properties);
	}

	private TaskExecutor initTaskExecutor()
	{
		return TaskExecutor.getInstance();
	}

	private void initNetworkHandler() throws InterruptedException
	{
		UDPClient.reInit(Config.getInstance().getWorkers());
	}

	@POST
	@Path("scenario")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericJsonObject scenario(GenericRequest<Scenario> json)
	{
		if (json == null || json.getData() == null || !json.getData().validate())
			return GenericResponse.error(ResponseData.INVALID_PARAMETERS);

		Scenario scenario = json.getData();
		try
		{
			initNetworkHandler();

			UUID scenarioID = UUID.randomUUID();
			List<Client> clientList = new ArrayList<>();
			OrchestratorProperties properties = OrchestratorProperties.fromScenarioProperties(scenarioID, scenario.getProperties(), scenario.getThreshold(), scenario.getStartThreshold(), scenario.getContinueOnError(), Config.getInstance().getInitialDelay());
			InetSocketAddress serverAddress = new InetSocketAddress(properties.getServerHostname(), properties.getServerPort());
			Orchestrator orchestrator = new Orchestrator(scenarioID, properties);
			Repeat repeat = scenario.getProperties().getRepeat();
			int totalCommads = 0;
			for (int i = 0; i < scenario.getCount(); i++)
			{
				int identityCounter = identifierStorage.countIdentity(properties.getIdentifierRegex(), properties.getStartIdentifier());
				String clientID = IdentifierParser.parseIdentifier(properties.getIdentifierRegex(), properties.getServerHostname(), identityCounter);

				int repeatCount = 1;
				long repeatInterval = 0L;
				if (repeat != null)
				{
					repeatCount = repeat.getCount();
					repeatInterval = repeat.getInterval();
				}
				ConcurrentLinkedQueue<Command> commands = CommandParser.retrieveCommands(scenario.getCommands(), repeatCount, repeatInterval);
				totalCommads += commands.size();

				Client client = Client.builder()//
						.clientID(clientID)//
						.serverAddress(serverAddress)//
						.commands(commands)//
						.orchestrator(orchestrator)//
						.continueOnError(properties.isContinueOnError())//
						.localHostname(Config.getInstance().getLocalHostname())//
						.build();
				clientList.add(client);
			}
			orchestrator.start(clientList, totalCommads);
			scenarioMap.put(scenarioID, orchestrator);
			return GenericResponse.success(scenarioID);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return GenericResponse.error("scenario start error, " + e.getMessage());
		}
	}

	@POST
	@Path("report")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericJsonObject report(GenericRequest<String> json)
	{
		if (json == null || json.getData() == null)
			return GenericResponse.error(ResponseData.INVALID_PARAMETERS);

		try
		{
			UUID id = UUID.fromString(json.getData());

			Orchestrator orchestrator = scenarioMap.remove(id);
			if (orchestrator == null)
				return GenericResponse.error(ResponseData.NOT_FOUND);

			ScenarioReport report = orchestrator.report();

			orchestrator.terminate();
			UDPClient.getInstance().terminate();

			return new ReportResponse(Arrays.asList(report));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return GenericResponse.error(e.getMessage());
		}
	}

	@POST
	@Path("clear")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericJsonObject clear(GenericRequest<UUID> json)
	{
		if (json == null || json.getData() == null)
			return GenericResponse.error(ResponseData.INVALID_PARAMETERS);

		Orchestrator orchestrator = scenarioMap.get(json.getData());
		if (orchestrator == null)
			return GenericResponse.error(ResponseData.NOT_FOUND);

		orchestrator.terminate();
		UDPClient.getInstance().terminate();

		return GenericResponse.success(json.getData());
	}

	public void stop()
	{
		TaskExecutor.getInstance().terminate();
	}
}
