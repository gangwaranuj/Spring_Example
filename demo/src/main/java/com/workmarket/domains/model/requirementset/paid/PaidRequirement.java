package com.workmarket.domains.model.requirementset.paid;

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
@Table(name = "paid_requirement")
public class PaidRequirement extends AbstractRequirement {
	public static final String DEFAULT_NAME = "Paid Requirement";
	public static final String HUMAN_NAME = "Paid";
	public static final String[] FILTERS = {"GROUP"};

	private int minimumAssignments; // minimum of paid assignments required

	@Column(name = "minimum_assignments")
	public int getMinimumAssignments() {
		return minimumAssignments;
	}

	public void setMinimumAssignments(int minimumAssignments) {
		this.minimumAssignments = minimumAssignments;
	}

	@Transient
	public String getName() {
		return "Min " + minimumAssignments + " paid assignments within last 3 months";
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
