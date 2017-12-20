package com.workmarket.domains.model.requirementset.document;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.requirementset.Requirable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "asset")
public class DocumentRequirable extends AbstractEntity implements Requirable {
	private String name;
	private String uuid;

	@Override
	@Column(name="name", insertable = false, updatable = false)
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Column(name="uuid", insertable = false, updatable = false)
	public String getUuid() {
		return  uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
