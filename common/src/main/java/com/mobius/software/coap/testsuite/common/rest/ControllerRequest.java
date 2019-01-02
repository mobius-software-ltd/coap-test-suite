package com.mobius.software.coap.testsuite.common.rest;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.mobius.software.coap.testsuite.common.model.ScenarioRequest;

@SuppressWarnings("serial")
public class ControllerRequest extends GenericRequest<List<ScenarioRequest>>
{
	private Long requestTimeout;

	public ControllerRequest()
	{
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
	}

	public boolean validate()
	{
		if (getData() == null || getData().isEmpty())
			return false;

		if (requestTimeout == null || requestTimeout < 1)
			return false;

		for (ScenarioRequest request : getData())
		{
			if (!request.validate())
				return false;
		}

		return true;
	}

	public Long getRequestTimeout()
	{
		return requestTimeout;
	}

	public void setRequestTimeout(Long requestTimeout)
	{
		this.requestTimeout = requestTimeout;
	}

}
