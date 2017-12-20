package com.workmarket.domains.model.account;


import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;

@Entity(name = "project_transaction")
@Table(name = "project_transaction")
@AuditChanges
public class ProjectTransaction extends RegisterTransaction {

	private static final long serialVersionUID = 1L;
	private Project project;
	private RegisterTransaction parentTransaction;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "project_id", referencedColumnName = "id")
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "parent_transaction_id", referencedColumnName = "id")
	public RegisterTransaction getParentTransaction() {
		return parentTransaction;
	}

	public void setParentTransaction(RegisterTransaction parentTransaction) {
		this.parentTransaction = parentTransaction;
	}
}
