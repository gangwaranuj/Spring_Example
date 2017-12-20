package com.workmarket.api.v2.model;

public class SignableEsignatureResponseDTO {

	private String clientId;
	private String signingUrl;

	private SignableEsignatureResponseDTO(final Builder builder) {
		this.clientId = builder.clientId;
		this.signingUrl = builder.signingUrl;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String clientId;
		private String signingUrl;

		private Builder() {
		}

		public Builder withClientId(final String clientId) {
			this.clientId = clientId;
			return this;
		}

		public Builder withSigningUrl(final String signingUrl) {
			this.signingUrl = signingUrl;
			return this;
		}

		public SignableEsignatureResponseDTO build() {
			return new SignableEsignatureResponseDTO(this);
		}
	}

	public String getClientId() {
		return clientId;
	}

	public String getSigningUrl() {
		return signingUrl;
	}
}
