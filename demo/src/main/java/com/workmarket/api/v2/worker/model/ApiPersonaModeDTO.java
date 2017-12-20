package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;

@ApiModel("PersonaMode")
@JsonDeserialize(builder = ApiPersonaModeDTO.Builder.class)
public class ApiPersonaModeDTO {
	private final PersonaMode personaMode;

	private ApiPersonaModeDTO(Builder builder) {
		this.personaMode = builder.personaMode;
	}

	public PersonaMode getPersonaMode() {
		return personaMode;
	}


	public static class Builder {
		private PersonaMode personaMode;

		public Builder() {
		}

		@JsonProperty("personaMode")
		public Builder personaMode(PersonaMode personaMode) {
			this.personaMode = personaMode;
			return this;
		}

		public ApiPersonaModeDTO build() {
			return new ApiPersonaModeDTO(this);
		}
	}
}
