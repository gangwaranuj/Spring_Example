package com.workmarket.domains.model.requirementset.industry;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.EligibilityVisitor;
import com.workmarket.domains.model.requirementset.SolrQueryVisitor;
import org.apache.solr.client.solrj.SolrQuery;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@AuditChanges
@Table(name = "industry_requirement")
public class IndustryRequirement extends AbstractRequirement {
	public static final String HUMAN_NAME = "Industry";
	public static final String[] FILTERS = {};

	private IndustryRequirable industryRequirable;

	@ManyToOne
	@JoinColumn(name = "industry_id")
	@Fetch(FetchMode.JOIN)
	public IndustryRequirable getIndustryRequirable() {
		return industryRequirable;
	}

	public void setIndustryRequirable(IndustryRequirable industryRequirable) {
		this.industryRequirable = industryRequirable;
	}

	@Override
	@Transient
	public boolean allowMultiple() {
		return true;
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
