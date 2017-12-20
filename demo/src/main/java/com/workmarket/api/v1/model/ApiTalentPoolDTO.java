package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "TalentPool")
@JsonDeserialize(builder = ApiTalentPoolDTO.Builder.class)
public class ApiTalentPoolDTO {
	private final Long id;
	private final String name;
	private final String description;
	private final Integer members;
	private final Boolean active;
	private final Boolean deleted;

	private ApiTalentPoolDTO(Builder builder) {
		id = builder.id;
		name = builder.name;
		description = builder.description;
		members = builder.members;
		active = builder.active;
		deleted = builder.deleted;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

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

	@ApiModelProperty(name = "members")
	@JsonProperty("members")
	public Integer getMembers() {
		return members;
	}

	@ApiModelProperty(name = "active")
	@JsonProperty("active")
	public Boolean getActive() {
		return active;
	}

	@ApiModelProperty(name = "deleted")
	@JsonProperty("deleted")
	public Boolean getDeleted() {
		return deleted;
	}
	
	public static final class Builder {
		private Long id;
		private String name;
		private String description;
		private Integer members;
		private Boolean active;
		private Boolean deleted;

		public Builder() {
		}

		public Builder(ApiTalentPoolDTO copy) {
			this.id = copy.id;
			this.name = copy.name;
			this.description = copy.description;
			this.members = copy.members;
			this.active = copy.active;
			this.deleted = copy.deleted;
		}

		@JsonProperty("id")
		public Builder withId(Long id) {
			this.id = id;
			return this;
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

		@JsonProperty("members")
		public Builder withMembers(Integer members) {
			this.members = members;
			return this;
		}

		@JsonProperty("active")
		public Builder withActive(Boolean active) {
			this.active = active;
			return this;
		}

		@JsonProperty("deleted")
		public Builder withDeleted(Boolean deleted) {
			this.deleted = deleted;
			return this;
		}

		public ApiTalentPoolDTO build() {
			return new ApiTalentPoolDTO(this);
		}
	}
}
