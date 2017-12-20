package com.workmarket.api.v2.worker.validators;

import javax.validation.Validator;
//import org.springframework.validation.Validator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import com.workmarket.web.validators.FilenameValidator;
import com.workmarket.api.v2.worker.model.AddDeliverableDTO;

@Component
public class AddDeliverableDTOValidator extends SpringValidatorAdapter {

	@Autowired private FilenameValidator filenameValidator;

	@Autowired
	public AddDeliverableDTOValidator(Validator validator) {

		super(validator);
	}

	@Override
	public boolean supports(Class<?> clazz) {

		return AddDeliverableDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object obj,
						 Errors errors) {


		//super.validate(obj, errors); //jsr303

		final String fileName = ((AddDeliverableDTO)obj).getName();

		filenameValidator.validate(fileName, errors);
	}
}
