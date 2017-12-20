package com.workmarket.domains.model.changelog.company;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@DiscriminatorValue("CRF")
@AuditChanges
public class CompanyRemoveFeatureChangeLog extends CompanyChangeLog {
	private static final long serialVersionUID = 6706920984945861428L;

	@NotNull
	@Size(min = 0, max = Constants.NAME_MAX_LENGTH)
	private String featureName;

	public CompanyRemoveFeatureChangeLog() {}

	public CompanyRemoveFeatureChangeLog(Company company, User actor, User masqueradeActor, String featureName) {
		super(company, actor, masqueradeActor);
		this.featureName = featureName;
	}

	@Column(name = "property_name", length = Constants.NAME_MAX_LENGTH, nullable = true)
	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	@Transient
	public String getDescription() {
		return "Remove feature";
	}
}
