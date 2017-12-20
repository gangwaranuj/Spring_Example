package com.workmarket.service.business.dto;

public class EsignatureTemplateDTO {

	private String templateUuid;
	private String name;

	private EsignatureTemplateDTO(final Builder builder) {
		this.templateUuid = builder.templateUuid;
		this.name = builder.name;
	}

	public static Builder newBuilder() {
		return new Builder();
	}


	public static final class Builder {
		private String templateUuid;
		private String name;

		private Builder() {
		}

		public Builder setTemplateUuid(final String templateUuid) {
			this.templateUuid = templateUuid;
			return this;
		}

		public Builder setName(final String name) {
			this.name = name;
			return this;
		}

		public EsignatureTemplateDTO build() {
			return new EsignatureTemplateDTO(this);
		}
	}

	public String getTemplateUuid() {
		return templateUuid;
	}

	public String getName() {
		return name;
	}
}
