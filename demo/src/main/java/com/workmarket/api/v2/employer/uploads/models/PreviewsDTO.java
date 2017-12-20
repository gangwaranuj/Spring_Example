package com.workmarket.api.v2.employer.uploads.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.api.client.util.Lists;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonDeserialize(builder = PreviewsDTO.Builder.class)
public class PreviewsDTO {
	private final String uuid;
	private final long count;
	private final List<PreviewDTO> previews = Lists.newArrayList();

	private PreviewsDTO(Builder builder) {
		this.uuid = builder.uuid;
		this.count = builder.count;

		for (PreviewDTO.Builder preview : builder.previews) {
			this.previews.add(preview.build());
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

	@ApiModelProperty(name = "previews")
	@JsonProperty("previews")
	public List<PreviewDTO> getPreviews() {
		return previews;
	}

	public static class Builder implements AbstractBuilder<PreviewsDTO> {
		private String uuid;
		private long count;
		private List<PreviewDTO.Builder> previews = Lists.newArrayList();

		public Builder(PreviewsDTO previewsDTO) {
			this.uuid = previewsDTO.uuid;
			this.count = previewsDTO.count;

			for (PreviewDTO preview : previewsDTO.previews) {
				this.previews.add(new PreviewDTO.Builder(preview));
			}
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

		@JsonProperty("previews")
		public Builder setPreviews(List<PreviewDTO.Builder> previews) {
			this.previews = previews;
			return this;
		}

		public Builder addPreview(PreviewDTO.Builder preview) {
			this.previews.add(preview);
			return this;
		}

		@Override
		public PreviewsDTO build() {
			return new PreviewsDTO(this);
		}
	}
}
