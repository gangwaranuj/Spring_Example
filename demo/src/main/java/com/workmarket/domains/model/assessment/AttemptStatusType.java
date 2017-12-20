package com.workmarket.domains.model.assessment;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="assessmentAttemptStatusType")
@Table(name="assessment_attempt_status_type")
public class AttemptStatusType extends LookupEntity {
	
	private static final long serialVersionUID = 1L;
	
	public static final String INPROGRESS = "inprogress";
	public static final String GRADE_PENDING = "gradePending";
	public static final String GRADED = "graded";
	public static final String COMPLETE = "complete";
	
	public AttemptStatusType() {}
	public AttemptStatusType(String code){
		super(code);
	}
	
	@Transient
	public boolean isInProgress() {
		return getCode().equals(INPROGRESS);
	}
	
	@Transient
	public boolean isGradePending() {
		return getCode().equals(GRADE_PENDING);
	}
	
	@Transient
	public boolean isGraded() {
		return getCode().equals(GRADED);
	}
	
	@Transient
	public boolean isComplete() {
		return getCode().equals(COMPLETE);
	}

	public static AttemptStatusType valueOf(String code) {
		return new AttemptStatusType(code);
	}
}