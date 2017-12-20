package com.workmarket.domains.model.account;

import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.pricing.AccountPricingServiceTypeEntity;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;

@Entity(name = "work_resource_transaction")
@Table(name = "work_resource_transaction")
@AuditChanges
public class WorkResourceTransaction extends RegisterTransaction {

	private static final long serialVersionUID = -467550612286657380L;

	private WorkResource workResource;
	//Was the transaction part of an invoice bundle??
	private boolean bundlePayment;
	//Was the transaction part of a batch payment (not within an invoice bundle);
	private boolean batchPayment = false;
	private Project project;
	private AccountPricingServiceTypeEntity accountPricingServiceTypeEntity;

	@ManyToOne
	@JoinColumn(name = "work_resource_id", referencedColumnName = "id", nullable = true)
	public WorkResource getWorkResource() {
		return workResource;
	}

	public void setWorkResource(WorkResource workResource) {
		this.workResource = workResource;
	}

	@Column(name = "bundle_payment", nullable = false)
	public boolean isBundlePayment() {
		return bundlePayment;
	}

	public void setBundlePayment(boolean bundlePayment) {
		this.bundlePayment = bundlePayment;
	}

	@Column(name = "batch_payment", nullable = false)
	public boolean isBatchPayment() {
		return batchPayment;
	}

	public void setBatchPayment(boolean batchPayment) {
		this.batchPayment = batchPayment;
	}

	@ManyToOne
	@JoinColumn(name = "project_id", referencedColumnName = "id")
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Embedded
	public AccountPricingServiceTypeEntity getAccountPricingServiceTypeEntity() {
		return accountPricingServiceTypeEntity;
	}

	public void setAccountPricingServiceTypeEntity(AccountPricingServiceTypeEntity accountPricingServiceTypeEntity) {
		this.accountPricingServiceTypeEntity = accountPricingServiceTypeEntity;
	}

	@Transient
	public AccountPricingType getAccountPricingType() {
		return accountPricingServiceTypeEntity.getAccountPricingType();
	}

	@Transient
	public AccountServiceType getAccountServiceType() {
		return accountPricingServiceTypeEntity.getAccountServiceType();
	}

	@Override
	public String toString() {
		return "WorkResourceTransaction [workResource=" + workResource + ", bundlePayment="
				+ bundlePayment + ", getAmount()=" + getAmount() + ", getRegisterTransactionType()=" + getRegisterTransactionType().getCode() + ", getPendingFlag()=" + getPendingFlag() + ", getId()="
				+ getId() + "]";
	}

}
