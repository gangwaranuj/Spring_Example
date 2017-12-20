package com.workmarket.domains.work.model.route;

public interface RoutingVisitor {
	void visit(AutoRoutingStrategy routingStrategy);
	void visit(LikeGroupsAutoRoutingStrategy routingStrategy);
	void visit(UserRoutingStrategy routingStrategy);
	void visit(GroupRoutingStrategy routingStrategy);
	void visit(PeopleSearchRoutingStrategy routingStrategy);
	void visit(LikeWorkAutoRoutingStrategy routingStrategy);
	void visit(PolymathAutoRoutingStrategy routingStrategy);
	void visit(VendorRoutingStrategy routingStrategy);
	void visit(VendorSearchRoutingStrategy routingStrategy);
	void visit(LikeGroupVendorRoutingStrategy routingStrategy);
	void visit(LikeWorkVendorRoutingStrategy routingStrategy);
	void visit(PolymathVendorRoutingStrategy routingStrategy);
}
