package com.workmarket.domains.model.account;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@AuditChanges
@Entity(name = "workFeeHistory")
@Table(name = "work_fee_history")
public class WorkFeeHistory extends AuditedEntity {


	private static final long serialVersionUID = 1L;


	private WorkFeeConfiguration workFeeConfiguration;
	private AccountRegister accountRegister;

	private BigDecimal percentage;
	private Calendar activeDate;
	private Calendar replacedDate;
	private Boolean active;

	@Column(name = "percentage")
	public BigDecimal getPercentage() {
		return percentage;
	}

	@Column(name = "active_date")
	public Calendar getActiveDate() {
		return activeDate;
	}

	@Column(name = "replaced_date")
	public Calendar getReplacedDate() {
		return replacedDate;
	}

	@Column(name = "active")
	public Boolean getActive() {
		return active;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public void setActiveDate(Calendar activeDate) {
		this.activeDate = activeDate;
	}

	public void setReplacedDate(Calendar replacedDate) {
		this.replacedDate = replacedDate;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "work_fee_configuration_id", referencedColumnName = "id")
	public WorkFeeConfiguration getWorkFeeConfiguration() {
		return workFeeConfiguration;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "account_register_id", referencedColumnName = "id", updatable = false)
	public AccountRegister getAccountRegister() {
		return accountRegister;
	}

	public void setWorkFeeConfiguration(WorkFeeConfiguration workFeeConfiguration) {
		this.workFeeConfiguration = workFeeConfiguration;
	}

	public void setAccountRegister(AccountRegister accountRegister) {
		this.accountRegister = accountRegister;
	}
}
