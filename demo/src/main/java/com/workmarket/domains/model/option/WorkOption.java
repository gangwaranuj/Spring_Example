package com.workmarket.domains.model.option;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "workOption")
@DiscriminatorValue("work")
public class WorkOption extends Option {

	public static final String MBO_ENABLED = "mbo_enabled";
	public static final String OFFLINE_PAYMENT = "offline_payment";
	public static final String DOCUMENTS_ENABLED = "documents_enabled";

	private static final long serialVersionUID = 6610389809647332177L;
	public WorkOption() {
		super();
	}

	public WorkOption(String name, String value, Long workId) {
		super(name, value);
		setEntityId(workId);
	}
}
