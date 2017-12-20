package com.workmarket.domains.model.requirementset.agreement;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.requirementset.Requirable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "contract")
public class AgreementRequirable extends AbstractEntity implements Requirable {
	private String name;

	@Override
	@Column(name="name", insertable = false, updatable = false)
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
