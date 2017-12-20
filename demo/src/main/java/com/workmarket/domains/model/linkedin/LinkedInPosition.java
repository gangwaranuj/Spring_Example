package com.workmarket.domains.model.linkedin;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.code.linkedinapi.schema.Company;
import com.google.code.linkedinapi.schema.EndDate;
import com.google.code.linkedinapi.schema.StartDate;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="linkedInPosition")
@Table(name="linkedin_position")
@AuditChanges
public class LinkedInPosition extends DeletableEntity {

	private static final long serialVersionUID = 643298516274100524L;

	private LinkedInPerson linkedInPerson;
	private String title;
	private String summary;
  	private LinkedInStartDate startDate;
  	private LinkedInEndDate endDate;
	private Boolean current;
	private LinkedInCompany company;

	public LinkedInPosition() {}

	@ManyToOne(fetch = FetchType.LAZY, cascade = {}, optional = false)
	@JoinColumn(name = "linkedin_person_id")
	public LinkedInPerson getLinkedInPerson()
	{
		return linkedInPerson;
	}

	public void setLinkedInPerson(LinkedInPerson linkedInPerson)
	{
		this.linkedInPerson = linkedInPerson;
	}

	@Column(name = "title", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "summary", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getSummary()
	{
		return summary;
	}

	public void setSummary(String summary)
	{
		this.summary = summary;
	}

	@Embedded
	public LinkedInStartDate getStartDate()
	{
		return startDate;
	}

	public void setStartDate(StartDate startDate) {
		this.startDate = startDate == null ? null :  new LinkedInStartDate(startDate);
	}

	@Embedded
	public LinkedInEndDate getEndDate()
	{
		return endDate;
	}

	public void setEndDate(EndDate endDate) {
		this.endDate = endDate == null ? null :  new LinkedInEndDate(endDate);
	}

	@Column(name = "current")
	public Boolean getCurrent() {
		return current;
	}

	public void setCurrent(Boolean current) {
		this.current = current;
	}

	@Embedded
	public LinkedInCompany getCompany()
	{
		return company;
	}

	public void setCompany(Company company) {
		this.company = company == null ? null : new LinkedInCompany(company);
	}

	public void setCompany(LinkedInCompany company) {
		this.company = company;
	}
}
