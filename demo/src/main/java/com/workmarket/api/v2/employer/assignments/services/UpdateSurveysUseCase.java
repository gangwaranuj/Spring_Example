package com.workmarket.api.v2.employer.assignments.services;

import com.google.common.collect.Sets;
import com.workmarket.api.v2.model.SurveyDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Set;

@Component
@Scope("prototype")
public class UpdateSurveysUseCase
	extends AbstractAssignmentUseCase<UpdateSurveysUseCase, Set<SurveyDTO>> {

	public UpdateSurveysUseCase(String id, Set<SurveyDTO> surveyDTOs, boolean readyToSend) {
		this.id = id;
		this.surveyDTOs = surveyDTOs;
		this.readyToSend = readyToSend;
	}

	@Override
	protected UpdateSurveysUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
		Assert.notNull(surveyDTOs);
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

		copySurveyDTOs();
	}

	@Override
	protected void process() {
		loadSurveys();
		loadWork();
	}

	@Override
	protected void save() throws ValidationException, WorkAuthorizationException {
		generateWorkSaveRequest();
		saveWork();
	}

	@Override
	protected void finish() {
		getWork();
		getSurveys();
		loadSurveyDTOs();
	}

	@Override
	protected UpdateSurveysUseCase handleExceptions() throws ValidationException, WorkActionException {
		handleValidationException();
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
