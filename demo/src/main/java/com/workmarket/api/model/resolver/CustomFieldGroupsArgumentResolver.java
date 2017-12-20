package com.workmarket.api.model.resolver;

import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomFieldGroupsArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		Class<?> parameterType = methodParameter.getParameterType();
		return methodParameter.hasParameterAnnotation(ApiArgumentResolver.class)
				&& (parameterType.isArray() && parameterType.isAssignableFrom(CustomFieldGroup[].class));
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
		if (supportsParameter(methodParameter)) {
			return evaluateArgument(nativeWebRequest.getNativeRequest(HttpServletRequest.class));
		}
		return null;
	}

	/**
	 * Example:
	 * custom_field_groups[<N>][id] - Custom field group identifier
	 * custom_field_groups[<N>][fields][<N>][id] - Custom field identifier
	 * custom_field_groups[<N>][fields][<N>][value] - Custom field value
	 * @param request Http Servlet Request
	 * @return an array of CustomFieldGroup
	 */
	protected CustomFieldGroup[] evaluateArgument(HttpServletRequest request) {
		Map<Integer,CustomFieldGroup> customFieldGroupMap = new TreeMap<Integer,CustomFieldGroup>();
		Map<Integer,Map<Integer,CustomField>> groupedCustomFieldMap = new TreeMap<Integer, Map<Integer,CustomField>>();

		Pattern pattern = Pattern.compile("custom_field_groups\\[([0-9]+)\\]\\[(id|fields)\\](?:\\[([0-9]+)\\]\\[id\\])?");

		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String paramName = (String)e.nextElement();
			Matcher matcher = pattern.matcher(paramName);

			if (matcher.matches()) {
				Integer groupIndex = Integer.valueOf(matcher.group(1)); // custom_field_groups[groupIndex][groupKey]
				String groupKey = matcher.group(2);
				CustomFieldGroup customFieldGroup = customFieldGroupMap.get(groupIndex);

				if (customFieldGroup == null) {
					customFieldGroup = new CustomFieldGroup();
					customFieldGroupMap.put(groupIndex, customFieldGroup);
				}

				customFieldGroup.setPosition(groupIndex);

				if ("id".equals(groupKey)) {
					customFieldGroup.setId( Long.parseLong(request.getParameter(paramName)) );
				}
				else if ("fields".equals(groupKey)) {
					Map<Integer,CustomField> customFieldMap = groupedCustomFieldMap.get(groupIndex);

					if (customFieldMap == null) {
						customFieldMap = new TreeMap<Integer,CustomField>();
						groupedCustomFieldMap.put(groupIndex, customFieldMap);
					}

					Integer fieldIndex = Integer.valueOf(matcher.group(3)); // custom_field_groups[<N>][fields][fieldIndex][id]
					CustomField customField = new CustomField();
					customField.setId( Long.parseLong(request.getParameter(paramName)) ); // paramName is custom_field_groups[<N>][fields][fieldIndex][id]
					customField.setValue( request.getParameter(String.format("custom_field_groups[%s][fields][%s][value]", groupIndex, fieldIndex)) );
					customFieldMap.put(fieldIndex, customField);
				}
			}
		}

		List<CustomFieldGroup> customFieldGroups = new LinkedList<CustomFieldGroup>();

		for (Map.Entry<Integer,CustomFieldGroup> customFieldGroupEntry : customFieldGroupMap.entrySet()) {
			Integer groupIndex = customFieldGroupEntry.getKey();
			CustomFieldGroup customFieldGroup = customFieldGroupEntry.getValue();
			Map<Integer,CustomField> customFieldMap = groupedCustomFieldMap.get(groupIndex);
			customFieldGroup.setFields(new ArrayList<CustomField>(customFieldMap.values()));
			customFieldGroups.add(customFieldGroup);
		}

		return customFieldGroups.toArray(new CustomFieldGroup[customFieldGroups.size()]);
	}
}