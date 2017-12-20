package com.workmarket.common.service.helpers;


import com.workmarket.domains.model.validation.ConstraintViolation;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;


public interface ServiceMessageHelper {

	List<String> getMessages(List<ConstraintViolation> violations);

	String getMessage(String message, Object... arguments);

	List<String> getAllErrors(BindingResult binding);

	String getMessage(ObjectError error);
}
