package com.workmarket.domains.model.requirementset.deliverableontime;

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
@Table(name = "deliverable_ontime_requirement")
public class DeliverableOnTimeRequirement extends AbstractRequirement {
	public static final String DEFAULT_NAME = "Deliverable On-time Requirement";
	public static final String HUMAN_NAME = "Minimum On-time Deliverables";
	public static final String[] FILTERS = {};

	// 0..100, minimum deliverable on-time percentage user needs to meet requirement
	private int minimumPercentage;

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
