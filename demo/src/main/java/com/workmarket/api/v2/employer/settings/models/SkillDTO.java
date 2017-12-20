package com.workmarket.api.v2.employer.settings.models;

public class SkillDTO {

	private final long id;
	private final String name;

	public SkillDTO(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static class Builder {
		private long id;
		private String name;

		public Builder() {
		}

		public Builder(SkillDTO skillDTO) {
			this.id = skillDTO.id;
			this.name = skillDTO.name;
		}

		public Builder setId(long id) {
			this.id = id;
			return this;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public SkillDTO build() {
			return new SkillDTO(this);
		}
	}
}
