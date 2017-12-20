package com.workmarket.domains.model.requirementset.companywork;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.EligibilityVisitor;
import com.workmarket.domains.model.requirementset.SolrQueryVisitor;
import org.apache.solr.client.solrj.SolrQuery;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@AuditChanges
@Table(name = "company_work_requirement")
public class CompanyWorkRequirement extends AbstractRequirement {
	public static final String DEFAULT_NAME = "Company Work Requirement";
	public static final String HUMAN_NAME = "Company Work";
	public static final String[] FILTERS = {"GROUP"};

	private int minimumWorkCount; // minimum amount of times user has worked for this company to pass requirement
	private CompanyWorkRequirable companyWorkRequirable;

	@Column(name = "minimum_work_count")
	public int getMinimumWorkCount() {
		return minimumWorkCount;
	}

	public void setMinimumWorkCount(int minimumWorkCount) {
		this.minimumWorkCount = minimumWorkCount;
	}

	@ManyToOne
	@JoinColumn(name = "company_id")
	@Fetch(FetchMode.JOIN)
	public CompanyWorkRequirable getCompanyWorkRequirable() { return companyWorkRequirable; }

	public void setCompanyWorkRequirable(CompanyWorkRequirable companyWorkRequirable) {
		this.companyWorkRequirable = companyWorkRequirable;
	}

	@Transient
	public String getName() {
		return "Minimum " + minimumWorkCount + " assignments with this company";
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
}
