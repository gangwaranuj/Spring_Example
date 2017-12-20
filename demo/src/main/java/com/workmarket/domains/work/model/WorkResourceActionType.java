package com.workmarket.domains.work.model;

public enum WorkResourceActionType {
	ACCEPT_WORK("Accept Work"),
	DECLINE_WORK("Decline Work"), 
	COUNTER_OFFER("Counter Offer"),
	QUESTION("Question"),
	NOTE("Note"),
	REROUTE_WORK("Reroute Work");

	private String actionTypeName;
	private WorkResourceActionType(String actionTypeName) {
		this.actionTypeName = actionTypeName;
	}
	
	public String getActionTypeName() {
		return actionTypeName;
	}

	public static WorkResourceActionType findActionType(String actionType) {
		if (actionType == null) {
			return null;
		}
		for (WorkResourceActionType action : WorkResourceActionType.values()) {
			if (action.getActionTypeName().equals(actionType)) {
				return action;
			}
		}
		return null;
	}
}
