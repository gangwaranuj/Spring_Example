package com.workmarket.domains.work.model.state;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.workmarket.domains.model.AbstractEntityAbstractEntityAssociation;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "workSubStatusTypeTemplateAssociation")
@Table(name = "work_sub_status_type_template_association")
@AttributeOverrides({ @AttributeOverride(name = "strong_id", column = @Column(name = "work_sub_status_type_id")), @AttributeOverride(name = "weak_id", column = @Column(name = "work_template_id")) })
@AssociationOverrides({ @AssociationOverride(name = "strong", joinColumns = @JoinColumn(name = "work_sub_status_type_id", referencedColumnName = "id")),
		@AssociationOverride(name = "weak", joinColumns = @JoinColumn(name = "work_template_id", referencedColumnName = "id")) })
@AuditChanges
public class WorkSubStatusTypeTemplateAssociation extends AbstractEntityAbstractEntityAssociation<WorkSubStatusType, WorkTemplate> {

	private static final long	serialVersionUID	= 1L;

	public WorkSubStatusTypeTemplateAssociation() {}

	public WorkSubStatusTypeTemplateAssociation(WorkSubStatusType workSubStatusType, WorkTemplate workTemplate) {
		super(workSubStatusType, workTemplate);
	}
}
