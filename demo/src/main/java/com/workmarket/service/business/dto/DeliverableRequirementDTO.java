package com.workmarket.service.business.dto;

import com.workmarket.domains.model.DeliverableRequirement;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * Created by rahul on 3/4/14
 */
public class DeliverableRequirementDTO implements Comparable<DeliverableRequirementDTO>, Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String type;
	private String instructions;
	private int numberOfFiles;
	private int priority;

	public DeliverableRequirementDTO() {
	}

	public DeliverableRequirementDTO(Long id, String type, String instructions, int numberOfFiles, int priority) {
		this.id = id;
		this.type = type;
		this.instructions = instructions;
		this.numberOfFiles = numberOfFiles;
		this.priority = priority;
	}

	public DeliverableRequirementDTO(Long id, String type, int numberOfFiles, int priority) {
		this.id = id;
		this.type = type;
		this.numberOfFiles = numberOfFiles;
		this.priority = priority;
	}

	public DeliverableRequirementDTO(DeliverableRequirement deliverableRequirement) {
		Assert.notNull(deliverableRequirement);

		this.id = deliverableRequirement.getId();
		this.type = deliverableRequirement.getType().getCode();
		this.instructions = deliverableRequirement.getInstructions();
		this.numberOfFiles = deliverableRequirement.getNumberOfFiles();
		this.priority = deliverableRequirement.getPriority();
	}

	public Long getId() {
		return id;
	}

	public DeliverableRequirementDTO setId(Long id) {
		this.id = id;
		return this;
	}

	public String getType() {
		return type;
	}

	public DeliverableRequirementDTO setType(String type) {
		this.type = type;
		return this;
	}

	public String getInstructions() {
		return instructions;
	}

	public DeliverableRequirementDTO setInstructions(String instructions) {
		this.instructions = instructions;
		return this;
	}

	public int getNumberOfFiles() {
		return numberOfFiles;
	}

	public DeliverableRequirementDTO setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
		return this;
	}

	public int getPriority() {
		return priority;
	}

	public DeliverableRequirementDTO setPriority(int priority) {
		this.priority = priority;
		return this;
	}

	@Override
	public int compareTo(DeliverableRequirementDTO that) {
		return this.priority - that.getPriority();
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (!(o instanceof DeliverableRequirementDTO)) {
			return false;
		}

		DeliverableRequirementDTO that = (DeliverableRequirementDTO) o;

		if (id != null ? !id.equals(that.id) : that.id != null) {
			return false;
		}
		if (numberOfFiles != that.numberOfFiles) {
			return false;
		}
		if (priority != that.priority) {
			return false;
		}

		if (id != null ? !id.equals(that.id) : that.id != null) {
			return false;
		}

		if (instructions != null ? !instructions.equals(that.instructions) : that.instructions != null) {
			return false;
		}

		if (type != null ? !type.equals(that.type) : that.type != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (instructions != null ? instructions.hashCode() : 0);
		result = 31 * result + numberOfFiles;
		result = 31 * result + priority;
		return result;
	}
}
