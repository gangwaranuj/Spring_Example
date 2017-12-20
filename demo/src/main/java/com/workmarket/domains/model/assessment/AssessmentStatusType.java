package com.workmarket.domains.model.assessment;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="assessmentStatusType")
@Table(name="assessment_status_type")
public class AssessmentStatusType extends LookupEntity {

	private static final long serialVersionUID = 1L;
	
	public static final String DRAFT = "draft";
	public static final String ACTIVE = "active";
	public static final String INACTIVE = "inactive";
	public static final String REMOVED = "removed";
	
	public AssessmentStatusType() {}
	public AssessmentStatusType(String code) {
		super(code);
	}
	
	@Transient
	public boolean isActive() {
		return getCode().equals(ACTIVE);
	}
}