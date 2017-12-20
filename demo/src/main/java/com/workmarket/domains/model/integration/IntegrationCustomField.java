package com.workmarket.domains.model.integration;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="integrationCustomField")
@Table(name="integration_custom_field")
@AttributeOverrides({
	@AttributeOverride(name="code", column=@Column(length=50)),
	@AttributeOverride(name="description", column=@Column(length=100))
})
public class IntegrationCustomField extends LookupEntity {

	private static final long serialVersionUID = 1L;

	//autotask
	public static final String WM_STATUS = "wm.status";
	public static final String WM_WORK_ID = "wm.work.id";
	public static final String WM_RESOURCE_ID = "wm.resource.id";
	public static final String WM_RESOURCE_FIRST_NAME = "wm.resource.first.name";
	public static final String WM_RESOURCE_LAST_NAME = "wm.resource.last.name";
	public static final String WM_RESOURCE_EMAIL = "wm.resource.email";
	public static final String WM_RESOURCE_PHONE = "wm.resource.phone";
	public static final String WM_RESOURCE_PHONE_MOBILE = "wm.resource.phone.mobile";
	public static final String WM_CHECKEDIN_ON = "wm.checkedin.on";
	public static final String WM_CHECKEDOUT_ON = "wm.checkedout.on";
	public static final String WM_RESOLUTION = "wm.resolution";
	public static final String WM_MAX_SPEND_LIMIT = "wm.max.spend.limit";
	public static final String WM_ACTUAL_SPEND_LIMIT = "wm.actual.spend.limit";
	public static final String WM_HOURS_WORKED = "wm.hours.worked";
	public static final String WM_TOTAL_COST = "wm.total.cost";
	public static final String WM_SPENT_LIMIT = "wm.spend.limit";
	public static final String WM_ADDITIONAL_EXPENSES = "wm.additional.expenses";

	private boolean enabled;

	public IntegrationCustomField() {}

	public IntegrationCustomField(String code) {
		super(code);
	}

	@Column(name="enabled", nullable=false, length=1)
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
