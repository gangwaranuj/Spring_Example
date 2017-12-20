package com.workmarket.web.helpers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.ValidationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import java.util.List;

public class ThriftValidationMessageHelper {
	public static BindingResult newBindingResult() {
		return newBindingResult("work");
	}

	public static BindingResult newBindingResult(String name) {
		return new MapBindingResult(Maps.newHashMap(), name);
	}

	public static void rejectViolations(List<ConstraintViolation> constraintViolations, BindingResult bindingResult) {
		for (ConstraintViolation constraintViolation : constraintViolations) {
			rejectViolation(constraintViolation, bindingResult);
		}
	}

	/**
	 * Translate <code>ConstraintViolation</code> to <code>BindingResult</code>
	 * in a way that can resolve to standard error messaging.
	 * @param v
	 * @param bindingResult
	 */
	public static void rejectViolation(ConstraintViolation v, BindingResult bindingResult) {
		List<Object> params = Lists.newArrayList();
		if (v.isSetProperty())
			params.add(StringUtils.capitalize(v.getProperty()));
		if (v.isSetParams())
			params.addAll(v.getParams());

		Object[] paramArray = params.toArray(new Object[0]);
		if (v.isSetProperty()) {
			bindingResult.rejectValue(v.getProperty(), v.getError(), paramArray, v.getWhy());
		} else {
			bindingResult.reject(v.getError(), paramArray, v.getWhy());
		}
	}

	/**
	 * Translate from Thrift validation errors to a BindingResult
	 * for display via the MessageBundle interface.
	 * @param e
	 */
	public static BindingResult buildBindingResult(ValidationException e) {
		BindingResult bindingResult = ThriftValidationMessageHelper.newBindingResult();
		for (com.workmarket.thrift.core.ConstraintViolation v : e.getErrors()) {
			ThriftValidationMessageHelper.rejectViolation(v, bindingResult);
		}
		return bindingResult;
	}
}
