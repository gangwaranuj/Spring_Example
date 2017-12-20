package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "routing_strategy_tracking")
@Table(name = "routing_strategy_tracking")
public class RoutingStrategyTracking extends AbstractEntity {

	private static final long serialVersionUID = -997676274161269684L;

	private Long workId;
	private Long userId;
	private Long routingStrategyId;
	private Integer rank;

	public RoutingStrategyTracking() { }

	public RoutingStrategyTracking(final Long workId,
	                               final Long userId,
	                               final Long routingStrategyId,
	                               final Integer rank) {
		this.workId = workId;
		this.userId = userId;
		this.routingStrategyId = routingStrategyId;
		this.rank = rank;
	}

	public RoutingStrategyTracking(final Long workId,
	                               final Long userId,
	                               final Long routingStrategyId) {
		this(workId, userId, routingStrategyId, 0);
	}

	@Column(name = "work_id", nullable = false, updatable = false)
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(final Long workId) {
		this.workId = workId;
	}

	@Column(name = "user_id", nullable = false, updatable = false)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	@Column(name = "rank")
	public Integer getRank() {
		return rank;
	}

	public void setRank(final Integer rank) {
		this.rank = rank;
	}

	@Column(name = "routing_strategy_id", nullable = false, updatable = false)
	public Long getRoutingStrategyId() {
		return routingStrategyId;
	}

	public void setRoutingStrategyId(final Long routingStrategyId) {
		this.routingStrategyId = routingStrategyId;
	}
}
