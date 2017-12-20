package com.workmarket.web.forms;

import java.util.List;

/**
 * Created by rahul on 2/27/14
 */
public class ResourceCompletionForm {
	Long id;
	String instructions;
	Integer hoursToComplete;
	List<DeliverableRequirementForm> deliverableRequirements;

	public ResourceCompletionForm() {}

	public ResourceCompletionForm(Long id, String instructions, Integer hoursToComplete) {
		this.id = id;
		this.instructions = instructions;
		this.hoursToComplete = hoursToComplete;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public Integer getHoursToComplete() {
		return hoursToComplete;
	}

	public void setHoursToComplete(Integer hoursToComplete) {
		this.hoursToComplete = hoursToComplete;
	}

	public List<DeliverableRequirementForm> getDeliverableRequirements() {
		return deliverableRequirements;
	}

	public void setDeliverableRequirements(List<DeliverableRequirementForm> deliverableRequirements) {
		this.deliverableRequirements = deliverableRequirements;
	}
}
