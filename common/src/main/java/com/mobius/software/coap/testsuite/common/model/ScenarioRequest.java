package com.mobius.software.coap.testsuite.common.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ScenarioRequest
{
	private String url;
	private Scenario scenario;

	public ScenarioRequest()
	{
	}

	public ScenarioRequest(String url, Scenario scenario)
	{
		this.url = url;
		this.scenario = scenario;
	}

	public boolean validate()
	{
		if (url == null || url.isEmpty())
			return false;
		if (scenario == null || !scenario.validate())
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((scenario == null) ? 0 : scenario.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScenarioRequest other = (ScenarioRequest) obj;
		if (scenario == null)
		{
			if (other.scenario != null)
				return false;
		}
		else if (!scenario.equals(other.scenario))
			return false;
		if (url == null)
		{
			if (other.url != null)
				return false;
		}
		else if (!url.equals(other.url))
			return false;
		return true;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public Scenario getScenario()
	{
		return scenario;
	}

	public void setScenario(Scenario scenario)
	{
		this.scenario = scenario;
	}

}
