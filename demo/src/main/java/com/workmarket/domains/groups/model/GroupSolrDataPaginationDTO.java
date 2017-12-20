package com.workmarket.domains.groups.model;

import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;

import java.util.List;

public class GroupSolrDataPaginationDTO {

	private final Integer start;
	private final Integer limit;
	private final String sortBy;
	private final String keyword;
	private final String objectiveType;
	private final Integer[] industries;
	private final Long userId;
	private GroupSolrDataPagination.SEARCH_TYPE searchType;
	private List<Long> companyIdsUserIsExclusiveTo;

	public static class Builder {

		private Integer start;
		private Integer limit;
		private String sortBy;
		private String keyword;
		private String objectiveType;
		private Integer[] industries;
		private Long userId;
		private GroupSolrDataPagination.SEARCH_TYPE searchType;
		private List<Long> companyIdsUserIsExclusiveTo;

		public Builder setStart(final Integer start) {
			this.start = start;
			return this;
		}

		public Builder setLimit(final Integer limit) {
			this.limit = limit;
			return this;
		}

		public Builder setSortBy(final String sortBy) {
			this.sortBy = sortBy;
			return this;
		}

		public Builder setKeyword(final String keyword) {
			this.keyword = keyword;
			return this;
		}

		public Builder setObjectiveType(final String objectiveType) {
			this.objectiveType = objectiveType;
			return this;
		}

		public Builder setIndustries(final Integer[] industries) {
			this.industries = industries;
			return this;
		}

		public Builder setUserId(final Long userId) {
			this.userId = userId;
			return this;
		}

		public Builder setSearchType(final GroupSolrDataPagination.SEARCH_TYPE searchType) {
			this.searchType = searchType;
			return this;
		}

		public Builder setCompanyIdsUserIsExclusiveTo(final List<Long> companyIdsUserIsExclusiveTo) {
			this.companyIdsUserIsExclusiveTo = companyIdsUserIsExclusiveTo;
			return this;
		}

		public GroupSolrDataPaginationDTO build() {
			return new GroupSolrDataPaginationDTO(
					start,
					limit,
					sortBy,
					keyword,
					objectiveType,
					industries,
					userId,
					searchType,
					companyIdsUserIsExclusiveTo);
		}
	}

	public GroupSolrDataPaginationDTO(
			final Integer start,
			final Integer limit,
			final String sortBy,
			final String keyword,
			final String objectiveType,
			final Integer[] industries,
			final Long userId,
			final GroupSolrDataPagination.SEARCH_TYPE searchType,
			final List<Long> companyIdsUserIsExclusiveTo) {
		this.start = start;
		this.limit = limit;
		this.sortBy = sortBy;
		this.keyword = keyword;
		this.objectiveType = objectiveType;
		this.industries = industries;
		this.userId = userId;
		this.searchType = searchType;
		this.companyIdsUserIsExclusiveTo = companyIdsUserIsExclusiveTo;
	}

	public Integer getStart() {
		return start;
	}

	public Integer getLimit() {
		return limit;
	}

	public String getSortBy() {
		return sortBy;
	}

	public String getKeyword() {
		return keyword;
	}

	public String getObjectiveType() {
		return objectiveType;
	}

	public Integer[] getIndustries() {
		return industries;
	}

	public Long getUserId() {
		return userId;
	}

	public GroupSolrDataPagination.SEARCH_TYPE getSearchType() {
		return searchType;
	}

	public List<Long> getCompanyIdsUserIsExclusiveTo() {
		return companyIdsUserIsExclusiveTo;
	}
}
