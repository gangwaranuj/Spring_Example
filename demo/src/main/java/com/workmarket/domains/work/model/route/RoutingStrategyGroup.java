package com.workmarket.domains.work.model.route;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.thrift.work.RoutingStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity(name = "routingStrategyGroup")
@Table(name = "routing_strategy_group")
@AuditChanges
public class RoutingStrategyGroup extends AuditedEntity {
	private static final long serialVersionUID = 4765653113528882098L;
	@OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade={CascadeType.ALL})
	private Set<RoutingStrategy> routingStrategies;
}
