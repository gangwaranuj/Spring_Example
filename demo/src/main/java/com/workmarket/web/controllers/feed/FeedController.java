package com.workmarket.web.controllers.feed;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.workmarket.data.solr.query.SolrMetricConstants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.domains.velvetrope.service.AdmissionService;
import com.workmarket.search.model.SearchType;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.feed.Feed;
import com.workmarket.service.business.feed.FeedService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.feed.FeedRequestParams;
import com.workmarket.web.validators.FeedRequestParamsValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.DisplayTool;
import org.apache.velocity.tools.generic.EscapeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/feed")
public class FeedController extends BaseController {

	@Autowired private VelocityEngine velocityEngine;
	@Autowired private FeedService feedService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private FeedRequestParamsValidator feedRequestParamsValidator;
	@Autowired private IndustryService industryService;
	@Autowired private UserService userService;
	@Autowired private LaneService laneService;
	@Autowired private AdmissionService admissionService;

	@RequestMapping(
		value = "/s",
		method = GET,
		produces = "text/javascript;charset=utf-8")
	public @ResponseBody String script(FeedRequestParams params, HttpServletRequest request) throws SolrServerException {
		Map<String, Object> model = Maps.newHashMap();
		SolrQuery query = new SolrQuery();
		model.put("feed", feedService.getFeed(params, query).getResults());
		model.put("escape", new EscapeTool());
		model.put("date", new DateTool());
		model.put("display", new DisplayTool());
		model.put("baseUrl", request.getScheme() + "://" + request.getHeader("host"));
		return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/template/feed/script.vm", model);
	}

	@RequestMapping(
		value = "/firehose",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Feed firehose(FeedRequestParams params) throws SolrServerException {
		BindingResult binding = new DataBinder(params).getBindingResult();
		feedRequestParamsValidator.validate(params, binding);
		if (binding.hasErrors()) {
			return new Feed().setErrorMessages(
				extract(binding.getAllErrors(), on(ObjectError.class).getDefaultMessage())
			);
		}

		ExtendedUserDetails userDetails = getCurrentUser();
		Assert.notNull(userDetails);

		User currentUser = userService.findUserById(userDetails.getId());

		List<Long> allCompaniesWhereUserIsResource = laneService.findAllCompaniesWhereUserIsResource(currentUser.getId(), LaneType.LANE_2);
		ImmutableList<Long> currentUserCompanyIds = ImmutableList.of(currentUser.getCompany().getId());

		if (currentUser.isUserExclusive()) {
			params.setExclusiveCompanyIds(allCompaniesWhereUserIsResource);
		} else if (isUserPrivateEmployee()) {
			params.setExclusiveCompanyIds(currentUserCompanyIds);
		}

		params.setExcludeCompanyIds(userService.findBlockedOrBlockedByCompanyIdsByUserId(currentUser.getId()));

		List<Long> nonMarketplaceCompanyIds = getNonMarketplaceCompanyIds(
			allCompaniesWhereUserIsResource,
			currentUserCompanyIds
		);
		params.getExcludeCompanyIds().addAll(nonMarketplaceCompanyIds);

		// query metrics
		// determine if the user is specifying location
		boolean userLoc = true;
		if (StringUtils.isNotEmpty(params.getPostalCode())) {
			if (StringUtils.equals(params.getPostalCode(), userDetails.getPostalCode())) {
				userLoc = false;
			}
		}

		boolean userTravelDistance = true;
		if (userDetails.getMaxTravelDistance() != null && StringUtils.isNotEmpty(params.getDistanceInMiles())) {
			if (StringUtils.equals(userDetails.getMaxTravelDistance().toString(), params.getDistanceInMiles())) {
				userTravelDistance = false;
			}
		}

		SolrQuery query = new SolrQuery();
		query.add(SolrMetricConstants.SEARCH_TYPE, SearchType.WORK_FEED.name());
		query.add(SolrMetricConstants.PERSONA, SolrMetricConstants.WORKER_PERSONA);
		if (getCurrentUser() != null && getCurrentUser().getId() != null) {
			query.add(SolrMetricConstants.USER, getCurrentUser().getId().toString());
		}
		if (getCurrentUser() != null && getCurrentUser().getCompanyId() != null) {
			query.add(SolrMetricConstants.COMPANY, getCurrentUser().getCompanyId().toString());
		}
		query.add(SolrMetricConstants.USER_INDUSTRY, Boolean.toString(false));
		query.add(SolrMetricConstants.USER_LOCATION, Boolean.toString(userLoc));
		query.add(SolrMetricConstants.USER_TRAVEL_DISTANCE, Boolean.toString(userTravelDistance));
		query.add(SolrMetricConstants.REQUEST_SOURCE, SolrMetricConstants.WEB_REQUEST);
		if (params.isFilterOutApplied()) {
			query.addFilterQuery("-applicantIds:" + currentUser.getId());
		}


		return feedService.getFeed(params, query);
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

	@RequestMapping(method = RequestMethod.GET)
	public String view(Model model) {
		model.addAttribute("feedValidationConstants", FeedRequestParamsValidator.VALIDATION_CONSTANTS);
		return "redirect:/home";
	}

	@RequestMapping(
		value = "/build",
		method = GET)
	public String build(Model model) {
		model.addAttribute("industries", industryService.getAllIndustryDTOs());
		return "redirect:/home";
	}

	@RequestMapping(
		value = "/validate_postal_code",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	public @ResponseBody boolean validatePostalCode(@RequestParam("p") String postalCode) {
		// Validate postalCode if not blank
		return StringUtils.isBlank(postalCode) || invariantDataService.findOrSavePostalCode(postalCode) != null;
	}
}
