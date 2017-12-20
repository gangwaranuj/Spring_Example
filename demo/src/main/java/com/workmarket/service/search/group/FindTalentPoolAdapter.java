package com.workmarket.service.search.group;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.search.gen.GroupMessages;
import com.workmarket.search.gen.Common.FacetCount;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolRequest;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolResponse;
import com.workmarket.search.gen.GroupMessages.Industry;
import com.workmarket.search.gen.GroupMessages.SearchType;
import com.workmarket.search.gen.GroupMessages.SortField;
import com.workmarket.search.gen.GroupMessages.TalentPool;
import com.workmarket.search.gen.Common.SortDirectionType;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.security.LaneContext;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class FindTalentPoolAdapter {

	private static Map<String, Object> EMPTY_LABEL = Collections.emptyMap();

	@Autowired private LaneService laneService;
	@Autowired private IndustryService industryService;
	@Autowired private UserService userService;

	public FindTalentPoolRequest buildFindTalentPoolRequest(
		final Integer start, final Integer limit,
		final String keyword, final String objectiveType,
		final Integer[] industryIds, final String sortby,
		final User searcher, final boolean isSearcherPrivateEmployee, final boolean isSearcherEmployeeWorker
	) {
		final FindTalentPoolRequest.Builder request = FindTalentPoolRequest.newBuilder();
		request.setUserId(searcher.getId());
		request.setCompanyId(searcher.getCompany().getId());
		if (start != null) {
			request.setStart(start.longValue());
		}
		if (limit != null) {
			request.setRows(limit.longValue());
		}
		if (industryIds != null && industryIds.length > 0) {
			List<Long> industries = Lists.newArrayList();
			for (Integer industryId : industryIds) {
				industries.add(industryId.longValue());
			}
			request.setIndustryFilter(
				GroupMessages.IndustryFilter.newBuilder()
					.setShowAll(false)
					.addAllIndustryIds(industries)
					.build());
		}
		if (StringUtils.isNotBlank(keyword)) {
			request.setKeyword(keyword);
		}
		if (StringUtils.isNotBlank(objectiveType)) {
			request.setObjectiveType(objectiveType);
		}
		if (searcher.isUserExclusive()) {
			request.addAllCompanyIdsExclusiveToUser(
				laneService.findAllCompaniesWhereUserIsResource(searcher.getId(), LaneType.LANE_2));
		} else if (isSearcherPrivateEmployee) {
			request.addAllCompanyIdsExclusiveToUser(Lists.newArrayList(searcher.getCompany().getId()));
		}
		final List<Long> blockedByCompanyIds = userService.findBlockedOrBlockedByCompanyIdsByUserId(searcher.getId());
		if (CollectionUtils.isNotEmpty(blockedByCompanyIds)) {
			request.addAllBlockedByCompanyIds(blockedByCompanyIds);
		}
		if (isSearcherEmployeeWorker) {
			request.setSearchType(SearchType.SEARCH_ALL_OPEN_COMPANY_GROUPS);
		} else {
			request.setSearchType(SearchType.SEARCH_ALL_OPEN_GROUPS);
		}
		if (sortby != null) {
			try {
				final SortField sortField = SortField.valueOf(sortby.toUpperCase());
				request.setSortField(sortField);
				//TODO: we should have additional parameter to indicate sort direction from UI
				if (SortField.NAME.equals(sortField)) {
					request.setSortDirection(SortDirectionType.asc);
				} else {
					request.setSortDirection(SortDirectionType.desc);
				}
			} catch (IllegalArgumentException e) {
				request.setSortField(SortField.SCORE);
				request.setSortDirection(SortDirectionType.desc);
			}
		}
		return request.build();
	}

	public Map<String, Object> convertTalentPoolToMap(
		final TalentPool talentPool,
		final SearchType searchType,
		final Long searcherId,
		final boolean isSearcherLane3Active,
		final boolean isSearcherAdmin,
		final boolean hasRequirements
	) {
		final ImmutableMap.Builder<String, Object> respMap = ImmutableMap.builder();

		return respMap
			.put("id", talentPool.getId())
			.put("name_short", StringUtilities.truncate(talentPool.getName(), 40))
			.put("name", talentPool.getName())
			.put("company_name", talentPool.getCompanyName())
			.put("avatar_uri", talentPool.getAvatarAssetLargeUri())
			.put("description_no_html", talentPool.getDescription())
			.put("industries", extractIndustries(talentPool))
			.put("member_count", talentPool.getMemberCount())
			.put("created_on", convertISODateString(talentPool.getCreatedOn()))
			.put("requires_approval", talentPool.getRequiresApproval())
			.put("has_requirements", hasRequirements)
			.put("active_flag", talentPool.getActiveFlag())
			.put("authorized_to_join", isAuthorizedToJoin(talentPool, searchType, searcherId, isSearcherLane3Active))
			.put("is_admin", isSearcherAdmin)
			.build();
	}

	public Map<String, Object> populateFilters(final FindTalentPoolRequest req, final FindTalentPoolResponse resp) {
		final ImmutableMap.Builder<String, Object> filters = ImmutableMap.builder();
		final ImmutableMap.Builder<String, Object> labels = ImmutableMap.builder();
		final ImmutableList.Builder<Object> industries = ImmutableList.builder();
		final ImmutableMap.Builder<String, Object> industryLabels = ImmutableMap.builder();
		final List<Long> industryIds = req.hasIndustryFilter() && req.getIndustryFilter().getIndustryIdsCount() > 0
			? req.getIndustryFilter().getIndustryIdsList()
			: Collections.<Long>emptyList();

		filters.put("keywords", req.getKeyword());
		filters.put("objective_type", req.getObjectiveType());
		if (resp.getFacetsCount() > 0 && resp.getFacetsList().get(0).getFacetCountsCount() > 0) {
			// we have only industry facet
			final List<FacetCount> industryCounts = resp.getFacetsList().get(0).getFacetCountsList();
			for (final FacetCount industryCount :industryCounts) {
				if (industryCount.getCount() > 0) {
					Long industryId = Long.parseLong(industryCount.getValue());
					industries.add(CollectionUtilities.newObjectMap(
						"id", industryCount.getValue(),
						"count", industryCount.getCount(),
						"filter_on", industryIds.contains(industryId)));
					final String industryLabel = industryService.getIndustryById(industryId).getName();
					industryLabels.put(industryCount.getValue(), industryLabel != null ? industryLabel : "");
				}
			}
		}
		filters.put("industries", industries.build());
		for (final String label : Lists.newArrayList("assessments", "certifications", "groups", "licenses", "lanes")) {
			labels.put(label, EMPTY_LABEL);
		}
		labels.put("industries", industryLabels.build());
		filters.put("labels", labels.build());
		return filters.build();
	}

	@VisibleForTesting
	List<Object> extractIndustries(final TalentPool talentPool) {
		final ImmutableList.Builder<Object> industries = ImmutableList.builder();
		for (final Industry industry : talentPool.getIndustriesList()) {
			industries.add(CollectionUtilities.newObjectMap(
				"id", industry.getId(),
				"name", industry.getName(),
				"description", ""));
		}
		return industries.build();
	}

	@VisibleForTesting
	String convertISODateString(final String iso8601) {
		if (StringUtils.isEmpty(iso8601)) {
			return StringUtils.EMPTY;
		}
		final Date date = DateUtilities.getDateFromISO8601(iso8601);
		return DateUtilities.format("MM/dd/yyyy", date);
	}

	@VisibleForTesting
	boolean isAuthorizedToJoin(
		final TalentPool talentPool,
		final SearchType searchType,
		final Long searcherId,
		final boolean isSearcherLane3Active
	) {
		// this is based on GroupSearchResultHydrator#67, don't know why we don't check other search types
		if (!SearchType.SEARCH_ALL_OPEN_GROUPS.equals(searchType)) {
			return false;
		}

		boolean isAuthorizedToJoin;
		if (talentPool.getLane0Flag() || talentPool.getLane1Flag() || talentPool.getLane2Flag()) {
			isAuthorizedToJoin = true;
		} else if (talentPool.getLane3Flag()) {
			isAuthorizedToJoin = isSearcherLane3Active;
		} else { // supposedly this should be "else if (talentPool.lane4Flag())", but lane4Flag is never set during indexing
			if (isSearcherLane3Active) {
				isAuthorizedToJoin = true;
			} else {
				final LaneContext laneContext =
					laneService.getLaneContextForUserAndCompany(searcherId, talentPool.getCompanyId());
				isAuthorizedToJoin = laneContext != null && laneContext.isInWorkerPool();
			}
		}

		return isAuthorizedToJoin;
	}
}
