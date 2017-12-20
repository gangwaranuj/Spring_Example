package com.workmarket.domains.model.linkedin;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.code.linkedinapi.schema.EndDate;
import com.google.code.linkedinapi.schema.StartDate;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="linkedInEducation")
@Table(name="linkedin_education")
@AuditChanges
public class LinkedInEducation extends DeletableEntity {

	private static final long serialVersionUID = -4838204122074406229L;

	private LinkedInPerson linkedInPerson;
	private String schoolName;
	private String degree;
	private String notes;
	private String activities;
	private String fieldOfStudy;
	private LinkedInStartDate startDate;
	private LinkedInEndDate endDate;

	public LinkedInEducation() {}

	@ManyToOne(fetch = FetchType.LAZY, cascade = {}, optional = false)
	@JoinColumn(name = "linkedin_person_id")
	public LinkedInPerson getLinkedInPerson() {
		return linkedInPerson;
	}

	public void setLinkedInPerson(LinkedInPerson linkedInPerson) {
		this.linkedInPerson = linkedInPerson;
	}

	@Column(name = "school_name", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	@Column(name = "degree", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	@Column(name = "notes", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Column(name = "activities", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getActivities() {
		return activities;
	}

	public void setActivities(String activities) {
		this.activities = activities;
	}

	@Column(name = "field_of_study", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getFieldOfStudy() {
		return fieldOfStudy;
	}

	public void setFieldOfStudy(String fieldOfStudy) {
		this.fieldOfStudy = fieldOfStudy;
	}


	@Embedded
	public LinkedInStartDate getStartDate() {
		return startDate;
	}

	public void setStartDate(StartDate startDate) {
		this.startDate = startDate == null ? null :  new LinkedInStartDate(startDate);
	}

	@Embedded
	public LinkedInEndDate getEndDate() {
		return endDate;
	}

	public void setEndDate(EndDate endDate) {
		this.endDate = endDate == null ? null :  new LinkedInEndDate(endDate);
	}
}
