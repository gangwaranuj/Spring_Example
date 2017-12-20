package com.workmarket.domains.model;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by rahul on 2/4/14
 */
@Entity(name = "deliverableRequirementGroup")
@Table(name = "deliverable_requirement_group")
@NamedQueries({
		@NamedQuery(name = "deliverableRequirementGroup.findByWorkNumber", query = "select w.deliverableRequirementGroup " +
				"from work w " +
				"where w.workNumber = :workNumber")
})
@AuditChanges
public class DeliverableRequirementGroup extends AuditedEntity {
	private static final long serialVersionUID = 1L;

	private String instructions;
	private int hoursToComplete;
	private boolean deadlineActive;
	private boolean reminderSent;
	private List<DeliverableRequirement> deliverableRequirements;

	@Column(name = "instructions", nullable = false)
	public String getInstructions() {
		return instructions;
	}

	@Column(name = "hours_to_complete", nullable = false, length = 11)
	public int getHoursToComplete() {
		return hoursToComplete;
	}

	@Column(name = "deadline_active", nullable = false)
	public boolean isDeadlineActive() {
		return deadlineActive;
	}

	@Column(name = "reminder_sent", nullable = false)
	public boolean isReminderSent() {
		return reminderSent;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "deliverableRequirementGroup")
	@Where(clause = "deleted = 0")
	@OrderBy("priority ASC")
	public List<DeliverableRequirement> getDeliverableRequirements() {
		return deliverableRequirements;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public void setHoursToComplete(int hoursToComplete) {
		this.hoursToComplete = hoursToComplete;
	}

	public void setDeadlineActive(boolean deadlineActive) {
		this.deadlineActive = deadlineActive;
	}

	public void setReminderSent(boolean reminderSent) {
		this.reminderSent = reminderSent;
	}

	public void setDeliverableRequirements(List<DeliverableRequirement> deliverableRequirements) {
		this.deliverableRequirements = deliverableRequirements;
	}
}
