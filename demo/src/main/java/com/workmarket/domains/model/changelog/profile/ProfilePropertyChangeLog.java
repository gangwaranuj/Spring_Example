package com.workmarket.domains.model.changelog.profile;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@DiscriminatorValue("PPC")
@AuditChanges
public class ProfilePropertyChangeLog extends ProfileChangeLog {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 0, max = Constants.NAME_MAX_LENGTH)
	private String propertyName;
	@Size(min = 0, max = Constants.TEXT_MAX_LENGTH)
	private String oldValue;
	@Size(min = 0, max = Constants.TEXT_MAX_LENGTH)
	private String newValue;

	public ProfilePropertyChangeLog() {

	}

	public ProfilePropertyChangeLog(Profile profile, User actor, User masqueradeActor, String propertyName, String oldValue, String newValue) {
		super(profile, actor, masqueradeActor);
		this.propertyName = propertyName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Column(name = "property_name", length = Constants.NAME_MAX_LENGTH, nullable = true)
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	@Column(name = "old_value", length = Constants.TEXT_MAX_LENGTH, nullable = true)
	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	@Column(name = "new_value", length = Constants.TEXT_MAX_LENGTH, nullable = true)
	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	@Transient
	public String getDescription() {
		return "Property changed";
	}
}
