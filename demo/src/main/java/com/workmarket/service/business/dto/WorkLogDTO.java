package com.workmarket.service.business.dto;

import javax.validation.constraints.NotNull;

public class WorkLogDTO {
	private Long workLogId;
	@NotNull
	private Long workId;
	@NotNull
	private String description;
	private Integer timeSpent = null;
	@NotNull
	private Boolean completed = Boolean.FALSE;

	public Long getWorkLogId()
	{
		return workLogId;
	}

	public void setWorkLogId(Long workLogId)
	{
		this.workLogId = workLogId;
	}

	public Long getWorkId()
	{
		return workId;
	}

	public void setWorkId(Long workId)
	{
		this.workId = workId;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Integer getTimeSpent()
	{
		return timeSpent;
	}

	public void setTimeSpent(Integer timeSpent)
	{
		this.timeSpent = timeSpent;
	}

	public Boolean getCompleted()
	{
		return completed;
	}

	public void setCompleted(Boolean completed)
	{
		this.completed = completed;
	}
}