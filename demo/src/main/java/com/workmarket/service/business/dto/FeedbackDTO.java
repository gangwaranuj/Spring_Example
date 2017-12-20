package com.workmarket.service.business.dto;

import com.workmarket.web.forms.work.WorkAssetForm;
import com.workmarket.domains.model.feedback.FeedbackPriority;
import com.workmarket.domains.model.feedback.FeedbackConcern;

import java.io.Serializable;
import java.util.List;

public class FeedbackDTO implements Serializable {

	private static final long serialVersionUID = -7856148696973186095L;
	private String title;
	private String type;
	private String description;

	private FeedbackConcern concern;
	private FeedbackPriority priority;

	private Long companyId;
	private Long userId;
	private List<WorkAssetForm> attachments;
	private String userAgent;

	public FeedbackDTO(){}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public FeedbackConcern getConcern() {
		return concern;
	}

	public void setConcern(FeedbackConcern concerns) {
		this.concern = concerns;
	}

	public FeedbackPriority getPriority() {
		return priority;
	}

	public void setPriority(FeedbackPriority priority) {
		this.priority = priority;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<WorkAssetForm> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<WorkAssetForm> attachments) {
		this.attachments = attachments;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

}