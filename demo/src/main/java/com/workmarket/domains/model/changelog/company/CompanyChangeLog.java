package com.workmarket.domains.model.changelog.company;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "companyChangeLog")
@Table(name = "company_changelog")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("C")
@AuditChanges
public class CompanyChangeLog extends AuditedEntity {
	private static final long serialVersionUID = 1L;

	private Company company;
	private User actor;
	private User masqueradeActor;

	protected CompanyChangeLog() {
	}

	protected CompanyChangeLog(Company company, User actor, User masqueradeActor) {
		this.company = company;
		this.actor = actor;
		this.masqueradeActor = masqueradeActor;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {})
	@JoinColumn(name = "company_id")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {})
	@JoinColumn(name = "actor_id")
	public User getActor() {
		return actor;
	}

	public void setActor(User actor) {
		this.actor = actor;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {})
	@JoinColumn(name = "masquerade_actor_id")
	public User getMasqueradeActor() {
		return masqueradeActor;
	}

	public void setMasqueradeActor(User masqueradeActor) {
		this.masqueradeActor = masqueradeActor;
	}

	@Transient
	public String getChangeLogType() {
		return this.getClass().getSimpleName();
	}

	@Transient
	public String getDescription() {
		return "Company changed";
	}
}
