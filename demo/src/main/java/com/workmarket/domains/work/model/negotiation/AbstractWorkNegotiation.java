package com.workmarket.domains.work.model.negotiation;

import com.workmarket.domains.model.ApprovableVerifiableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Calendar;

@Entity(name="abstractWorkNegotiation")
@Table(name="work_negotiation")
@DiscriminatorColumn(name="type", discriminatorType= DiscriminatorType.STRING)
@DiscriminatorValue(AbstractWorkNegotiation.BASE)
@NamedQueries({})
@AuditChanges
public class AbstractWorkNegotiation extends ApprovableVerifiableEntity {

	public static final String BASE = "base";
	public static final String BUDGET_INCREASE = "budget";
	public static final String EXPENSE = "expense";
	public static final String APPLY = "apply";
	public static final String NEGOTIATION = "negotiation";
	public static final String RESCHEDULE = "reschedule";
	public static final String BONUS = "bonus";

	private static final long serialVersionUID = 8599427883059728945L;

	private Work work;
	private WorkNote note;
	private WorkNote declineNote;
	private User requestedBy;
	private Calendar requestedOn;
	private User approvedBy;
	private Calendar approvedOn;
	private Boolean initiatedByResource = Boolean.TRUE;
	private Boolean duringCompletion = Boolean.FALSE;
	private Boolean scheduleNegotiation = Boolean.FALSE;


	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="work_id", referencedColumnName="id", nullable=false, updatable = false)
	public Work getWork() {
		return this.work;
	}
	public void setWork(Work work) {
		this.work = work;
	}

	@OneToOne
	@JoinColumn(name = "note_id", referencedColumnName = "id", nullable = true)
	@Fetch(FetchMode.JOIN)
	public WorkNote getNote() {
		return note;
	}
	public void setNote(WorkNote note) {
		this.note = note;
	}

	@OneToOne
	@JoinColumn(name = "decline_note_id", referencedColumnName = "id", nullable = true)
	@Fetch(FetchMode.JOIN)
	public WorkNote getDeclineNote() {
		return declineNote;
	}
	public void setDeclineNote(WorkNote declineNote) {
		this.declineNote = declineNote;
	}

	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="requestor_id", referencedColumnName="id", nullable=false)
	public User getRequestedBy() {
		return this.requestedBy;
	}
	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}

	@Column(name="negotiate_schedule_flag")
	public Boolean isScheduleNegotiation() {
		return this.scheduleNegotiation;
	}
	public void setScheduleNegotiation(Boolean scheduleNegotiation) {
		this.scheduleNegotiation = scheduleNegotiation;
	}

	@Column(name="requested_on", nullable=false)
	public Calendar getRequestedOn() {
		return this.requestedOn;
	}
	public void setRequestedOn(Calendar requestedOn) {
		this.requestedOn = requestedOn;
	}

	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="approver_id", referencedColumnName="id", nullable=true)
	public User getApprovedBy() {
		return this.approvedBy;
	}
	public void setApprovedBy(User approvedBy) {
		this.approvedBy = approvedBy;
	}

	@Column(name="approved_on", nullable=true)
	public Calendar getApprovedOn() {
		return this.approvedOn;
	}
	public void setApprovedOn(Calendar approvedOn) {
		this.approvedOn = approvedOn;
	}

	@Column(name="requestor_is_resource", nullable=false)
	public Boolean isInitiatedByResource() {
		return initiatedByResource;
	}
	public void setInitiatedByResource(Boolean initiatedByResource) {
		this.initiatedByResource = initiatedByResource;
	}

	@Column(name="during_completion", nullable=false)
	public Boolean isDuringCompletion() {
		return duringCompletion;
	}

	public void setDuringCompletion(Boolean duringCompletion) {
		this.duringCompletion = duringCompletion;
	}

	@Transient
	public String getNegotiationType() {
		return BASE;
	}
}
