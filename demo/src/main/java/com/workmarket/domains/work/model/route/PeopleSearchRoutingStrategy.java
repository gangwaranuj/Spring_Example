package com.workmarket.domains.work.model.route;


import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Set;

@Entity(name = "peopleSearchRoutingStrategy")
@DiscriminatorValue(PeopleSearchRoutingStrategy.PEOPLE_SEARCH_ROUTING_STRATEGY)
@AuditChanges
public class PeopleSearchRoutingStrategy extends AbstractRoutingStrategy {

	public static final String PEOPLE_SEARCH_ROUTING_STRATEGY = "peoplesearch";

	private Set<String> userNumbers;

	private Long dispatcherId;

	public PeopleSearchRoutingStrategy() {
		super();
	}

	@Transient
	public Set<String> getUserNumbers() {
		return userNumbers;
	}

	public void setUserNumbers(final Set<String> userNumbers) {
		this.userNumbers = userNumbers;
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
		return PEOPLE_SEARCH_ROUTING_STRATEGY;
	}

	@Override
	public void execute(RoutingVisitor routingVisitor) {
		routingVisitor.visit(this);
	}
}
