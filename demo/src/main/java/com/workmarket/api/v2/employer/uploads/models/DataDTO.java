package com.workmarket.api.v2.employer.uploads.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

@JsonDeserialize(builder = DataDTO.Builder.class)
public class DataDTO {
	private final String uuid;
	private final long count;
	private final List<Map<String, CellDTO>> data = Lists.newArrayList();

	private DataDTO(Builder builder) {
		this.uuid = builder.uuid;
		this.count = builder.count;

		for (Map<String, CellDTO.Builder> dataBuilder : builder.data) {
			Map<String, CellDTO> map = Maps.newHashMap();
			for (String key : dataBuilder.keySet()) {
				map.put(key, dataBuilder.get(key).build());
			}
			this.data.add(map);
		}
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

	@ApiModelProperty(name = "data")
	@JsonProperty("data")
	public List<Map<String, CellDTO>> getData() {
		return data;
	}

	public static class Builder implements AbstractBuilder<DataDTO> {
		private String uuid;
		private long count;
		private List<Map<String, CellDTO.Builder>> data = Lists.newArrayList();

		public Builder(DataDTO dataDTO) {
			this.uuid = dataDTO.uuid;
			this.count = dataDTO.count;

			for (Map<String, CellDTO> data : dataDTO.data) {
				Map<String, CellDTO.Builder> map = Maps.newHashMap();
				for (String key : data.keySet()) {
					map.put(key, new CellDTO.Builder(data.get(key)));
				}
				this.data.add(map);
			}
		}

		public Builder() {}

		@JsonProperty("uuid") public Builder setUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("count") public Builder setCount(long count) {
			this.count = count;
			return this;
		}

		@JsonProperty("data") public Builder setData(List<Map<String, CellDTO.Builder>> data) {
			this.data = data;
			return this;
		}

		public Builder addData(Map<String, CellDTO.Builder> data) {
			this.data.add(data);
			return this;
		}

		@Override
		public DataDTO build() {
			return new DataDTO(this);
		}
	}
}
