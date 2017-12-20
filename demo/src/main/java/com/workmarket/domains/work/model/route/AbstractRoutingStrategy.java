package com.workmarket.domains.work.model.route;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.DeliveryStatusType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Calendar;

@Entity(name = "routingStrategy")
@Table(name = "routing_strategy")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("base")
@AuditChanges
public abstract class AbstractRoutingStrategy extends DeletableEntity {

	private static final long serialVersionUID = 2653006914412524099L;
	private Work work;
	private Integer delayMinutes = 0;

	private Calendar initializedOn;
	private Calendar routedOn;
	private boolean assignToFirstToAccept;
	private DeliveryStatusType deliveryStatus = new DeliveryStatusType(DeliveryStatusType.NONE);
	private RoutingStrategySummary summary = new RoutingStrategySummary();

	private WorkAuthorizationResponse workAuthorizationResponse;
	private WorkRoutingResponseSummary workRoutingResponseSummary;
	private RoutingStrategyGroup routingStrategyGroup;

	protected AbstractRoutingStrategy() {
	}


	@ManyToOne
	@JoinColumn(name = "routing_strategy_group_id", nullable = true, updatable = false)
	public RoutingStrategyGroup getRoutingStrategyGroup() {
		return routingStrategyGroup;
	}

	public void setRoutingStrategyGroup(RoutingStrategyGroup routingStrategyGroup) {
		this.routingStrategyGroup = routingStrategyGroup;
	}

	@ManyToOne
	@JoinColumn(name = "work_id", nullable = false, updatable = false)
	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	@Column(name = "initialized_on")
	public Calendar getInitializedOn() {
		return initializedOn;
	}

	public void setInitializedOn(Calendar initializedOn) {
		this.initializedOn = initializedOn;
	}

	@Column(name = "routed_on")
	public Calendar getRoutedOn() {
		return routedOn;
	}

	public void setRoutedOn(Calendar routedOn) {
		this.routedOn = routedOn;
	}

	@Column(name = "delay_minutes", nullable = false)
	public Integer getDelayMinutes() {
		return delayMinutes;
	}

	public void setDelayMinutes(Integer delayMinutes) {
		this.delayMinutes = delayMinutes;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "delivery_status_type_code", referencedColumnName = "code", nullable = false)
	public DeliveryStatusType getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(DeliveryStatusType deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	@Column(name = "assign_to_first_resource")
	public boolean isAssignToFirstToAccept() {
		return assignToFirstToAccept;
	}

	public void setAssignToFirstToAccept(boolean assignToFirstToAccept) {
		this.assignToFirstToAccept = assignToFirstToAccept;
	}

	@Embedded
	public RoutingStrategySummary getSummary() {
		return summary;
	}

	public void setSummary(RoutingStrategySummary summary) {
		this.summary = summary;
	}

	@Transient
	public abstract String getType();

	@Transient
	public WorkAuthorizationResponse getWorkAuthorizationResponse() {
		return workAuthorizationResponse;
	}

	public void setWorkAuthorizationResponse(WorkAuthorizationResponse workAuthorizationResponse) {
		this.workAuthorizationResponse = workAuthorizationResponse;
	}

	@Transient
	public WorkRoutingResponseSummary getWorkRoutingResponseSummary() {
		return workRoutingResponseSummary;
	}

	public void setWorkRoutingResponseSummary(WorkRoutingResponseSummary workRoutingResponseSummary) {
		this.workRoutingResponseSummary = workRoutingResponseSummary;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractRoutingStrategy)) return false;
		if (!super.equals(o)) return false;

		AbstractRoutingStrategy that = (AbstractRoutingStrategy) o;

		if (delayMinutes != null ? !delayMinutes.equals(that.delayMinutes) : that.delayMinutes != null) return false;
		if (!deliveryStatus.equals(that.deliveryStatus)) return false;
		if (initializedOn != null ? !initializedOn.equals(that.initializedOn) : that.initializedOn != null)
			return false;
		if (routedOn != null ? !routedOn.equals(that.routedOn) : that.routedOn != null) return false;
		if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
		if (!work.equals(that.work)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + work.hashCode();
		result = 31 * result + (delayMinutes != null ? delayMinutes.hashCode() : 0);
		result = 31 * result + (initializedOn != null ? initializedOn.hashCode() : 0);
		result = 31 * result + (routedOn != null ? routedOn.hashCode() : 0);
		result = 31 * result + deliveryStatus.hashCode();
		result = 31 * result + (summary != null ? summary.hashCode() : 0);
		return result;
	}

	@Transient
	public abstract void execute(RoutingVisitor routingVisitor);
}
