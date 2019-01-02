package com.mobius.software.coap.testsuite.common.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Repeat
{
	private Long interval;
	private Integer count;

	public Repeat()
	{

	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
	}
	
	public Long getInterval()
	{
		return interval;
	}

	public void setInterval(Long interval)
	{
		this.interval = interval;
	}

	public Integer getCount()
	{
		return count;
	}

	public void setCount(Integer count)
	{
		this.count = count;
	}

	public boolean validate()
	{
		return interval != null && interval >= 0 && count != null && count >= 0;
	}
}
