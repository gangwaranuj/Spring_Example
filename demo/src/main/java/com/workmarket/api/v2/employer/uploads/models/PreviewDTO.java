package com.workmarket.api.v2.employer.uploads.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

@JsonDeserialize(builder = PreviewDTO.Builder.class)
public class PreviewDTO {
	private final AssignmentDTO assignmentDTO;
	private final Map<String, CellDTO> rowData = Maps.newHashMap();
	private final List<ApiBaseError> parseErrors;
	private final List<ApiBaseError> validationErrors;
	private final int rowNumber;
	private final String uuid;
	private final String[] row;

	private PreviewDTO(Builder builder) {
		this.assignmentDTO = builder.assignmentDTO.build();

		for (String key : builder.rowData.keySet()) {
			this.rowData.put(key, builder.rowData.get(key).build());
		}

		this.parseErrors = builder.parseErrors;
		this.validationErrors = builder.validationErrors;
		this.rowNumber = builder.rowNumber;
		this.uuid = builder.uuid;
		this.row = builder.row;
	}

	@ApiModelProperty(name = "assignmentDTO")
	@JsonProperty("assignmentDTO")
	public AssignmentDTO getAssignmentDTO() {
		return assignmentDTO;
	}

	@ApiModelProperty(name = "rowData")
	@JsonProperty("rowData")
	public Map<String, CellDTO> getRowData() {
		return rowData;
	}

	@ApiModelProperty(name = "parseErrors")
	@JsonProperty("parseErrors")
	public List<ApiBaseError> getParseErrors() {
		return parseErrors;
	}

	@ApiModelProperty(name = "validationErrors")
	@JsonProperty("validationErrors")
	public List<ApiBaseError> getValidationErrors() {
		return validationErrors;
	}

	@ApiModelProperty(name = "rowNumber")
	@JsonProperty("rowNumber")
	public int getRowNumber() {
		return rowNumber;
	}

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "row")
	@JsonProperty("row")
	public String[] getRow() {
		return row;
	}

	public static class Builder implements AbstractBuilder<PreviewDTO> {
		private AssignmentDTO.Builder assignmentDTO = new AssignmentDTO.Builder();
		private Map<String, CellDTO.Builder> rowData = Maps.newHashMap();
		private List<ApiBaseError> parseErrors = Lists.newArrayList();
		private List<ApiBaseError> validationErrors = Lists.newArrayList();
		private int rowNumber;
		private String uuid;
		private String[] row;

		public Builder(PreviewDTO previewDTO) {
			this.assignmentDTO = new AssignmentDTO.Builder(previewDTO.assignmentDTO);

			for (String key : previewDTO.rowData.keySet()) {
				this.rowData.put(key, new CellDTO.Builder(previewDTO.rowData.get(key)));
			}

			this.parseErrors = previewDTO.parseErrors;
			this.validationErrors = previewDTO.validationErrors;
			this.rowNumber = previewDTO.rowNumber;
			this.uuid = previewDTO.uuid;
			this.row = previewDTO.row;
		}

		public Builder() {}

		public boolean hasParseErrors() {
			return !this.validationErrors.isEmpty();
		}

		@JsonProperty("assignmentDTO") public Builder setAssignmentDTO(AssignmentDTO.Builder assignmentDTO) {
			this.assignmentDTO = assignmentDTO;
			return this;
		}
		@JsonProperty("rowData") public Builder setRowData(Map<String, CellDTO.Builder> rowData) {
			this.rowData = rowData;
			return this;
		}

		@JsonProperty("parseErrors") public Builder setParseErrors(List<ApiBaseError> parseErrors) {
			this.parseErrors = parseErrors;
			return this;
		}

		public Builder addParseError(ApiBaseError parseError) {
			this.parseErrors.add(parseError);
			return this;
		}

		public Builder addValidationError(ApiBaseError validationError) {
			this.validationErrors.add(validationError);
			return this;
		}

		@JsonProperty("validationErrors") public Builder setValidationErrors(List<ApiBaseError> validationErrors) {
			this.validationErrors = validationErrors;
			return this;
		}

		@JsonProperty("rowNumber") public Builder setRowNumber(int rowNumber) {
			this.rowNumber = rowNumber;
			return this;
		}

		@JsonProperty("uuid") public Builder setUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("row") public Builder setRow(String[] row) {
			this.row = row;
			return this;
		}

		@Override
		public PreviewDTO build() {
			return new PreviewDTO(this);
		}
	}
}
