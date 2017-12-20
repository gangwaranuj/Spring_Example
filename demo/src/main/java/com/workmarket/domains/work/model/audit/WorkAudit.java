package com.workmarket.domains.work.model.audit;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.workmarket.domains.model.AbstractEntity;

@Entity(name = "work_audit")
@Table(name = "work_audit")
public class WorkAudit extends AbstractEntity {
	
	private static final long serialVersionUID = -8836344447913273922L;
	public Long workId;
	public WorkAuditType workAuditAction;
	public Calendar createdOn;

	@Column(name = "work_id", nullable = false)
	public Long getWorkId() {
		return workId;
	}
	public void setWorkId(Long workId) {
		this.workId = workId;
	}
	@Enumerated(EnumType.STRING)
	@Column(name = "work_audit_action_type", nullable = false)
	public WorkAuditType getWorkAuditAction() {
		return workAuditAction;
	}
	public void setWorkAuditAction(WorkAuditType workAuditAction) {
		this.workAuditAction = workAuditAction;
	}
   @Column(name = "created_on", nullable = false, updatable = false)
	public Calendar getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Calendar createdOn) {
		this.createdOn = createdOn;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" Id: " + getId());
		sb.append(" workId: " + workId);
		sb.append(" workAuditAction: " + workAuditAction);
		sb.append(" createdOn: " + createdOn);
		return sb.toString();
	}
	

}
