package com.workmarket.thrift.work;

import com.workmarket.domains.model.ApprovalStatus;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Resource implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private com.workmarket.thrift.core.User user;
	private com.workmarket.thrift.core.Status status;
	private boolean assigned;
	private boolean confirmed;
	private boolean checkedIn;
	private boolean checkedOut;
	private long invitedOn;
	private long declinedOn;
	private long assignedOn;
	private long confirmedOn;
	private List<TimeTrackingEntry> timeTrackingLog;
	private double hoursWorked;
	private double unitsProcessed;
	private Negotiation budgetNegotiation;
	private Negotiation expenseNegotiation;
	private Negotiation bonusNegotiation;

	private Negotiation rescheduleNegotiation;
	private Negotiation pendingNegotiation;
	private List<com.workmarket.thrift.assessment.AssessmentAttemptPair> assessmentAttempts;
	private long timeTrackingDuration;
	private double additionalExpenses;
	private double bonus;
	private List<ResourceNote> resourceNotes;
	private double distanceToAssignment;
	private Schedule appointment;
	private long lastRemindedToCompleteOn;
	private List<ResourceLabel> labels;

	public Resource() {
	}

	public long getId() {
		return this.id;
	}

	public Resource setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public com.workmarket.thrift.core.User getUser() {
		return this.user;
	}

	public Resource setUser(com.workmarket.thrift.core.User user) {
		this.user = user;
		return this;
	}

	/**
	 * Returns true if field user is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetUser() {
		return this.user != null;
	}

	public com.workmarket.thrift.core.Status getStatus() {
		return this.status;
	}

	public Resource setStatus(com.workmarket.thrift.core.Status status) {
		this.status = status;
		return this;
	}

	/**
	 * Returns true if field status is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetStatus() {
		return this.status != null;
	}

	public boolean isAssigned() {
		return this.assigned;
	}

	public Resource setAssigned(boolean assigned) {
		this.assigned = assigned;
		return this;
	}

	public boolean isConfirmed() {
		return this.confirmed;
	}

	public Resource setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
		return this;
	}

	public boolean isCheckedIn() {
		return this.checkedIn;
	}

	public Resource setCheckedIn(boolean checkedIn) {
		this.checkedIn = checkedIn;
		return this;
	}

	public boolean isCheckedOut() {
		return this.checkedOut;
	}

	public Resource setCheckedOut(boolean checkedOut) {
		this.checkedOut = checkedOut;
		return this;
	}

	public long getInvitedOn() {
		return this.invitedOn;
	}

	public Resource setInvitedOn(long invitedOn) {
		this.invitedOn = invitedOn;
		return this;
	}

	public boolean isSetInvitedOn() {
		return (invitedOn > 0L);
	}


	public long getDeclinedOn() {
		return this.declinedOn;
	}

	public Resource setDeclinedOn(long declinedOn) {
		this.declinedOn = declinedOn;
		return this;
	}

	/**
	 * Returns true if field declinedOn is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetDeclinedOn() {
		return (declinedOn > 0L);
	}

	public long getAssignedOn() {
		return this.assignedOn;
	}

	public Resource setAssignedOn(long assignedOn) {
		this.assignedOn = assignedOn;
		return this;
	}

	public boolean isSetAssignedOn() {
		return (assignedOn > 0L);
	}

	public long getConfirmedOn() {
		return this.confirmedOn;
	}

	public Resource setConfirmedOn(long confirmedOn) {
		this.confirmedOn = confirmedOn;
		return this;
	}

	public boolean isSetConfirmedOn() {
		return (confirmedOn > 0L);
	}

	public int getTimeTrackingLogSize() {
		return (this.timeTrackingLog == null) ? 0 : this.timeTrackingLog.size();
	}

	public java.util.Iterator<TimeTrackingEntry> getTimeTrackingLogIterator() {
		return (this.timeTrackingLog == null) ? null : this.timeTrackingLog.iterator();
	}

	public void addToTimeTrackingLog(TimeTrackingEntry elem) {
		if (this.timeTrackingLog == null) {
			this.timeTrackingLog = new ArrayList<TimeTrackingEntry>();
		}
		this.timeTrackingLog.add(elem);
	}

	public List<TimeTrackingEntry> getTimeTrackingLog() {
		return this.timeTrackingLog;
	}

	public Resource setTimeTrackingLog(List<TimeTrackingEntry> timeTrackingLog) {
		this.timeTrackingLog = timeTrackingLog;
		return this;
	}

	/**
	 * Returns true if field timeTrackingLog is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetTimeTrackingLog() {
		return this.timeTrackingLog != null;
	}

	public double getHoursWorked() {
		return this.hoursWorked;
	}

	public Resource setHoursWorked(double hoursWorked) {
		this.hoursWorked = hoursWorked;
		return this;
	}

	public boolean isSetHoursWorked() {
		return (hoursWorked > 0D);
	}

	public double getUnitsProcessed() {
		return this.unitsProcessed;
	}

	public Resource setUnitsProcessed(double unitsProcessed) {
		this.unitsProcessed = unitsProcessed;
		return this;
	}

	public boolean isSetUnitsProcessed() {
		return (unitsProcessed > 0D);
	}

	public Negotiation getBudgetNegotiation() {
		return this.budgetNegotiation;
	}

	public Resource setBudgetNegotiation(Negotiation budgetNegotiation) {
		this.budgetNegotiation = budgetNegotiation;
		return this;
	}

	public Negotiation getExpenseNegotiation() {
		return expenseNegotiation;
	}

	public Resource setExpenseNegotiation(Negotiation expenseNegotiation) {
		this.expenseNegotiation = expenseNegotiation;
		return this;
	}

	/**
	 * Returns true if field budgetNegotiation is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetSpendLimitNegotiation() {
		return this.budgetNegotiation != null;
	}

	public Negotiation getBonusNegotiation() {
		return bonusNegotiation;
	}

	public Resource setBonusNegotiation(Negotiation bonusNegotiation) {
		this.bonusNegotiation = bonusNegotiation;
		return this;
	}

	public Negotiation getRescheduleNegotiation() {
		return this.rescheduleNegotiation;
	}

	public Resource setRescheduleNegotiation(Negotiation rescheduleNegotiation) {
		this.rescheduleNegotiation = rescheduleNegotiation;
		return this;
	}

	/**
	 * Returns true if field rescheduleNegotiation is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetRescheduleNegotiation() {
		return this.rescheduleNegotiation != null;
	}

	public Negotiation getPendingNegotiation() {
		return this.pendingNegotiation;
	}

	public Resource setPendingNegotiation(Negotiation pendingNegotiation) {
		this.pendingNegotiation = pendingNegotiation;
		return this;
	}

	/**
	 * Returns true if field pendingNegotiation is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetPendingNegotiation() {
		return this.pendingNegotiation != null;
	}

	public int getAssessmentAttemptsSize() {
		return (this.assessmentAttempts == null) ? 0 : this.assessmentAttempts.size();
	}

	public java.util.Iterator<com.workmarket.thrift.assessment.AssessmentAttemptPair> getAssessmentAttemptsIterator() {
		return (this.assessmentAttempts == null) ? null : this.assessmentAttempts.iterator();
	}

	public void addToAssessmentAttempts(com.workmarket.thrift.assessment.AssessmentAttemptPair elem) {
		if (this.assessmentAttempts == null) {
			this.assessmentAttempts = new ArrayList<com.workmarket.thrift.assessment.AssessmentAttemptPair>();
		}
		this.assessmentAttempts.add(elem);
	}

	public List<com.workmarket.thrift.assessment.AssessmentAttemptPair> getAssessmentAttempts() {
		return this.assessmentAttempts;
	}

	public Resource setAssessmentAttempts(List<com.workmarket.thrift.assessment.AssessmentAttemptPair> assessmentAttempts) {
		this.assessmentAttempts = assessmentAttempts;
		return this;
	}

	/**
	 * Returns true if field assessmentAttempts is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetAssessmentAttempts() {
		return this.assessmentAttempts != null;
	}

	public long getTimeTrackingDuration() {
		return this.timeTrackingDuration;
	}

	public Resource setTimeTrackingDuration(long timeTrackingDuration) {
		this.timeTrackingDuration = timeTrackingDuration;
		return this;
	}

	public boolean isSetTimeTrackingDuration() {
		return (timeTrackingDuration > 0L);
	}

	public double getAdditionalExpenses() {
		return this.additionalExpenses;
	}

	public Resource setAdditionalExpenses(double additionalExpenses) {
		this.additionalExpenses = additionalExpenses;
		return this;
	}

	public boolean isSetAdditionalExpenses() {
		return (additionalExpenses > 0D);
	}

	public double getBonus() {
		return bonus;
	}

	public Resource setBonus(double bonus) {
		this.bonus = bonus;
		return this;
	}

	public boolean isSetBonus() {
		return (bonus > 0D);
	}

	public int getResourceNotesSize() {
		return (this.resourceNotes == null) ? 0 : this.resourceNotes.size();
	}

	public java.util.Iterator<ResourceNote> getResourceNotesIterator() {
		return (this.resourceNotes == null) ? null : this.resourceNotes.iterator();
	}

	public void addToResourceNotes(ResourceNote elem) {
		if (this.resourceNotes == null) {
			this.resourceNotes = new ArrayList<ResourceNote>();
		}
		this.resourceNotes.add(elem);
	}

	public List<ResourceNote> getResourceNotes() {
		return this.resourceNotes;
	}

	public Resource setResourceNotes(List<ResourceNote> resourceNotes) {
		this.resourceNotes = resourceNotes;
		return this;
	}

	/**
	 * Returns true if field resourceNotes is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetResourceNotes() {
		return this.resourceNotes != null;
	}

	public double getDistanceToAssignment() {
		return this.distanceToAssignment;
	}

	public Resource setDistanceToAssignment(double distanceToAssignment) {
		this.distanceToAssignment = distanceToAssignment;
		return this;
	}

	public boolean isSetDistanceToAssignment() {
		return (distanceToAssignment > 0D);
	}

	public Schedule getAppointment() {
		return this.appointment;
	}

	public Resource setAppointment(Schedule appointment) {
		this.appointment = appointment;
		return this;
	}

	/**
	 * Returns true if field appointment is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetAppointment() {
		return this.appointment != null;
	}

	public long getLastRemindedToCompleteOn() {
		return this.lastRemindedToCompleteOn;
	}

	public Resource setLastRemindedToCompleteOn(long lastRemindedToCompleteOn) {
		this.lastRemindedToCompleteOn = lastRemindedToCompleteOn;
		return this;
	}

	public boolean isSetLastRemindedToCompleteOn() {
		return (lastRemindedToCompleteOn > 0L);
	}

	public List<ResourceLabel> getLabels() {
		return labels;
	}

	public Resource setLabels(List<ResourceLabel> labels) {
		this.labels = labels;
		return this;
	}

	public void addToLabels(ResourceLabel elem) {
		if (this.labels == null) {
			this.labels = new ArrayList<>();
		}
		this.labels.add(elem);
	}

	public boolean isBudgetNegotiationPending() {
		return budgetNegotiation != null
				&& budgetNegotiation.getApprovalStatus() != null
				&& ApprovalStatus.PENDING.toString().equalsIgnoreCase(budgetNegotiation.getApprovalStatus().getCode());
	}

	public boolean isExpenseNegotiationPending() {
		return expenseNegotiation != null
				&& expenseNegotiation.getApprovalStatus() != null
				&& ApprovalStatus.PENDING.toString().equalsIgnoreCase(expenseNegotiation.getApprovalStatus().getCode());
	}

	public boolean isBonusNegotiationPending() {
		return bonusNegotiation != null
				&& bonusNegotiation.getApprovalStatus() != null
				&& ApprovalStatus.PENDING.toString().equalsIgnoreCase(bonusNegotiation.getApprovalStatus().getCode());
	}

	public boolean isRescheduleNegotiationPending() {
		return rescheduleNegotiation != null
				&& rescheduleNegotiation.getApprovalStatus() != null
				&& ApprovalStatus.PENDING.toString().equalsIgnoreCase(rescheduleNegotiation.getApprovalStatus().getCode());
	}

	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}

