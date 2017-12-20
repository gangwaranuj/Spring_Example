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

@Entity(name = "vendorRoutingStrategy")
@DiscriminatorValue(VendorRoutingStrategy.VENDOR_ROUTING_STRATEGY)
@AuditChanges
public class VendorRoutingStrategy extends AbstractRoutingStrategy {

	public static final String VENDOR_ROUTING_STRATEGY = "vendor";

	private Set<Long> companyIds;

	private Set<String> companyNumbers;

	public VendorRoutingStrategy() { super(); }

	@ElementCollection(fetch= FetchType.EAGER)
	@CollectionTable(name = "vendor_routing_strategy_association",
			joinColumns = @JoinColumn(name = "routing_strategy_id"))
	@Column(name = "company_id")
	public Set<Long> getCompanyIds() { return companyIds; }

	public void setCompanyIds(Set<Long> companyIds) { this.companyIds = companyIds; }

	@Transient
	public Set<String> getCompanyNumbers() { return companyNumbers; }

	public void setCompanyNumbers(Set<String> companyNumbers) { this.companyNumbers = companyNumbers; }

	@Override
	@Transient
	public String getType() { return VENDOR_ROUTING_STRATEGY; }

	@Override
	@Transient
	public void execute(RoutingVisitor routingVisitor) { routingVisitor.visit(this); }
}
