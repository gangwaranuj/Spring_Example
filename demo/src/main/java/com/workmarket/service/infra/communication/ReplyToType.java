package com.workmarket.service.infra.communication;

public enum ReplyToType {
	TRANSACTIONAL(1L, "Transactional"),
	INVITATION(2L, "Invitation"),
	USER(3L, "User"),
	TRANSACTIONAL_FROM_USER(4L, "Transactional From User"),
	TRANSACTIONAL_FROM_COMPANY(5L, "Transactional From Company"),
	INVITATION_FROM_USER(6L, "Invitation From User"),
	INVOICE(7L, "Invoices"),
	PUBLIC_USER(8L, "Public User");

	private Long id;
	private String description;

	ReplyToType(Long id, String description) {
		this.id = id;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
}
