package com.mobius.software.coap.testsuite.common.rest;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@SuppressWarnings("serial")
public class GenericResponse<T> extends GenericJsonObject
{
	private T data;

	public GenericResponse()
	{
		super();
	}

	public GenericResponse(String status, String message, T data)
	{
		super(status, message);
		this.data = data;
	}

	public static <T> GenericResponse<T> success(T data)
	{
		return new GenericResponse<>(ResponseData.SUCCESS, null, data);
	}

	public static GenericJsonObject error(String message)
	{
		return new GenericResponse<>(ResponseData.ERROR, message, null);
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
		int result = super.hashCode();
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		GenericResponse other = (GenericResponse) obj;
		if (data == null)
		{
			if (other.data != null)
				return false;
		}
		else if (!data.equals(other.data))
			return false;
		return true;
	}

	public T getData()
	{
		return data;
	}

	public void setData(T data)
	{
		this.data = data;
	}
}
