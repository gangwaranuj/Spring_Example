package com.workmarket.domains.work.model.route;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * LikeGroup strategy for vendor.
 */
@Entity(name = "likeGroupVendorRoutingStrategy")
@DiscriminatorValue(LikeGroupVendorRoutingStrategy.LIKE_GROUP_VENDOR_ROUTING_STRATEGY)
@AuditChanges
public class LikeGroupVendorRoutingStrategy extends AbstractRoutingStrategy {

    public static final String LIKE_GROUP_VENDOR_ROUTING_STRATEGY = "likegroupvendor";

    @Override
    @Transient
    public String getType() {
        return LIKE_GROUP_VENDOR_ROUTING_STRATEGY;
    }

    @Override
    @Transient
    public void execute(RoutingVisitor routingVisitor) {
        routingVisitor.visit(this);
    }
}
