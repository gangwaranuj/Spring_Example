package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("UpdateAsset")
@JsonDeserialize(builder = UpdateAssetDTO.Builder.class)
public class UpdateAssetDTO {

	@NotNull
	private String data;

	private UpdateAssetDTO(Builder builder) {
		data = builder.data;
	}

	@ApiModelProperty(name = "data")
	@JsonProperty("data")
	public String getData() {
		return data;
	}

	public static final class Builder {
		private String data;

		public Builder() {
		}

		public Builder(UpdateAssetDTO copy) {
			this.data = copy.data;
		}

		@JsonProperty("data")
		public Builder withData(String data) {
			this.data = data;
			return this;
		}

		public UpdateAssetDTO build() {
			return new UpdateAssetDTO(this);
		}
	}
}
