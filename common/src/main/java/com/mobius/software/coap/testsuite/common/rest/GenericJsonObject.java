package com.mobius.software.coap.testsuite.common.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@SuppressWarnings("serial")
@XmlRootElement
public abstract class GenericJsonObject implements Serializable
{
	private String status;
	private String message;

	public GenericJsonObject()
	{
		super();
	}

	public GenericJsonObject(String status, String message)
	{
		this.status = status;
		this.message = message;
	}

	public boolean ok()
	{
		return this.status.equals(ResponseData.SUCCESS) && message == null;
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
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		GenericJsonObject other = (GenericJsonObject) obj;
		if (message == null)
		{
			if (other.message != null)
				return false;
		}
		else if (!message.equals(other.message))
			return false;
		if (status == null)
		{
			if (other.status != null)
				return false;
		}
		else if (!status.equals(other.status))
			return false;
		return true;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
}
