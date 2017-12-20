package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModel;

@ApiModel("TalentPool")
@JsonDeserialize(builder = TalentPoolDTO.Builder.class)
public class TalentPoolDTO {
	private final Long id;
	private final String name;
	private final String description;
	private final Integer requirements;
	private final Integer requirementsMet;
	private final Boolean eligible;

	private TalentPoolDTO(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.description = builder.description;
		this.requirements = builder.requirements;
		this.requirementsMet= builder.requirementsMet;
		this.eligible = builder.eligible;
	}

	public static class Builder implements AbstractBuilder<TalentPoolDTO> {
		private Long id;
		private String name;
		private String description;
		private Integer requirements;
		private Integer requirementsMet;
		private Boolean eligible;

		public Builder(TalentPoolDTO talentPoolDTO) {
			this.id = talentPoolDTO.id;
			this.name = talentPoolDTO.name;
			this.description = talentPoolDTO.description;
			this.requirements = talentPoolDTO.requirements;
			this.requirementsMet= talentPoolDTO.requirementsMet;
			this.eligible = talentPoolDTO.eligible;
		}

		public Builder() {}

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

		@JsonProperty("requirements") public Builder setRequirements(Integer requirements) {
			this.requirements = requirements;
			return this;
		}


		@JsonProperty("requirementsMet") public Builder setRequirementsMet(Integer requirementsMet) {
			this.requirementsMet = requirementsMet;
			return this;
		}

		@JsonProperty("eligible") public Builder setEligible(Boolean eligible) {
			this.eligible = eligible;
			return this;
		}

		@Override
		public TalentPoolDTO build() {
			return new TalentPoolDTO(this);
		}
	}
}
