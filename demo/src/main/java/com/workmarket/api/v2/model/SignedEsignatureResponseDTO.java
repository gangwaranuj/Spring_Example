package com.workmarket.api.v2.model;

public class SignedEsignatureResponseDTO {

	private String executedUrl;

	private SignedEsignatureResponseDTO(final Builder builder) {
		executedUrl = builder.executedUrl;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String executedUrl;

		private Builder() {
		}

		public Builder withExecutedUrl(final String executedUrl) {
			this.executedUrl = executedUrl;
			return this;
		}

		public SignedEsignatureResponseDTO build() {
			return new SignedEsignatureResponseDTO(this);
		}
	}

	public String getExecutedUrl() {
		return executedUrl;
	}
}
