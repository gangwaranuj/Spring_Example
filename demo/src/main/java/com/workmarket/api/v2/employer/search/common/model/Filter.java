package com.workmarket.api.v2.employer.search.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * Class used to represent a filter.
 * @param <T> The type of id for the filter - can be integer or string
 */
abstract class Filter<T> {
	private final String name;
	private final T id;
	private final Long count;

	/**
	 * Constructor.
	 * @param name The name of the filter
	 * @param id The id for the filter
	 * @param count The count for the number of elements
	 */
	Filter(final String name, final T id, final Long count) {
		this.name = name;
		this.id = id;
		this.count = count;
	}

	/**
	 * Gets the name.
	 *
	 * @return java.lang.String The name
	 */
	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	/**
	 * Gets the id.
	 *
	 * @return T The id
	 */
	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public T getId() {
		return id;
	}

	/**
	 * Gets the count.
	 *
	 * @return java.lang.Long The count
	 */
	@ApiModelProperty(name = "count")
	@JsonProperty("count")
	public Long getCount() {
		return count;
	}

}
