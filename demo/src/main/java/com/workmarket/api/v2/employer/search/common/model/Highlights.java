package com.workmarket.api.v2.employer.search.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Value object holding details of our "explain" related to matches of data in a specific
 * set of fields.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Highlights extends BaseDTO {
	private final ImmutableList<String> certifications;
	private final ImmutableList<String> skills;
	private final ImmutableList<String> licenses;


	/**
	 * Constructor (for Jackson)
	 * @param certifications The highlights from the set of certifications
	 * @param skills The highlights from the set of skills
	 * @param licenses The highlights from the set of licenses
	 */
	public Highlights(@JsonProperty("certifications") final List<String> certifications,
	                  @JsonProperty("skills") final List<String> skills,
	                  @JsonProperty("licenses") final List<String> licenses) {
		if (CollectionUtils.isNotEmpty(certifications)) {
			this.certifications = ImmutableList.copyOf(certifications);
		} else {
			this.certifications = ImmutableList.of();
		}

		if (CollectionUtils.isNotEmpty(skills)) {
			this.skills = ImmutableList.copyOf(skills);
		} else {
			this.skills = ImmutableList.of();
		}

		if (CollectionUtils.isNotEmpty(licenses)) {
			this.licenses = ImmutableList.copyOf(licenses);
		} else {
			this.licenses = ImmutableList.of();
		}
	}

	/**
	 * Constructor.
	 * @param builder The builder
	 */
	private Highlights(final Builder builder) {
		this.certifications = builder.certificationsBuilder.build();
		this.skills = builder.skillsBuilder.build();
		this.licenses = builder.licensesBuilder.build();
	}

	/**
	 * Gets the certifications.
	 *
	 * @return java.util.List<java.lang.String> The certifications
	 */
	public ImmutableList<String> getCertifications() {
		return certifications;
	}

	/**
	 * Gets the skills.
	 *
	 * @return java.util.List<java.lang.String> The skills
	 */
	public ImmutableList<String> getSkills() {
		return skills;
	}

	/**
	 * Gets the licenses.
	 *
	 * @return java.util.List<java.lang.String> The licenses
	 */
	public List<String> getLicenses() {
		return licenses;
	}

	/**
	 * A builder used to create instances of our Highlights.
	 */
	public static class Builder {
		private ImmutableList.Builder<String> certificationsBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<String> skillsBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<String> licensesBuilder = new ImmutableList.Builder<>();


		/**
		 * Constructor.
		 */
		public Builder() {
		}

		/**
		 * Construct (copy) a new builder from the given value object.
		 * @param highlights The object we are constructing our builder from
		 */
		public Builder(final Highlights highlights) {
			certificationsBuilder.addAll(highlights.certifications);
			skillsBuilder.addAll(highlights.skills);
			licensesBuilder.addAll(highlights.licenses);
		}

		/**
		 * Add a new certification.
		 * @param certification The certification to add
		 * @return Builder The builder
		 */
		public Builder addCertification(final String certification) {
			certificationsBuilder.add(certification);
			return this;
		}

		/**
		 * Add a set of new certifications.
		 * @param certifications The certifications to add
		 * @return Builder The builder
		 */
		public Builder addCertifications(final List<String> certifications) {
			certificationsBuilder.addAll(certifications);
			return this;
		}

		/**
		 * Add a new skill.
		 * @param skill The skill to add
		 * @return Builder The builder
		 */
		public Builder addSkill(final String skill) {
			skillsBuilder.add(skill);
			return this;
		}

		/**
		 * Add a set of new skills.
		 * @param skills The skills to add
		 * @return Builder The builder
		 */
		public Builder addSkills(final List<String> skills) {
			skillsBuilder.addAll(skills);
			return this;
		}

		/**
		 * Add a new license.
		 * @param license The license to add
		 * @return Builder The builder
		 */
		public Builder addLicense(final String license) {
			licensesBuilder.add(license);
			return this;
		}

		/**
		 * Add a set of new licenses.
		 * @param licenses The licenses to add
		 * @return Builder The builder
		 */
		public Builder addLicenses(final List<String> licenses) {
			licensesBuilder.addAll(licenses);
			return this;
		}

		/**
		 * Build our instance.
		 * @return Highlights The new instance
		 */
		public Highlights build() {
			return new Highlights(this);
		}
	}
}
