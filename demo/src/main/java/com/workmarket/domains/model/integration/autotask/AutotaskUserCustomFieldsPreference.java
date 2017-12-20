package com.workmarket.domains.model.integration.autotask;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.integration.IntegrationCustomField;

import javax.persistence.*;

@Entity(name="autotaskUserCustomFieldsPreference")
@Table(name="autotask_user_custom_fields_preference")
@NamedQueries({
		@NamedQuery(name="autotaskUserCustomFieldsPreference.byAutotaskUserAndFieldType",
				query="from autotaskUserCustomFieldsPreference where autotaskUser.id = :autotask_user_id and integrationCustomField.code = :integration_custom_field_code"),
		@NamedQuery(name="autotaskUserCustomFieldsPreference.byAutataskUser",
				query="from autotaskUserCustomFieldsPreference where autotaskUser.id = :autotask_user_id")
})
public class AutotaskUserCustomFieldsPreference extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	private AutotaskUser autotaskUser;
	private IntegrationCustomField integrationCustomField;
	private String customFieldName;
	private boolean enabled;

	@ManyToOne(fetch= FetchType.LAZY, optional=false)
	@JoinColumn(name="autotask_user_id")
	public AutotaskUser getAutotaskUser() {
		return autotaskUser;
	}

	public void setAutotaskUser(AutotaskUser autotaskUser) {
		this.autotaskUser = autotaskUser;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="integration_custom_field_code", referencedColumnName="code", nullable=false)
	public IntegrationCustomField getIntegrationCustomField() {
		return integrationCustomField;
	}

	public void setIntegrationCustomField(IntegrationCustomField integrationCustomField) {
		this.integrationCustomField = integrationCustomField;
	}

	@Column(name="autotask_custom_field_name", nullable=false, length=1)
	public String getCustomFieldName() {
		return customFieldName;
	}

	public void setCustomFieldName(String customFieldName) {
		this.customFieldName = customFieldName;
	}

	@Column(name="enabled", nullable=false, length=1)
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}