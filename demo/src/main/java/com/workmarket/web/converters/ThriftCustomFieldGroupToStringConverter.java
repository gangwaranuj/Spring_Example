package com.workmarket.web.converters;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class ThriftCustomFieldGroupToStringConverter implements Converter<CustomFieldGroup, String> {

	@Autowired private JsonSerializationService jsonService;

	@Override
	public String convert(CustomFieldGroup customFieldGroup) {
		if (customFieldGroup == null) {
			return null;
		}

		List<Map<String, Object>> cfOrder = Lists.newArrayList();
			cfOrder.add(CollectionUtilities.newObjectMap(
				"group_id", customFieldGroup.getId(),
				"pos", customFieldGroup.getPosition()
			));

		List<Map<String, Object>> fields = Lists.newArrayList();
		if (isNotEmpty(customFieldGroup.getFields())) {
			for (CustomField field : customFieldGroup.getFields()) {
				fields.add(CollectionUtilities.newObjectMap(
					"id", field.getId(),
					"group_pos", customFieldGroup.getPosition(),
					"name", field.getName(),
					"value", StringUtils.defaultIfEmpty(field.getValue(), ""),
					"defaults", StringUtils.defaultIfEmpty(field.getDefaultValue(), ""),
					"options", WorkCustomField.isDropdown(field.getDefaultValue()) ? WorkCustomField.getDropdownValues(field.getDefaultValue()) : Lists.newArrayListWithCapacity(0),
					"is_dropdown", WorkCustomField.isDropdown(field.getDefaultValue()),
					"required", field.isIsRequired(),
					"type", field.getType()
				));
			}
		}
		return jsonService.toJson(fields);
	}

}
