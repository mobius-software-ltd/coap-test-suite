package com.mobius.software.coap.testsuite.runner.executor;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mobius.software.coap.testsuite.common.model.Scenario;
import com.mobius.software.coap.testsuite.common.model.ScenarioContext;
import com.mobius.software.coap.testsuite.common.model.ScenarioReport;
import com.mobius.software.coap.testsuite.common.model.ScenarioRequest;
import com.mobius.software.coap.testsuite.common.rest.ControllerRequest;
import com.mobius.software.coap.testsuite.common.rest.GenericRequest;
import com.mobius.software.coap.testsuite.common.rest.GenericResponse;
import com.mobius.software.coap.testsuite.common.rest.ReportResponse;
import com.mobius.software.coap.testsuite.common.util.Requester;


public class RequestExecutor
{
	private static final Log logger = LogFactory.getLog(RequestExecutor.class);

	public GenericFuture<ScenarioContext> execute(ControllerRequest requestData)
	{
		if (!requestData.validate())
			throw new IllegalArgumentException("invalid controller request!");

		final GenericFuture<ScenarioContext> future = new GenericFuture<>(requestData.getData().size());
		ExecutorService service = Executors.newFixedThreadPool(requestData.getData().size());
		for (ScenarioRequest scenarioRequest : requestData.getData())
		{
			service.submit(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						String scenarioURL = scenarioRequest.getUrl() + "/scenario";
						GenericRequest<Scenario> request = new GenericRequest<>(scenarioRequest.getScenario());
						@SuppressWarnings("unchecked")
						GenericResponse<String> response = Requester.post(GenericResponse.class, request, scenarioURL);
						if (response.ok())
						{
							UUID id = UUID.fromString(response.getData());
							scenarioRequest.getScenario().setId(id);
							ScenarioContext ctx = new ScenarioContext(scenarioRequest.getUrl(), id);
							future.addResponse(ctx);
						}
						else
							logger.error("controller returned error " + response);
					}
					catch (Exception e)
					{
						logger.error(e.getMessage(), e);
					}
				}
			});
		}

		try
		{
			service.shutdownNow();
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}

		return future;
	}

	public GenericFuture<ScenarioReport> fetchReport(List<ScenarioContext> contexts)
	{
		final GenericFuture<ScenarioReport> future = new GenericFuture<>(contexts.size());
		ExecutorService service = Executors.newFixedThreadPool(contexts.size());
		for (ScenarioContext requestContext : contexts)
		{
			service.submit(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						String url = requestContext.getBaseURL() + "/report";
						GenericRequest<String> request = new GenericRequest<>(requestContext.getScenarioID().toString());
						ReportResponse response = Requester.report(request, url);
						if (response.ok())
							future.addResponse(response.getData().get(0));
						else
							logger.error("controller returned error " + response);
					}
					catch (Exception e)
					{
						logger.error(e.getMessage(), e);
					}
				}
			});
		}

		try
		{
			service.shutdownNow();
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}

		return future;
	}
}
