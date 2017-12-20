package com.workmarket.domains.work.service.dashboard;

import ch.lambdaj.function.convert.PropertyExtractor;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.solr.query.SolrMetricConstants;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.postalcode.PostalCodeUtilities;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.request.SearchSortDirection;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.request.work.WorkSearchSortType;
import com.workmarket.search.request.work.WorkSearchType;
import com.workmarket.search.response.work.DashboardAddressUtilities;
import com.workmarket.search.response.work.DashboardResponse;
import com.workmarket.search.response.work.DashboardResponseSidebar;
import com.workmarket.search.response.work.DashboardResult;
import com.workmarket.search.response.work.DashboardResultList;
import com.workmarket.search.response.work.DashboardStatus;
import com.workmarket.search.response.work.DashboardStatusFilter;
import com.workmarket.search.response.work.WorkMilestoneFilter;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CompanyIdentityDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.BuyerIdentityDTO;
import com.workmarket.service.business.feed.Feed;
import com.workmarket.service.business.feed.FeedItem;
import com.workmarket.service.business.feed.FeedService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.forms.feed.FeedRequestParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;

@Service
public class MobileDashboardServiceImpl
    implements MobileDashboardService {

    private static final Log logger = LogFactory.getLog(MobileDashboardServiceImpl.class);

    @Autowired WorkSearchService workSearchService;
    @Autowired WorkDashboardResultParser workDashboardResultParser;
    @Autowired ProfileService profileService;
    @Autowired UserService userService;
    @Autowired FeedService feedService;
    @Autowired InvariantDataService invariantDataService;
    @Autowired IndustryService industryService;
    @Autowired CompanyService companyService;
    @Autowired WorkService workService;

    @Override
    public DashboardResponseSidebar getMobileHomeCounts(final Long userId,
                                                        final String userNumber) {

        DashboardStatusFilter filter = new DashboardStatusFilter().setStatusCode(WorkStatusType.ALL);

        WorkSearchRequest request = new WorkSearchRequest()
            .setUserNumber(userNumber)
            .setStartRow(0)
            .setPageSize(0)
            .setStatusFilter(filter)
            .setIncludeLabelDrilldownFacet(false)
            .setMobile(true)
            .setWorkSearchType(WorkSearchType.HOMEPAGE_SYSTEM)
            .setWorkSearchRequestUserType(WorkSearchRequestUserType.RESOURCE);

        WorkSearchResponse response = executeSearch(userId,request);

        DashboardResponseSidebar sidebar = new DashboardResponseSidebar();

        if (response != null && response.getAggregates() != null) {

            for (Map.Entry<String, Integer> entry : response.getAggregates().getCounts().entrySet()) {

                DashboardStatus status = new DashboardStatus();

                status.setStatusId(entry.getKey());
                status.setStatusCount(entry.getValue());
                status.setStatusName(entry.getKey());

                sidebar.putToDashboardStatuses(status.getStatusId(), status);
            }
        }

        return sidebar;
    }

    @Override
    public DashboardResponse getAssignmentListByStatus(
        final ExtendedUserDetails user,
        final WorkStatusType statusType,
        final Integer page,
        final Integer pageSize,
        final String sort) {

        WorkSearchResponse response = executeSearch
            (
                user.getId(),
                buildSearchRequest
                    (
                        user.getUserNumber(),
                        statusType,
                        page,
                        pageSize,
                        sort
                    )
            );
        DashboardResultList results = createResultList(response);

        DashboardResponse dashboardResponse = new DashboardResponse();

        dashboardResponse.setDashboardResultList(results);

        return dashboardResponse;
    }

    @Override
    public Feed getWorkFeed(final Long userId,
                            final String latitude,
                            final String longitude,
                            final Integer page) {

        return getWorkFeed(userId, latitude, longitude, page, null);
    }

    @Override
    public Feed getWorkFeed(final Long userId,
                            /*final*/ String latitude,
                            /*final*/ String longitude,
                            final Integer page,
                            final Integer pageSize) {

        FeedRequestParams params = new FeedRequestParams();

        int limit = (pageSize == null) ? DEFAULT_ASSIGNMENT_LIST_PAGE_SIZE : pageSize;
        /*
          if (minResults != null) {
          if (minResults > limit) {
          limit = minResults; // set the higher limit if we want more results
          }
          params.setMinResults(minResults);
          }
        */
        int start = (page - 1) * limit;
        params.setStart(start);
        params.setLimit(limit);

        //params.setMinResults(limit);

        /*
          boolean userLoc = false;

          if (!StringUtilities.all(latitude, longitude)) {

          Coordinate coords = profileService.findLatLongForUser(userId);

          if (coords != null) {

          latitude = coords.getLatitude() != null ? coords.getLatitude().toString() : "";
          longitude = coords.getLongitude() != null ? coords.getLongitude().toString() : "";
          userLoc = true;
          }
          }
        */

        final User user = userService.getUserWithRoles(userId);
        if (userService.isEmployeeWorker(user)) {
            params.setExclusiveCompanyIds(ImmutableList.of(user.getCompany().getId()));
        }

        params.setLatitude(latitude);
        params.setLongitude(longitude);

        return getWorkFeed(userId,params);
    }

    @Override
    public Feed getWorkFeed(final Long userId,
                            final FeedRequestParams params) {

        ProfileDTO profile = profileService.findProfileDTO(userId);
        Assert.notNull(profile);

        if (params.getIndustryId() == null) {

            List<Long> industryIds = industryService.getIndustryIdsForProfile(profile.getProfileId());

            params.setIndustryId(StringUtils.join(industryIds, ","));
        }

        boolean userLocation = false;

        if (params.getVirtual() == false) {

            params.setPostalCode(profile.getPostalCode());

            if (!StringUtilities.all(params.getLatitude(),
                                     params.getLongitude())) {

                Coordinate coords = profileService.findLatLongForUser(userId);

                if (coords != null) {

                    params.setLatitude(coords.getLatitude() != null ? coords.getLatitude().toString() : "");
                    params.setLongitude(coords.getLongitude() != null ? coords.getLongitude().toString() : "");
                    userLocation = true;
                }
            }
        }

        // add some tracking
        SolrQuery query = new SolrQuery();
        query.add(SolrMetricConstants.SEARCH_TYPE, SearchType.WORK_FEED.name());
        query.add(SolrMetricConstants.PERSONA, SolrMetricConstants.WORKER_PERSONA);
        if (userId != null) {
            query.add(SolrMetricConstants.USER, userId.toString());
        }
        query.add(SolrMetricConstants.USER_INDUSTRY, Boolean.toString(true));
        query.add(SolrMetricConstants.USER_LOCATION, Boolean.toString(userLocation));
        query.add(SolrMetricConstants.REQUEST_SOURCE, SolrMetricConstants.MOBILE_REQUEST);

        try {

            return feedService.getFeed(params, query);
        }
        catch (SolrServerException e) {

            logger.error("Exception fetching mobile feed " + e);
            //return new Feed().setResults(new ArrayList<FeedItem>());
            throw new RuntimeException("xxx");
        }
    }

    @Override
    public WorkSearchResponse executeSearch(final Long userId,
                                            final WorkSearchRequest request) {

        Assert.notNull(userId);
        Assert.notNull(request);
        logger.info(request);

        return workSearchService.searchAllWorkByUserId(userId, request);
    }

    @Override
    public WorkSearchRequest buildSearchRequest(String userNumber, WorkStatusType workStatusType, Integer page, Integer pageSize) {
        return buildSearchRequest(userNumber, workStatusType, page, pageSize, "");
    }

    public WorkSearchRequest buildSearchRequest(
        final String userNumber,
        final WorkStatusType workStatusType,
        final Integer page,
        final Integer pageSize,
        final String sort) {

        int limit = (pageSize == null) ? DEFAULT_ASSIGNMENT_LIST_PAGE_SIZE : pageSize;
        int start = (page - 1) * limit;

        DashboardStatusFilter filter = new DashboardStatusFilter()
            .setStatusCode(workStatusType.getCode());
        WorkSearchRequest request;
        if (workStatusType.getCode().equals(WorkStatusType.AVAILABLE)) {
            request = buildAvailableSearchRequest(userNumber,workStatusType,page,pageSize);
        } else {
            request = new WorkSearchRequest()
                    .setUserNumber(userNumber)
                    .setStartRow(start)
                    .setPageSize(limit)
                    .setWorkSearchRequestUserType(WorkSearchRequestUserType.RESOURCE)
                    .setWorkSearchType(WorkSearchType.HOMEPAGE_SYSTEM)
                    .setStatusFilter(filter);
        }
        setSort(sort, request);

        return request;
    }

    private void setSort(final String sort, final WorkSearchRequest request) {
        if (StringUtils.isNotBlank(sort)) {
            final SearchSortDirection direction = sort.startsWith("-")
                ? SearchSortDirection.DESCENDING : SearchSortDirection.ASCENDING;
            final String trimedSort = sort.startsWith("-") ? sort.substring(1) : sort;
            try {
                final WorkSearchSortType type =
                    WorkSearchSortType.valueOf(
                        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, trimedSort).toUpperCase());
                request
                    .setSortBy(type)
                    .setSortDirection(direction);
                return;
            } catch (final IllegalArgumentException e) {
                logger.error("Invalid sort type " + sort, e);
            }
        }

        // default
        request
            .setSortBy(WorkSearchSortType.SCHEDULED_FROM)
            .setSortDirection(SearchSortDirection.DESCENDING);
    }

    private WorkSearchRequest buildAvailableSearchRequest(final String userNumber,
                                                          final WorkStatusType workStatusType,
                                                          final Integer page,
														  final Integer pageSize) {

        int limit = (pageSize == null) ? DEFAULT_ASSIGNMENT_LIST_PAGE_SIZE : pageSize;
        int start = (page - 1) * limit;

        DashboardStatusFilter filter = new DashboardStatusFilter()
            .setStatusCode(workStatusType.getCode());

        return new WorkSearchRequest()
            .setUserNumber(userNumber)
            .setStartRow(start)
            .setPageSize(limit)
            .setWorkMilestoneFilter(WorkMilestoneFilter.SCHEDULED_DATE)
            .setSortDirection(SearchSortDirection.ASCENDING)
            .setWorkSearchRequestUserType(WorkSearchRequestUserType.RESOURCE)
            .setWorkSearchType(WorkSearchType.HOMEPAGE_SYSTEM)
            .setStatusFilter(filter);
    }

    private DashboardResultList createResultList(final WorkSearchResponse workSearchResponse) {

        DashboardResultList resultList = new DashboardResultList();

        resultList.setLastUpdated(System.currentTimeMillis());
        resultList.setPageNumber(workSearchResponse.getCurrentPage());
        resultList.setTotalNumberOfPages(workSearchResponse.getNumberOfPages());
        workDashboardResultParser.parseResult(workSearchResponse.getResults(), resultList);
        resultList.setTotalResults(workSearchResponse.getTotalResultsCount());
        resultList.setResultIds(convert(workSearchResponse.getResults(), new PropertyExtractor("workId")));

        return resultList;
    }

    //////////////////

    @Override
    public List<Map<String, Object>> parseResults(final ExtendedUserDetails user,
                                                  final DashboardResultList dashboardResultList) {

        if (dashboardResultList == null || dashboardResultList.getResults() == null) {
            return new ArrayList<>();
        }

        final List<Map<String, Object>> rows = Lists.newArrayList();
        final List<Long> companyIds = Lists.newArrayList();
        for (DashboardResult dashboardResult : dashboardResultList.getResults()) {
            companyIds.add(dashboardResult.getOwnerCompanyId());
        }
        final List<CompanyIdentityDTO> companyIdentitiesByIds = companyService.findCompanyIdentitiesByIds(companyIds);
        final Map<Long, CompanyIdentityDTO> companyIdentities = Maps.newHashMap();
        for(final CompanyIdentityDTO companyIdentity : companyIdentitiesByIds) {
            companyIdentities.put(companyIdentity.getCompanyId(), companyIdentity);
        }
        for (DashboardResult dashboardResult : dashboardResultList.getResults()) {
            rows.add(parseWork(user, dashboardResult, companyIdentities));
        }

        return rows;
    }

    @Override
    public List<Map<String, Object>> parseResults(final ExtendedUserDetails user,
                                                  final Feed feed) {

        if (feed == null || feed.getResults() == null) {
            return new ArrayList<>();
        }

        final List<Map<String, Object>> rows = Lists.newArrayList();
        final List<Long> workIds = Lists.newArrayList();
        for (FeedItem feedItem : feed.getResults()) {
            workIds.add(feedItem.getId());
        }
        final List<BuyerIdentityDTO> buyerIdentitiesByWorkIds = workService.findBuyerIdentitiesByWorkIds(workIds);
        final Map<Long, BuyerIdentityDTO> buyerIdentities = Maps.newHashMap();
        for(final BuyerIdentityDTO buyerIdentity : buyerIdentitiesByWorkIds) {
            buyerIdentities.put(buyerIdentity.getWorkId(), buyerIdentity);
        }
        for (FeedItem feedItem : feed.getResults()) {
            rows.add(parseWork(user, feedItem, buyerIdentities));
        }

        return rows;
    }

    private Map<String, Object> parseWork(final ExtendedUserDetails user,
                                          final DashboardResult dashboardResult,
                                          final Map<Long, CompanyIdentityDTO> companyIdentities) {

        Map<String, Object> result = parseSchedule(user, dashboardResult, Maps.<String, Object>newHashMap());

        result = parsePricing(user, dashboardResult, result);

        result = parseCompany(dashboardResult, companyIdentities, result);

        result.putAll
            (
                CollectionUtilities.newObjectMap
                (
                    "id", dashboardResult.getWorkNumber(),
                    "work_id", dashboardResult.getId(),
                    "work_number", dashboardResult.getWorkNumber(),
                    "title", dashboardResult.getTitle(),
                    "title_short", StringUtilities.truncate(dashboardResult.getTitle(), 50),
                    "company", dashboardResult.getOwnerCompanyName(),
                    "company_id", String.valueOf(dashboardResult.getOwnerCompanyId()),
                    "city", dashboardResult.getAddress().getCity(),
                    "state", dashboardResult.getAddress().getState(),
                    "postal_code", dashboardResult.getAddress().getPostalCode(),
                    "address", DashboardAddressUtilities.formatAddressShort(dashboardResult.getAddress()),
                    "latitude", (dashboardResult.getAddress() != null ? dashboardResult.getAddress().getLatitude() : null),
                    "longitude", (dashboardResult.getAddress() != null ? dashboardResult.getAddress().getLongitude() : null),
                    "location_offsite", !dashboardResult.getResultFlags().isAddressOnsiteFlag(),
                    "work_amount", dashboardResult.getBuyerTotalCost(),
                    "amount_earned", dashboardResult.getAmountEarned(),
                    "paid_on", (dashboardResult.isSetPaidOn()) ? DateUtilities.formatMillis("M/dd", dashboardResult.getPaidOn(), user.getTimeZoneId()) : "",
                    "due_on", (dashboardResult.isSetDueDate()) ? DateUtilities.formatMillis("M/dd", dashboardResult.getDueDate(), user.getTimeZoneId()) : "",
                    "paid_on_millis", dashboardResult.isSetPaidOn() ? dashboardResult.getPaidOn() : 0L,
                    "due_on_millis", dashboardResult.isSetDueDate() ? dashboardResult.getDueDate() : 0L,
                    "payment_terms_enabled", dashboardResult.getResultFlags().isPaymentTermsEnabled(),
                    "payment_terms_days", dashboardResult.getPaymentTermsDays(),
                    "status", dashboardResult.getWorkStatusTypeCode(),
                    "status_description", dashboardResult.getWorkStatusTypeDescription(),
                    "sent_date", dashboardResult.getSentDate(),
                    "completed_date", dashboardResult.getCompletedDate()
                )
            );

        return result;
    }

    private Map<String, Object> parseWork(final ExtendedUserDetails user,
                                          final FeedItem feedItem,
                                          final Map<Long, BuyerIdentityDTO> buyerIdentities) {


        Map<String, Object> result = parseSchedule(user, feedItem, Maps.<String, Object>newHashMap());
        result = parsePricing(user, feedItem, result);
        result = parseCompany(feedItem, buyerIdentities, result);

        result.putAll
            (
                CollectionUtilities.newObjectMap
                (
                    "id", feedItem.getWorkNumber(),
                    "work_id", feedItem.getId(),
                    "work_number", feedItem.getWorkNumber(),
                    "title", feedItem.getPublicTitle(),
                    "title_short", StringUtilities.truncate(feedItem.getPublicTitle(), 50),
                    "company", feedItem.getCompanyName(),
                    "city", feedItem.getCity(),
                    "state", feedItem.getState(),
                    "postal_code", feedItem.getPostalCode(),
                    "address", PostalCodeUtilities.formatAddressShort(feedItem.getCity(), feedItem.getState(), feedItem.getPostalCode(), feedItem.getCountry()),
                    "latitude", feedItem.getLatitude(),
                    "longitude", feedItem.getLongitude(),
                    "location_offsite", feedItem.isOffSite(),
                    "work_amount", feedItem.getWorkPrice() // Is this right?
                )
            );

        return result;
    }

    private Map<String, Object> parseCompany(DashboardResult dashboardResult,
                                             final Map<Long, CompanyIdentityDTO> companyIdentities,
                                             Map<String, Object> result) {

        final CompanyIdentityDTO companyIdentity = companyIdentities.get(dashboardResult.getOwnerCompanyId());

        result.put("company_number", companyIdentity.getCompanyNumber());
        result.put("company_uuid", companyIdentity.getUuid());

        return result;

    }

    private Map<String, Object> parseCompany(FeedItem feedItem,
                                             final Map<Long, BuyerIdentityDTO> buyerIdentities,
                                             Map<String, Object> result) {

        final BuyerIdentityDTO buyerIdentity = buyerIdentities.get(feedItem.getId());


        result.put("company_number", buyerIdentity.getCompanyNumber());
        result.put("company_uuid", buyerIdentity.getCompanyUuid());

        return result;

    }

    private Map<String, Object> parseSchedule(ExtendedUserDetails user,
                                              DashboardResult dashboardResult,
                                              Map<String, Object> result) {

        String startDate = null;
        String startTime = null;
        String endDate = null;
        String endTime = null;

        if (dashboardResult.isSetScheduleFrom()) {

            String timeZone = (dashboardResult.getResultFlags().isAddressOnsiteFlag() ? dashboardResult.getTimeZoneId() : user.getTimeZoneId());

            startDate = DateUtilities.formatMillis("M/dd", dashboardResult.getScheduleFrom(), timeZone);
            if (dashboardResult.isSetScheduleThrough()) {
                /* Do it like this to avoid showing time zone twice if it's a range */
                startTime = DateUtilities.formatMillis("h:mma", dashboardResult.getScheduleFrom(), timeZone);
                endDate = DateUtilities.formatMillis("M/dd", dashboardResult.getScheduleThrough(), timeZone);
                endTime = DateUtilities.formatMillis("h:mma z", dashboardResult.getScheduleThrough(), timeZone);
            }
            else {
                startTime = DateUtilities.formatMillis("h:mma z", dashboardResult.getScheduleFrom(), timeZone);
            }
        }

        result.put("start_date", startDate);
        result.put("start_time", startTime);
        result.put("end_date", endDate);
        result.put("end_time", endTime);
        result.put("start_datetime_millis", dashboardResult.getScheduleFrom());

        if (dashboardResult.isSetScheduleThrough()) {
            result.put("end_datetime_millis", dashboardResult.getScheduleThrough());
        }

        return result;
    }

    private Map<String, Object> parseSchedule(final ExtendedUserDetails user,
                                              final FeedItem feedItem,
                                              final Map<String, Object> result) {

        String startDate = null;
        String startTime = null;

		String postalCode;

		if (StringUtils.isNotBlank(feedItem.getPostalCode())) {
			postalCode = feedItem.getPostalCode();
		}
		else if (StringUtils.isNotBlank(user.getPostalCode())) {
			postalCode = user.getPostalCode();
		}
		else {
			return result;
		}

        PostalCode pc = invariantDataService.getPostalCodeByCode(postalCode);
        String timeZone = (pc == null ? Constants.WM_POSTAL_CODE : pc.getTimeZoneName());

        startDate = DateUtilities.formatMillis("M/dd", feedItem.getScheduleFromDate().getTime(), timeZone);
        startTime = DateUtilities.formatMillis("h:mma z", feedItem.getScheduleFromDate().getTime(), timeZone);

        result.put("start_date", startDate);
        result.put("start_time", startTime);
        result.put("start_datetime_millis", feedItem.getScheduleFromDate().getTime());

        return result;
    }

    private Map<String, Object> parsePricing(final ExtendedUserDetails user, final DashboardResult dashboardResult,
                                             final Map<String, Object> result) {

        String price;
        if (dashboardResult.getResultFlags().isInternal() || user.isEmployeeWorker()) {
            price = "-";
            result.put("pricing_type", "Internal");
        }
        else {
            // if round $ number, don't show ".00", it wastes valuable mobile real estate
            price = org.apache.commons.lang.StringUtils.removeEnd(NumberUtilities.currency(dashboardResult.getAmountEarned() > 0 ? dashboardResult.getAmountEarned() : dashboardResult.getSpendLimit()), ".00");

            String spendLimit = org.apache.commons.lang.StringUtils.removeEnd(NumberUtilities.currency(dashboardResult.getSpendLimit()), ".00");

            result.put("spend_limit", spendLimit);
            result.put("pricing_type", getNicePricingType(dashboardResult.getPricingType()));
        }

        result.put("price", price);



        return result;
    }

    private Map<String, Object> parsePricing(
      final ExtendedUserDetails user,
      final FeedItem feedItem,
      final Map<String, Object> result
    ) {
        String price;

        if (PricingStrategyType.INTERNAL.toString().equals(feedItem.getPricingType()) || user.isEmployeeWorker()) {
            price = "-";
        } else {
            // if round $ number, don't show ".00", it wastes valuable mobile real estate
            price = org.apache.commons.lang.StringUtils.removeEnd(NumberUtilities.currency(feedItem.getSpendLimit()), ".00");
        }

        String pricingType = getNicePricingType(feedItem.getPricingType());

        result.put("price", price);
        result.put("pricing_type", pricingType);

        return result;
    }

    private String getNicePricingType(final String pricingTypeCode) {

        String nicePricingType = org.apache.commons.lang.StringUtils.EMPTY;

        if (org.apache.commons.lang.StringUtils.isNotBlank(pricingTypeCode)) {

            if (pricingTypeCode.equals("FLAT")) {
                nicePricingType = "Flat";
            }
            else if (pricingTypeCode.equals("PER_HOUR")) {
                nicePricingType = "Hourly";
            }
            else if (pricingTypeCode.equals("BLENDED_PER_HOUR")) {
                nicePricingType = "Blended";
            }
            else if (pricingTypeCode.equals("PER_UNIT")) {
                nicePricingType = "Unit";
            }  /* didn't say "Per Unit" here because it's misleading since we're showing max earnings, not per unit earnings */
            else if (pricingTypeCode.equals("INTERNAL")) {
                nicePricingType = "Internal";
            }
        }

        return nicePricingType;
    }
}
