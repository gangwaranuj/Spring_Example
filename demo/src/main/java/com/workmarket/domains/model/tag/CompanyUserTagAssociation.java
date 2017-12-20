package com.workmarket.domains.model.tag;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity(name="companyUserTagAssociation")
@NamedQueries({
		@NamedQuery(name="CompanyUserTagAssociation.findByIds", query="select a from companyUserTagAssociation a where a.company.id = :companyId and a.user.id = :userId and a.tag.id = :tagId"),

		@NamedQuery(name="CompanyUserTagAssociation.findAllActiveCompanyUserTagAssociations",
				query="select a from companyUserTagAssociation a where a.tag.class = com.workmarket.domains.model.tag.CompanyTag and a.company.id = :companyId and a.user.id = :userId and a.deleted = false"),

		@NamedQuery(name="CompanyUserTagAssociation.findAllActiveCompanyAdminUserTagAssociations",
				query="select a from companyUserTagAssociation a where a.tag.class = com.workmarket.domains.model.tag.CompanyAdminTag and a.company.id = :companyId and a.user.id = :userId and a.deleted = false")
})
@DiscriminatorValue("CUT")
@AuditChanges
public class CompanyUserTagAssociation extends UserTagAssociation
{
	private static final long serialVersionUID = 1L;

	private Company company;

	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {})
	@JoinColumn(name = "company_id")
	public Company getCompany()
	{
		return company;
	}

	public void setCompany(Company company)
	{
		this.company = company;
	}
}
