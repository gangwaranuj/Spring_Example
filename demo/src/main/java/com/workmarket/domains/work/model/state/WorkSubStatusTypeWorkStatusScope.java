package com.workmarket.domains.work.model.state;

import com.workmarket.domains.model.AbstractEntityLookupEntityAssociation;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity(name = "workSubStatusTypeWorkStatusScope")
@Table(name = "work_sub_status_type_work_status_scope")
@AttributeOverrides({ @AttributeOverride(name = "strong_id", column = @Column(name = "work_sub_status_type_id")),
		@AttributeOverride(name = "weak_code", column = @Column(name = "work_status_type_code")) })
@AssociationOverrides({ @AssociationOverride(name = "strong", joinColumns = @JoinColumn(name = "work_sub_status_type_id", referencedColumnName = "id")),
		@AssociationOverride(name = "weak", joinColumns = @JoinColumn(name = "work_status_type_code", referencedColumnName = "code")) })
@AuditChanges
public class WorkSubStatusTypeWorkStatusScope extends AbstractEntityLookupEntityAssociation<WorkSubStatusType, WorkStatusType> {

	private static final long	serialVersionUID	= 1L;

	public WorkSubStatusTypeWorkStatusScope() {}

	public WorkSubStatusTypeWorkStatusScope(WorkSubStatusType workSubStatusType, WorkStatusType workStatusType) {
		super(workSubStatusType, workStatusType);
	}
}
