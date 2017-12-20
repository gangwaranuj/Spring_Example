package com.workmarket.domains.model.requirementset.license;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.requirementset.Requirable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "license")
public class LicenseRequirable extends AbstractEntity implements Requirable {
	private String name;
	private String state;

	@Override
	@Column(name="name", insertable = false, updatable = false)
	public String getName() {
		return state + " - " + name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Column(name="state", insertable = false, updatable = false)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
