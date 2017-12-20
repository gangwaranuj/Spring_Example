package com.workmarket.domains.groups.model;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "workGroupAssociation")
@Table(name = "work_group_association")
@AuditChanges
public class WorkGroupAssociation extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	private AbstractWork work;
	private UserGroup group;
	private boolean assignToFirstToAccept;

	public WorkGroupAssociation() {
		super();
	}


	public WorkGroupAssociation(AbstractWork work, UserGroup group, boolean assignToFirstToAccept) {
		this.work = work;
		this.group = group;
		this.assignToFirstToAccept = assignToFirstToAccept;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "work_id", updatable = false)
	public AbstractWork getWork() {
		return work;
	}

	public void setWork(AbstractWork work) {
		this.work = work;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "group_id")
	public UserGroup getGroup() {
		return group;
	}

	public void setGroup(UserGroup group) {
		this.group = group;
	}

	@Column(name = "assign_to_first_resource")
	public boolean isAssignToFirstToAccept() {
		return assignToFirstToAccept;
	}

	public void setAssignToFirstToAccept(boolean assignToFirstToAccept) {
		this.assignToFirstToAccept = assignToFirstToAccept;
	}

}
