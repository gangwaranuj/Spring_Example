package com.workmarket.domains.model.requirementset.test;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.EligibilityVisitor;
import com.workmarket.domains.model.requirementset.SolrQueryVisitor;
import org.apache.solr.client.solrj.SolrQuery;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@AuditChanges
@Table(name = "test_requirement")
public class TestRequirement extends AbstractRequirement {
	public static final String HUMAN_NAME = "Test";
	public static final String[] FILTERS = {};

	private TestRequirable testRequirable;

	@ManyToOne
	@JoinColumn(name = "test_id")
	@Fetch(FetchMode.JOIN)
	@Where(clause = "type = 'graded'")
	public TestRequirable getTestRequirable() {
		return testRequirable;
	}

	public void setTestRequirable(TestRequirable test) {
		this.testRequirable = test;
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
