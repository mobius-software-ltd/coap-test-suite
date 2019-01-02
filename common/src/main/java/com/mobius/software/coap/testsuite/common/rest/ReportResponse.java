package com.mobius.software.coap.testsuite.common.rest;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.mobius.software.coap.testsuite.common.model.ScenarioReport;

@SuppressWarnings("serial")
public class ReportResponse extends GenericJsonObject
{
	private List<ScenarioReport> data;

	public ReportResponse()
	{
	}

	public ReportResponse(List<ScenarioReport> data)
	{
		super(ResponseData.SUCCESS, null);
		this.data = data;
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
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		ReportResponse other = (ReportResponse) obj;
		if (data == null)
		{
			if (other.data != null)
				return false;
		}
		else if (!data.equals(other.data))
			return false;
		return true;
	}

	public List<ScenarioReport> getData()
	{
		return data;
	}

	public void setData(List<ScenarioReport> data)
	{
		this.data = data;
	}

}
