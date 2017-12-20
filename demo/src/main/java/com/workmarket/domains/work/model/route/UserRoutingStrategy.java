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

@Entity(name = "userRoutingStrategy")
@DiscriminatorValue(UserRoutingStrategy.USER_ROUTING_STRATEGY)
@AuditChanges
public class UserRoutingStrategy extends AbstractRoutingStrategy {

	private static final long serialVersionUID = -1367736299728368474L;
	public static final String USER_ROUTING_STRATEGY = "user";

	private Set<String> userNumbers;

	private Set<Long> userIds;

	public UserRoutingStrategy() {
		super();
	}

	@ElementCollection(fetch= FetchType.EAGER)
	@CollectionTable(name = "user_routing_strategy_association",
			joinColumns = @JoinColumn(name = "routing_strategy_id"))
	@Column(name = "user_number")
	public Set<String> getUserNumbers() {
		return userNumbers;
	}

	public void setUserNumbers(Set<String> userNumbers) {
		this.userNumbers = userNumbers;
	}

	@Transient
	public Set<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(final Set<Long> userIds) {
		this.userIds = userIds;
	}

	@Override
	@Transient
	public String getType() {
		return USER_ROUTING_STRATEGY;
	}

	@Override
	@Transient
	public void execute(RoutingVisitor routingVisitor) {
		routingVisitor.visit(this);
	}
}
