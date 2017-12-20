package com.workmarket.dao.assessment;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.assessment.SurveyAssessment;
import org.springframework.stereotype.Repository;

@Repository
public class SurveyAssessmentDAOImpl extends AbstractDAO<SurveyAssessment> implements SurveyAssessmentDAO {
	@Override
	protected Class<?> getEntityClass() {
		return SurveyAssessment.class;
	}
}
