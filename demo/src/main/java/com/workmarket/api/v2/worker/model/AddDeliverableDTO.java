package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@ApiModel("AddDeliverable")
@JsonDeserialize(builder = AddDeliverableDTO.Builder.class)
public class AddDeliverableDTO {

	@NotNull private final String name;
	private final String description;
	@Min(0) private final Integer position;
	@NotNull private final String data;

	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@ApiModelProperty(name = "description")
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@ApiModelProperty(name = "position")
	@JsonProperty("position")
	public Integer getPosition() {
		return position;
	}

	@ApiModelProperty(name = "data")
	@JsonProperty("data")
	public String getData() {
		return data;
	}

	private AddDeliverableDTO(Builder builder) {
		name = builder.name;
		description = builder.description;
		position = builder.position;
		data = builder.data;
	}

	public static final class Builder {
		private String name;
		private String description;
		private Integer position;
		private String data;

		public Builder() {
		}

		public Builder(AddDeliverableDTO copy) {
			this.name = copy.name;
			this.description = copy.description;
			this.position = copy.position;
			this.data = copy.data;
		}

		@JsonProperty("name")
		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("description")
		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		@JsonProperty("position")
		public Builder withPosition(Integer position) {
			this.position = position;
			return this;
		}

		@JsonProperty("data")
		public Builder withData(String data) {
			this.data = data;
			return this;
		}

		public AddDeliverableDTO build() {
			return new AddDeliverableDTO(this);
		}
	}
}
