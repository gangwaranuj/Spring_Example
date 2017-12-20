package com.workmarket.domains.model.account;

import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@AuditChanges
@Entity(name = "workFeeConfiguration")
@Table(name = "work_fee_configuration")
public class WorkFeeConfiguration extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	private AccountRegister accountRegister;
	private Calendar activeDate;
	private Calendar replacedDate;
	private Boolean active;

	private List<WorkFeeBand> workFeeBands;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "account_register_id", updatable = false)
	public AccountRegister getAccountRegister() {
		return accountRegister;
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

	public void setActiveDate(Calendar activeDate) {
		this.activeDate = activeDate;
	}

	public void setReplacedDate(Calendar replacedDate) {
		this.replacedDate = replacedDate;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "work_fee_configuration_id", referencedColumnName = "id")
	public List<WorkFeeBand> getWorkFeeBands() {
		return workFeeBands;
	}

	public void setWorkFeeBands(List<WorkFeeBand> workFeeBands) {
		this.workFeeBands = workFeeBands;
	}

	public void setAccountRegister(AccountRegister accountRegister) {
		this.accountRegister = accountRegister;
	}

}
