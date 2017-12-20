package com.workmarket.service.search.user;

import com.workmarket.data.solr.repository.UserBoostFields;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.query.SearchQuery;
import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.SearchError;
import com.workmarket.search.SearchErrorType;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.search.SortDirectionType;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PeopleSearchSortImpl implements PeopleSearchSort {

	public PeopleSearchSortImpl() {
		super();
	}

	@Override
	public void addSortField(SearchQuery query, PeopleSearchTransientData data) throws SearchException {
		Pagination pagination = ((PeopleSearchRequest)data.getOriginalRequest()).getPaginationRequest();
		if (pagination == null) {
			addRelevancyQuery(query, SortDirectionType.DESC, data);
			return;
		}
		// default sort direction is descending
		if (!pagination.isSetSortDirection()) {
			pagination.setSortDirection(SortDirectionType.DESC);
		}
		if (!pagination.isSetSortBy()) {
			// if no sort is specified, we assume relevancy (score)
			addRelevancyQuery(query, pagination.getSortDirection(), data);
			return;
		}
		// most sorts are done by a column that's already in the index.
		// this is why we have findSortColumn(). However, name sort and
		// location sort require additional logic.
		switch (pagination.getSortBy()) {
			case NAME:
				query.addSort(new SolrQuery.SortClause(UserSearchableFields.LAST_NAME_SORT.getName(), getSolrQueryOrder(pagination.getSortDirection())));
				query.addSort(new SolrQuery.SortClause(UserSearchableFields.FIRST_NAME_SORT.getName(), getSolrQueryOrder(pagination.getSortDirection())));
				break;
			case DISTANCE:
				query.addSort(new SolrQuery.SortClause("geodist()", getSolrQueryOrder(pagination.getSortDirection())));
				addRelevancyQuery(query, pagination.getSortDirection(), data);
				// note: the required parameters to sort by geodist is 1)
				// sfield=location and 2) the lat/lon request string
				// both of these parameters will be included in every query, but the
				// third parameter "d", which measures the
				// radius to add to the filter, will only get added when there's an
				// actual radius
				break;
			case RATING:
				query.addSort(new SolrQuery.SortClause(UserSearchableFields.RATING.getName(), getSolrQueryOrder(pagination.getSortDirection())));
				query.addSort(new SolrQuery.SortClause(UserSearchableFields.RATING_COUNT.getName(), getSolrQueryOrder(pagination.getSortDirection())));
				break;
			case RELEVANCY:
				addRelevancyQuery(query, pagination.getSortDirection(), data);
				break;
			default: {
				Long companyId = data.getCurrentUser().getCompanyId();
				query.addSort(new SolrQuery.SortClause(findSortColumn(pagination, companyId), getSolrQueryOrder(pagination.getSortDirection())));
			}
		}
	}

	private String findSortColumn(Pagination pagination, Long companyId) throws SearchException {
		switch (pagination.getSortBy()) {
			case RATING:
				return UserSearchableFields.RATING.getName();
			case HOURLY_RATE:
				return UserSearchableFields.HOURLY_RATE.getName();
			case LANE:
				return ("C" + companyId + "_i");
			case RELEVANCY:
				return "score";
			case WORK_CANCELLED:
				return UserBoostFields.CANCELLED_LABEL_COUNT.getName();
			case WORK_COMPLETED:
				return UserBoostFields.WORK_COMPLETED_COUNT.getName();
			case CREATED_ON:
				return UserSearchableFields.CREATED_ON.getName();
			default: {
				SearchError error = new SearchError();
				error.setError(SearchErrorType.INVALID_REQUEST);
				error.setWhy("Unsupported search type " + pagination.getSortBy());
				throw new SearchException("Search type in pagination was unsupported: " + pagination, Arrays.asList(error));
			}
		}
	}

	private ORDER getSolrQueryOrder(SortDirectionType sortDirection) {
		switch (sortDirection) {
			case ASC:
				return ORDER.asc;
			case DESC:
				return ORDER.desc;
			default:
				return ORDER.asc;
		}
	}

	private void addRelevancyQuery(SolrQuery query, SortDirectionType sortDirection, PeopleSearchTransientData data) {
		query.addSort(new SolrQuery.SortClause("score", getSolrQueryOrder(sortDirection)));
		if (!data.isEnhancedRelevancy()) {
			query.addSort(new SolrQuery.SortClause(UserBoostFields.WORK_COMPLETED_COUNT.getName(), getSolrQueryOrder(sortDirection)));
			query.addSort(new SolrQuery.SortClause(UserBoostFields.ON_TIME_PERCENTAGE.getName(), getSolrQueryOrder(sortDirection)));
		}
	}
}
