package com.workmarket.service.business.feed;

import com.google.common.annotations.VisibleForTesting;
import com.workmarket.data.solr.query.SolrMetricConstants;
import com.workmarket.search.gen.Common.PostalCode;
import com.workmarket.search.gen.Common.RequestMonitor;
import com.workmarket.search.gen.FeedMessages;
import com.workmarket.search.gen.FeedMessages.FindWorkFeedRequest;
import com.workmarket.search.gen.FeedMessages.FindWorkFeedResponse;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.forms.feed.FeedRequestParams;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindWorkFeedAdapter {

	public static final int FIRST_ROW = 0;
	public static final int DEFAULT_LIMIT = 10;
	public static final int FEED_RETRY_LIMIT = 5;
	public static final int FEED_RETRY_INCREMENT = 200; // miles

	@Autowired private InvariantDataService invariantDataService;


	public FindWorkFeedRequest buildFindWorkFeedRequest(final FeedRequestParams params, final SolrQuery query) {
		final FindWorkFeedRequest.Builder request = FindWorkFeedRequest.newBuilder();

		request.setStart(params.getStart() != null ? params.getStart().longValue() : FIRST_ROW)
			.setLimit(params.getLimit() != null ? params.getLimit().longValue() : DEFAULT_LIMIT)
			.setRetryLimit(params.getRetryLimit() != null ? params.getRetryLimit().longValue() : FEED_RETRY_LIMIT)
			.setRetryDistanceIncrement(params.getRetryDistanceIncrement() != null ? params.getRetryDistanceIncrement().longValue() : FEED_RETRY_INCREMENT)
			.setIncludeResults(params.getIncludeResults())
			.setVirtual(params.getVirtual())
			.setFilterOutApplied(params.getFilterOutApplied());

		request.setPostalCode(buildPostalCode(params));

		if (params.getMinResults() != null) {
			request.setMinResults(params.getMinResults().longValue());
		}
		if (StringUtils.isNotBlank(params.getCompanyId())) {
			request.setCompanyId(params.getCompanyId());
		}
		if (StringUtils.isNotBlank(params.getState())) {
			request.setState(params.getState());
		}
		if (StringUtils.isNotBlank(params.getDistanceInMiles())) {
			request.setDistanceInMiles(StringUtilities.stripXSSAndEscapeHtml(params.getDistanceInMiles()));
		}
		if (StringUtils.isNotBlank(params.getWhen())) {
			request.setWhen(params.getWhen());
		}
		if (params.getStartDate() != null) {
			request.setStartDate(params.getStartDate());
		}
		if (params.getEndDate() != null) {
			request.setEndDate(params.getEndDate());
		}
		if (StringUtils.isNotBlank(params.getKeyword())) {
			request.setKeyword(StringUtilities.stripXSSAndEscapeHtml(params.getKeyword()));
		}
		if (StringUtils.isNotBlank(params.getIndustryId())) {
			for(String industryId : StringUtils.split(params.getIndustryId(), ",")) {
				request.addIndustryIds(Long.parseLong(StringUtils.trim(industryId)));
			}
		}
		if (CollectionUtils.isNotEmpty(params.getExcludeCompanyIds())) {
			request.addAllExcludeCompanyIds(params.getExcludeCompanyIds());
		}
		if (CollectionUtils.isNotEmpty(params.getExclusiveCompanyIds())) {
			request.addAllExclusiveCompanyIds(params.getExclusiveCompanyIds());
		}
		if (CollectionUtils.isNotEmpty(params.getFilter())) {
			request.addAllFilter(params.getFilter());

		}
		if (CollectionUtils.isNotEmpty(params.getSort())) {
			request.addAllSort(params.getSort());
		}
		final RequestMonitor requestMonitor = buildRequestMonitor(query);
		request.setRequestMonitor(requestMonitor);

		return request.build();
	}

	public Feed extractFeed(final FindWorkFeedRequest request, final FindWorkFeedResponse response) {
		Feed feed = new Feed();
		if (!response.getStatus().getSuccess()) {
			feed.getErrorMessages().addAll(response.getStatus().getMessagesList());
			return feed;
		}

		feed.setTotalCount(response.getNumFound());
		feed.setPage(request.getStart());
		feed.setPageSize((int) request.getLimit());
		if (StringUtils.isNotBlank(request.getDistanceInMiles())) {
			feed.setDistanceRadiusInMiles(Double.parseDouble(request.getDistanceInMiles()));
		}

		for (final FeedMessages.Feed fItem : response.getFeedsList()) {
			FeedItem feedItem = new FeedItem();
			feedItem.setId(fItem.getId());
			feedItem.setWorkNumber(fItem.getWorkNumber());
			feedItem.setPublicTitle(fItem.getPublicTitle());
			feedItem.setDescription(fItem.getDescription());
			feedItem.setState(fItem.getState());
			feedItem.setCity(fItem.getCity());
			feedItem.setCountry(fItem.getCountry());
			feedItem.setPostalCode(fItem.getPostalCode());
			feedItem.setOffSite(fItem.getOffSite());
			feedItem.setScheduleFromDate(DateUtilities.getDateFromISO8601(fItem.getScheduleFromDate()));
			feedItem.setCreatedDate(DateUtilities.getDateFromISO8601(fItem.getCreatedDate()));
			feedItem.setSpendLimit(fItem.getSpendLimit());
			feedItem.setCompanyName(fItem.getCompanyName());
			feedItem.setWorkStatusTypeCode(fItem.getWorkStatusTypeCode());
			feedItem.setLatitude(fItem.getLatitude());
			feedItem.setLongitude(fItem.getLongitude());
			feedItem.setAssignToFirstResource(fItem.getAssignToFirstResource());
			feedItem.setShowInFeed(fItem.getShowInFeed());
			feedItem.setPricingType(fItem.getPricingType());
			feedItem.setWorkPrice(fItem.getWorkPrice());
			feed.getResults().add(feedItem);
		}

		return feed;
	}

	@VisibleForTesting
	PostalCode buildPostalCode(final FeedRequestParams params) {
		final PostalCode.Builder postalCode = PostalCode.newBuilder();
		if (params.getVirtual()) {
			return postalCode.build();
		}

		if (StringUtils.isNotBlank(params.getPostalCode())) {
			postalCode.setCode(StringUtilities.stripXSSAndEscapeHtml(params.getPostalCode()));
		}

		if (hasValidLatLon(params)) {
			postalCode
				.setLatitude(params.getLatitude())
				.setLongitude(params.getLongitude());
		} else if (StringUtils.isNotBlank(params.getPostalCode())) {
			com.workmarket.domains.model.postalcode.PostalCode pc =
				invariantDataService.getPostalCodeByCode(params.getPostalCode());
			if (pc != null) {
				postalCode
					.setLatitude(pc.getLatitude().toString())
					.setLongitude(pc.getLongitude().toString());
			}
		}
		return postalCode.build();
	}

	@VisibleForTesting
	boolean hasValidLatLon(final FeedRequestParams params) {
		final String lat = params.getLatitude();
		final String lon = params.getLongitude();
		if (NumberUtils.isNumber(lat) && NumberUtils.isNumber(lon)) {
			double latDouble = NumberUtils.toDouble(lat);
			double lonDouble = NumberUtils.toDouble(lon);
			if (latDouble >= -90 && latDouble <= 90 && lonDouble >= -180 && lonDouble <= 180) {
				return true;
			}
		}
		return false;
	}

	private RequestMonitor buildRequestMonitor(final SolrQuery query) {
		final RequestMonitor.Builder requestMonitor = RequestMonitor.newBuilder();
		if (StringUtils.isNotBlank(query.get(SolrMetricConstants.USER))) {
			requestMonitor.setMUser(query.get(SolrMetricConstants.USER));
		}
		if (StringUtils.isNotBlank(query.get(SolrMetricConstants.COMPANY))) {
			requestMonitor.setMCompany(query.get(SolrMetricConstants.COMPANY));
		}
		if (StringUtils.isNotBlank(query.get(SolrMetricConstants.SEARCH_TYPE))) {
			requestMonitor.setMSearchType(query.get(SolrMetricConstants.SEARCH_TYPE));
		}
		if (StringUtils.isNotBlank(query.get(SolrMetricConstants.REQUEST_SOURCE))) {
			requestMonitor.setMRequestSource(query.get(SolrMetricConstants.REQUEST_SOURCE));
		}
		if (StringUtils.isNotBlank(query.get(SolrMetricConstants.PERSONA))) {
			requestMonitor.setMPersona(query.get(SolrMetricConstants.PERSONA));
		}
		return requestMonitor.build();
	}
}
