package com.workmarket.domains.model;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.ViewType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

@Entity(name = "work_resource")
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@Table(name = "work_resource")
@NamedQueries({
		@NamedQuery(name = "workResource.notDeclinedForWork",
			query = "FROM work_resource r WHERE r.work.id = :work_id AND r.workResourceStatusType.code != 'declined'"),
		@NamedQuery(name = "workResource.setDispatcher",
			query = "UPDATE work_resource AS wr SET dispatcherId = :dispatcherId WHERE wr.work.id = :workId AND wr.user.id = :userId")
})
@AuditChanges
public class WorkResource extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	private Work work;
	private User user;
	private WorkResourceStatusType workResourceStatusType;

	private BigDecimal hoursWorked;
	private BigDecimal unitsProcessed;

	private boolean confirmed;
	private Calendar confirmedOn;
	private boolean assignedToWork;
	private boolean checkedIn;

	@Deprecated
	private WorkResource delegator;

	private List<WorkResourceTimeTracking> timeTracking = Lists.newArrayList();
	private BigDecimal additionalExpenses;
	private BigDecimal bonus;

	private Calendar viewedOn;
	private ViewType viewType;
	private Integer optimisticLockVersion;

	private DateRange appointment;
	private boolean cancelledAssignment = false;
	private Calendar lastRemindedToCompleteOn;
	private boolean targeted = true;
	private int score;
	private Long dispatcherId;
	private boolean assignToFirstToAccept;

	public WorkResource() {}

	public WorkResource(Work work, User user) {
		this.work = work;
		this.user = user;
		this.workResourceStatusType = new WorkResourceStatusType(WorkResourceStatusType.OPEN);
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_id", referencedColumnName = "id", updatable = false)
	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "work_resource_status_type_code", referencedColumnName = "code", nullable = false)
	public WorkResourceStatusType getWorkResourceStatusType() {
		return workResourceStatusType;
	}

	public void setWorkResourceStatusType(WorkResourceStatusType workResourceStatusType) {
		this.workResourceStatusType = workResourceStatusType;
	}

	@Column(name = "hours_worked")
	public BigDecimal getHoursWorked() {
		return hoursWorked;
	}

	public void setHoursWorked(BigDecimal hoursWorked) {
		this.hoursWorked = hoursWorked;
	}

	@Column(name = "units_processed")
	public BigDecimal getUnitsProcessed() {
		return unitsProcessed;
	}

	public void setUnitsProcessed(BigDecimal unitsProcessed) {
		this.unitsProcessed = unitsProcessed;
	}

	@Column(name = "confirmed_flag")
	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	@Column(name = "confirmed_on")
	public Calendar getConfirmedOn() {
		return confirmedOn;
	}

	public void setConfirmedOn(Calendar confirmedOn) {
		this.confirmedOn = confirmedOn;
	}

	@Column(name = "assigned_to_work")
	public boolean isAssignedToWork() {
		return assignedToWork;
	}

	public void setAssignedToWork(boolean assignedToWork) {
		this.assignedToWork = assignedToWork;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delegator_id", referencedColumnName = "id", nullable = true)
	public WorkResource getDelegator() {
		return delegator;
	}

	public void setDelegator(WorkResource delegator) {
		this.delegator = delegator;
	}

	@OneToMany(mappedBy = "workResource", cascade = {}, fetch = FetchType.LAZY)
	public List<WorkResourceTimeTracking> getTimeTracking() {
		return timeTracking;
	}

	public void setTimeTracking(List<WorkResourceTimeTracking> timeTracking) {
		this.timeTracking = timeTracking;
	}

	@Column(name = "checkedin_flag")
	public boolean isCheckedIn() {
		return checkedIn;
	}

	public void setCheckedIn(boolean checkedIn) {
		this.checkedIn = checkedIn;
	}

	/**
	 * Requested $ for additional expenses *
	 */
	@Column(name = "additional_expenses", nullable = true)
	public BigDecimal getAdditionalExpenses() {
		return additionalExpenses;
	}

	public void setAdditionalExpenses(BigDecimal additionalExpenses) {
		this.additionalExpenses = additionalExpenses;
	}

	@Column(name = "bonus", nullable = true)
	public BigDecimal getBonus() {
		return bonus;
	}

	public void setBonus(BigDecimal bonus) {
		this.bonus = bonus;
	}

	@Column(name = "viewed_on", nullable = true)
	public Calendar getViewedOn() {
		return viewedOn;
	}

	public void setViewedOn(Calendar viewedOn) {
		this.viewedOn = viewedOn;
	}

	@Column(name = "view_type", nullable = true)
	public String getViewType() {
		if (viewType == null) {
			return null;
		}
		return viewType.getTypeString();
	}

	public void setViewType(String viewTypeString) {
		if (StringUtils.isNotBlank(viewTypeString)) {
			this.viewType = ViewType.findViewType(viewTypeString);
		}
	}

	@Column(name = "score", nullable = false)
	public int getScore() {
		return score;
	}

	public WorkResource setScore(int score) {
		this.score = score;
		return this;
	}

	@Column(name = "assign_to_first_resource")
	public boolean isAssignToFirstToAccept() {
		return assignToFirstToAccept;
	}

	public void setAssignToFirstToAccept(boolean assignToFirstToAccept) {
		this.assignToFirstToAccept = assignToFirstToAccept;
	}

	@Transient
	public Boolean isActive() {
		return getWorkResourceStatusType().getCode().equals(WorkResourceStatusType.ACTIVE);
	}

	@Transient
	public Boolean isOpen() {
		return getWorkResourceStatusType().getCode().equals(WorkResourceStatusType.OPEN);
	}

	@Transient
	public Boolean isDeclined() {
		return getWorkResourceStatusType().getCode().equals(WorkResourceStatusType.DECLINED);
	}

	@Transient
	public Boolean isCancelled() {
		return getWorkResourceStatusType().getCode().equals(WorkResourceStatusType.CANCELLED);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		final Long workId;
		if (work == null) {
			workId = null;
		} else {
			workId = work.getId();
		}
		builder.append("work id:").append(workId).append(" workResourceId: ").append(getId());
		return builder.toString();
	}

	@Version
	@Column(name = "optimistic_lock_version")
	public Integer getOptimisticLockVersion() {
		return optimisticLockVersion;
	}

	public void setOptimisticLockVersion(Integer optimisticLockVersion) {
		this.optimisticLockVersion = optimisticLockVersion;
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "from", column = @Column(name = "appointment_from", nullable = true)),
			@AttributeOverride(name = "through", column = @Column(name = "appointment_through", nullable = true))
	})
	public DateRange getAppointment() {
		return appointment;
	}

	public void setAppointment(DateRange appointment) {
		this.appointment = appointment;
	}

	@Column(name = "cancelled_assignment", nullable = false)
	public boolean isCancelledAssignment() {
		return cancelledAssignment;
	}

	public void setCancelledAssignment(boolean cancelledAssignment) {
		this.cancelledAssignment = cancelledAssignment;
	}

	@Column(name = "last_reminded_to_complete_on")
	public Calendar getLastRemindedToCompleteOn() {
		return lastRemindedToCompleteOn;
	}

	public void setLastRemindedToCompleteOn(Calendar lastRemindedToCompleteOn) {
		this.lastRemindedToCompleteOn = lastRemindedToCompleteOn;
	}

	@Column(name = "targeted", nullable = false)
	public void setTargeted(boolean targeted) {
		this.targeted = targeted;
	}

	public boolean isTargeted() {
		return targeted;
	}

	@Column(name = "dispatcher_id")
	public Long getDispatcherId() {
		return dispatcherId;
	}

	public void setDispatcherId(Long dispatcherId) {
		this.dispatcherId = dispatcherId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof WorkResource)) {
			return false;
		}

		WorkResource that = (WorkResource) o;

		if (!user.getId().equals(that.user.getId())) {
			return false;
		}
		return work.getId().equals(that.work.getId());

	}

	@Override
	public int hashCode() {
		int result = 0;
		result = 31 * result + work.getId().hashCode();
		result = 31 * result + user.getId().hashCode();
		return result;
	}
}
