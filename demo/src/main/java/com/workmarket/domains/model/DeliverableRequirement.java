package com.workmarket.domains.model;

import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by rahul on 3/5/14
 */
@Entity(name = "deliverableRequirement")
@Table(name = "deliverable_requirement")
@AuditChanges
public class DeliverableRequirement extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	private DeliverableRequirementGroup deliverableRequirementGroup;
	private WorkAssetAssociationType type;
	private String instructions;
	private int numberOfFiles;
	private int priority;

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional=false)
	@JoinColumn(name = "deliverable_requirement_group_id", updatable = false)
	public DeliverableRequirementGroup getDeliverableRequirementGroup() {
		return deliverableRequirementGroup;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deliverable_type", referencedColumnName = "code", nullable = false)
	public WorkAssetAssociationType getType() {
		return type;
	}

	@Column(name = "instructions", nullable = false)
	public String getInstructions() {
		return instructions;
	}

	@Column(name = "number_of_files", nullable = false)
	public int getNumberOfFiles() {
		return numberOfFiles;
	}

	@Column(name = "priority", nullable = false)
	public int getPriority() {
		return priority;
	}

	public void setDeliverableRequirementGroup(DeliverableRequirementGroup deliverableRequirementGroup) {
		this.deliverableRequirementGroup = deliverableRequirementGroup;
	}

	public void setType(WorkAssetAssociationType workAssetAssociationType) {
		this.type = workAssetAssociationType;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public void setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}
