package com.workmarket.domains.model.requirementset.groupmembership;

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
@Table(name = "group_membership_requirement")
public class GroupMembershipRequirement extends AbstractRequirement {
	public static final String HUMAN_NAME = "Group Membership";
	public static final String[] FILTERS = {};

	private GroupMembershipRequirable groupMembershipRequirable;

	@ManyToOne
	@JoinColumn(name = "user_group_id")
	@Fetch(FetchMode.JOIN)
	public GroupMembershipRequirable getGroupMembershipRequirable() {
		return groupMembershipRequirable;
	}

	public void setGroupMembershipRequirable(GroupMembershipRequirable groupMembershipRequirable) {
		this.groupMembershipRequirable = groupMembershipRequirable;
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
