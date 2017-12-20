package com.workmarket.web.validators;

import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.thrift.work.CustomFieldGroupSaveRequest;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hamcrest.CoreMatchers.equalTo;

@Component
public class CustomFieldGroupSaveRequestValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return CustomFieldGroupSaveRequest.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CustomFieldGroupSaveRequest request = (CustomFieldGroupSaveRequest) target;
		CustomFieldGroup customFieldGroup = request.getCustomFieldGroup();
		WorkCustomFieldGroup fieldGroup = request.getFieldGroup();

		boolean isCompleteAction = request.isCompleteAction();
		boolean isAdmin = request.isAdmin();
		boolean isActiveResource = request.isActiveResource();
		boolean isSentAction = request.isSentAction();

		for (WorkCustomField field : fieldGroup.getActiveWorkCustomFields()) {
			Long fieldId = field.getId();
			List<CustomField> customFieldList = customFieldGroup.getFields();
			CustomField submittedField = selectFirst(customFieldList,
					having(on(CustomField.class).getId(), equalTo(fieldId))
			);
			String submittedValue = submittedField == null ? "" : submittedField.getValue();
			boolean validField = (isNotBlank(submittedValue) && field.getRequiredFlag()) || !field.getRequiredFlag();

			if (isAdmin && !isActiveResource) {
				if (field.isResourceType()) {
					if (isCompleteAction && !validField) {
						errors.reject("NotNull", new Object[]{field.getName()}, "");
					}
				} else {
					if((isCompleteAction || isSentAction) && !validField) {
						errors.reject("NotNull", new Object[]{field.getName()}, "");
					}
				}
			}

			if (isActiveResource && field.isResourceType()) {
				if (isCompleteAction && !validField) {
					errors.reject("NotNull", new Object[]{field.getName()}, "");
				}
			}
		}
	}
}