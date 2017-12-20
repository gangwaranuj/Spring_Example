package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("AcceptOnBehalf")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcceptOnBehalfDTO {
	String note;
	String userNumber;

	public AcceptOnBehalfDTO() {}

	@ApiModelProperty(name = "note")
	@JsonProperty("note")
	public String getNote() {
		return note;
	}

	public AcceptOnBehalfDTO setNote(String note) {
		this.note = note;
		return this;
	}

	@ApiModelProperty(name = "userNumber")
	@JsonProperty("userNumber")
	public String getUserNumber() {
		return userNumber;
	}

	public AcceptOnBehalfDTO setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}
}
