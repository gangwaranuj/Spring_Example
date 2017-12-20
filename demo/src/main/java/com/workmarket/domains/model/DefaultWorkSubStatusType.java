package com.workmarket.domains.model;

import com.workmarket.domains.work.model.state.WorkSubStatusDescriptor;

import javax.persistence.*;

@Entity(name = "defaultWorkSubStatusType")
@Table(name = "default_work_sub_status_type")
@AttributeOverrides({
		@AttributeOverride(name = "code", column = @Column(length = 35)),
		@AttributeOverride(name = "description", column = @Column(length = 35)) })
public class DefaultWorkSubStatusType extends LookupEntity {

	private static final long serialVersionUID = -8397437326076484895L;

	private WorkSubStatusDescriptor subStatusDescriptor;

	public DefaultWorkSubStatusType() {
		super();
	}

	public DefaultWorkSubStatusType(String code) {
		super(code);
	}

	@Embedded
	public WorkSubStatusDescriptor getSubStatusDescriptor() {
		return subStatusDescriptor;
	}

	public void setSubStatusDescriptor(WorkSubStatusDescriptor subStatusDescriptor) {
		this.subStatusDescriptor = subStatusDescriptor;
	}
}