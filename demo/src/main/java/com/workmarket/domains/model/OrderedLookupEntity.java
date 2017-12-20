package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class OrderedLookupEntity extends LookupEntity {
	private Integer order;

	public OrderedLookupEntity() {}
	public OrderedLookupEntity(String code) {
		super(code);
	}
	public OrderedLookupEntity(String code, String description) {
		super(code, description);
	}

	@Column(name="order")
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
}
