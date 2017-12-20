package com.workmarket.service.business.dto;

public class ToolDTO {

    private Long toolId;
	private String name;
	private String description;
	private Long industryId;

	public Long getToolId()
	{
		return toolId;
	}

	public void setToolId(Long toolId)
	{
		this.toolId = toolId;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	public Long getIndustryId() {
		return industryId;
	}
}