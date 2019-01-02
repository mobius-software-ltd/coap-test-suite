package com.mobius.software.coap.testsuite.common.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ClientError
{
	private ErrorType type;
	private String message;
	private Long timestamp;

	public ClientError()
	{
	}

	public ClientError(ErrorType type, String message)
	{
		this.type = type;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("type:").append(type).append(",");
		sb.append("message:").append(message).append(",");
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timestamp);
		sb.append("time:").append(calendar.getTime());
		return sb.toString();
	}

	public ErrorType getType()
	{
		return type;
	}

	public void setType(ErrorType type)
	{
		this.type = type;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Long timestamp)
	{
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ClientError other = (ClientError) obj;
		if (message == null)
		{
			if (other.message != null)
				return false;
		}
		else if (!message.equals(other.message))
			return false;
		if (timestamp == null)
		{
			if (other.timestamp != null)
				return false;
		}
		else if (!timestamp.equals(other.timestamp))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
