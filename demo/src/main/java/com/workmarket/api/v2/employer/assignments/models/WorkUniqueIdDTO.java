package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

// TODO - API - bring in the proper builder pattern here
@ApiModel("WorkUniqueId")
public class WorkUniqueIdDTO {

	private String externalUniqueIdDisplayName;
	private String externalUniqueIdValue;
	private Integer externalUniqueIdVersion;

	@JsonCreator
	public WorkUniqueIdDTO(@JsonProperty("externalUniqueIdDisplayName") String externalUniqueIdDisplayName,
												 @JsonProperty("externalUniqueIdValue") String externalUniqueIdValue,
												 @JsonProperty("externalUniqueIdVersion") Integer externalUniqueIdVersion) {
		this.externalUniqueIdDisplayName = externalUniqueIdDisplayName;
		this.externalUniqueIdValue = externalUniqueIdValue;
		this.externalUniqueIdVersion = externalUniqueIdVersion;
	}

	public WorkUniqueIdDTO(com.workmarket.domains.work.model.WorkUniqueId workUniqueId) {
		if (workUniqueId != null) {
			externalUniqueIdDisplayName = workUniqueId.getDisplayName();
			externalUniqueIdValue = workUniqueId.getIdValue();
			externalUniqueIdVersion = workUniqueId.getVersion();
		}
	}

	@ApiModelProperty(name = "externalUniqueIdDisplayName")
	@JsonProperty("externalUniqueIdDisplayName")
	public String getExternalUniqueIdDisplayName() {
		return externalUniqueIdDisplayName;
	}

	@ApiModelProperty(name = "externalUniqueIdValue")
	@JsonProperty("externalUniqueIdValue")
	public String getExternalUniqueIdValue() {
		return externalUniqueIdValue;
	}

	@ApiModelProperty(name = "externalUniqueIdVersion")
	@JsonProperty("externalUniqueIdVersion")
	public Integer getExternalUniqueIdVersion() {
		return externalUniqueIdVersion;
	}
}
