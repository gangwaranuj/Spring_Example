package com.workmarket.domains.model.note.concern;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="workConcern")
@DiscriminatorValue("work")
@AuditChanges
public class WorkConcern extends Concern{

	private static final long serialVersionUID = 1L;

	private Work work;

	public WorkConcern() {
		super();
	}

    public WorkConcern(String message, Work work) {
        super(message);
        this.setWork(work);
    }

    @ManyToOne(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="work_id", referencedColumnName="id")
	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	@Transient
	public String getType() {
		return "work";
	}

	@Override
	@Transient
	public Long getEntityId() {
		return work.getId();
	}

	@Override
	@Transient
	public String getEntityNumber() {
		return work.getWorkNumber();
	}
}
