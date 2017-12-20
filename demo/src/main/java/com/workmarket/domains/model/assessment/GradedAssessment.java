package com.workmarket.domains.model.assessment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="gradedAssessment")
@DiscriminatorValue(AbstractAssessment.GRADED_ASSESSMENT_TYPE)
@AuditChanges
public class GradedAssessment extends AbstractAssessment {
	private static final long serialVersionUID = 1L;

	@Transient
	public String getType() {
		return AbstractAssessment.GRADED_ASSESSMENT_TYPE;
	}
}
