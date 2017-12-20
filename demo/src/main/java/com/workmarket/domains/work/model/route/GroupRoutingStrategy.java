package com.workmarket.domains.work.model.route;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import java.util.Set;

@Entity(name = "groupRoutingStrategy")
@DiscriminatorValue(GroupRoutingStrategy.GROUP_ROUTING_STRATEGY)
@AuditChanges
public class GroupRoutingStrategy extends AbstractRoutingStrategy {

	private static final long serialVersionUID = -1367736299728368474L;
	public static final String GROUP_ROUTING_STRATEGY = "group";

	private Set<Long> userGroups;

	public GroupRoutingStrategy() {
		super();
	}

	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(
		name = "group_routing_strategy_association",
		joinColumns = @JoinColumn(name = "routing_strategy_id")
	)
	@Column(name = "user_group_id")
	public Set<Long> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(Set<Long> userGroups) {
		this.userGroups = userGroups;
	}

	@Override
	@Transient
	public String getType() {
		return GROUP_ROUTING_STRATEGY;
	}

	@Override
	@Transient
	public void execute(RoutingVisitor routingVisitor) {
		routingVisitor.visit(this);
	}
}
