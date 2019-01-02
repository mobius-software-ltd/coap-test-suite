package com.mobius.software.coap.testsuite.common.util;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.mobius.software.coap.testsuite.common.rest.GenericRequest;
import com.mobius.software.coap.testsuite.common.rest.ReportResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class Requester
{
	public static <REQ, RESP> RESP post(Class<RESP> responseClazz, REQ request, String url)
	{
		ClientConfig config = new DefaultClientConfig();
		config.getClasses().add(JacksonJsonProvider.class);
		Client client = Client.create(new DefaultClientConfig());
		try
		{
			WebResource.Builder builder = client.resource(url).getRequestBuilder();
			builder.type(MediaType.APPLICATION_JSON);
			builder.accept(MediaType.APPLICATION_JSON);
			return builder.post(responseClazz, request);
		}
		finally
		{
			client.destroy();
		}
	}
	
	public static ReportResponse report(GenericRequest<String> request, String url)
	{
		ClientConfig config = new DefaultClientConfig();
		config.getClasses().add(JacksonJsonProvider.class);
		Client client = Client.create(new DefaultClientConfig());
		try
		{
			WebResource.Builder builder = client.resource(url).getRequestBuilder();
			builder.type(MediaType.APPLICATION_JSON);
			builder.accept(MediaType.APPLICATION_JSON);
			return builder.post(ReportResponse.class, request);
		}
		finally
		{
			client.destroy();
		}
	}
}
