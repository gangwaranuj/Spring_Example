package com.workmarket.common.template.email;

import com.workmarket.domains.model.Company;
import com.workmarket.configuration.Constants;

public class LockedCompanyAccountClientServicesEmailTemplate extends AbstractClientServicesEmailTemplate {

	private static final long serialVersionUID = -929691573282645897L;
	private Company company;

	public LockedCompanyAccountClientServicesEmailTemplate(Company company) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL);
		this.company = company;
	}

	public Company getCompany() {
		return company;
	}
}
