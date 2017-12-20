package com.workmarket.domains.work.service.resource.action;

import com.workmarket.domains.work.model.WorkResourceAction;
import com.workmarket.service.exception.InvalidParameterException;
import com.workmarket.thrift.work.DeclineWorkOfferRequest;
import com.workmarket.thrift.work.ResourceNoteRequest;
import com.workmarket.thrift.work.WorkQuestionRequest;

public interface WorkResourceActionService {

	WorkResourceAction findAction(DeclineWorkOfferRequest request) throws InvalidParameterException;

	WorkResourceAction findAction(ResourceNoteRequest request);

	WorkResourceAction findAction(WorkQuestionRequest request) ;

	WorkResourceAction findById(Long actionCodeId);

	
}
