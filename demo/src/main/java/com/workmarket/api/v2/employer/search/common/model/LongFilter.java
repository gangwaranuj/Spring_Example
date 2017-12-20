package com.workmarket.api.v2.employer.search.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Filter where the id field is a Long.
 */
public class LongFilter extends Filter<Long> {

	/**
	 * Constructor.
	 * @param name The name for the filter
	 * @param id The id for the filter
	 * @param count The count for the filter
	 */
	public LongFilter(@JsonProperty("name") final String name,
	                  @JsonProperty("id") final Long id,
	                  @JsonProperty("count") final Long count) {
		super(name, id, count);
	}

	/**
	 * Constructor.
	 * @param builder The builder holding the values
	 */
	private LongFilter(final Builder builder) {
		super(builder.name, builder.id, builder.count);
	}

	/**
	 * Builder for the LongFilter
	 */
	public static class Builder {
		private String name;
		private Long id;
		private Long count;

		/**
		 * Constructor.
		 */
		public Builder() {

		}

		/**
		 * Constructor (copy).
		 * @param filter The value object to seed the builder from
		 */
		public Builder(final LongFilter filter) {
			this.name = filter.getName();
			this.id = filter.getId();
			this.count = filter.getCount();
		}

		/**
		 * Sets the name.
		 *
		 * @param name The name to set
		 * @return Builder The builder
		 */
		public Builder setName(final String name) {
			this.name = name;
			return this;
		}

		/**
		 * Sets the id.
		 *
		 * @param id The id to set
		 * @return Builder The builder
		 */
		public Builder setId(final Long id) {
			this.id = id;
			return this;
		}

		/**
		 * Sets the count.
		 *
		 * @param count The count to set
		 * @return Builder The builder
		 */
		public Builder setCount(final Long count) {
			this.count = count;
			return this;
		}

		public LongFilter build() {
			return new LongFilter(this);
		}
	}
}
