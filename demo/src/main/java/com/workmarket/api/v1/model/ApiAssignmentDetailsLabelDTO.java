package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "AssignmentDetailsLabel")
@JsonDeserialize(builder = ApiAssignmentDetailsLabelDTO.Builder.class)
public class ApiAssignmentDetailsLabelDTO {
	private final String code;
	private final String note;
	private final String description;
	private final Boolean userResolvable;
	private final String colorRgb;
	private final Long id;
	private final Boolean setId;
	private final Boolean setDescription;
	private final Boolean setCode;
	private final Boolean setColorRgb;
	private final Boolean setNote;

	private ApiAssignmentDetailsLabelDTO(Builder builder) {
		code = builder.code;
		note = builder.note;
		description = builder.description;
		userResolvable = builder.userResolvable;
		colorRgb = builder.colorRgb;
		id = builder.id;
		setId = builder.setId;
		setDescription = builder.setDescription;
		setCode = builder.setCode;
		setColorRgb = builder.setColorRgb;
		setNote = builder.setNote;
	}

	@ApiModelProperty(name = "code")
	@JsonProperty("code")
	public String getCode() {
		return code;
	}

	@ApiModelProperty(name = "note")
	@JsonProperty("note")
	public String getNote() {
		return note;
	}
	
	@ApiModelProperty(name = "description")
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@ApiModelProperty(name = "userResolvable")
	@JsonProperty("userResolvable")
	public Boolean getUserResolvable() {
		return userResolvable;
	}

	@ApiModelProperty(name = "colorRgb")
	@JsonProperty("colorRgb")
	public String getColorRgb() {
		return colorRgb;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@ApiModelProperty(name = "isSetId")
	@JsonProperty("isSetId")
	public Boolean getSetId() {
		return setId;
	}

	@ApiModelProperty(name = "isSetDescription")
	@JsonProperty("isSetDescription")
	public Boolean getSetDescription() {
		return setDescription;
	}

	@ApiModelProperty(name = "isSetCode")
	@JsonProperty("isSetCode")
	public Boolean getSetCode() {
		return setCode;
	}

	@ApiModelProperty(name = "isSetColorRgb")
	@JsonProperty("isSetColorRgb")
	public Boolean getSetColorRgb() {
		return setColorRgb;
	}

	@ApiModelProperty(name = "isSetNote")
	@JsonProperty("isSetNote")
	public Boolean getSetNote() {
		return setNote;
	}


	public static final class Builder {
		private String code;
		private String note;
		private String description;
		private Boolean userResolvable;
		private String colorRgb;
		private Long id;
		private Boolean setId;
		private Boolean setDescription;
		private Boolean setCode;
		private Boolean setColorRgb;
		private Boolean setNote;

		public Builder() {
		}

		public Builder(ApiAssignmentDetailsLabelDTO copy) {
			this.code = copy.code;
			this.note = copy.note;
			this.description = copy.description;
			this.userResolvable = copy.userResolvable;
			this.colorRgb = copy.colorRgb;
			this.id = copy.id;
			this.setId = copy.setId;
			this.setDescription = copy.setDescription;
			this.setCode = copy.setCode;
			this.setColorRgb = copy.setColorRgb;
			this.setNote = copy.setNote;
		}

		@JsonProperty("code")
		public Builder withCode(String code) {
			this.code = code;
			return this;
		}

		@JsonProperty("note")
		public Builder withNote(String note) {
			this.note = note;
			return this;
		}

		@JsonProperty("description")
		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		@JsonProperty("user_resolvable")
		public Builder withUserResolvable(Boolean userResolvable) {
			this.userResolvable = userResolvable;
			return this;
		}

		@JsonProperty("color_rgb")
		public Builder withColorRgb(String colorRgb) {
			this.colorRgb = colorRgb;
			return this;
		}

		@JsonProperty("id")
		public Builder withId(Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("set_id")
		public Builder withSetId(Boolean setId) {
			this.setId = setId;
			return this;
		}

		@JsonProperty("set_description")
		public Builder withSetDescription(Boolean setDescription) {
			this.setDescription = setDescription;
			return this;
		}

		@JsonProperty("set_code")
		public Builder withSetCode(Boolean setCode) {
			this.setCode = setCode;
			return this;
		}

		@JsonProperty("set_color_rgb")
		public Builder withSetColorRgb(Boolean setColorRgb) {
			this.setColorRgb = setColorRgb;
			return this;
		}

		@JsonProperty("set_note")
		public Builder withSetNote(Boolean setNote) {
			this.setNote = setNote;
			return this;
		}

		public ApiAssignmentDetailsLabelDTO build() {
			return new ApiAssignmentDetailsLabelDTO(this);
		}
	}
}
