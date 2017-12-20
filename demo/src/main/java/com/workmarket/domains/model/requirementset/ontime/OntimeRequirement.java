package com.workmarket.domains.model.requirementset.ontime;

import com.workmarket.domains.model.audit.AuditChanges;
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
@Table(name = "ontime_requirement")
public class OntimeRequirement extends AbstractRequirement {
	public static final String DEFAULT_NAME = "On-time Requirement";
	public static final String HUMAN_NAME = "Minimum On-time Arrival";
	public static final String[] FILTERS = {};

	private int minimumPercentage; // 0..100, minimum ontime percent user needs to meet requirement

	@Column(name = "minimum_percentage")
	public int getMinimumPercentage() {
		return minimumPercentage;
	}

	public void setMinimumPercentage(int minimumPercentage) {
		this.minimumPercentage = minimumPercentage;
	}

	@Transient
	public String getName() {
		return minimumPercentage + "% within last 3 months";
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
