package com.workmarket.thrift.assessment.validator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.validation.MessageKeys;
import com.workmarket.thrift.assessment.AttemptResponseRequest;
import com.workmarket.thrift.assessment.Response;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.ValidationException;

@Component
public class AttemptResponseRequestValidator {
	public void validate(AttemptResponseRequest request) throws ValidationException {
		List<ConstraintViolation> errors = Lists.newLinkedList();
		validateResponse(request, errors);
		
		if (errors.size() > 0) {
			throw new ValidationException("Unable to save response", errors);
		}
	}
	
	private void validateResponse(AttemptResponseRequest request, List<ConstraintViolation> errors) {
		if (!request.isSetItemId()) {
			errors.add(new ConstraintViolation().setProperty("item").setError(MessageKeys.NOT_NULL));
			return;
		}
		
		if (request.getResponsesSize() == 0) {
			ConstraintViolation v = new ConstraintViolation()
				.setProperty("responses")
				.setError(MessageKeys.NOT_NULL);
			v.addToParams(String.valueOf(request.getItemId()));
			errors.add(v);
		} else {
			for (Response r : request.getResponses()) {
				if (!((r.isSetValue() && StringUtils.isNotEmpty(r.getValue())) || (r.isSetChoice() && r.getChoice().isSetId()) || (r.getAssetsSize() > 0) || r.getUploadsSize() > 0)) {
					ConstraintViolation v = new ConstraintViolation()
						.setProperty("response")
						.setError(MessageKeys.NOT_NULL);
					v.addToParams(String.valueOf(request.getItemId()));
					errors.add(v);
				}
			}
		}
	}
}
