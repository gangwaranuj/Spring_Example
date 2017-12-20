package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.model.SurveyDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AssignmentSurveysServiceImpl implements AssignmentSurveysService {
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public Set<SurveyDTO> get(String id) throws WorkActionException {
		return useCaseFactory
			.getUseCase(GetSurveysUseCase.class, id)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public Set<SurveyDTO> update(String id, Set<SurveyDTO> surveyDTOs, boolean readyToSend) throws WorkActionException, ValidationException {
		return useCaseFactory
			.getUseCase(UpdateSurveysUseCase.class, id, surveyDTOs, readyToSend)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
