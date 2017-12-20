package com.workmarket.web.controllers;

import com.google.common.collect.ImmutableList;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.domains.groups.service.UserGroupRequirementSetService;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.Sort;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.search.group.indexer.model.GroupSolrData;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.domains.velvetrope.service.AdmissionService;
import com.workmarket.dto.TalentPoolMembershipDTO;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolRequest;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolResponse;
import com.workmarket.search.gen.GroupMessages.TalentPool;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserGroupServiceImpl;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.dto.GroupSearchFilterDTO;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.search.SearchService;
import com.workmarket.service.search.group.FindTalentPoolAdapter;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.helpers.ActionGroupsHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Controller
@RequestMapping("/search-groups")
@SessionAttributes("start")
public class SearchgroupsController extends BaseController {

	private static final Log logger = LogFactory.getLog(SearchgroupsController.class);

	@Autowired private SearchService searchService;
	@Autowired private RequestService requestService;
	@Autowired private UserService userService;
	@Autowired private LaneService laneService;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private UserGroupRequirementSetService userGroupRequirementSetService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private VendorService vendorService;
	@Autowired private FindTalentPoolAdapter findTalentPoolAdapter;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private FeatureEntitlementService featureEntitlementService;
	@Autowired private AdmissionService admissionService;

	@RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
	public String index(@RequestParam(value = "keyword", required = false) String keyword, ModelMap model) {

		logger.debug("filter_form GET");
		model.addAttribute("group_sort_options", UserGroupServiceImpl.SortOption.values());
		model.addAttribute("group_objective_options", ActionGroupsHelper.groupObjectiveOptions);
		model.addAttribute("selected_navigation_link", "groups.search");
		model.addAttribute("pageId", "groupSearch");

		if (model.get("start") == null) {
			model.put("start", 0); // default to beginning
		}
		model.addAttribute("start", model.get("start"));

		if (StringUtilities.isNotEmpty(keyword)) {
			model.addAttribute("defaultsearch", keyword.trim());
		}

		model.addAttribute("is_wm_admin_csv", true);
		model.addAttribute("keyword", "");
		model.addAttribute("industry", new ArrayList<Integer>());

		if (getCurrentUser().isDispatcher()) {
			TalentPoolMembershipDTO dto = vendorService.getAllVendorUserGroupMemberships(getCurrentUser().getId());
			model.addAttribute("groupMembershipsCount", dto.getMemberships().size() + dto.getApplications().size());
			model.addAttribute("groupInvitationsCount", dto.getInvitations().size());
		} else {
			model.addAttribute("groupMembershipsCount", userGroupService.countUserGroupMemberships(getCurrentUser().getId()));
			model.addAttribute("groupInvitationsCount", requestService.countUserGroupInvitationsByInvitedUser(getCurrentUser().getId()));
		}

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "talentPools",
			"data", CollectionUtilities.newObjectMap(
				"type", "search",
				"start", model.get("start")
			),
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/groups/search-groups";
	}

