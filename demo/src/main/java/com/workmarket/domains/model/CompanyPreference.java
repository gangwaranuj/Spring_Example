package com.workmarket.domains.model;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by ianha on 2/13/14
 */
@Entity(name = "companyPreference")
@Table(name = "company_preference")
@AuditChanges
public class CompanyPreference extends AuditedEntity {
	private AutoAcceptEnum autoAccept = AutoAcceptEnum.FIRST_TO_APPLY;
	private Company company;
	private String externalIdDisplayName;
	private boolean externalIdActive = false;
	private Integer externalIdVersion = 0;
	private boolean drugTest;
	private boolean backgroundCheck;

	public CompanyPreference() {}

	public CompanyPreference(Company company) {
		this.company = company;
	}

	@Column(name = "auto_accept")
	@Enumerated(EnumType.STRING)
	public AutoAcceptEnum getAutoAccept() {
		return autoAccept;
	}

	public void setAutoAccept(AutoAcceptEnum autoAccept) {
		this.autoAccept = autoAccept;
	}

	@OneToOne
	@JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
	public Company getCompany() { return this.company; }

	public void setCompany(Company company) { this.company = company; }

	@Column(name = "external_id_display_name")
	public String getExternalIdDisplayName() {
		return externalIdDisplayName;
	}

	public CompanyPreference setExternalIdDisplayName(String externalIdDisplayName) {
		this.externalIdDisplayName = externalIdDisplayName;
		return this;
	}

	@Column(name = "external_id_active", nullable = false)
	public boolean isExternalIdActive() {
		return externalIdActive;
	}

	public void setExternalIdActive(boolean externalIdActive) {
		this.externalIdActive = externalIdActive;
	}

	@Column(name = "external_id_version", nullable = false)
	public Integer getExternalIdVersion() {
		return externalIdVersion;
	}

	public void setExternalIdVersion(Integer externalIdVersion) {
		this.externalIdVersion = externalIdVersion;
	}

	public void incrementExternalIdVersion(){
		this.externalIdVersion++;
	}

	@Column(name = "drug_test", nullable = false)
	public boolean isDrugTest() {
		return drugTest;
	}

	public void setDrugTest(boolean drugTest) {
		this.drugTest = drugTest;
	}

	@Column(name = "background_check", nullable = false)
	public boolean isBackgroundCheck() {
		return backgroundCheck;
	}

	public void setBackgroundCheck(boolean backgroundCheck) {
		this.backgroundCheck = backgroundCheck;
	}
}
