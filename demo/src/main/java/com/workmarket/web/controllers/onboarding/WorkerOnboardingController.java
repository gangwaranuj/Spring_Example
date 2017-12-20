package com.workmarket.web.controllers.onboarding;

import com.google.api.client.util.Maps;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.workmarket.data.solr.query.SolrMetricConstants;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.qualification.SkillRecommenderDTO;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.api.v2.model.ApiJobTitleDTO;
import com.workmarket.domains.onboarding.model.Qualification;
import com.workmarket.domains.onboarding.model.PhoneInfoDTO;
import com.workmarket.domains.onboarding.model.WorkerOnboardingDTO;
import com.workmarket.dto.SuggestionDTO;
import com.workmarket.search.model.SearchType;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.SkillService;
import com.workmarket.service.business.SpecialtyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.IndustryDTO;
import com.workmarket.service.business.feed.Feed;
import com.workmarket.service.business.feed.FeedService;
import com.workmarket.service.business.onboarding.OnboardMappingService;
import com.workmarket.service.business.qualification.QualificationRecommender;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.service.locale.LocaleService;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.RestCode;
import com.workmarket.web.exceptions.NotFoundException;
import com.workmarket.web.exceptions.ValidationException;
import com.workmarket.web.forms.feed.FeedRequestParams;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import rx.Observable;
import rx.functions.Action1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


/**
 * Defines the end points for the Worker Onboarding screens.
 */
@Controller
@RequestMapping("/onboarding")
public class WorkerOnboardingController extends WorkerOnboardingBaseController {
	private static final Logger logger = LoggerFactory.getLogger(WorkerOnboardingController.class);

	@Autowired OnboardMappingService onboardMappingService;
	@Autowired ProfileService profileService;
	@Autowired InvariantDataService invariantDataService;
	@Autowired UserService userService;
	@Autowired WorkSearchService workSearchService;
	@Autowired MessageBundleHelper messageBundleHelper;
	@Autowired FeedService feedService;
	@Autowired IndustryService industryService;
	@Autowired SuggestionService suggestionsService;
	@Autowired WebRequestContextProvider webRequestContextProvider;
	@Autowired SkillService skillService;
	@Autowired SpecialtyService specialtyService;
	@Autowired private LocaleService localeService;
	@Autowired private FeatureEntitlementService featureEntitlementService;

	@Autowired private QualificationRecommender qualificationRecommender;

	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model, HttpServletRequest request, SitePreference site) {
		Map<String, Object> map = profileService.getProjectionMapByUserNumber(getCurrentUser().getUserNumber(), "id");

		model.addAttribute("profileId", map.get("id"));
		model.addAttribute("isMobile", isMobile(request, site));

		return "web/pages/home/onboarding";
	}

	@RequestMapping(value = "/profiles/{profileId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map workerGet(HttpServletResponse response, HttpServletRequest request, @PathVariable("profileId") Long profileId) throws Exception {
		Profile profile = profileService.findById(profileId);

		if (profile == null ||
			profile.getId() == null ||
			!profile.getUser().getUuid().equals(getCurrentUser().getUuid())) {
			throw new NotFoundException(RestCode.PROFILE_NOT_FOUND);
		}

		Company company = profileService.findCompany(getCurrentUser().getId());

		return onboardMappingService.mapProfile(request.getParameter("flds"), profile, company, webRequestContextProvider
				.getRequestContext());
	}

	@RequestMapping(value = "/profiles/{profileId}", method = {RequestMethod.PUT, RequestMethod.POST}, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	public @ResponseBody Map workerPut(@Valid @RequestBody WorkerOnboardingDTO dto, HttpServletResponse response, @PathVariable("profileId") Long profileId, BindingResult result) throws Exception {
		Profile profile = profileService.findById(profileId);

		if(profile == null || !Objects.equals(profile.getUser().getId(), getCurrentUser().getId())) {
			throw new NotFoundException(RestCode.PROFILE_NOT_FOUND);
		}


		if(dto.getFirstName() == null) {
			dto.setFirstName(profile.getUser().getFirstName());
		}
		if(dto.getLastName() == null) {
			dto.setLastName(profile.getUser().getLastName());
		}
		if(dto.getEmail() == null) {
			dto.setEmail(profile.getUser().getEmail());
		}

		WorkerOnboardingDTOValidator validator = new WorkerOnboardingDTOValidator(messageBundleHelper, userService, profile.getUser().getUserNumber());
		List<PhoneInfoDTO> phones = dto.getPhones();

		if (isNotEmpty(phones)) {
			String callingCodeIdex = dto.getPhones().get(0).getCode();
			PhoneInfoDTO phoneInfo = dto.getPhones().get(0);
			phoneInfo.setCode(invariantDataService.findCallingCodeFromID(Long.valueOf(callingCodeIdex)).getCallingCodeId());
			validator.validate(dto, result);
			phoneInfo.setCode(callingCodeIdex);
		} else {
			validator.validate(dto, result);
		}

		if (result.hasErrors()) {
			throw new ValidationException(result.getAllErrors());
		}


		if (profile == null || profile.getId() == null) {
			throw new NotFoundException(RestCode.PROFILE_NOT_FOUND);
		}

		Company company = profileService.findCompany(getCurrentUser().getId());
		profileService.saveOnboardPhoneCodes(profile.getId(), dto, true);
		profileService.saveOnboardProfile(profile.getUser().getId(), profile.getId(), company, dto, true);

		final boolean hasLocaleFeature = featureEntitlementService.hasFeatureToggle(profile.getUser().getId(), "locale");

		if (hasLocaleFeature && dto.getCountryIso() != null) {
			localeService.setPreferredFormat(profile.getUser().getUuid(), dto.getCountryIso());
		}

		Profile updatedProfile = profileService.findById(profileId);

		return onboardMappingService.mapProfile(null, updatedProfile, company, webRequestContextProvider.getRequestContext());
	}

	@RequestMapping(value = "/industries", method = GET, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map industries(HttpServletResponse response) throws Exception {
		Map<Long, String> industryMap = Maps.newLinkedHashMap();

		for (IndustryDTO industry : industryService.getAllIndustryDTOs()) {
			industryMap.put(industry.getId(), industry.getName());
		}

		return CollectionUtilities.newObjectMap("industries", industryMap);
	}

	@RequestMapping(value = "/available_assignments", method = GET, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Feed availableAssignments(
			FeedRequestParams params,
			HttpServletResponse response) throws SolrServerException {

		SolrQuery query = new SolrQuery();
		params.setLimit(200);

		// metrics
		query.add(SolrMetricConstants.SEARCH_TYPE, SearchType.WORK_FEED_ONBOARDING.name());
		query.add(SolrMetricConstants.PERSONA, SolrMetricConstants.WORKER_PERSONA);
		if (getCurrentUser() != null && getCurrentUser().getId() != null) {
			query.add(SolrMetricConstants.USER, getCurrentUser().getId().toString());
		}
		if (getCurrentUser() != null && getCurrentUser().getCompanyId() != null) {
			query.add(SolrMetricConstants.COMPANY, getCurrentUser().getId().toString());
		}
		query.add(SolrMetricConstants.USER_INDUSTRY, Boolean.toString(false));
		query.add(SolrMetricConstants.USER_LOCATION, Boolean.toString(false));
		query.add(SolrMetricConstants.REQUEST_SOURCE, SolrMetricConstants.WEB_REQUEST);

		Feed feed = feedService.getFeed(params, query);

		if (!params.getIncludeResults()) {
			feed.setResults(null);
		}

		return feed;
	}
}
