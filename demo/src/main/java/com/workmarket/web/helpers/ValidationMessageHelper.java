package com.workmarket.web.helpers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.validation.ConstraintViolation;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import java.util.List;

/**
 *
 * This is a port of ThriftValidationMessageHelper for the non-Thrift constraints validation
 */
public class ValidationMessageHelper {

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
		if (v.getParams() != null) {
			for (Object param : v.getParams())
				params.add(StringUtils.capitalize(param.toString()));
		}
		Object[] paramArray = params.toArray(new Object[params.size()]);

		if (paramArray.length == 0)
			bindingResult.reject(v.getKey(), "");
		else
			bindingResult.reject(v.getKey(), paramArray, "");
		
	}
}
