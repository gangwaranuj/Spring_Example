package com.workmarket.domains.model.requirementset;

import com.workmarket.domains.model.audit.AuditedEntity;
import org.apache.solr.client.solrj.SolrQuery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "requirement")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class AbstractRequirement extends AuditedEntity {
	private RequirementSet requirementSet;
	private boolean mandatory = true;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requirement_set_id", updatable = false)
	public RequirementSet getRequirementSet() {
		return this.requirementSet;
	}

	public void setRequirementSet(RequirementSet requirementSet) {
		this.requirementSet = requirementSet;
	}

	@Transient
	public abstract String getHumanTypeName();

	@Transient
	public abstract void accept(EligibilityVisitor visitor, Criterion criterion);

	@Transient
	public abstract void accept(SolrQueryVisitor visitor, SolrQuery query);

	@Column(name = "mandatory")
	public boolean isMandatory() { return mandatory; }

	public void setMandatory(boolean mandatory) { this.mandatory = mandatory; }

	@Transient
	public boolean allowMultiple() {
		return false;
	}

}
