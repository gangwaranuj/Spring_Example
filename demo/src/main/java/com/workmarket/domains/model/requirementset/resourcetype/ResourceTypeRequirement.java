package com.workmarket.domains.model.requirementset.resourcetype;

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
@Table(name = "resource_type_requirement")
public class ResourceTypeRequirement extends AbstractRequirement {
	public static final String HUMAN_NAME = "Worker Type";
	public static final String[] FILTERS = {};

	private ResourceTypeRequirable resourceTypeRequirable;
	private Long resourceTypeId;

	@Transient
	public ResourceTypeRequirable getResourceTypeRequirable() {
		return resourceTypeRequirable != null ? resourceTypeRequirable : createResourceType();
	}

	@Transient
	public void setResourceTypeRequirable(ResourceTypeRequirable resourceTypeRequirable) {
		this.resourceTypeId = resourceTypeRequirable.getId();
		this.resourceTypeRequirable = resourceTypeRequirable;
	}

	@Column(name = "resource_type_id")
	public Long getResourceTypeId() {
		return resourceTypeId;
	}

	public void setResourceTypeId(Long resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}

	@Override
	@Transient
	public String getHumanTypeName() {
		return HUMAN_NAME;
	}

	@Override
	public void accept(EligibilityVisitor visitor, Criterion criterion) {
		visitor.visit(criterion, this);
	}

	@Override
	@Transient
	public void accept(SolrQueryVisitor visitor, SolrQuery query) {
		visitor.visit(query, this);
	}

	private ResourceTypeRequirable createResourceType() {
		return new ResourceTypeRequirable(ResourceType.getById(this.getResourceTypeId()));
	}
}
