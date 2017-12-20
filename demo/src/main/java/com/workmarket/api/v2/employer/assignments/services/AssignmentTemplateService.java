package com.workmarket.api.v2.employer.assignments.services;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.v2.employer.assignments.models.TemplateDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;

import java.util.Map;

public interface AssignmentTemplateService {
	TemplateDTO get(String id) throws WorkActionException;
	TemplateDTO create(TemplateDTO templateDTO) throws ValidationException;
	TemplateDTO update(String id, TemplateDTO templateDTO) throws ValidationException, WorkActionException;
	ImmutableList<Map> getProjectedTemplates(String[] fields) throws Exception;
	ImmutableList<Map> getTemplates() throws Exception;
}
