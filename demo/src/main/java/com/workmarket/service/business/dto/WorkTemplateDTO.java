package com.workmarket.service.business.dto;

public class WorkTemplateDTO extends WorkDTO {

	private String templateName;
	private String templateDescription;

	public String getTemplateName()
	{
		return templateName;
	}

	public void setTemplateName(String templateName)
	{
		this.templateName = templateName;
	}

	public String getTemplateDescription()
	{
		return templateDescription;
	}

	public void setTemplateDescription(String templateDescription)
	{
		this.templateDescription = templateDescription;
	}
}
