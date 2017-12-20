package com.workmarket.api.helpers;

import com.google.common.collect.ImmutableSet;
import com.workmarket.api.v2.model.PaginationPage;
import com.workmarket.api.v2.common.util.GenericMapper;
import com.workmarket.api.v2.worker.model.WorkersSearchRequest;
import com.workmarket.api.v2.worker.model.WorkersSearchResult;
import com.workmarket.search.SortDirectionType;
import com.workmarket.search.request.LocationFilter;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.request.user.PeopleSearchSortByType;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.service.search.user.PeopleSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by ianha on 4/3/15.
 */
@Service
public class PeopleSearchServiceAdapterImpl implements PeopleSearchServiceAdapter {
    @Autowired PeopleSearchService peopleSearchService;
    @Autowired @Qualifier("workersSearchResultMapper") GenericMapper<PeopleSearchResult, WorkersSearchResult> mapper;

    @Override
    public PaginationPage searchPeople(WorkersSearchRequest request, Long userId, Long companyId) throws Exception {
        PeopleSearchResponse response = peopleSearchService.searchPeople(buildPeopleSearchRequest(request, userId, companyId));

        int totalResuls = response.getTotalResultsCount();
        int numberOfPages = totalResuls / request.getPageSize() + (totalResuls % request.getPageSize() > 0 ? 1 : 0);
        return new PaginationPage()
                .setResults(mapper.map(response.getResults()))
                .setPage(request.getPage())
                .setPageSize(request.getPageSize())
                .setTotalPageCount(numberOfPages)
                .setTotalRecordCount(totalResuls);
    }

    private PeopleSearchRequest buildPeopleSearchRequest(WorkersSearchRequest searchRequest, Long userId, Long companyId) {
        PeopleSearchRequest request = new PeopleSearchRequest();
        request.setUserId(userId);
        request.setPaginationRequest(buildPagination(searchRequest));
        request.setKeyword(searchRequest.getKeyword());

        ((PeopleSearchRequest)request.setLocationFilter(buildLocationFilter(searchRequest)))
                .setCountryFilter(searchRequest.getCountries())
                .setIndustryFilter(searchRequest.getIndustries())
                .setCompanyFilter(companyId != null ? ImmutableSet.of(companyId) : null)
                .setKeyword(searchRequest.getKeyword());

        return request;
    }

    private LocationFilter buildLocationFilter(WorkersSearchRequest searchRequest) {
        if (searchRequest.getAddress() == null) {
            return null;
        }

        LocationFilter filter = new LocationFilter();
        filter.setWillingToTravelTo(searchRequest.getAddress());
        filter.setMaxMileFromResourceToLocation(searchRequest.getRadius());

        return filter;
    }

    private Pagination buildPagination(WorkersSearchRequest searchRequest) {
        Pagination p = new Pagination();

        p.setPageSize(searchRequest.getPageSize());
        p.setPageNumber(searchRequest.getPage());
        p.setCursorPosition((searchRequest.getPage()-1)*searchRequest.getPageSize());
        if (searchRequest.getOrder() != null) {
            p.setSortDirection(SortDirectionType.findByName(searchRequest.getOrder()));
        }
        if (searchRequest.getSortby() != null) {
            p.setSortBy(PeopleSearchSortByType.findByName(searchRequest.getSortby()));
        }

        return p;
    }
}
