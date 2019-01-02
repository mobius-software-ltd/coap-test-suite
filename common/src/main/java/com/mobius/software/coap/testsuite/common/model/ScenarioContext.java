package com.mobius.software.coap.testsuite.common.model;

import java.util.UUID;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ScenarioContext
{
	private final String baseURL;
	private final UUID scenarioID;

	public ScenarioContext(String baseURL, UUID scenarioID)
	{
		this.baseURL = baseURL;
		this.scenarioID = scenarioID;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
	}
	
	public String getBaseURL()
	{
		return baseURL;
	}
	public UUID getScenarioID()
	{
		return scenarioID;
	}
}
