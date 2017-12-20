package com.workmarket.api.v2.employer.assignments.services;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.v2.employer.assignments.models.TemplateDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AssignmentTemplateServiceImpl implements AssignmentTemplateService {
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public TemplateDTO get(String id) throws WorkActionException {
		return useCaseFactory
			.getUseCase(GetTemplateUseCase.class, id)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public TemplateDTO create(TemplateDTO templateDTO) throws ValidationException {
		return useCaseFactory
			.getUseCase(CreateTemplateUseCase.class, templateDTO)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public TemplateDTO update(String id, TemplateDTO templateDTO) throws WorkActionException, ValidationException {
		return useCaseFactory
			.getUseCase(UpdateTemplateUseCase.class, id, templateDTO)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public ImmutableList<Map> getProjectedTemplates(String[] fields) throws Exception {
		return useCaseFactory
			.getUseCase(GetProjectedTemplatesUseCase.class, (Object) fields)
			.execute()
			.andReturn();
	}

	@Override
	public ImmutableList<Map> getTemplates() throws Exception {
		return useCaseFactory
			.getUseCase(GetTemplatesUseCase.class)
			.execute()
			.andReturn();
	}
}
