package com.workmarket.domains.model.requirementset.agreement;

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
@Table(name = "agreement_requirement")
public class AgreementRequirement extends AbstractRequirement {
	public static final String HUMAN_NAME = "Agreement";
	public static final String[] FILTERS = {};

	private AgreementRequirable agreementRequirable;

	@ManyToOne
	@JoinColumn(name = "agreement_id")
	@Fetch(FetchMode.JOIN)
	public AgreementRequirable getAgreementRequirable() {
		return agreementRequirable;
	}

	public void setAgreementRequirable(AgreementRequirable agreementRequirable) {
		this.agreementRequirable = agreementRequirable;
	}

	@Override
	@Transient
	public String getHumanTypeName() {
		return HUMAN_NAME;
	}

	@Override
	@Transient
	public boolean allowMultiple() {
		return true;
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
