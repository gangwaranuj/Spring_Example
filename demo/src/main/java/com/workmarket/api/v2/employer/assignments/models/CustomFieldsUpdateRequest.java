package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.workmarket.api.v2.model.CustomFieldDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("CustomFieldsUpdateRequest")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomFieldsUpdateRequest {

	@JsonProperty("id") long groupId;
	@JsonProperty("fields") List<CustomFieldDTO> fields;
	@JsonProperty("groups") List<CustomFieldGroupDTO> groups;

	public CustomFieldsUpdateRequest() {}

	public CustomFieldsUpdateRequest(List<CustomFieldGroupDTO> groups) {this.groups = groups;}

	public CustomFieldsUpdateRequest(long groupId,List<CustomFieldDTO> customFields) {
		this.groupId = groupId;
		this.fields = customFields;
	}


	@ApiModelProperty(name = "groups")
	@JsonProperty("groups")
	public List<CustomFieldGroupDTO> getGroups() {
		return ListUtils.emptyIfNull(groups);
	}

	public CustomFieldsUpdateRequest setGroups(List<CustomFieldGroupDTO> groups) {
		this.groups = groups;
		return this;
	}


	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public long getGroupId() {
		return groupId;
	}

	public CustomFieldsUpdateRequest setGroupId(long groupId) {
		this.groupId = groupId;
		return this;
	}

	@ApiModelProperty(name = "fields")
	@JsonProperty("fields")
	public List<CustomFieldDTO> getFields() {
		return fields;
	}

	public CustomFieldsUpdateRequest setFields(List<CustomFieldDTO> fields) {
		this.fields = fields;
		return this;
	}

	@Override
	public String toString(){
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
