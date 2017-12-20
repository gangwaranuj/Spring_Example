package com.workmarket.domains.model.reporting;

import com.workmarket.domains.model.validation.ConstraintViolation;

import java.util.ArrayList;
import java.util.List;

public class CustomReportUpdateResponse {
	Boolean success;
	List<ConstraintViolation> errors;

	public CustomReportUpdateResponse(){
		this.success = true;
		this.errors = new ArrayList<ConstraintViolation>();
	}

	public Boolean isSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public List<ConstraintViolation> getErrors() {
		return errors;
	}

	public void setErrors(List<ConstraintViolation> errors) {
		this.errors = errors;
	}

	public void addError(ConstraintViolation error){
		this.errors.add(error);
	}
}
