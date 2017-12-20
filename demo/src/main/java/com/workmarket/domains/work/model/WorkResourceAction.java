package com.workmarket.domains.work.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.configuration.Constants;


@Entity(name = "work_resource_action_types")
public class WorkResourceAction extends AbstractEntity implements Comparable<WorkResourceAction> {

	private static final long serialVersionUID = 1260653941318081629L;

	private String actionCode;
	private WorkResourceActionType actionType;
	private String actionDescription;
	private Integer actionTypeOrder;

	@Size(min = 1, max = 64)
	@Column(name="action_code", nullable = false, length = 64)
	public String getActionCode() {
		return actionCode;
	}
	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}
	@Size(min = 0, max = 20)
	@Column(name = "action_type", nullable = false)
	public String getActionType() {
		if (actionType == null) {
			return null;
		}
		return actionType.getActionTypeName();
	}
	public void setActionType(String actionType) {
		this.actionType = WorkResourceActionType.findActionType(actionType);
	}
	@Column(name = "action_type_order", nullable = false)
	public Integer getActionTypeOrder() {
		return actionTypeOrder;
	}
	public void setActionTypeOrder(Integer actionTypeOrder) {
		this.actionTypeOrder = actionTypeOrder;
	}
	@Size(min = 0, max = Constants.TEXT_LONG)
	@Column(name = "action_description", nullable = true)
	public String getActionDescription() {
		return actionDescription;
	}
	public void setActionDescription(String actionDescription) {
		this.actionDescription = actionDescription;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((actionCode == null) ? 0 : actionCode.hashCode());
		result = prime
				* result
				+ ((actionDescription == null) ? 0 : actionDescription
						.hashCode());
		result = prime * result
				+ ((actionType == null) ? 0 : actionType.hashCode());
		result = prime * result
				+ ((actionTypeOrder == null) ? 0 : actionTypeOrder.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorkResourceAction other = (WorkResourceAction) obj;
		if (actionCode == null) {
			if (other.actionCode != null)
				return false;
		} else if (!actionCode.equals(other.actionCode))
			return false;
		if (actionDescription == null) {
			if (other.actionDescription != null)
				return false;
		} else if (!actionDescription.equals(other.actionDescription))
			return false;
		if (actionType != other.actionType)
			return false;
		if (actionTypeOrder == null) {
			if (other.actionTypeOrder != null)
				return false;
		} else if (!actionTypeOrder.equals(other.actionTypeOrder))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "WorkResourceAction " + "[id= " + this.getId() + ", actionCode=" + actionCode + ", actionType="
				+ actionType + ", actionDescription=" + actionDescription
				+ ", actionTypeOrder=" + actionTypeOrder + "]";
	}

	@Transient
	public WorkResourceActionType actionType() {
		return actionType;
	}
	@Override
	public int compareTo(WorkResourceAction o) {
		return this.getActionTypeOrder() - o.getActionTypeOrder();
	}


}
