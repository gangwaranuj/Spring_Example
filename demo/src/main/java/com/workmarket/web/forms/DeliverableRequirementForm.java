package com.workmarket.web.forms;

/**
 * Created by rahul on 2/28/14
 */
public class DeliverableRequirementForm {
	Long id;
	String type;
	String instructions;
	int numberOfFiles;

	public DeliverableRequirementForm() {}

	public DeliverableRequirementForm(Long id, String type, String instructions, int numberOfFiles) {
		this.id = id;
		this.type = type;
		this.instructions = instructions;
		this.numberOfFiles = numberOfFiles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public int getNumberOfFiles() {
		return numberOfFiles;
	}

	public void setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}
}
