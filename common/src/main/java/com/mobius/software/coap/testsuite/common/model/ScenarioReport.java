package com.mobius.software.coap.testsuite.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ScenarioReport
{
	private UUID scenarioID;
	private Boolean status;
	private Long startTime;
	private Long finishTime;
	private Integer totalClients;
	private Integer totalCommands;
	private Integer finishedClients;
	private Integer failedClients;
	private Integer finishedCommands;
	private Integer failedCommands;
	private Integer totalErrors = 0;
	private List<Counters> counters;
	private Counter duplicatesIn;
	private Counter duplicatesOut;
	private List<ClientError> errors;

	public ScenarioReport()
	{
	}

	private ScenarioReport(UUID scenarioID, Boolean status, long startTime, long finishTime, int totalClients, int totalCommands, int finishedClients, int failedClients, int finishedCommands, int failedCommands, int totalErrors, List<Counters> counters, Counter duplicatesIn, Counter duplicatesOut, List<ClientError> errors)
	{
		this.scenarioID = scenarioID;
		this.status = status;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.totalClients = totalClients;
		this.totalCommands = totalCommands;
		this.finishedClients = finishedClients;
		this.failedClients = failedClients;
		this.finishedCommands = finishedCommands;
		this.failedCommands = failedCommands;
		this.totalErrors = totalErrors;
		this.counters = counters;
		this.duplicatesIn = duplicatesIn;
		this.duplicatesOut = duplicatesOut;
		this.errors = errors;
	}

	public static ScenarioReport create(UUID scenarioID, long startTime, long finishTime, int totalClients, int totalCommands, List<Report> clientReports)
	{
		Boolean status = calculateStatus(clientReports);
		Integer finishedClients = calculateFinishedClients(clientReports);
		Integer failedClients = calculateFailedClients(clientReports);
		Integer finishedCommands = calculateFinishedCommands(clientReports);
		Integer failedCommands = calculateFailedCommands(clientReports);
		Integer totalErrors = calculateTotalErrors(clientReports);

		List<Counters> counters = calculateCounters(clientReports);
		List<ClientError> errors = calculateErrors(clientReports);

		Counter duplicatesIn = new Counter(0, Direction.INCOMING);
		duplicatesIn.add(calculateDuplicatesIn(clientReports));
		Counter duplicatesOut = new Counter(0, Direction.OUTGOING);

		return new ScenarioReport(scenarioID, status, startTime, finishTime, totalClients, totalCommands, finishedClients, failedClients, finishedCommands, failedCommands, totalErrors, counters, duplicatesIn, duplicatesOut, errors);
	}

	private static List<Counters> calculateCounters(List<Report> clientReports)
	{
		Map<MessageType, Counters> countersMap = new HashMap<>();
		for (Report report : clientReports)
		{
			for (Counters c : report.getCounters())
			{
				MessageType type = c.getIn().getCommand();
				Counters curr = countersMap.get(type);
				if (curr == null)
				{
					curr = c;
					countersMap.put(type, curr);
				}
				else if (c.getIn().getCount() > 0 || c.getOut().getCount() > 0)
				{
					curr.getIn().add(c.getIn().getCount());
					curr.getOut().add(c.getOut().getCount());
				}
			}

		}

		return new ArrayList<>(countersMap.values());
	}

	private static List<ClientError> calculateErrors(List<Report> clientReports)
	{
		List<ClientError> errors = new ArrayList<>();
		for (Report report : clientReports)
			errors.addAll(report.getErrors());
		return errors;
	}

	private static int calculateDuplicatesIn(List<Report> clientReports)
	{
		int duplicates = 0;
		for (Report report : clientReports)
			duplicates += report.getDuplicatesReceived();
		return duplicates;
	}

	private static Boolean calculateStatus(List<Report> clientReports)
	{
		boolean status = true;
		for (Report report : clientReports)
		{
			if (!report.getErrors().isEmpty() || report.getUnfinishedCommands() > 0)
			{
				status = false;
				break;
			}
		}
		return status;
	}

	private static Integer calculateFinishedClients(List<Report> clientReports)
	{
		int count = 0;
		for (Report report : clientReports)
		{
			if (report.getErrors().isEmpty() && report.getUnfinishedCommands() == 0)
				count++;
		}
		return count;
	}

	private static Integer calculateFailedClients(List<Report> clientReports)
	{
		int count = 0;
		for (Report report : clientReports)
		{
			if (!report.getErrors().isEmpty() || report.getUnfinishedCommands() > 0)
				count++;
		}
		return count;
	}

	private static Integer calculateFinishedCommands(List<Report> clientReports)
	{
		int count = 0;
		for (Report report : clientReports)
			count += report.getFinishedCommands();
		return count;
	}

	private static Integer calculateFailedCommands(List<Report> clientReports)
	{
		int count = 0;
		for (Report report : clientReports)
			count += report.getUnfinishedCommands();
		return count;
	}

	private static Integer calculateTotalErrors(List<Report> clientReports)
	{
		int count = 0;
		for (Report report : clientReports)
			count += report.getErrors().size();
		return count;
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
		result = prime * result + ((counters == null) ? 0 : counters.hashCode());
		result = prime * result + ((duplicatesIn == null) ? 0 : duplicatesIn.hashCode());
		result = prime * result + ((duplicatesOut == null) ? 0 : duplicatesOut.hashCode());
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		result = prime * result + ((failedClients == null) ? 0 : failedClients.hashCode());
		result = prime * result + ((failedCommands == null) ? 0 : failedCommands.hashCode());
		result = prime * result + ((finishTime == null) ? 0 : finishTime.hashCode());
		result = prime * result + ((finishedClients == null) ? 0 : finishedClients.hashCode());
		result = prime * result + ((finishedCommands == null) ? 0 : finishedCommands.hashCode());
		result = prime * result + ((scenarioID == null) ? 0 : scenarioID.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((totalClients == null) ? 0 : totalClients.hashCode());
		result = prime * result + ((totalCommands == null) ? 0 : totalCommands.hashCode());
		result = prime * result + ((totalErrors == null) ? 0 : totalErrors.hashCode());
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
		ScenarioReport other = (ScenarioReport) obj;
		if (counters == null)
		{
			if (other.counters != null)
				return false;
		}
		else if (!counters.equals(other.counters))
			return false;
		if (duplicatesIn == null)
		{
			if (other.duplicatesIn != null)
				return false;
		}
		else if (!duplicatesIn.equals(other.duplicatesIn))
			return false;
		if (duplicatesOut == null)
		{
			if (other.duplicatesOut != null)
				return false;
		}
		else if (!duplicatesOut.equals(other.duplicatesOut))
			return false;
		if (errors == null)
		{
			if (other.errors != null)
				return false;
		}
		else if (!errors.equals(other.errors))
			return false;
		if (failedClients == null)
		{
			if (other.failedClients != null)
				return false;
		}
		else if (!failedClients.equals(other.failedClients))
			return false;
		if (failedCommands == null)
		{
			if (other.failedCommands != null)
				return false;
		}
		else if (!failedCommands.equals(other.failedCommands))
			return false;
		if (finishTime == null)
		{
			if (other.finishTime != null)
				return false;
		}
		else if (!finishTime.equals(other.finishTime))
			return false;
		if (finishedClients == null)
		{
			if (other.finishedClients != null)
				return false;
		}
		else if (!finishedClients.equals(other.finishedClients))
			return false;
		if (finishedCommands == null)
		{
			if (other.finishedCommands != null)
				return false;
		}
		else if (!finishedCommands.equals(other.finishedCommands))
			return false;
		if (scenarioID == null)
		{
			if (other.scenarioID != null)
				return false;
		}
		else if (!scenarioID.equals(other.scenarioID))
			return false;
		if (startTime == null)
		{
			if (other.startTime != null)
				return false;
		}
		else if (!startTime.equals(other.startTime))
			return false;
		if (status == null)
		{
			if (other.status != null)
				return false;
		}
		else if (!status.equals(other.status))
			return false;
		if (totalClients == null)
		{
			if (other.totalClients != null)
				return false;
		}
		else if (!totalClients.equals(other.totalClients))
			return false;
		if (totalCommands == null)
		{
			if (other.totalCommands != null)
				return false;
		}
		else if (!totalCommands.equals(other.totalCommands))
			return false;
		if (totalErrors == null)
		{
			if (other.totalErrors != null)
				return false;
		}
		else if (!totalErrors.equals(other.totalErrors))
			return false;
		return true;
	}

	public UUID getScenarioID()
	{
		return scenarioID;
	}

	public void setScenarioID(UUID scenarioID)
	{
		this.scenarioID = scenarioID;
	}

	public Boolean getStatus()
	{
		return status;
	}

	public void setStatus(Boolean status)
	{
		this.status = status;
	}

	public Long getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Long startTime)
	{
		this.startTime = startTime;
	}

	public Long getFinishTime()
	{
		return finishTime;
	}

	public void setFinishTime(Long finishTime)
	{
		this.finishTime = finishTime;
	}

	public Integer getTotalClients()
	{
		return totalClients;
	}

	public void setTotalClients(Integer totalClients)
	{
		this.totalClients = totalClients;
	}

	public Integer getTotalCommands()
	{
		return totalCommands;
	}

	public void setTotalCommands(Integer totalCommands)
	{
		this.totalCommands = totalCommands;
	}

	public Integer getFinishedClients()
	{
		return finishedClients;
	}

	public void setFinishedClients(Integer finishedClients)
	{
		this.finishedClients = finishedClients;
	}

	public Integer getFailedClients()
	{
		return failedClients;
	}

	public void setFailedClients(Integer failedClients)
	{
		this.failedClients = failedClients;
	}

	public Integer getFinishedCommands()
	{
		return finishedCommands;
	}

	public void setFinishedCommands(Integer finishedCommands)
	{
		this.finishedCommands = finishedCommands;
	}

	public Integer getFailedCommands()
	{
		return failedCommands;
	}

	public void setFailedCommands(Integer failedCommands)
	{
		this.failedCommands = failedCommands;
	}

	public Integer getTotalErrors()
	{
		return totalErrors;
	}

	public void setTotalErrors(Integer totalErrors)
	{
		this.totalErrors = totalErrors;
	}

	public List<Counters> getCounters()
	{
		return counters;
	}

	public void setCounters(List<Counters> counters)
	{
		this.counters = counters;
	}

	public Counter getDuplicatesIn()
	{
		return duplicatesIn;
	}

	public void setDuplicatesIn(Counter duplicatesIn)
	{
		this.duplicatesIn = duplicatesIn;
	}

	public Counter getDuplicatesOut()
	{
		return duplicatesOut;
	}

	public void setDuplicatesOut(Counter duplicatesOut)
	{
		this.duplicatesOut = duplicatesOut;
	}

	public List<ClientError> getErrors()
	{
		return errors;
	}

	public void setErrors(List<ClientError> errors)
	{
		this.errors = errors;
	}
}
