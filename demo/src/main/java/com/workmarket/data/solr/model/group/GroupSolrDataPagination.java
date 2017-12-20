package com.workmarket.data.solr.model.group;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.search.group.indexer.model.GroupSolrData;
import com.workmarket.service.business.dto.GroupSearchFilterDTO;
import org.springframework.util.Assert;

public class GroupSolrDataPagination extends AbstractPagination<GroupSolrData> implements Pagination<GroupSolrData> {

	public enum SEARCH_TYPE {
		SEARCH_COMPANY_GROUPS,
		SEARCH_ALL_OPEN_GROUPS,
		SEARCH_ALL_OPEN_COMPANY_GROUPS,
		SEARCH_ALL_OPEN_ACTIVE_COMPANY_GROUPS,
		SEARCH_COMPANY_GROUPS_LOCATION_MANAGER,
		SEARCH_COMPANY_ROUTABLE_GROUPS
	}

	public enum FILTER_KEYS {
	}

	public enum SORTS {
		CREATED_ON,
		NAME,
		OPEN_MEMBERSHIP,
		MEMBER_COUNT,
		ACTIVE,
		COMPANY_NAME,
		CREATOR_FULL_NAME,
		RELEVANCY
	}

	GroupSearchFilterDTO searchFilter = new GroupSearchFilterDTO();

	public GroupSearchFilterDTO getSearchFilter() {
		return searchFilter;
	}

	public String getSortColumn(SORTS sort) {
		Assert.notNull(sort);
		switch (sort) {
		case CREATED_ON:
			return "createdOn";
		case NAME:
			return "nameSort";
		case OPEN_MEMBERSHIP:
			return "openMembership";
		case MEMBER_COUNT:
			return "memberCount";
		case ACTIVE:
			return "activeFlag";
		case COMPANY_NAME:
			return "companyName";
		case CREATOR_FULL_NAME:
			return "creatorFullName";
		case RELEVANCY:
			return "score";

		default:
			Assert.isTrue(false, "Invalid sort");
		}
		return null;
	}

	public String getSortOrder(SORTS sort) {
		Assert.notNull(sort);
		switch (sort) {
		case CREATED_ON:
			return "ASC";
		case NAME:
			return "ASC";
		case OPEN_MEMBERSHIP:
			return "DESC";
		case MEMBER_COUNT:
			return "DESC";
		case ACTIVE:
			return "ASC";
		case COMPANY_NAME:
			return "DESC";
		case RELEVANCY:
			return "DESC";

		default:
			Assert.isTrue(false, "Invalid sort");
		}
		return null;
	}

	private SEARCH_TYPE searchType = SEARCH_TYPE.SEARCH_ALL_OPEN_COMPANY_GROUPS;

	public SEARCH_TYPE getSearchType() {
		return searchType;
	}

	public void setSearchType(SEARCH_TYPE searchType) {
		this.searchType = searchType;
	}

	@Override
	public String toString() {
		return "GroupSTOPagination{" +
				"searchFilter=" + searchFilter +
				", searchType=" + searchType +
				'}';
	}
}
