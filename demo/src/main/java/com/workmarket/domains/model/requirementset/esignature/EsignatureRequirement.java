package com.workmarket.domains.model.requirementset.esignature;

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
@Table(name = "esignature_requirement")
public class EsignatureRequirement extends AbstractRequirement {

	public static final String DEFAULT_NAME = "eSignature";
	public static final String HUMAN_NAME = "eSignature";
	public static final String[] FILTERS = {"REQUIREMENT_SET"};

	private String templateUuid;

	@Column(name = "template_uuid")
	public String getTemplateUuid() {
		return templateUuid;
	}

	public void setTemplateUuid(final String templateUuid) {
		this.templateUuid = templateUuid;
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
