package com.workmarket.web.controllers;

import com.workmarket.utility.CollectionUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import ch.lambdaj.function.convert.Converter;

import static ch.lambdaj.Lambda.convert;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.workmarket.utility.CollectionUtilities.first;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * User: nick
 * Date: 3/28/12
 * Time: 4:07 PM
 * This controller provides endpoint metadata for documentation purposes
 */
@Controller
@RequestMapping("/admin/endpoints")
public class AdminEndpointsController {

	private static final Converter<NameValueExpression<String>, String> NAME_VALUE_EXPRESSION_STRING_CONVERTER = new Converter<NameValueExpression<String>, String>() {
		@Override public String convert(NameValueExpression<String> expr) {
			return String.format("%s=%s", expr.getName(), expr.getValue()).replaceAll("\\[\\]", "");
		}
	};

	@Autowired private RequestMappingHandlerMapping handlerMapping;

	@RequestMapping(method = GET)
	public String show(Model model) {
		model.addAttribute("requestMappings", getMappings(this.handlerMapping.getHandlerMethods()));

		return "web/pages/admin/endpoints";
	}

	@RequestMapping(method = GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, String>> showJson() {
		return getMappings(this.handlerMapping.getHandlerMethods());
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, String>> getMappings(Map<RequestMappingInfo, HandlerMethod> methods) {

		List<Map<String, String>> result = new ArrayList<>();
		// need to convert due to the weird way Spring stores request mappings
		for (RequestMappingInfo method : methods.keySet()) {
			String javaClassAndMethod = getClassAndMethod(methods.get(method).getMethod());

			result.add(CollectionUtilities.newStringMap(
					"patterns", first(method.getPatternsCondition().getPatterns()),
					"javaMethod", javaClassAndMethod,
					"methods", firstNonNull(first(method.getMethodsCondition().getMethods()), "").toString(),
					"headers", first(convert(method.getHeadersCondition().getExpressions(), NAME_VALUE_EXPRESSION_STRING_CONVERTER)),
					"consumes", firstNonNull(first(method.getConsumesCondition().getExpressions()), "").toString(),
					"produces", firstNonNull(first(method.getProducesCondition().getExpressions()), "").toString()));
		}
		return result;
	}

	private String getClassAndMethod(Method m) {
		String[] fullyQualifiedNameParts = m.getDeclaringClass().toString().split(Pattern.quote("."));
		String className = fullyQualifiedNameParts[fullyQualifiedNameParts.length-1];
		return String.format("%s.%s", className, m.getName());
	}
}
