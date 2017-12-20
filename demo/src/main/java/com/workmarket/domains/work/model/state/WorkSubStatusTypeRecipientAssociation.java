package com.workmarket.domains.work.model.state;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity(name = "workSubStatusTypeRecipientAssociation")
@Table(name = "work_sub_status_type_to_user_association")
@AuditChanges
public class WorkSubStatusTypeRecipientAssociation extends DeletableEntity {

	private static final long serialVersionUID = 8054714860290310154L;

	@NotNull
	private WorkSubStatusType workSubStatusType;

	@NotNull
	private User recipient;

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional = false)
	@JoinColumn(name = "work_sub_status_type_id")
	public WorkSubStatusType getWorkSubStatusType() {
		return workSubStatusType;
	}

	public void setWorkSubStatusType(WorkSubStatusType workSubStatusType) {
		this.workSubStatusType = workSubStatusType;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	public User getRecipient() { return recipient; }

	public void setRecipient(User recipient) { this.recipient = recipient; }

}
