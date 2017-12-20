package com.workmarket.domains.model.block;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.workmarket.domains.model.Company;

@Embeddable
public class BlockingCompany implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Company company;
	
	public BlockingCompany() {}
	
	public BlockingCompany(Company company) {
		this.company = company;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="blocking_company_id")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
