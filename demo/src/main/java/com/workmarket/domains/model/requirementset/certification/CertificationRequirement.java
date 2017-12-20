package com.workmarket.domains.model.requirementset.certification;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.requirementset.AbstractExpirableRequirement;
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
@Table(name = "certification_requirement")
public class CertificationRequirement extends AbstractExpirableRequirement {
	public static final String HUMAN_NAME = "Certification";
	public static final String[] FILTERS = {};

	private CertificationRequirable certificationRequirable;

	@ManyToOne
	@JoinColumn(name = "certification_id")
	@Fetch(FetchMode.JOIN)
	public CertificationRequirable getCertificationRequirable() {
		return certificationRequirable;
	}

	public void setCertificationRequirable(CertificationRequirable certificationRequirable) {
		this.certificationRequirable = certificationRequirable;
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
