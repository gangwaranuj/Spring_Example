package com.workmarket.domains.work.model.route;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Set;

@Entity(name = "vendorSearchRoutingStrategy")
@DiscriminatorValue(VendorSearchRoutingStrategy.VENDOR_SEARCH_ROUTING_STRATEGY)
@AuditChanges
public class VendorSearchRoutingStrategy extends AbstractRoutingStrategy {

    public static final String VENDOR_SEARCH_ROUTING_STRATEGY = "vendorsearch";

    private Set<String> companyNumbers;
    private Long dispatcherId;


    public VendorSearchRoutingStrategy() {
        super();
    }

    @Transient
    public Set<String> getCompanyNumbers() {
        return companyNumbers;
    }

    public void setCompanyNumbers(final Set<String> companyNumbers) {
        this.companyNumbers = companyNumbers;
    }

    @Transient
    public Long getDispatcherId() {
        return dispatcherId;
    }

    public void setDispatcherId(Long dispatcherId) {
        this.dispatcherId = dispatcherId;
    }

    @Override
    @Transient
    public String getType() {
        return VENDOR_SEARCH_ROUTING_STRATEGY;
    }

    @Override
    @Transient
    public void execute(RoutingVisitor routingVisitor) {
        routingVisitor.visit(this);
    }
}
