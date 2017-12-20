package com.workmarket.domains.model.option;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "companyOption")
@DiscriminatorValue("company")
public class CompanyOption extends Option {

	private static final long serialVersionUID = -8784885648008058503L;

	public static final String MBO_ENABLED = "mbo_enabled";
	public static final String MBO_REQUIRED = "mbo_required";
	public static final String DOCUMENTS_ENABLED = "documents_enabled";
	public static final String HIDE_CONTACT_ENABLED = "hide_contact_enabled";

	public CompanyOption() {
		super();
	}

	public CompanyOption(String name, String value, Long companyId) {
		super(name, value);
		setEntityId(companyId);
	}
}
