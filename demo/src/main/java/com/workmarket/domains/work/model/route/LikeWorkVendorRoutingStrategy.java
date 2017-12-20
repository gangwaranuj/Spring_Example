package com.workmarket.domains.work.model.route;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * LikeWork strategy for vendor.
 */
@Entity(name = "likeWorkVendorRoutingStrategy")
@DiscriminatorValue(LikeWorkVendorRoutingStrategy.LIKE_WORK_VENDOR_ROUTING_STRATEGY)
@AuditChanges
public class LikeWorkVendorRoutingStrategy extends AbstractRoutingStrategy {

    public static final String LIKE_WORK_VENDOR_ROUTING_STRATEGY = "likeworkvendor";

    @Override
    @Transient
    public String getType() {
        return LIKE_WORK_VENDOR_ROUTING_STRATEGY;
    }

    @Override
    @Transient
    public void execute(RoutingVisitor routingVisitor) {
        routingVisitor.visit(this);
    }
}
