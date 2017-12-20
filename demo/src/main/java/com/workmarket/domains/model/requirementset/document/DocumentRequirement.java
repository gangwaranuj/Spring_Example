package com.workmarket.domains.model.requirementset.document;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
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

@Entity
@AuditChanges
@Table(name = "document_requirement")
public class DocumentRequirement extends AbstractRequirement {
	public static final String HUMAN_NAME = "Document";
	public static final String[] FILTERS = {"REQUIREMENT_SET"};

	private DocumentRequirable documentRequirable;
	private boolean requiresExpirationDate;

	@ManyToOne
	@JoinColumn(name = "document_id")
	@Fetch(FetchMode.JOIN)
	public DocumentRequirable getDocumentRequirable() {
		return documentRequirable;
	}

	public void setDocumentRequirable(DocumentRequirable documentRequirable) {
		this.documentRequirable = documentRequirable;
	}

	@Column(name = "requires_expiration_date")
	public boolean isRequiresExpirationDate() {
		return requiresExpirationDate;
	}

	public void setRequiresExpirationDate(boolean requiresExpirationDate) {
		this.requiresExpirationDate = requiresExpirationDate;
	}

	@Override
	@Transient
	public boolean allowMultiple() {
		return true;
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
