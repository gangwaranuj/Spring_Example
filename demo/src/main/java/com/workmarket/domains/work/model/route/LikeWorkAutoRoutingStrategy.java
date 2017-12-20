package com.workmarket.domains.work.model.route;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity(name = "LikeWorkAutoRoutingStrategy")
@DiscriminatorValue(LikeWorkAutoRoutingStrategy.LIKE_WORK_AUTO_ROUTING_STRATEGY)
@AuditChanges
public class LikeWorkAutoRoutingStrategy extends AbstractRoutingStrategy {

	public static final String LIKE_WORK_AUTO_ROUTING_STRATEGY = "likework";

	@Override
	@Transient
	public String getType() {
		return LIKE_WORK_AUTO_ROUTING_STRATEGY;
	}

	@Override
	@Transient
	public void execute(RoutingVisitor routingVisitor) {
		routingVisitor.visit(this);
	}
}
