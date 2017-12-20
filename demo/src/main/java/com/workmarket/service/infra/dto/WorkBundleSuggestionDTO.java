package com.workmarket.service.infra.dto;

/**
 * User: micah
 * Date: 8/26/13
 * Time: 4:22 PM
 */
public class WorkBundleSuggestionDTO {
	private Long id;
	private String workNumber;
	private String title;
	private String internalOwner;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInternalOwner() { return internalOwner; }

	public void setInternalOwner(String internalOwner) { this.internalOwner = internalOwner; }
}
