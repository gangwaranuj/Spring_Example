package com.workmarket.domains.model.requirementset.companywork;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.requirementset.Requirable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by ianha on 1/5/14
 */
@Entity
@Table(name = "company")
public class CompanyWorkRequirable extends AbstractEntity implements Requirable {
	private String name;

	@Override
	@Column(name = "name", insertable = true, updatable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
