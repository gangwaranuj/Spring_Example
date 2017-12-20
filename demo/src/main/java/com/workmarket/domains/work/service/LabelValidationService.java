package com.workmarket.domains.work.service;

import com.workmarket.service.business.dto.WorkSubStatusTypeDTO;
import com.workmarket.service.exception.account.InvalidPricingException;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.web.helpers.MessageBundleHelper;

import java.util.List;

public interface LabelValidationService {
	List<ConstraintViolation> validateLabel(WorkSubStatusTypeDTO dto, MessageBundleHelper messageHelper);
}
