package com.workmarket.domains.model.assessment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="surveyAssessment")
@DiscriminatorValue(AbstractAssessment.SURVEY_ASSESSMENT_TYPE)
@AuditChanges
public class SurveyAssessment extends AbstractAssessment {
	private static final long serialVersionUID = 1L;

	@Transient
	public String getType() {
		return AbstractAssessment.SURVEY_ASSESSMENT_TYPE;
	}
}
