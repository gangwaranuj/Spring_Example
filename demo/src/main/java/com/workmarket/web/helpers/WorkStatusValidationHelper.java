package com.workmarket.web.helpers;


import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.thrift.core.ConstraintViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkStatusValidationHelper {

	@Autowired private WorkService workService;
	@Autowired private MessageBundleHelper messageHelper;

	public List<ConstraintViolation> validateUpdateOnWorkStatus(Long workId) {
		List<ConstraintViolation> errors = Lists.newArrayList();
		Work workModel = workService.findWork(workId);
		if (workModel != null && workModel.isActive()) {
			String messageKey = "assignment.update_not_allowed";
			String error = messageHelper.getMessage(messageKey);
			errors.add(new ConstraintViolation()
				.setWhy(error)
				.setError(error));
		}
		return errors;
	}
}
