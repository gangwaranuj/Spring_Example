package com.workmarket.domains.model.requirementset.companytype;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.company.CompanyType;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.EligibilityVisitor;
import com.workmarket.domains.model.requirementset.SolrQueryVisitor;
import org.apache.solr.client.solrj.SolrQuery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@AuditChanges
@Table(name = "company_type_requirement")
public class CompanyTypeRequirement extends AbstractRequirement {
	public static final String HUMAN_NAME = "Company Type";
	public static final String[] FILTERS = {};

	private CompanyTypeRequirable companyTypeRequirable;
	private Long companyTypeId;

	@Transient
	public CompanyTypeRequirable getCompanyTypeRequirable() {
		return companyTypeRequirable != null ? companyTypeRequirable : createCompanyTypeRequirable();
	}

	@Transient
	public void setCompanyTypeRequirable(CompanyTypeRequirable companyTypeRequirable) {
		this.companyTypeId = companyTypeRequirable.getId();
		this.companyTypeRequirable = companyTypeRequirable;
	}

	@Column(name = "company_type_id")
	public Long getCompanyTypeId() {
		return companyTypeId;
	}

	public void setCompanyTypeId(Long companyTypeId) {
		this.companyTypeId = companyTypeId;
	}

	@Override
	@Transient
	public String getHumanTypeName() {
		return HUMAN_NAME;
	}

	@Override
	@Transient
	public void accept(EligibilityVisitor visitor, Criterion criterion) {
		visitor.visit(criterion, this);
	}

	@Override
	@Transient
	public void accept(SolrQueryVisitor visitor, SolrQuery query) {
		visitor.visit(query, this);
	}

	@Transient
	private CompanyTypeRequirable createCompanyTypeRequirable() {
		return new CompanyTypeRequirable(CompanyType.getById(this.getCompanyTypeId()));
	}
}
