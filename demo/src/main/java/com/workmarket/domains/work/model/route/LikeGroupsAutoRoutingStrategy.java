package com.workmarket.domains.work.model.route;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity(name = "likeGroupsAutoRoutingStrategy")
@DiscriminatorValue(LikeGroupsAutoRoutingStrategy.LIKE_GROUPS_AUTO_ROUTING_STRATEGY)
@AuditChanges
public class LikeGroupsAutoRoutingStrategy extends AbstractRoutingStrategy {

	public static final String LIKE_GROUPS_AUTO_ROUTING_STRATEGY = "likegroup";

	@Override
	@Transient
	public String getType() {
		return LIKE_GROUPS_AUTO_ROUTING_STRATEGY;
	}

	@Override
	@Transient
	public void execute(RoutingVisitor routingVisitor) {
		routingVisitor.visit(this);
	}
}
