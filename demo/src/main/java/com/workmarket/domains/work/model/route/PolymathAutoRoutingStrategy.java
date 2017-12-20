package com.workmarket.domains.work.model.route;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity(name = "PolymathAutoRoutingStrategy")
@DiscriminatorValue(PolymathAutoRoutingStrategy.POLYMATH_AUTO_ROUTING_STRATEGY)
@AuditChanges
public class PolymathAutoRoutingStrategy extends AbstractRoutingStrategy {

	public static final String POLYMATH_AUTO_ROUTING_STRATEGY = "polymath";

	@Override
	@Transient
	public String getType() {
		return POLYMATH_AUTO_ROUTING_STRATEGY;
	}

	@Override
	@Transient
	public void execute(RoutingVisitor routingVisitor) {
		routingVisitor.visit(this);
	}
}
