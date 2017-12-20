package com.workmarket.domains.work.model.route;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Polymath strategy for vendor.
 */
@Entity(name = "polymathVendorRoutingStrategy")
@DiscriminatorValue(PolymathVendorRoutingStrategy.POLYMATH_VENDOR_ROUTING_STRATEGY)
@AuditChanges
public class PolymathVendorRoutingStrategy  extends AbstractRoutingStrategy {

    public static final String POLYMATH_VENDOR_ROUTING_STRATEGY = "polymathvendor";

    @Override
    @Transient
    public String getType() {
        return POLYMATH_VENDOR_ROUTING_STRATEGY;
    }

    @Override
    @Transient
    public void execute(RoutingVisitor routingVisitor) {
        routingVisitor.visit(this);
    }
}
