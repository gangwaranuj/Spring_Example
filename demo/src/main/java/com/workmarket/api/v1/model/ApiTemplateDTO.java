package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "Template")
@JsonDeserialize(builder = ApiTemplateDTO.Builder.class)
public class ApiTemplateDTO extends ApiAssignmentDetailsDTO {

// 		responseMap.put("template_id", responseMap.get("id"));
//
//	// Since we are sending back this template to be used in assignment creation, we will
//	// remove some items that aren't needed.
//	// Unset some nodes that shouldn't be part of the assignment process.
//		responseMap.remove("id");
//		responseMap.remove("status");
//		responseMap.remove("substatuses");
//		responseMap.remove("payment");
//		responseMap.remove("history");
//		responseMap.remove("active_resource");
//		responseMap.remove("resolution");
//		responseMap.remove("short_url");
//

	private final String templateName;
	private final String templateDescription;
	
	@ApiModelProperty(name = "template_name")
	@JsonProperty("template_name")
	public String getTemplateName() {
		return templateName;
	}

	@ApiModelProperty(name = "template_description")
	@JsonProperty("template_description")
	public String getTemplateDescription() {
		return templateDescription;
	}

	@Override
	@ApiModelProperty("template_id")
	@JsonProperty("template_id")
	public String getId() {
		return super.getId();
	}

	@Override
	@JsonIgnore
	public String getShortUrl() {
		return null;
	}

	@Override
	@JsonIgnore
	public String getStatus() {
		return null;
	}

	@Override
	@JsonIgnore
	public List<ApiAssignmentDetailsLabelDTO> getSubstatuses() {
		return null;
	}

	@Override
	@JsonIgnore
	public List<ApiAssignmentDetailsLabelDTO> getLabels() {
		return null;
	}

	@Override
	@JsonIgnore
	public String getResolution() {
		return null;
	}

	@Override
	@JsonIgnore
	public ApiActiveResourceDTO getActiveResource() {
		return null;
	}

	@Override
	@JsonIgnore
	public ApiPaymentDTO getPayment() {
		return null;
	}

	@Override
	@JsonIgnore
	public List<ApiHistoryDTO> getHistory() {
		return null;
	}

	public ApiTemplateDTO(ApiTemplateDTO.Builder builder) {
		super(builder);
		this.templateName = builder.templateName;
		this.templateDescription = builder.templateDescription;
	}

	public static class Builder extends ApiAssignmentDetailsDTO.Builder {

		private String templateName;
		private String templateDescription;

		public Builder() {
		}

		public Builder(ApiAssignmentDetailsDTO copy) {
			super(copy);
		}

		@JsonProperty("template_name")
		public Builder withTemplateName(String templateName) {
			this.templateName = templateName;
			return this;
		}

		@JsonProperty("template_description")
		public Builder withTemplateDescription(String templateDescription) {
			this.templateDescription = templateDescription;
			return this;
		}

		public ApiTemplateDTO build() {
			return new ApiTemplateDTO(this);
		}
	}
}
