package com.workmarket.domains.model.requirementset.esignature;

import com.workmarket.domains.model.requirementset.Requirable;

public class EsignatureRequirable implements Requirable {

	private String templateUuid;
	private String name;

	private EsignatureRequirable(final Builder builder) {
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

		public EsignatureRequirable build() {
			return new EsignatureRequirable(this);
		}
	}

	public String getTemplateUuid() {
		return templateUuid;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
