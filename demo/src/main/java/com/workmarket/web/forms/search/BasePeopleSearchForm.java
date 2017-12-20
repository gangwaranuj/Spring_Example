package com.workmarket.web.forms.search;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.search.SortDirectionType;
import com.workmarket.search.request.user.Constants;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.request.user.PeopleSearchSortByType;

import java.util.Set;

// TODO: Remove UserSearchForm and use this class to refactor into separate forms for the different people search types
public class BasePeopleSearchForm extends BaseWorkerSearchForm {

	private Integer default_lane;
	private String resource_mode;
	private Set<Integer> lanes;

	private static final long serialVersionUID = -8149706019431939261L;

	public Pagination getPagination() {
		Pagination p = new Pagination();
		p.setCursorPosition(getStart());
		p.setPageSize(getLimit());

		switch (getSortby()) {
			case "distance_asc":
				p.setSortBy(PeopleSearchSortByType.DISTANCE);
				p.setSortDirection(SortDirectionType.ASC);
				break;
			case "name_asc":
				p.setSortBy(PeopleSearchSortByType.NAME);
				p.setSortDirection(SortDirectionType.ASC);
				break;
			case "name_desc":
				p.setSortBy(PeopleSearchSortByType.NAME);
				p.setSortDirection(SortDirectionType.DESC);
				break;
			case "rating_desc":
				p.setSortBy(PeopleSearchSortByType.RATING);
				p.setSortDirection(SortDirectionType.DESC);
				break;
			case "hourly_rate_asc":
				p.setSortBy(PeopleSearchSortByType.HOURLY_RATE);
				p.setSortDirection(SortDirectionType.ASC);
				break;
			case "hourly_rate_desc":
				p.setSortBy(PeopleSearchSortByType.HOURLY_RATE);
				p.setSortDirection(SortDirectionType.DESC);
				break;
			case "work_completed_desc":
				p.setSortBy(PeopleSearchSortByType.WORK_COMPLETED);
				p.setSortDirection(SortDirectionType.DESC);
				break;
			case "work_completed_asc":
				p.setSortBy(PeopleSearchSortByType.WORK_COMPLETED);
				p.setSortDirection(SortDirectionType.ASC);
				break;
			case "work_cancelled_desc":
				p.setSortBy(PeopleSearchSortByType.WORK_CANCELLED);
				p.setSortDirection(SortDirectionType.DESC);
				break;
			case "work_cancelled_asc":
				p.setSortBy(PeopleSearchSortByType.WORK_CANCELLED);
				p.setSortDirection(SortDirectionType.ASC);
				break;
			case "lane":
				p.setSortBy(PeopleSearchSortByType.LANE);
				p.setSortDirection(SortDirectionType.ASC);
				break;
			case "created_on_desc":
				p.setSortBy(PeopleSearchSortByType.CREATED_ON);
				p.setSortDirection(SortDirectionType.DESC);
				break;
			case "created_on_asc":
				p.setSortBy(PeopleSearchSortByType.CREATED_ON);
				p.setSortDirection(SortDirectionType.ASC);
				break;
			case "relevance":
			default:
				p.setSortBy(Constants.DEFAULT_SORT);
				p.setSortDirection(SortDirectionType.DESC);
				break;
		}

		return p;
	}

	public Set<LaneType> getLaneFilter() {
		if (default_lane != null) {
			lanes.add(default_lane);
		}

		if (resource_mode != null) {
			switch (resource_mode) {
				case "resources": lanes = Sets.newHashSet(1, 2, 3); break;
				case "employees": lanes = Sets.newHashSet(0, 1); break;
				case "all": default: lanes = Sets.newHashSet(); break;
			}
		}

		if (lanes == null || lanes.isEmpty()) { return null; }

		Set<LaneType> laneTypes = Sets.newHashSet();
		for (Integer lane : lanes) {
			laneTypes.add(LaneType.findByValue(lane));
		}
		return laneTypes;
	}
}
