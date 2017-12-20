package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Set;

/**
 * Created by ianha on 4/1/15.
 */
@ApiModel("WorkersSearchRequest")
@JsonDeserialize(builder = WorkersSearchRequest.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkersSearchRequest {

	private final String keyword;

	// Pagination filters
	private final int page;
	private final int pageSize;
	private final String sortby; // Types of PeopleSearchSortByType
	private final String order;

	// Geo filters
	private final int radius; // miles
	private final String address; // Human readable string of address; Google resolved
	private final Set<String> countries;
	private final Set<Long> industries;

	private WorkersSearchRequest(Builder builder) {
		this.keyword = builder.keyword;
		this.page = builder.page;
		this.pageSize = builder.pageSize;
		this.sortby = builder.sortby;
		this.order = builder.order;
		this.radius = builder.radius;
		this.address = builder.address;
		this.countries = builder.countries;
		this.industries = builder.industries;
	}

	@ApiModelProperty(name = "keyword")
	@JsonProperty("keyword")
	public String getKeyword() {
		return keyword;
	}

	@ApiModelProperty(name = "page")
	@JsonProperty("page")
	public int getPage() {
		return page;
	}

	@ApiModelProperty(name = "pageSize")
	@JsonProperty("pageSize")
	public int getPageSize() {
		return pageSize;
	}

	@ApiModelProperty(name = "sortby")
	@JsonProperty("sortby")
	public String getSortby() {
		return sortby;
	}

	@ApiModelProperty(name = "order")
	@JsonProperty("order")
	public String getOrder() {
		return order;
	}

	@ApiModelProperty(name = "radius")
	@JsonProperty("radius")
	public int getRadius() {
		return radius;
	}

	@ApiModelProperty(name = "address")
	@JsonProperty("address")
	public String getAddress() {
		return address;
	}

	@ApiModelProperty(name = "countries")
	@JsonProperty("countries")
	public Set<String> getCountries() {
		return countries;
	}

	@ApiModelProperty(name = "industries")
	@JsonProperty("industries")
	public Set<Long> getIndustries() {
		return industries;
	}


	public static class Builder {
		private String keyword = "";
		private int page = 1;
		private int pageSize = 25;
		private String sortby = "relevancy";
		private String order = "desc";
		private int radius = 100;
		private String address;
		private Set<String> countries;
		private Set<Long> industries;

		@JsonProperty("keyword")
		public Builder keyword(String keyword) {
			this.keyword = keyword;
			return this;
		}

		@JsonProperty("page")
		public Builder page(int page) {
			this.page = page;
			return this;
		}

		@JsonProperty("pageSize")
		public Builder pageSize(int pageSize) {
			this.pageSize = pageSize;
			return this;
		}

		@JsonProperty("sortBy")
		public Builder sortby(String sortby) {
			this.sortby = sortby;
			return this;
		}

		@JsonProperty("order")
		public Builder order(String order) {
			this.order = order;
			return this;
		}

		@JsonProperty("radius")
		public Builder radius(int radius) {
			this.radius = radius;
			return this;
		}

		@JsonProperty("address")
		public Builder address(String address) {
			this.address = address;
			return this;
		}

		@JsonProperty("countries")
		public Builder countries(Set<String> countries) {
			this.countries = countries;
			return this;
		}

		@JsonProperty("industries")
		public Builder industries(Set<Long> industries) {
			this.industries = industries;
			return this;
		}

		public Builder fromPrototype(WorkersSearchRequest prototype) {
			keyword = prototype.keyword;
			page = prototype.page;
			pageSize = prototype.pageSize;
			sortby = prototype.sortby;
			order = prototype.order;
			radius = prototype.radius;
			address = prototype.address;
			countries = prototype.countries;
			industries = prototype.industries;
			return this;
		}

		public WorkersSearchRequest build() {
			return new WorkersSearchRequest(this);
		}
	}
}
