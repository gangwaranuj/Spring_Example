package com.workmarket.domains.model.company;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="companyStatusType")
@Table(name="company_status_type")
public class CompanyStatusType extends LookupEntity {
	
	private static final long serialVersionUID = 1L;

	public static final String ACTIVE = "active";
	public static final String SUSPENDED = "suspended";
	public static final String LOCKED = "locked";
	
	public CompanyStatusType(){
		super();
	}

	public CompanyStatusType(String code){
		super(code);
	}
}
