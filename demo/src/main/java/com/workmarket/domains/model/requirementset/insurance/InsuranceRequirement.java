package com.workmarket.domains.model.requirementset.insurance;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.requirementset.AbstractExpirableRequirement;
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
import java.math.BigDecimal;

@Entity
@AuditChanges
@Table(name = "insurance_requirement")
public class InsuranceRequirement extends AbstractExpirableRequirement {
	public static final String HUMAN_NAME = "Insurance";
	public static final String NAME_TEMPLATE = "%s ($%s)";
	public static final String[] FILTERS = {};

	private InsuranceRequirable insuranceRequirable;
	private BigDecimal minimumCoverageAmount = new BigDecimal("0");

	@ManyToOne
	@JoinColumn(name = "insurance_id")
	@Fetch(FetchMode.JOIN)
	public InsuranceRequirable getInsuranceRequirable() {
		return insuranceRequirable;
	}

	public void setInsuranceRequirable(InsuranceRequirable insuranceRequirable) {
		this.insuranceRequirable = insuranceRequirable;
	}

	@Column(name = "minimum_coverage_amount")
	public BigDecimal getMinimumCoverageAmount() {
		return minimumCoverageAmount;
	}

	public void setMinimumCoverageAmount(BigDecimal minimumCoverageAmount) {
		this.minimumCoverageAmount = minimumCoverageAmount;
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
	public boolean allowMultiple() {
		return true;
	}

	@Override
	@Transient
	public void accept(SolrQueryVisitor visitor, SolrQuery query) {
		visitor.visit(query, this);
	}
}
