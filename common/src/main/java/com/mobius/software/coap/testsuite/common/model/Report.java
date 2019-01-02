package com.mobius.software.coap.testsuite.common.model;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Report
{
	private String clientID;
	private List<Counters> counters;
	private int duplicatesReceived;
	private int finishedCommands;
	private int unfinishedCommands;
	private List<ClientError> errors;
	
	public Report()
	{
	}

	public Report(String clientID, List<Counters> counters, int duplicatesReceived, int finishedCommands, int unfinishedCommands, List<ClientError> errors)
	{
		this.clientID = clientID;
		this.counters = counters;
		this.duplicatesReceived = duplicatesReceived;
		this.finishedCommands = finishedCommands;
		this.unfinishedCommands = unfinishedCommands;
		this.errors = errors;
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
		result = prime * result + ((clientID == null) ? 0 : clientID.hashCode());
		result = prime * result + ((counters == null) ? 0 : counters.hashCode());
		result = prime * result + duplicatesReceived;
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		result = prime * result + finishedCommands;
		result = prime * result + unfinishedCommands;
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
		Report other = (Report) obj;
		if (clientID == null)
		{
			if (other.clientID != null)
				return false;
		}
		else if (!clientID.equals(other.clientID))
			return false;
		if (counters == null)
		{
			if (other.counters != null)
				return false;
		}
		else if (!counters.equals(other.counters))
			return false;
		if (duplicatesReceived != other.duplicatesReceived)
			return false;
		if (errors == null)
		{
			if (other.errors != null)
				return false;
		}
		else if (!errors.equals(other.errors))
			return false;
		if (finishedCommands != other.finishedCommands)
			return false;
		if (unfinishedCommands != other.unfinishedCommands)
			return false;
		return true;
	}

	public String getClientID()
	{
		return clientID;
	}

	public void setClientID(String clientID)
	{
		this.clientID = clientID;
	}

	public List<Counters> getCounters()
	{
		return counters;
	}

	public void setCounters(List<Counters> counters)
	{
		this.counters = counters;
	}

	public int getDuplicatesReceived()
	{
		return duplicatesReceived;
	}

	public void setDuplicatesReceived(int duplicatesReceived)
	{
		this.duplicatesReceived = duplicatesReceived;
	}

	public int getUnfinishedCommands()
	{
		return unfinishedCommands;
	}

	public void setUnfinishedCommands(int unfinishedCommands)
	{
		this.unfinishedCommands = unfinishedCommands;
	}

	public List<ClientError> getErrors()
	{
		return errors;
	}

	public void setErrors(List<ClientError> errors)
	{
		this.errors = errors;
	}

	public int getFinishedCommands()
	{
		return finishedCommands;
	}

	public void setFinishedCommands(int finishedCommands)
	{
		this.finishedCommands = finishedCommands;
	}
}