	/**
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/retrieve", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public
	@ResponseBody
	Map<String, Object> retrieve(
			@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer limit,
			@RequestParam(required = false) String sortby, @RequestParam(value = "group_keyword", required = false) String keyword,
			@RequestParam(required = false) String objective_type,
			@RequestParam(value = "industry[]", required = false) Integer[] industry,
			ModelMap model,
			HttpServletRequest request) throws Exception {

		logger.debug("filter_form POST");
		model.put("start", start); // store in session so the pagination index is remembered when they come back to the HTML page in question
		Long userId = getCurrentUser().getId();
		User user = userService.findUserById(userId);

		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> groups = new ArrayList<>();

		if (featureEntitlementService.hasPercentRolloutFeatureToggle(Constants.SEARCH_SERVICE_GROUP)) {
			FindTalentPoolRequest req = findTalentPoolAdapter.buildFindTalentPoolRequest(
				start,
				limit,
				keyword,
				objective_type,
				industry,
				sortby,
				user, isUserPrivateEmployee(),
				getCurrentUser().isEmployeeWorker());

			FindTalentPoolResponse resp = searchService.findTalentPools(req);

			boolean isAdmin = request.isUserInRole("ROLE_MANAGER") || request.isUserInRole("ROLE_ADMINISTRATOR") || request.isUserInRole("ROLE_INTERNAL");
			boolean isSearcherLane3Active = authenticationService.isLane3Active(user);
			for(TalentPool talentPool : resp.getTalentPoolsList()) {
				groups.add(
					findTalentPoolAdapter.convertTalentPoolToMap(
						talentPool,
						req.getSearchType(),
						user.getId(),
						isSearcherLane3Active,
						isAdmin,
						userGroupRequirementSetService.userGroupHasRequirements(talentPool.getId())));
			}
			result.put("results", groups);
			result.put("results_count", resp.getNumFound());
			result.put("filters", findTalentPoolAdapter.populateFilters(req, resp));
		} else {
			GroupSolrDataPagination pagination = buildGroupSearchRequest(
				start,
				limit,
				keyword,
				objective_type,
				industry,
				sortby,
				user, isUserPrivateEmployee(),
				getCurrentUser().isEmployeeWorker());

			GroupSolrDataPagination searchResults = searchService.searchAllGroups(pagination);

			// TODO: filter out the unused fields
			for (GroupSolrData group : searchResults.getResults()) {
				groups.add(mapGroupSolrData(group, request));
			}

			result.put("results", groups);
			result.put("results_count", searchResults.getRowCount());
			result.put("filters", populateFilters(searchResults));
		}

		return result;
	}

	private Map<String, Object> mapGroupSolrData(GroupSolrData group, HttpServletRequest request) {
		Map<String, Object> g = new HashMap<>();
		g.put("id", group.getId());
		g.put("name_short", StringUtilities.truncate(group.getName(), 40));
		g.put("name", group.getName());
		g.put("company_name", group.getCompanyName());
		g.put("avatar_uri", group.getAvatarAssetLargeUri());
		g.put("description_no_html", StringUtilities.truncate(StringUtilities.stripHTML(group.getDescription()),70));
		g.put("industries", getIndustriesFromGroup(group));
		g.put("member_count", group.getMemberCount());
		g.put("created_on", DateUtilities.format("MM/dd/yyyy", group.getCreatedOn()));
		g.put("requires_approval", group.getRequiresApproval());
		g.put("has_requirements", userGroupRequirementSetService.userGroupHasRequirements(group.getId()));
		//groups.put("requirements", group.getRequirements);
		g.put("active_flag", group.getActiveFlag());
		g.put("authorized_to_join", group.getAuthorizedToJoin());
		g.put("is_admin", request.isUserInRole("ROLE_MANAGER")
			|| request.isUserInRole("ROLE_ADMINISTRATOR")
			|| request.isUserInRole("ROLE_INTERNAL"));

		return g;
	}

	// TODO: convert this to a bean + inner classes
	private Map<String, Object> populateFilters(GroupSolrDataPagination filters) {
		Map<String, Object> result = new HashMap<>();
		final List<Object> industries = new ArrayList<>();
		final Map<String, Object> industryLabels = new HashMap<>();
		final Map<String, Object> emptyMap = new HashMap<>();

		result.put("labels", CollectionUtilities.newObjectMap(
			"assessments", emptyMap,
			"certifications", emptyMap,
			"groups", emptyMap,
			"industries", industryLabels,
			"licenses", emptyMap,
			"lanes", emptyMap
		));

		result.put("keywords", filters.getSearchFilter().getKeywords());
		result.put("objective_type", filters.getSearchFilter().getObjectiveType());
		for (final GroupSearchFilterDTO.FacetFieldFilter industry : filters.getSearchFilter().getIndustries().getFacetFieldFilters()) {
			if (industry.getCount() > 0) {
				industries.add(CollectionUtilities.newObjectMap(
					"id", industry.getId(),
					"count", industry.getCount(),
					"filter_on", industry.getFilterOn()
				));
			}
			industryLabels.put(industry.getId(), industry.getLabel());
		}
		result.put("industries", industries);
		return result;
	}

	private List<Object> getIndustriesFromGroup(GroupSolrData group) {
		List<Object> result = new ArrayList<>();

		// original controller assumes all three collections come back in the same order
		for (int i = 0; i < group.getIndustryDescriptions().size(); i++) {
			result.add(CollectionUtilities.newObjectMap(
				"id", group.getIndustryIds().get(i),
				"name", group.getIndustryNames().get(i),
				"description", group.getIndustryDescriptions().get(i)
			));
		}

		return result;
	}

	private GroupSolrDataPagination buildGroupSearchRequest(
		final Integer start, final Integer limit,
		final String keyword, final String objectiveType,
		final Integer[] industryIds, final String sortby,
		final User searcher, final boolean isSearcherPrivateEmployee, final boolean isSearcherEmployeeWorker
	) {
		GroupSolrDataPagination pagination = new GroupSolrDataPagination();

		pagination.setStartRow(start);
		pagination.setResultsLimit(limit);

		if (sortby != null && !sortby.equals("default")) {
			final Sort sort = new Sort(sortby, ActionGroupsHelper.sortOptions.containsKey(sortby) ? Pagination.SORT_DIRECTION.ASC : Pagination.SORT_DIRECTION.DESC);
			pagination.setSorts(new ArrayList<Sort>() {{
				add(sort);
			}});
		}

		GroupSearchFilterDTO filter = pagination.getSearchFilter();
		if (industryIds != null && industryIds.length > 0) {
			filter.getIndustries().setIds(industryIds);
		}
		filter.setUserId(searcher.getId());
		if (StringUtils.isNotBlank(keyword)) {
			filter.setKeywords(keyword);
		}
		if (StringUtils.isNotBlank(objectiveType)) {
			filter.setObjectiveType(objectiveType);
		}

		List<Long> allCompaniesWhereUserIsResource = laneService.findAllCompaniesWhereUserIsResource(searcher.getId(), LaneType.LANE_2);
		ImmutableList<Long> currentUserCompanyIds = ImmutableList.of(searcher.getCompany().getId());

		if (searcher.isUserExclusive()) {
			filter.setCompanyIdsExclusiveToUser(allCompaniesWhereUserIsResource);
		} else if (isSearcherPrivateEmployee) {
			filter.setCompanyIdsExclusiveToUser(currentUserCompanyIds);
		}

		filter.setBlockedByCompanyIds(userService.findBlockedOrBlockedByCompanyIdsByUserId(searcher.getId()));

		List<Long> nonMarketplaceCompanyIds = getNonMarketplaceCompanyIds(
			allCompaniesWhereUserIsResource,
			currentUserCompanyIds
		);
		filter.getBlockedByCompanyIds().addAll(nonMarketplaceCompanyIds);

		if (isSearcherEmployeeWorker) {
			pagination.setSearchType(GroupSolrDataPagination.SEARCH_TYPE.SEARCH_ALL_OPEN_COMPANY_GROUPS);
		} else {
			pagination.setSearchType(GroupSolrDataPagination.SEARCH_TYPE.SEARCH_ALL_OPEN_GROUPS);
		}
		return pagination;
	}

	@SafeVarargs
	private final List<Long> getNonMarketplaceCompanyIds(List<Long>... ignorableNonMarketplaceCompanyIds) {
		// We need a list of companies that do not have the MARKETPLACE feature.
		// Here, we rely on the fact that all valid companies with MARKETPLACE will also
		// have one of the ENTERPRISE, PROFESSIONAL, or TRANSACTIONAL feature turned on.
		// We get those that do not have MARKETPLACE and exclude those previously
		// collected with valid non-marketplace associations.
		//   TODO[Jim]: perhaps there is a more elegant way to identify these companies via Velvet Rope
		List<Admission> admissions = admissionService.findAllAdmissionsByKeyNameExcludingVenueForVenues(
			"companyId",
			Venue.MARKETPLACE,
			Venue.ENTERPRISE,
			Venue.PROFESSIONAL,
			Venue.TRANSACTIONAL
		);
		List<Long> nonMarketplaceCompanyIds = extract(admissions, on(Admission.class).getLongValue());
		for (List<Long> ids : ignorableNonMarketplaceCompanyIds) {
			nonMarketplaceCompanyIds.removeAll(ids);
		}
		return nonMarketplaceCompanyIds;
	}
}
