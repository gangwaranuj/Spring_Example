package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import com.workmarket.domains.model.assessment.SurveyAssessment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Survey")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = SurveyDTO.Builder.class)
public class SurveyDTO {

	private Long id;
	private String name;
	private String description;
	private Boolean required;

	private SurveyDTO(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.description = builder.description;
		this.required = builder.required;

	}


	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	public Long getId() {
		return id;
	}

	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	public String getName() {
		return name;
	}


	@ApiModelProperty(name = "description")
	@JsonProperty("description")
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	public String getDescription() {
		return description;
	}

	@ApiModelProperty(name = "required")
	@JsonProperty("required")
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	public Boolean getRequired() {
		return required;
	}

	public static class Builder implements AbstractBuilder<SurveyDTO> {
		private Long id;
		private String name;
		private String description;
		private Boolean required;

		public Builder(SurveyDTO surveyDTO) {
			this.id = surveyDTO.id;
			this.name = surveyDTO.name;
			this.description = surveyDTO.description;
			this.required = surveyDTO.required;
		}

		public Builder(SurveyAssessment surveyDTO) {
			this.id = surveyDTO.getId();
			this.name = surveyDTO.getName();
			this.description = surveyDTO.getDescription();
			this.required = false;
		}

		public Builder() {
		}

		@JsonProperty("id") public Builder setId(Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("name") public Builder setName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("description") public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		@JsonProperty("required") public Builder setRequired(Boolean required) {
			this.required = required;
			return this;
		}

		@Override
		public SurveyDTO build() {
			return new SurveyDTO(this);
		}
	}
}
