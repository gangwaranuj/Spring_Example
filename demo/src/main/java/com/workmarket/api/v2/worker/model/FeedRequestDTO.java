package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.domains.work.service.dashboard.MobileDashboardService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("FeedRequest")
@JsonDeserialize(builder = FeedRequestDTO.Builder.class)
public class FeedRequestDTO {

	private final String keyword;
	private final Integer industryId;
	private final Double latitude;
	private final Double longitude;
	private final Double radius;
	private final Boolean virtual;
	private final String fields;
	private final String when;
	private final Integer page;
	private final Integer pageSize;
	private final boolean sortByDistance;
	private final boolean filterOutApplied;
	private final List<String> sort; // sort fields prefixed with optional "-" for desc sort direction; asc by default
	private final Long startDate; // UTC millis from epoch
	private final Long endDate; // UTC millis from epoch
	private final List<String> filter; // filter-key value pairs "key=value"

	private FeedRequestDTO(Builder builder) {
		keyword = builder.keyword;
		industryId = builder.industryId;
		latitude = builder.latitude;
		longitude = builder.longitude;
		radius = builder.radius;
		virtual = builder.virtual;
		fields = builder.fields;
		when = builder.when;
		page = builder.page;
		pageSize = builder.pageSize == null ? MobileDashboardService.DEFAULT_ASSIGNMENT_FEED_PAGE_SIZE : builder.pageSize;
		sortByDistance = builder.sortByDistance;
		filterOutApplied = builder.filterOutApplied;
		sort = builder.sort;
		startDate = builder.startDate;
		endDate = builder.endDate;
		filter = builder.filter;
	}

	@ApiModelProperty(name = "keyword")
	@JsonProperty("keyword")
	public String getKeyword() {
		return keyword;
	}

	@ApiModelProperty(name = "industryId")
	@JsonProperty("industryId")
	public Integer getIndustryId() {
		return industryId;
	}

	@ApiModelProperty(name = "latitude")
	@JsonProperty("latitude")
	public Double getLatitude() {
		return latitude;
	}

	@ApiModelProperty(name = "longitude")
	@JsonProperty("longitude")
	public Double getLongitude() {
		return longitude;
	}

	@ApiModelProperty(name = "radius")
	@JsonProperty("radius")
	public Double getRadius() {
		return radius;
	}

	@ApiModelProperty(name = "virtual")
	@JsonProperty("virtual")
	public Boolean getVirtual() {
		return virtual;
	}

	@ApiModelProperty(name = "fields")
	@JsonProperty("fields")
	public String getFields() {
		return fields;
	}

	@ApiModelProperty(name = "when")
	@JsonProperty("when")
	public String getWhen() {
		return when;
	}

	@ApiModelProperty(name = "page")
	@JsonProperty("page")
	public Integer getPage() {
		return page;
	}

	@ApiModelProperty(name = "pageSize")
	@JsonProperty("pageSize")
	public Integer getPageSize() {
		return pageSize;
	}

	@ApiModelProperty(name = "sortByDistance")
	@JsonProperty("sortByDistance")
	public boolean isSortByDistance() {
		return sortByDistance;
	}

	@ApiModelProperty(name = "filterOutApplied")
	@JsonProperty("filterOutApplied")
	public boolean isFilterOutApplied() {
		return filterOutApplied;
	}

	@ApiModelProperty(name = "sort")
	@JsonProperty("sort")
	public List<String> getSort() {
		return sort;
	}

	@ApiModelProperty(name = "startDate")
	@JsonProperty("startDate")
	public Long getStartDate() {
		return startDate;
	}

	@ApiModelProperty(name = "endDate")
	@JsonProperty("endDate")
	public Long getEndDate() {
		return endDate;
	}

	@ApiModelProperty(name = "filter")
	@JsonProperty("filter")
	public List<String> getFilter() {
		return filter;
	}

	public static final class Builder {
		private String keyword;
		private Integer industryId;
		private Double latitude;
		private Double longitude;
		private Double radius;
		private Boolean virtual = false;
		private String fields;
		private String when = "all";
		private Integer page = 1;
		private Integer pageSize = MobileDashboardService.DEFAULT_ASSIGNMENT_FEED_PAGE_SIZE;
		private boolean sortByDistance;
		private boolean filterOutApplied = false;
		private List<String> sort;
		private Long startDate;
		private Long endDate;
		private List<String> filter;

		public Builder() {
		}

		public Builder(FeedRequestDTO copy) {
			this.keyword = copy.keyword;
			this.industryId = copy.industryId;
			this.latitude = copy.latitude;
			this.longitude = copy.longitude;
			this.radius = copy.radius;
			this.virtual = copy.virtual;
			this.fields = copy.fields;
			this.when = copy.when;
			this.page = copy.page;
			this.pageSize = copy.pageSize;
			this.sortByDistance = copy.sortByDistance;
			this.filterOutApplied = copy.filterOutApplied;
			this.sort = copy.sort;
			this.startDate = copy.startDate;
			this.endDate = copy.endDate;
			this.filter = copy.filter;
		}

		@JsonProperty("keyword")
		public Builder withKeyword(String keyword) {
			this.keyword = keyword;
			return this;
		}

		@JsonProperty("industryId")
		public Builder withIndustryId(Integer industryId) {
			this.industryId = industryId;
			return this;
		}

		@JsonProperty("latitude")
		public Builder withLatitude(Double latitude) {
			this.latitude = latitude;
			return this;
		}

		@JsonProperty("longitude")
		public Builder withLongitude(Double longitude) {
			this.longitude = longitude;
			return this;
		}

		@JsonProperty("radius")
		public Builder withRadius(Double radius) {
			this.radius = radius;
			return this;
		}

		@JsonProperty("virtual")
		public Builder withVirtual(Boolean virtual) {
			this.virtual = virtual;
			return this;
		}

		@JsonProperty("fields")
		public Builder withFields(String fields) {
			this.fields = fields;
			return this;
		}

		@JsonProperty("when")
		public Builder withWhen(String when) {
			this.when = when;
			return this;
		}

		@JsonProperty("page")
		public Builder withPage(Integer page) {
			this.page = page;
			return this;
		}

		@JsonProperty("pageSize")
		public Builder withPageSize(Integer pageSize) {
			this.pageSize = pageSize;
			return this;
		}

		@JsonProperty("sortByDistance")
		public Builder withSortByDistance(boolean sortByDistance) {
			this.sortByDistance = sortByDistance;
			return this;
		}

		@JsonProperty("filterOutApplied")
		public Builder withFilterOutApplied(boolean filterOutApplied) {
			this.filterOutApplied = filterOutApplied;
			return this;
		}

		@JsonProperty("sort")
		public Builder withSort(List<String> sort) {
			this.sort = sort;
			return this;
		}

		@JsonProperty("startDate")
		public Builder withStartDate(Long startDate) {
			this.startDate = startDate;
			return this;
		}

		@JsonProperty("endDate")
		public Builder withEndDate(Long endDate) {
			this.endDate = endDate;
			return this;
		}

		@JsonProperty("filter")
		public Builder withFilter(List<String> filter) {
			this.filter = filter;
			return this;
		}

		public FeedRequestDTO build() {
			return new FeedRequestDTO(this);
		}
	}
}
