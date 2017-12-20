package com.workmarket.search.request.user;

import com.workmarket.search.request.TrackableSearchRequest;
import com.workmarket.thrift.work.Work;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class AssignmentResourceSearchRequest extends TrackableSearchRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String workNumber;
	private String description;
	private String skills;
	private long boostIndustryId;
	private Work work;

	@Override
	public String getRequestType() {
		return ASSIGNMENT_REQUEST;
	}

	public AssignmentResourceSearchRequest() {
		super();
	}

	public AssignmentResourceSearchRequest(PeopleSearchRequest request) {
		super(request);
	}

	public String getWorkNumber() {
		return this.workNumber;
	}

	public AssignmentResourceSearchRequest setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public boolean isSetWorkNumber() {
		return this.workNumber != null;
	}

	public String getDescription() {
		return this.description;
	}

	public AssignmentResourceSearchRequest setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isSetDescription() {
		return this.description != null;
	}

	public String getSkills() {
		return this.skills;
	}

	public AssignmentResourceSearchRequest setSkills(String skills) {
		this.skills = skills;
		return this;
	}

	public boolean isSetSkills() {
		return this.skills != null;
	}

	public long getBoostIndustryId() {
		return this.boostIndustryId;
	}

	public AssignmentResourceSearchRequest setBoostIndustryId(long boostIndustryId) {
		this.boostIndustryId = boostIndustryId;
		return this;
	}

	public boolean isSetBoostIndustryId() {
		return (boostIndustryId > 0L);
	}

	public AssignmentResourceSearchRequest setRequest(PeopleSearchRequest request) {
		super.setRequest(request);
		return this;
	}

	public Work getWork() { return this.work; }

	public AssignmentResourceSearchRequest setWork(Work work) {
		this.work = work;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof AssignmentResourceSearchRequest)
			return this.equals((AssignmentResourceSearchRequest) that);
		return false;
	}

	private boolean equals(AssignmentResourceSearchRequest that) {
		if (that == null)
			return false;

		boolean this_present_workNumber = true && this.isSetWorkNumber();
		boolean that_present_workNumber = true && that.isSetWorkNumber();
		if (this_present_workNumber || that_present_workNumber) {
			if (!(this_present_workNumber && that_present_workNumber))
				return false;
			if (!this.workNumber.equals(that.workNumber))
				return false;
		}

		boolean this_present_request = true && this.isSetRequest();
		boolean that_present_request = true && that.isSetRequest();
		if (this_present_request || that_present_request) {
			if (!(this_present_request && that_present_request))
				return false;
			if (!this.getRequest().equals(that.getRequest()))
				return false;
		}

		boolean this_present_description = true && this.isSetDescription();
		boolean that_present_description = true && that.isSetDescription();
		if (this_present_description || that_present_description) {
			if (!(this_present_description && that_present_description))
				return false;
			if (!this.description.equals(that.description))
				return false;
		}

		boolean this_present_skills = true && this.isSetSkills();
		boolean that_present_skills = true && that.isSetSkills();
		if (this_present_skills || that_present_skills) {
			if (!(this_present_skills && that_present_skills))
				return false;
			if (!this.skills.equals(that.skills))
				return false;
		}

		boolean this_present_boostIndustryId = true && this.isSetBoostIndustryId();
		boolean that_present_boostIndustryId = true && that.isSetBoostIndustryId();
		if (this_present_boostIndustryId || that_present_boostIndustryId) {
			if (!(this_present_boostIndustryId && that_present_boostIndustryId))
				return false;
			if (this.boostIndustryId != that.boostIndustryId)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_workNumber = true && (isSetWorkNumber());
		builder.append(present_workNumber);
		if (present_workNumber)
			builder.append(workNumber);

		boolean present_request = true && (isSetRequest());
		builder.append(present_request);
		if (present_request)
			builder.append(getRequest());

		boolean present_description = true && (isSetDescription());
		builder.append(present_description);
		if (present_description)
			builder.append(description);

		boolean present_skills = true && (isSetSkills());
		builder.append(present_skills);
		if (present_skills)
			builder.append(skills);

		boolean present_boostIndustryId = true && (isSetBoostIndustryId());
		builder.append(present_boostIndustryId);
		if (present_boostIndustryId)
			builder.append(boostIndustryId);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AssignmentResourceSearchRequest(");
		boolean first = true;

		if (isSetWorkNumber()) {
			sb.append("workNumber:");
			if (this.workNumber == null) {
				sb.append("null");
			} else {
				sb.append(this.workNumber);
			}
			first = false;
		}
		if (!first) sb.append(", ");
		sb.append("request:");
		if (this.getRequest() == null) {
			sb.append("null");
		} else {
			sb.append(this.getRequest());
		}
		first = false;
		if (isSetDescription()) {
			if (!first) sb.append(", ");
			sb.append("description:");
			if (this.description == null) {
				sb.append("null");
			} else {
				sb.append(this.description);
			}
			first = false;
		}
		if (isSetSkills()) {
			if (!first) sb.append(", ");
			sb.append("skills:");
			if (this.skills == null) {
				sb.append("null");
			} else {
				sb.append(this.skills);
			}
			first = false;
		}
		if (isSetBoostIndustryId()) {
			if (!first) sb.append(", ");
			sb.append("boostIndustryId:");
			sb.append(this.boostIndustryId);
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}

