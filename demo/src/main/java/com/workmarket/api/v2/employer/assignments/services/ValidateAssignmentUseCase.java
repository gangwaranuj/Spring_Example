package com.workmarket.api.v2.employer.assignments.services;

import com.google.api.client.util.Lists;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.domains.work.service.validator.WorkSaveRequestValidator;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

@Component
@Scope("prototype")
public class ValidateAssignmentUseCase extends BaseCreateAssignmentUseCase<ValidateAssignmentUseCase, List<ApiBaseError>> {
	@Autowired private WorkSaveRequestValidator workSaveRequestValidator;
	@Autowired private MessageSource messageSource;

	private List<ApiBaseError> validationErrors = Lists.newArrayList();

	public ValidateAssignmentUseCase(AssignmentDTO assignmentDTO, boolean readyToSend) {
		this.assignmentDTO = assignmentDTO;
		this.readyToSend = readyToSend;
	}

	protected void save() throws ValidationException {
		generateWorkSaveRequest();
		if (this.readyToSend) {
			workSaveRequestValidator.validateWork(this.workSaveRequest);
		} else {
			workSaveRequestValidator.validateWorkDraft(this.workSaveRequest);
		}
	}

	@Override
	protected ValidateAssignmentUseCase me() {
		return this;
	}

	@Override
	public ValidateAssignmentUseCase handleExceptions() {
		if (exception instanceof ValidationException) {
			ValidationException validationException = (ValidationException) exception;
			BindingResult bindingResult = ThriftValidationMessageHelper.buildBindingResult(validationException);

			for (ObjectError error : bindingResult.getAllErrors()) {
				if(error instanceof FieldError) {
					validationErrors.add(
						new ApiBaseError(
							error.getCode(),
							messageSource.getMessage(error, null),
							((FieldError)error).getField(),
							error.getObjectName()
						)
					);
				}
				else {
					validationErrors.add(new ApiBaseError(error.getCode(), messageSource.getMessage(error, null)));
				}
			}
		}
		return this;
	}

	@Override
	public List<ApiBaseError> andReturn() {
		return this.validationErrors;
	}
}
