package com.workmarket.service.business.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class WorkCustomFieldGroupDTO {
	
	private Long workCustomFieldGroupId;
	@NotEmpty
	private String name;
	@Valid
	@NotNull
	private List<WorkCustomFieldDTO> workCustomFields = new ArrayList<>();
	private boolean required = false;
	private Integer position;
	
	public Long getWorkCustomFieldGroupId() {
		return workCustomFieldGroupId;
	}
	public void setWorkCustomFieldGroupId(Long workCustomFieldGroupId) {
		this.workCustomFieldGroupId = workCustomFieldGroupId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<WorkCustomFieldDTO> getWorkCustomFields() {
		return workCustomFields;
	}
	public void setWorkCustomFields(List<WorkCustomFieldDTO> workCustomFields) {
		this.workCustomFields = workCustomFields;
	}
	
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}

	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WorkCustomFieldGroupDTO that = (WorkCustomFieldGroupDTO) o;

		if (required != that.required) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (position != null ? !position.equals(that.position) : that.position != null) return false;
		if (workCustomFieldGroupId != null ? !workCustomFieldGroupId.equals(that.workCustomFieldGroupId) : that.workCustomFieldGroupId != null)
			return false;
		if (workCustomFields != null ? !workCustomFields.equals(that.workCustomFields) : that.workCustomFields != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = workCustomFieldGroupId != null ? workCustomFieldGroupId.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (workCustomFields != null ? workCustomFields.hashCode() : 0);
		result = 31 * result + (required ? 1 : 0);
		result = 31 * result + (position != null ? position.hashCode() : 0);
		return result;
	}
}
