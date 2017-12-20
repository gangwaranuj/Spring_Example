package com.workmarket.domains.model.clientservice;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "clientServiceAlert")
@Table(name = "client_service_alert")
@NamedQueries({})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("GENERIC")
@AuditChanges
public class ClientServiceAlert extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	public static final String GENERIC = "GENERIC";

	@NotNull
	private Company company;

	@Size(min = Constants.TEXT_MIN_LENGTH, max = Constants.TEXT_MAX_LENGTH)
	private String description;

	@Column(name = "description", nullable = true, length = Constants.TEXT_MAX_LENGTH)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
}
