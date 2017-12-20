package com.workmarket.domains.model.changelog.user;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@DiscriminatorValue("UPC")
@AuditChanges
public class UserPropertyChangeLog extends UserChangeLog {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 0, max = Constants.NAME_MAX_LENGTH)
	private String propertyName;
	@Size(min = 0, max = Constants.TEXT_MAX_LENGTH)
	private String oldValue;
	@Size(min = 0, max = Constants.TEXT_MAX_LENGTH)
	private String newValue;

	public UserPropertyChangeLog() {}


	public UserPropertyChangeLog(Long user, Long actor, Long masqueradeActor, String propertyName, String oldValue, String newValue) {
		super(user, actor, masqueradeActor);
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
	@Override
	public String getDescription() {
		return "Property changed";
	}
}
