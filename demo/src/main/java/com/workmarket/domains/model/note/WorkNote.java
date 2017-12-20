package com.workmarket.domains.model.note;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.workmarket.domains.model.PrivacyType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@DiscriminatorValue("workNote")
@AuditChanges
public class WorkNote extends Note {

	private static final long serialVersionUID = 1L;

	private Work work;

	public WorkNote() {}

	public WorkNote(String content, Work work) {
		super(content);
		this.work = work;
	}

	public WorkNote(String content, Work work, PrivacyType privacy) {
		super(content);
		this.work = work;
		setPrivacy(privacy);
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="work_id", referencedColumnName="id", updatable = false)
	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

}
