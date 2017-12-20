package com.workmarket.domains.model.requirementset.drugtest;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.EligibilityVisitor;
import com.workmarket.domains.model.requirementset.SolrQueryVisitor;
import org.apache.solr.client.solrj.SolrQuery;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@AuditChanges
@Table(name = "drug_test_requirement")
public class DrugTestRequirement extends AbstractRequirement {
	public static final String DEFAULT_NAME = "Passed Sterling Drug Test";
	public static final String HUMAN_NAME = "Drug Test";
	public static final String[] FILTERS = {};

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
