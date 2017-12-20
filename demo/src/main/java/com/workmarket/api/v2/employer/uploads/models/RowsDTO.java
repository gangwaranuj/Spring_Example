package com.workmarket.api.v2.employer.uploads.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.api.client.util.Lists;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonDeserialize(builder = RowsDTO.Builder.class)
public class RowsDTO {
	private final String uuid;
	private final long count;
	private final List<String[]> rows;

	private RowsDTO(Builder builder) {
		this.uuid = builder.uuid;
		this.count = builder.count;
		this.rows = builder.rows;
	}

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "count")
	@JsonProperty("count")
	public long getCount() {
		return count;
	}

	@ApiModelProperty(name = "rows")
	@JsonProperty("rows")
	public List<String[]> getRows() {
		return rows;
	}

	public static class Builder implements AbstractBuilder<RowsDTO> {
		private String uuid;
		private long count = 0;
		private List<String[]> rows = Lists.newArrayList();

		public Builder(RowsDTO dataDTO) {
			this.uuid = dataDTO.uuid;
			this.count = dataDTO.count;
			this.rows = dataDTO.rows;
		}

		public Builder() {}

		@JsonProperty("uuid")
		public Builder setUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("count")
		public Builder setCount(long count) {
			this.count = count;
			return this;
		}

		@JsonProperty("rows")
		public Builder setRows(List<String[]> rows) {
			this.rows = rows;
			return this;
		}

		@JsonProperty("row")
		public Builder addRow(String[] row) {
			this.rows.add(row);
			return this;
		}

		@Override
		public RowsDTO build() {
			return new RowsDTO(this);
		}
	}
}
