package com.workmarket.api.v2.employer.search.worker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.search.common.model.BaseDTO;

/**
 * Value object representing some verification (like a background check).
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = Verification.Builder.class)
public class Verification extends BaseDTO {
	private final String id;
	private final String name;
	private final String checkDate;

	/**
	 * Constructor.
	 * @param builder The builder used to create this instance
	 */
	private Verification(final Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.checkDate = builder.checkDate;
	}

	/**
	 * Gets the id.
	 *
	 * @return java.lang.String The id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the name.
	 *
	 * @return java.lang.String The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the checkDate.
	 *
	 * @return java.lang.String The checkDate
	 */
	public String getCheckDate() {
		return checkDate;
	}

	/**
	 * Builder used to create instance of our verification.
	 */
	public static class Builder {
		private String id;
		private String name;
		private String checkDate;

		/**
		 * Constructor.
		 */
		public Builder() {

		}

		/**
		 * Constructor.
		 * @param verification The verification to build this from
		 */
		public Builder(final Verification verification) {
			this.id = verification.id;
			this.name = verification.name;
			this.checkDate = verification.checkDate;
		}

		/**
		 * Sets the id.
		 *
		 * @param id The id to set
		 * @return Builder The updated builder
		 */
		@JsonProperty("id") public Builder setId(final String id) {
			this.id = id;
			return this;
		}

		/**
		 * Sets the name.
		 *
		 * @param name The name to set
		 * @return Builder The updated builder
		 */
		@JsonProperty("name") public Builder setName(final String name) {
			this.name = name;
			return this;
		}

		/**
		 * Sets the checkDate.
		 *
		 * @param checkDate The checkDate to set
		 * @return Builder The updated builder
		 */
		@JsonProperty("checkDate") public Builder setCheckDate(final String checkDate) {
			this.checkDate = checkDate;
			return this;
		}

		/**
		 * Construct our verification instance.
		 * @return Verification The new instance
		 */
		public Verification build() {
			return new Verification(this);
		}
	}
}
