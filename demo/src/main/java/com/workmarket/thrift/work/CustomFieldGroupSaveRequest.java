package com.workmarket.thrift.work;

import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;

public class CustomFieldGroupSaveRequest {

	private CustomFieldGroup customFieldGroup;
	private WorkCustomFieldGroup fieldGroup;

	private boolean isActiveResource;
	private boolean isAdmin;
	private boolean completeAction;
	private boolean isSentAction;

	public CustomFieldGroupSaveRequest(CustomFieldGroup customFieldGroup,
		WorkCustomFieldGroup fieldGroup,
		boolean activeResource,
		boolean admin,
		boolean completeAction,
		boolean sentAction) {

		this.customFieldGroup = customFieldGroup;
		this.fieldGroup = fieldGroup;
		this.isActiveResource = activeResource;
		this.isAdmin = admin;
		this.completeAction = completeAction;
		this.isSentAction = sentAction;
	}

	public CustomFieldGroup getCustomFieldGroup() {
		return customFieldGroup;
	}

	public void setCustomFieldGroup(CustomFieldGroup group) {
		this.customFieldGroup = group;
	}

	public WorkCustomFieldGroup getFieldGroup() {
		return fieldGroup;
	}

	public void setFieldGroup(WorkCustomFieldGroup fieldGroup) {
		this.fieldGroup = fieldGroup;
	}

	public boolean isActiveResource() {
		return isActiveResource;
	}

	public void setActiveResource(boolean activeResource) {
		this.isActiveResource = activeResource;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		this.isAdmin = admin;
	}

	public boolean isCompleteAction() {
		return completeAction;
	}

	public void setCompleteAction(boolean completeAction) {
		this.completeAction = completeAction;
	}

	public boolean isSentAction() {
		return isSentAction;
	}

	public void setSentAction(boolean sentAction) {
		this.isSentAction = sentAction;
	}

}
