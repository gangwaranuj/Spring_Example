package com.workmarket.api.v2.employer.assignments.services;


import com.google.api.client.util.Sets;
import com.workmarket.api.v2.model.SurveyDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Set;

@Component
@Scope("prototype")
public class GetSurveysUseCase
	extends AbstractAssignmentUseCase<GetSurveysUseCase, Set<SurveyDTO>> {

	public GetSurveysUseCase(String id) {
		this.id = id;
	}

	@Override
	protected GetSurveysUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
	}

	@Override
	protected void init() throws WorkActionException {
		getUser();
		generateWorkRequest();
		getWorkResponse();
	}

	@Override
	protected void prepare() {
		getWork();
		getSurveys();
	}

	@Override
	protected void finish() {
		loadSurveyDTOs();
	}

	@Override
	protected GetSurveysUseCase handleExceptions() throws WorkActionException {
		handleWorkActionException();
		return this;
	}

	@Override
	public Set<SurveyDTO> andReturn() {
		Set<SurveyDTO> surveyDTOs = Sets.newHashSet();

		for (SurveyDTO.Builder builder : surveyDTOBuilders) {
			surveyDTOs.add(builder.build());
		}

		return surveyDTOs;
	}
}
