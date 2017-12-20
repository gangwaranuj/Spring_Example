package com.workmarket.domains.work.model.route;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity(name = "autoRoutingStrategy")
@DiscriminatorValue(AutoRoutingStrategy.AUTO_ROUTING_STRATEGY)
@AuditChanges
public class AutoRoutingStrategy extends AbstractRoutingStrategy {

	public static final String AUTO_ROUTING_STRATEGY = "auto";

	@Override
	@Transient
	public String getType() {
		return AUTO_ROUTING_STRATEGY;
	}

	@Override
	@Transient
	public void execute(RoutingVisitor routingVisitor) {
		routingVisitor.visit(this);
	}
}
