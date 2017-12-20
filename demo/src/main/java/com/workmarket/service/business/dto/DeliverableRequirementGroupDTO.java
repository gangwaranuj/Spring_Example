package com.workmarket.service.business.dto;

import com.google.api.client.util.Lists;
import com.workmarket.domains.model.DeliverableRequirement;
import com.workmarket.domains.model.DeliverableRequirementGroup;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rahul on 2/27/14
 */
public class DeliverableRequirementGroupDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String instructions;
	private int hoursToComplete;
	private List<DeliverableRequirementDTO> deliverableRequirementDTOs;

	public DeliverableRequirementGroupDTO() {
	}

	public DeliverableRequirementGroupDTO(Long id, String instructions, Integer hoursToComplete) {
		this.id = id;
		this.instructions = instructions;
		this.hoursToComplete = hoursToComplete == null ? 0 : hoursToComplete;
	}

	public DeliverableRequirementGroupDTO(DeliverableRequirementGroup deliverableRequirementGroup) {
		this.id = deliverableRequirementGroup.getId();
		this.instructions = deliverableRequirementGroup.getInstructions();
		this.hoursToComplete = deliverableRequirementGroup.getHoursToComplete();
		this.deliverableRequirementDTOs = Lists.newArrayList();

		for (DeliverableRequirement deliverableRequirement : deliverableRequirementGroup.getDeliverableRequirements()) {
			DeliverableRequirementDTO deliverableRequirementDTO = new DeliverableRequirementDTO(deliverableRequirement);
			this.deliverableRequirementDTOs.add(deliverableRequirementDTO);
		}
	}

	public Long getId() {
		return id;
	}

	public DeliverableRequirementGroupDTO setId(Long id) {
		this.id = id;
		return this;
	}

	public String getInstructions() {
		return instructions;
	}

	public DeliverableRequirementGroupDTO setInstructions(String instructions) {
		this.instructions = instructions;
		return this;
	}

	public int getHoursToComplete() {
		return hoursToComplete;
	}

	public DeliverableRequirementGroupDTO setHoursToComplete(int hoursToComplete) {
		this.hoursToComplete = hoursToComplete;
		return this;
	}

	public List<DeliverableRequirementDTO> getDeliverableRequirementDTOs() {
		return deliverableRequirementDTOs;
	}

	public DeliverableRequirementGroupDTO setDeliverableRequirementDTOs(List<DeliverableRequirementDTO> deliverableRequirementDTOs) {
		this.deliverableRequirementDTOs = deliverableRequirementDTOs;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof DeliverableRequirementGroupDTO)){
			return false;
		}

		DeliverableRequirementGroupDTO that = (DeliverableRequirementGroupDTO) o;

		if (hoursToComplete != that.hoursToComplete){
			return false;
		}
		if (deliverableRequirementDTOs != null ? !deliverableRequirementDTOs.equals(that.deliverableRequirementDTOs) : that.deliverableRequirementDTOs != null) {
			return false;
		}
		if (id != null ? !id.equals(that.id) : that.id != null) {
			return false;
		}
		if (instructions != null ? !instructions.equals(that.instructions) : that.instructions != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (instructions != null ? instructions.hashCode() : 0);
		result = 31 * result + hoursToComplete;
		result = 31 * result + (deliverableRequirementDTOs != null ? deliverableRequirementDTOs.hashCode() : 0);
		return result;
	}
}
