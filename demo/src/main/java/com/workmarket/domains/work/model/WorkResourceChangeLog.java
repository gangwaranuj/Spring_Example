package com.workmarket.domains.work.model;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity(name = "work_resource_change_log")
@Table(name = "work_resource_change_log")
@AuditChanges
public class WorkResourceChangeLog extends AuditedEntity {

	private static final long serialVersionUID = 2766209309780129899L;

	private WorkResource workResource;
	private User masqueradeUser;
	private User onBehalfOfUser;
	private WorkResourceAction workResourceAction;
	private String changeLogNote;

	public WorkResourceChangeLog() {}

	public WorkResourceChangeLog(final WorkResource resource,
			 final User masqueradeUser,
			 final User onBehalfOfUser,
			 final WorkResourceAction workResourceAction) {
		this.workResource = resource;
		this.masqueradeUser = masqueradeUser;
		this.onBehalfOfUser = onBehalfOfUser;
		this.workResourceAction = workResourceAction;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_resource_id", referencedColumnName = "id", updatable = false)
	public WorkResource getWorkResource() {
		return workResource;
	}

	public void setWorkResource(WorkResource workResource) {
		this.workResource = workResource;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "masquerade_user_id", referencedColumnName = "id", updatable = false)
	public User getMasqueradeUser() {
		return masqueradeUser;
	}

	public void setMasqueradeUser(User masqueradeUser) {
		this.masqueradeUser = masqueradeUser;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "on_behalf_of_user_id", referencedColumnName = "id", updatable = false)
	public User getOnBehalfOfUser() {
		return onBehalfOfUser;
	}

	public void setOnBehalfOfUser(User onBehalfOfUser) {
		this.onBehalfOfUser = onBehalfOfUser;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_resource_action_code_id", referencedColumnName = "id", updatable = false)
	public WorkResourceAction getWorkResourceAction() {
		return workResourceAction;
	}

	public void setWorkResourceAction(WorkResourceAction workResourceAction) {
		this.workResourceAction = workResourceAction;
	}

	@Column(name = "change_log_note", length = Constants.TEXT_LONG)
	public String getChangeLogNote() {
		return changeLogNote;
	}

	public void setChangeLogNote(String changeLogNote) {
		this.changeLogNote = changeLogNote;
	}
}
