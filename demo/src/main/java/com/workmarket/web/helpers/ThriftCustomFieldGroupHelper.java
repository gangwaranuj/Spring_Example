package com.workmarket.web.helpers;

import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.Work;

public class ThriftCustomFieldGroupHelper {
	public static void setRequiredThriftCustomFieldGroup(WorkCustomFieldGroup requiredGroup, Work work) {
		if (work.getCustomFieldGroups() != null) {
			for (CustomFieldGroup g : work.getCustomFieldGroups()) {
				if (requiredGroup.getId().equals(g.getId())) {
					return;
				}
			}
		}

		CustomFieldGroup thriftGroup = new CustomFieldGroup()
			.setId(requiredGroup.getId())
			.setName(requiredGroup.getName())
			.setIsRequired(requiredGroup.isRequired())
			.setPosition(work.getCustomFieldGroupsSize());

		for (WorkCustomField field : requiredGroup.getWorkCustomFields()) {
			if (!field.getDeleted()) {
				thriftGroup.addToFields(
					new CustomField()
						.setId(field.getId())
						.setName(field.getName())
						.setValue(
							field.getDefaultValue() != null && field.getDefaultValue().contains(",") ?
								"" :
								field.getDefaultValue()
						)
						.setIsRequired(field.getRequiredFlag())
						.setVisibleToResource(field.getVisibleToResourceFlag())
						.setVisibleToOwner(field.getVisibleToOwnerFlag())
						.setType(field.getWorkCustomFieldType().getCode())
						.setDefaultValue(field.getDefaultValue())
				);
			}
		}

		if (thriftGroup.hasFields()) {
			work.addToCustomFieldGroups(thriftGroup);
		}
	}
}
