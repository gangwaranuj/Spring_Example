package com.workmarket.domains.model.requirementset.abandon;

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
@Table(name = "abandon_requirement")
public class AbandonRequirement extends AbstractRequirement {
	public static final String DEFAULT_NAME = "Abandon Requirement";
	public static final String NAME_TEMPLATE = "%s within last 6 months";
	public static final String HUMAN_NAME = "Maximum Abandoned Assignments";
	public static final String[] FILTERS = {};

	private int maximumAllowed; // max ceiling of abandons

	@Column(name = "maximum_allowed")
	public int getMaximumAllowed() {
		return maximumAllowed;
	}

	public void setMaximumAllowed(int maximumAllowed) {
		this.maximumAllowed = maximumAllowed;
	}

	@Transient
	public String getName() {
		return String.format(NAME_TEMPLATE, maximumAllowed);
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
