package com.workmarket.service.business.upload.parser;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.WorkUploadColumnType;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import com.workmarket.thrift.work.exception.WorkRowParseErrorType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CustomFieldParserImpl implements CustomFieldParser {

	@Override
	public void build(WorkUploaderBuildResponse response, WorkUploaderBuildData buildData) {
		if (!response.getWork().isSetCustomFieldGroups()) {
			return;
		}

		Map<String, String> types = buildData.getTypes();

		Work work = response.getWork();
		final WorkUploadColumnType columnName = WorkUploadColumnType.newInstance(WorkUploadColumn.CUSTOM_FIELD.getUploadColumnName());

		for (CustomFieldGroup g : work.getCustomFieldGroups()) {
			for (CustomField f : g.getFields()) {
				String value = StringUtils.defaultString(
						types.get(columnName.getDerivedCode(f.getId())),
						f.getValue());

				if (StringUtils.contains(f.getDefaultValue(), ",")) {
					// this is a dropdown list, get the options
					List<String> options = Lists.newArrayList(Splitter.onPattern("\\s*,\\s*").split(f.getDefaultValue()));
					if (StringUtils.isNotEmpty(value) && !options.contains(value)) {
						// if the value isn't in the option list, error
						response.addToRowParseErrors(newInvalidOptionError(f.getName(), value));
					} else {
						// if the option is empty or in the list, set it
						f.setValue(value);
					}
				} else {
					// this isn't a dropdown list, get the value or the default
					value = StringUtils.defaultIfEmpty(value, f.getDefaultValue());
					if (StringUtils.isNotEmpty(value)) {
						// unless the option is empty, set it
						f.setValue(value);
					}
				}
			}
		}
	}

	private WorkRowParseError newInvalidOptionError(String name, String value) {
		WorkRowParseError e = new WorkRowParseError();
		e.setMessage(value + " is not an acceptable value for \"" + name + "\".");
		e.setColumn(WorkUploadColumn.CUSTOM_FIELD);
		e.setData(value);
		e.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		return e;
	}
}
