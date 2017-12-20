package com.workmarket.web.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.common.core.RequestContext;
import com.workmarket.dao.summary.work.WorkHistorySummaryDAO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.asset.UserLinkAssociation;
import com.workmarket.domains.model.comment.Comment;
import com.workmarket.domains.model.comment.CommentPagination;
import com.workmarket.domains.model.composer.ComposerField;
import com.workmarket.domains.model.composer.ComposerFieldInstance;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.velvetrope.guest.WebGuest;
import com.workmarket.domains.velvetrope.rope.ProfileCustomFieldRope;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkPagination;
import com.workmarket.domains.work.model.WorkResourceFeedbackPagination;
import com.workmarket.domains.work.model.WorkResourceFeedbackRow;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.CommentService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.UserCommentDTO;
import com.workmarket.service.business.dto.UserProfileCompletenessDTO;
import com.workmarket.service.composer.ComposerService;
import com.workmarket.service.web.ProfileFacadeService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.facade.ProfileFacade;
import com.workmarket.web.forms.profile.AddCommentForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.RatingStarsHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import com.workmarket.service.web.WebRequestContextProvider;

@Controller
@RequestMapping("/profile")
public class ProfileController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
	@Autowired private ProfileService profileService;
	@Autowired private ProfileFacadeService profileFacadeService;
	@Autowired private UserGroupService groupService;
	@Autowired private WorkService workService;
	@Autowired private WorkResourceService resourceService;
	@Autowired private UserService userService;
	@Autowired private CommentService commentService;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private AnalyticsService analyticsService;
	@Autowired private WorkHistorySummaryDAO workHistorySummaryDAO;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private ComposerService composerService;
	@Autowired @Qualifier("profileCustomFieldDoorman") private Doorman profileCustomFieldDoorman;

	@Value("${baseurl}")
	private String baseUrl;

	private static final Integer MAX_PROFILE_ASSETS_ALLOWED = 10;

	@RequestMapping(method = GET)
	public ModelAndView indexCurrentUser(Model model) throws Exception {
		model.addAttribute("baseurl", baseUrl);
		model.addAttribute("isCanadian", Country.CANADA.equals(getCurrentUser().getCountry()));
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/profile"));
		model.addAttribute("isUserTelaidPrivate", profileService.isUserTelaidPrivate(getCurrentUser().getId()));
		String userNumber = getCurrentUser().getUserNumber();
		return getProfileFacade(userNumber, true);
	}

	// Never link to this
	// TODO: Legacy support for /profile/index/{userNumber}
	@RequestMapping(
		value = "/index/{userNumber}",
		method = GET)
	public String profileIndexRedirect(@PathVariable String userNumber) {
		return "redirect:/profile/" + userNumber;
	}

	@RequestMapping(
		value = "/{userNumber}",
		method = GET)
	public ModelAndView indexSpecificUser(
		Model model,
		@PathVariable String userNumber) throws Exception {

		model.addAttribute("baseurl", baseUrl);
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/profile"));
		model.addAttribute("isUserTelaidPrivate", false);

		return getProfileFacade(userNumber, true);
	}

	@RequestMapping(
		value = "/company/{companyNumber}",
		method = GET)
	public ModelAndView indexSpecificCompany(
		Model model,
		@PathVariable String companyNumber) throws Exception {

		model.addAttribute("baseurl", baseUrl);
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/profile"));

		return getProfileFacade(companyNumber, false);
	}

	@RequestMapping(
		value = "/{userNumber}",
		method = GET, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map indexUser(HttpServletResponse response, HttpServletRequest request, @PathVariable String userNumber) throws Exception {
		ProfileFacade facade;
		if (request.getParameter("isVendor") != null) {
			facade = profileFacadeService.findProfileFacadeByCompanyNumber(userNumber);

			final Map<String, Object> result = Maps.newHashMap();
			result.put("facade", facade);
			result.put("allScorecard", analyticsService.getVendorScoreCard(facade.getCompanyId()));
			result.put("companyScorecard", analyticsService.getVendorScoreCardForCompany(getCurrentUser().getCompanyId(), facade.getCompanyId()));
			response.setStatus(response.SC_OK);
			return result;
		} else {
			facade = profileFacadeService.findProfileFacadeByUserNumber(userNumber);
		}
		if (facade == null) {
			response.setStatus(response.SC_NOT_FOUND);
			return null;
		}

		if (!profileFacadeService.isCurrentUserAuthorizedToSeeProfile(getCurrentUser(), facade)) {
			response.setStatus(response.SC_UNAUTHORIZED);
			return null;
		}

		final Map<String, Object> result = getProfileFacadeMetadata(facade);
		result.put("facade", facade);
		addScoreCardToResponse(userNumber, result);
		response.setStatus(response.SC_OK);
		return result;
	}

	private void addScoreCardToResponse(String userNumber,  Map<String, Object> result) {
		if (StringUtils.isNotBlank(userNumber) && result != null) {
			User user = userService.findUserByUserNumber(userNumber);
			if (user != null) {
				result.put("allScorecard", analyticsService.getResourceScoreCard(user.getId()));
				result.put("companyScorecard", analyticsService.getResourceScoreCardForCompany(getCurrentUser().getCompanyId(), user.getId()));
				Integer paidAssignmentForCompanyCount = MapUtils.getInteger(workHistorySummaryDAO.countWorkForCompany(Lists.newArrayList(user.getId()), getCurrentUser().getCompanyId(), WorkStatusType.PAID), user.getId(), 0);
				result.put("paidassignforcompany", paidAssignmentForCompanyCount);
				if (!getCurrentUser().getUserNumber().equals(userNumber)) {
					result.put("companyScorecard", analyticsService.getResourceScoreCardForCompany(getCurrentUser().getCompanyId(), user.getId()));
				}
			}
		}
	}

	private ModelAndView getProfileFacade(String number, Boolean isUser) throws Exception {
		ProfileFacade facade;
		if(isUser) {
			facade = profileFacadeService.findProfileFacadeByUserNumber(number);
			if (!profileFacadeService.isCurrentUserAuthorizedToSeeProfile(getCurrentUser(), facade)) {
				return new ModelAndView("forward:/profile/notauthorized");
			}
		} else {
			facade = profileFacadeService.findProfileFacadeByCompanyNumber(number);
		}

		if (facade == null) {
			return new ModelAndView("redirect:/profile");
		}

		ModelAndView model;
		final Map<String, Object> profileFacadeMap;
		if (isUser) {
			model = new ModelAndView("web/pages/profile/user/index");
			for (Map.Entry<String, Object> entry : getProfileFacadeMetadata(facade).entrySet()) {
				model.addObject(entry.getKey(), entry.getValue());
			}
			profileFacadeMap = getProfileFacadeMap(facade);
		} else {
			model = new ModelAndView("web/pages/profile/company/index");
			profileFacadeMap =  Maps.newHashMap();
			profileFacadeMap.put("facade", facade);
			profileFacadeMap.put("allScorecard", jsonSerializationService.toJson(analyticsService.getVendorScoreCard(facade.getCompanyId())));
			profileFacadeMap.put("companyScorecard", jsonSerializationService.toJson(
				analyticsService.getVendorScoreCardForCompany(getCurrentUser().getCompanyId(), facade.getCompanyId())));
		}

		if (profileFacadeMap != null) {
			for (Map.Entry<String, Object> entry : profileFacadeMap.entrySet()) {
				model.addObject(entry.getKey(), entry.getValue());
			}
		}
		Boolean isDispatch = getCurrentUser().getCompanyId().equals(facade.getCompanyId());
		model.addObject("isDispatch", isDispatch);

		return model;
	}

	private Map<String, Object> getProfileFacadeMap(ProfileFacade facade) {
		final Map<String, Object> result = Maps.newHashMap();
		result.put("facade", facade); // Deprecated
		result.put("facadeJSON", jsonSerializationService.toJson(facade));
		result.put("laneType", facade.getLaneType());
		result.put("isOwner", facade.isOwner());
		result.put("isSuspended", facade.isSuspended());
		result.put("isDeactivated", facade.isDeactivated());
		result.put("hasSharedWorkerRole", facade.hasSharedWorkerRole());
		result.put("roleNames", StringUtils.join(facade.getRoleNames(), ", "));
		result.put("laneAccess", facade.getLaneAccess());
		result.put("displayGetWork", facade.hasGetWorkAccess());
		result.put("profileCompleteness", facade.getProfileCompleteness());
		result.put("latitude", String.valueOf(facade.getLatitude()));
		result.put("longitude", String.valueOf(facade.getLongitude()));
		result.put("backgroundImageUri", facade.getBackgroundImageUri());
		result.put("confirmedBank", facade.isConfirmedBankAccount());
		result.put("memberSince", facade.getCreatedOn());
		result.put("mboStatus", facade.getMboStatus());
		result.put("hasVerifiedTaxEntity", facade.hasVerifiedTaxEntity());
		result.put("hasRejectedTaxEntity", facade.hasRejectedTaxEntity());
		result.put("hasTaxEntity", facade.getTaxEntityExists());
		result.put("taxEntityCountry", facade.getTaxEntityCountry());
		result.put("hasPhoto", facade.hasPhoto());
		result.put("hasVideo", facade.hasVideo());
		result.put("inviteGroups", facade.getGroupsAvailableToInvite());
		result.put("groupPermissions", jsonSerializationService.toJson(facade.getCurrentCompanyGroupPermission()));
		result.put("allScorecard", jsonSerializationService.toJson(analyticsService.getResourceScoreCard(facade.getId())));
		result.put("companyScorecard", jsonSerializationService.toJson(analyticsService.getResourceScoreCardForCompany(getCurrentUser().getCompanyId(), facade.getId())));

		Integer paidAssignmentForCompanyCount = MapUtils.getInteger(workHistorySummaryDAO.countWorkForCompany(Lists.newArrayList(facade.getId()), getCurrentUser().getCompanyId(), WorkStatusType.PAID), facade.getId(), 0);
		result.put("paidassignforcompany", paidAssignmentForCompanyCount);

		return result;
	}

	private Map<String, Object> getProfileFacadeMetadata(ProfileFacade facade) throws Exception {

		// Determine various permissions / roles
		boolean isInternal = getCurrentUser().hasAnyRoles("ROLE_INTERNAL");
		boolean isGroupOwner = false;
		boolean isEveryone = false;

		if (!facade.isOwner()) {
			isGroupOwner = !groupService.findCompanyOwnedGroupsHavingUserAsMember(getCurrentUser().getCompanyId(), facade.getId()).isEmpty();
			if (!isGroupOwner) {
				isEveryone = true;
			}
		}

		boolean isAdmin = getCurrentUser().hasAnyRoles("ACL_ADMIN", "ACL_MANAGER");
		boolean isCompanyAdmin = isAdmin && getCurrentUser().getCompanyId().equals(facade.getCompanyId());
		boolean isLane4LimitedVisibility = (!facade.isOwner() && !isInternal && facade.isLane4());
		boolean allowTagging = !facade.isOwner() && !isLane4LimitedVisibility;

		List<UserAssetAssociation> profileMedias = profileService.findAllUserProfileOrderedAssetAssociations(facade.getId());

		List imageSmallOutput = Lists.newArrayListWithExpectedSize(MAX_PROFILE_ASSETS_ALLOWED);
		List imageLargeOutput = Lists.newArrayListWithExpectedSize(MAX_PROFILE_ASSETS_ALLOWED);
		List imageOutput = Lists.newArrayListWithExpectedSize(MAX_PROFILE_ASSETS_ALLOWED);
		final Set<Integer> assetOrders = Sets.newHashSet();

		for (UserAssetAssociation assetAssociation : profileMedias) {
			Asset asset = assetAssociation.getAsset();
			if (asset.getOrder() != null) {
				imageSmallOutput.add(assetAssociation.getTransformedSmallAsset());
				imageLargeOutput.add(assetAssociation.getTransformedLargeAsset());

				imageOutput.add(asset);
				assetOrders.add(asset.getOrder());

				if (asset.isMedia()) {
					facade.setVideo(true);
				} else {
					facade.setPhoto(true);
				}
			}
		}

		List<UserLinkAssociation> linkAssociations = profileService.findAllUserProfileEmbedVideoAssociations(facade.getId());
		if (!linkAssociations.isEmpty()) {
			facade.setVideo(true);
		}

		for (UserLinkAssociation association : linkAssociations) {
			assetOrders.add(association.getAssetOrder());
			imageLargeOutput.add(association.getLink());
			imageSmallOutput.add(association.getLink());
			imageOutput.add(association.getLink());
		}

		int nextImagePosition = profileMedias.size() + linkAssociations.size();
		for (int i = 0; i < MAX_PROFILE_ASSETS_ALLOWED; ++i) {
			if (!assetOrders.contains(i)) {
				nextImagePosition = i;
				break;
			}
		}

		final Map<String, Object> result = Maps.newHashMap();
		result.put("isEveryone", isEveryone);
		result.put("isGroupOwner", isGroupOwner);
		result.put("needUSATaxEntity", false);
		result.put("isAdmin", isAdmin);
		result.put("isCompanyAdmin", isCompanyAdmin);
		result.put("isInternal", isInternal);
		result.put("isLane4LimitedVisibility", isLane4LimitedVisibility);
		result.put("taggingUri", allowTagging ? "/tags/tag_user" : null);
		result.put("allowTagging", allowTagging);
		result.put("MaxProfileAssetsAllowed", MAX_PROFILE_ASSETS_ALLOWED);
		result.put("NextImagePosition", nextImagePosition);

		MutableBoolean hasProfileCustomField = new MutableBoolean(false);
		profileCustomFieldDoorman.welcome(new WebGuest(getCurrentUser()), new ProfileCustomFieldRope(hasProfileCustomField));
		result.put("hasProfileCustomField", hasProfileCustomField.isTrue());
		if (hasProfileCustomField.isTrue()) {
			RequestContext requestContext = webRequestContextProvider.getRequestContext();
			if (requestContext.getCompanyId() == null) {
				ExtendedUserDetails user = getCurrentUser();
				String companyUuid = companyService.findCompanyById(user.getCompanyId()).getUuid();
				requestContext.setCompanyId(companyUuid);
			   if ("monolith".equals(requestContext.getUserId())) {
				   requestContext.setUserId(user.getUuid());
			   }
			}

			List<ComposerField> composerFields =
				ImmutableList.of(new ComposerField("PROFILE", facade.getUuid(), "byline"));
			List<ComposerFieldInstance> composerFieldInstances = composerService.getValues(requestContext, composerFields);
			result.put("profileCustomFields", composerFieldInstances);
		}

		//Profile images and videos
		result.put("ImageSmallOutput", imageSmallOutput);
		result.put("ImageLargeOutput", imageLargeOutput);
		result.put("ImageOutput", imageOutput);

		return result;
	}

	private Map<String, Object> getCompanyProfileFacadeMetadata(ProfileFacade facade) throws Exception {

		List<UserAssetAssociation> profileMedias = profileService.findAllUserProfileOrderedAssetAssociations(facade.getId());

		List imageSmallOutput = Lists.newArrayListWithExpectedSize(MAX_PROFILE_ASSETS_ALLOWED);
		List imageLargeOutput = Lists.newArrayListWithExpectedSize(MAX_PROFILE_ASSETS_ALLOWED);
		List imageOutput = Lists.newArrayListWithExpectedSize(MAX_PROFILE_ASSETS_ALLOWED);
		final Set<Integer> assetOrders = Sets.newHashSet();

		for (UserAssetAssociation assetAssociation : profileMedias) {
			Asset asset = assetAssociation.getAsset();
			if (asset.getOrder() != null) {
				imageSmallOutput.add(assetAssociation.getTransformedSmallAsset());
				imageLargeOutput.add(assetAssociation.getTransformedLargeAsset());

				imageOutput.add(asset);
				assetOrders.add(asset.getOrder());

				if (asset.isMedia()) {
					facade.setVideo(true);
				} else {
					facade.setPhoto(true);
				}
			}
		}

		List<UserLinkAssociation> linkAssociations = profileService.findAllUserProfileEmbedVideoAssociations(facade.getId());
		if (!linkAssociations.isEmpty()) {
			facade.setVideo(true);
		}

		for (UserLinkAssociation association : linkAssociations) {
			assetOrders.add(association.getAssetOrder());
			imageLargeOutput.add(association.getLink());
			imageSmallOutput.add(association.getLink());
			imageOutput.add(association.getLink());
		}

		int nextImagePosition = profileMedias.size() + linkAssociations.size();
		for (int i = 0; i < MAX_PROFILE_ASSETS_ALLOWED; ++i) {
			if (!assetOrders.contains(i)) {
				nextImagePosition = i;
				break;
			}
		}

		final Map<String, Object> result = Maps.newHashMap();
		result.put("needUSATaxEntity", false);
		result.put("MaxProfileAssetsAllowed", MAX_PROFILE_ASSETS_ALLOWED);
		result.put("NextImagePosition", nextImagePosition);

		//Profile images and videos
		result.put("ImageSmallOutput", imageSmallOutput);
		result.put("ImageLargeOutput", imageLargeOutput);
		result.put("ImageOutput", imageOutput);

		return result;
	}

	@RequestMapping(
		value = "/workhistory",
		method = GET)
	public ModelAndView workhistory(
		HttpServletRequest httpRequest, @RequestParam("id") String userNumber) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		Long userId = userService.findUserId(userNumber);

		WorkPagination pagination = request.newPagination(WorkPagination.class);
		pagination.setSortColumn(WorkPagination.SORTS.WORK_ID);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
		pagination.addFilter(WorkPagination.FILTER_KEYS.WORK_STATUS, WorkStatusType.PAID);

		pagination = workService.findWorkByBuyerAndWorkResource(getCurrentUser().getId(), userId, pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		for (Work w : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				DateUtilities.format("MM/dd/yyyy", w.getScheduleFrom(), getCurrentUser().getTimeZoneId()),
				w.getTitle(),
				StringUtils.abbreviate(w.getDescription(), 80)
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", w.getWorkNumber()
			);

			response.addRow(data, meta);
		}

		ModelAndView m = new ModelAndView("web/pages/profile/workhistory");
		m.addObject("response", response);
		return m;
	}

	@RequestMapping(
		value = "/nosuchprofile",
		method = GET)
	public String nosuchprofile() {
		return "web/pages/profile/nosuchprofile";
	}

	@RequestMapping(
		value = "/notauthorized",
		method = GET)
	public String notauthorized() {
		return "web/pages/profile/notauthorized";
	}

	@RequestMapping(
		value = "/get_user_comments",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getUserComments(
		HttpServletRequest httpRequest,
		@RequestParam("id") String userNumber,
		Model model) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, CommentPagination.SORTS.CREATED_ON.toString());
		}});

		Long userId = userService.findUserId(userNumber);

		CommentPagination pagination = new CommentPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		pagination = commentService.findAllActiveCompanyUserComments(getCurrentUser().getCompanyId(), userId, pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		List<Comment> comments = pagination.getResults();
		List<User> creators = userService.findAllUsersByIds(CollectionUtilities.newListPropertyProjection(comments, "creatorId"));

		for (Comment c : comments) {
			List<String> data = Lists.newArrayList(
				DateUtilities.format("MM/dd/yyyy", c.getCreatedOn(), getCurrentUser().getTimeZoneId()),
				c.getComment(),
				CollectionUtilities.findFirst(creators, "id", c.getCreatorId()).getFullName(),
				null
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", c.getId(),
				"user_id", c.getCreatorId()
			);

			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/add_comment_to_user",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder addCommentToUser(
		@Valid @ModelAttribute AddCommentForm form,
		BindingResult bindingResult) {

		AjaxResponseBuilder response = new AjaxResponseBuilder();

		if (bindingResult.hasErrors()) {
			messageHelper.addMessage(response, "profile.add_comment_to_user.error");
			return response.setSuccessful(false);
		}

		User user = userService.findUserByUserNumber(form.getId());
		UserCommentDTO dto = new UserCommentDTO();
		dto.setComment(form.getComment());
		dto.setUserId(user.getId());

		try {
			commentService.saveOrUpdateCompanyUserComment(getCurrentUser().getCompanyId(), dto);
			messageHelper.addMessage(response, "profile.add_comment_to_user.success");
			return response.setSuccessful(true);

		} catch (Exception e) {
			logger.warn("Error saving userComment for userId={} and comment: {}", new Object[]{dto.getUserId(), dto.getComment()}, e);
			messageHelper.addMessage(response, "profile.add_comment_to_user.error");
			return response.setSuccessful(false);
		}
	}

	@RequestMapping(
		value = "/delete_user_comment",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder deleteUserComment(
		@RequestParam(value = "id") Long commentId) {

		MessageBundle bundle = MessageBundle.newInstance();
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		try {
			commentService.deleteComment(commentId);
		} catch (Exception ex) {
			messageHelper.addError(bundle, "profile.delete_user_comment.error");
			return response.setMessages(bundle.getErrors());
		}
		messageHelper.addSuccess(bundle, "profile.delete_user_comment.success");

		return response.setSuccessful(true).setMessages(bundle.getSuccess());
	}

	@RequestMapping(
		value = "/completeness_percentage",
		method = GET)
	public ModelAndView completenessPercentage() throws Exception {
		UserProfileCompletenessDTO completeness = profileService.getUserProfileCompleteness(getCurrentUser().getId());

		ModelAndView m = new ModelAndView("web/partials/profile/completeness-percentage");
		m.addObject("profileCompleteness", completeness);
		return m;
	}

	@RequestMapping(
		value = "/{userNumber}/ratings",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String ratings(
		@PathVariable("userNumber") String userNumber,
		Model model) {

		model.addAttribute("isOwner", userNumber.equals(getCurrentUser().getUserNumber()));
		return "web/pages/profile/ratings";
	}

	@RequestMapping(
		value = "/{userNumber}/ratings.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void ratingsList(
		HttpServletRequest httpRequest,
		@PathVariable("userNumber") String userNumber,
		Model model) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setFilterMapping(ImmutableMap.<String, Enum<?>>of(
			"scopeToCompany", WorkResourceFeedbackPagination.FILTER_KEYS.COMPANY_SCOPE
		));

		Long userId = userService.findUserId(userNumber);

		WorkResourceFeedbackPagination pagination = request.newPagination(WorkResourceFeedbackPagination.class);
		pagination = resourceService.findResourceFeedbackForUser(userId, pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		for (WorkResourceFeedbackRow r : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				r.getWorkTitle(),
				RatingStarsHelper.getStars(r.getRatingValue())
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"companyName", r.getCompanyName(),
				"workTitle", r.getWorkTitle(),
				"workNumber", r.getWorkNumber(),
				"workSchedule", DateUtilities.format("MM/dd/YYYY", r.getWorkSchedule().getFrom(), getCurrentUser().getTimeZoneId()),
				"ratingCode", RatingStarsHelper.getRatingCode(r.getRatingValue() == null ? 0 : r.getRatingValue()),
				"qualityCode", RatingStarsHelper.getRatingCode(r.getQualityValue() == null ? 0 : r.getQualityValue()),
				"professionalismCode", RatingStarsHelper.getRatingCode(r.getProfessionalismValue() == null ? 0 : r.getProfessionalismValue()),
				"communicationCode", RatingStarsHelper.getRatingCode(r.getCommunicationValue() == null ? 0 : r.getCommunicationValue()),
				"ratingValue", RatingStarsHelper.getLevels(r.getRatingValue() == null ? 0 : r.getRatingValue()),
				"qualityValue", RatingStarsHelper.getLevels(r.getQualityValue() == null ? 0 : r.getQualityValue()),
				"professionalismValue", RatingStarsHelper.getLevels(r.getProfessionalismValue() == null ? 0 : r.getProfessionalismValue()),
				"communicationValue", RatingStarsHelper.getLevels(r.getCommunicationValue() == null ? 0 : r.getCommunicationValue()),
				"ratingReview", r.getRatingReview(),
				"resourceLabels", CollectionUtilities.extractKeyValues(r.getResourceLabels(), "workResourceLabelType.code", "workResourceLabelType.description")
			);

			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}

}
