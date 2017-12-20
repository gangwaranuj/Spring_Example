package com.workmarket.service.business.event;

import java.util.Set;

public class AddToWorkerPoolEvent extends Event {

	private Long companyId;
	private String userNumber;
	private Set<String> cartUsers;
	private static final long serialVersionUID = -8085814949967116319L;

	public AddToWorkerPoolEvent(Long companyId, String userNumber, Set<String> cartUsers) {
		this.companyId = companyId;
		this.userNumber = userNumber;
		this.cartUsers = cartUsers;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public Set<String> getCartUsers() {
		return cartUsers;
	}

}
