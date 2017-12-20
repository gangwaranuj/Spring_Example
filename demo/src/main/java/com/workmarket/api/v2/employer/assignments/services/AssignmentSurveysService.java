package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.model.SurveyDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;

import java.util.Set;

public interface AssignmentSurveysService {
	Set<SurveyDTO> get(String id) throws WorkActionException;
	Set<SurveyDTO> update(String id, Set<SurveyDTO> shipmentDTO, boolean readyToSend) throws WorkActionException, ValidationException;
}
