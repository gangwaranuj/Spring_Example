package com.workmarket.web.controllers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.DefaultBackgroundImage;
import com.workmarket.domains.model.asset.type.UserAssetAssociationType;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.service.dashboard.WorkDashboardService;
import com.workmarket.dto.TalentPoolMembershipDTO;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.request.work.WorkSearchType;
import com.workmarket.search.response.work.DashboardResponseSidebar;
import com.workmarket.search.response.work.DashboardStatus;
import com.workmarket.service.admin.DefaultBackgroundImageService;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.validators.FeedRequestParamsValidator;
import com.workmarket.web.validators.FilenameValidator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.workmarket.utility.CollectionUtilities.newObjectMap;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/home")
public class HomeController extends BaseController {

	@Autowired private ProfileService profileService;
	@Autowired private CompanyService companyService;
	@Autowired private RatingService ratingService;
	@Autowired private WorkDashboardService workDashboardService;
	@Autowired private BillingService billingService;
	@Autowired private RequestService requestService;
	@Autowired private UserGroupService groupService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private DefaultBackgroundImageService defaultBackgroundImageService;
	@Autowired private FilenameValidator filenameValidator;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private AnalyticsService analyticsService;
	@Autowired private BankingService bankingService;
	@Autowired private UserService userService;
	@Autowired private LaneService laneService;
	@Autowired private VendorService vendorService;

	@RequestMapping(
		method = GET
	)
	public String index(
		Model model,
		HttpServletRequest request,
		SitePreference site,
		HttpServletResponse httpResponse) throws Exception {

		if (isMobile(request, site)) {
			return "redirect:/mobile";
		}

		// Fake that existing users already saw the welcome tour, since it'll be annoying to them to see it now
		if (userService.findUserById(getCurrentUser().getId()).getCreatedOn().before(DateUtilities.getCalendarFromString("2013-01-10"))) {
			Cookie cookie = new Cookie("welcome-tour", "ridden");
			cookie.setMaxAge(Integer.MAX_VALUE);
			httpResponse.addCookie(cookie);
		}

		if (getCurrentUser().isBuyer()) {
			// Profile box
			model.addAttribute("companyAvatars", companyService.findCompanyAvatars(getCurrentUser().getCompanyId()));

			// People box
			model.addAttribute("pendingGroupApplicationsCount", groupService.countPendingMembershipsByCompany(getCurrentUser().getCompanyId()));

			// Payments box
			model.addAttribute("paymentsDueCount", billingService.countAllDueWorkByCompany(getCurrentUser().getCompanyId()));

			//Company On-boarding Progress Bar
			model.addAttribute("renderCompanyOnboardingProgress", companyService.isApplicableToRenderOnboardingProgress(getCurrentUser().getCompanyId()));
		}

		if (getCurrentUser().isSeller()) {
			// Profile box
			Optional<PostalCode> postalCode = profileService.findPostalCodeForUser(getCurrentUser().getId());
			if (postalCode.isPresent()) {
				model.addAttribute("postalCode", postalCode.get());
			}

			model.addAttribute("profileCompleteness", profileService.getUserProfileCompleteness(getCurrentUser().getId()));
			model.addAttribute("averageRating", ratingService.findSatisfactionRateForUser(getCurrentUser().getId()));
			model.addAttribute("totalRating", ratingService.countAllUserRatings(getCurrentUser().getId()));

			// Money box
			model.addAttribute("available_balance", accountRegisterServicePrefundImpl.calculateWithdrawableCashByCompany(getCurrentUser().getCompanyId()));

			// GCC card banner message
			boolean hasGccAccount = bankingService.hasGCCAccount(getCurrentUser().getId());
			boolean showCreateGccAccountBanner = (!hasGccAccount && Boolean.TRUE.equals(getCurrentUser().isSeller()));
			model.addAttribute("showGccBanner", showCreateGccAccountBanner);
			model.addAttribute("isLane3Active", laneService.isLane3Active(getCurrentUser().getId()));

			// Fast Funds notice
			model.addAttribute("hasFastFunds", billingService.hasAtLeastOneFastFundableInvoice(getCurrentUser().getId()));
		}

		// People Box
		if (getCurrentUser().isDispatcher()) {
			TalentPoolMembershipDTO dto = vendorService.getAllVendorUserGroupMemberships(getCurrentUser().getId());
			model.addAttribute("groupInvitationsCount", dto.getInvitations().size());
			// Fast Funds notice
			model.addAttribute("hasFastFunds", billingService.hasAtLeastOneFastFundableInvoice(getCurrentUser().getId()));
		} else if (getCurrentUser().isSeller()) {
			model.addAttribute("groupInvitationsCount", requestService.countUserGroupInvitationsByInvitedUser(getCurrentUser().getId()));
		}

		// Work box
		WorkSearchRequest searchRequest = new WorkSearchRequest(getCurrentUser().getUserNumber());
		searchRequest.setWorkSearchRequestUserType(WorkSearchRequestUserType.RESOURCE);
		searchRequest.setWorkSearchType(WorkSearchType.HOMEPAGE_SYSTEM);
		DashboardResponseSidebar dashboardSidebar = workDashboardService.generateWorkDashboardSidebar(searchRequest);

		Map<String, DashboardStatus> statuses = dashboardSidebar.getDashboardStatuses();
		Calendar now = DateUtilities.getCalendarNow();

		model.addAttribute("workAvailableCount", statusHasWorkCount(statuses, WorkStatusType.AVAILABLE));
		model.addAttribute("workAlertsCount", statusHasWorkCount(statuses, WorkStatusType.EXCEPTION));
		model.addAttribute("workPendingApprovalCount", statusHasWorkCount(statuses, WorkStatusType.COMPLETE));
		model.addAttribute("memberSince", userService.findUserById(getCurrentUser().getId()).getCreatedOn());
		model.addAttribute("spendLimit", getSpendLimit());
		model.addAttribute("apLimit", getAPLimit());
		model.addAttribute("buyerScoreCard", analyticsService.getBuyerScoreCardByUserId(getCurrentUser().getId()));
		model.addAttribute("resourceScoreCard", analyticsService.getResourceScoreCard(getCurrentUser().getId()));
		model.addAttribute("last90Start", DateUtilities.lastNDaysMidnight(90));
		model.addAttribute("last90End", now);
		model.addAttribute("userHasLockedCompany", getCurrentUser().getCompanyIsLocked());
		model.addAttribute("promoDismissed", userService.findPromoDismissed());

		MboProfile mboProfile = getCurrentUser().getMboProfile();
		if (mboProfile != null) {
			model.addAttribute("mboProfile", mboProfile);
		}

		model.addAttribute("feedValidationConstants", FeedRequestParamsValidator.VALIDATION_CONSTANTS);
		return "web/pages/home/index";
	}

	@RequestMapping(
		value = "/change_background",
		method = GET
	)
	public String changeBackground(Model model) {

		List<DefaultBackgroundImage> backgroundImages = defaultBackgroundImageService.getAll();
		model.addAttribute("backgroundImages", backgroundImages);
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/"));

		return "web/partials/home/change_background";
	}

	@RequestMapping(
		value  = "/change_background",
		method = POST
	)
	public @ResponseBody void changeBackground(@RequestParam("assetId") Long assetId) {

		Asset backgroundImage = assetManagementService.findAssetById(assetId);
		assetManagementService.changeUserBackgroundImage(getCurrentUser().getId(), backgroundImage);
		authenticationService.refreshSessionForUser(getCurrentUser().getId());
	}

	@RequestMapping(
		value = "/upload_background_image",
		method = POST,
		produces = APPLICATION_JSON_VALUE,
		consumes = APPLICATION_OCTET_STREAM_VALUE
	)
	public @ResponseBody Map<String, Object> uploadBackgroundImage(
		@RequestParam("qqfile") String qqfile,
		HttpServletRequest request) {

		String fileName = StringUtilities.urlDecode(request.getHeader("X-File-Name"));
		MapBindingResult bind = new MapBindingResult(Maps.newHashMap(), "fileName");
		filenameValidator.validate(fileName, bind);
		if (bind.hasErrors()) {
			return newObjectMap(
				"successful", false,
				"errors", messageHelper.getAllErrors(bind)
			);
		}

		String contentType = MimeTypeUtilities.guessMimeType(fileName);

		if (!MimeTypeUtilities.isImage(contentType)) {
			return newObjectMap(
				"successful", false,
				"errors", messageHelper.getMessage("upload.invalid", contentType)
			);
		}

		try {
			File tmpFile = File.createTempFile(qqfile, null);
			FileUtils.copyInputStreamToFile(request.getInputStream(), tmpFile);

			AssetDTO dto = new AssetDTO();
			dto.setSourceFilePath(tmpFile.getAbsolutePath());
			dto.setName(fileName);
			dto.setMimeType(contentType);
			dto.setAssociationType(UserAssetAssociationType.BACKGROUND_IMAGE);

			Asset asset = assetManagementService.storeAssetForUser(dto, getCurrentUser().getId());
			authenticationService.refreshSessionForUser(getCurrentUser().getId());

			return newObjectMap(
				"successful", true,
				"uri", asset.getUri()
			);

		} catch (Exception e) {
			return newObjectMap(
				"successful", false,
				"errors", messageHelper.getMessage("upload.IOException", contentType)
			);
		}
	}

	@RequestMapping(
		value = "/upload_background_image",
		method = POST,
		produces = TEXT_HTML_VALUE,
		consumes = MULTIPART_FORM_DATA_VALUE
	)
	public @ResponseBody String uploadBackgroundImageForIE(
		@RequestParam("qqfile") MultipartFile attachment) {
		// For IE
		String fileName = attachment.getOriginalFilename();

		MapBindingResult bind = new MapBindingResult(Maps.newHashMap(), "fileName");
		filenameValidator.validate(fileName, bind);
		if (bind.hasErrors()) {
			return new JSONObject(newObjectMap(
				"successful", false,
				"errors", messageHelper.getAllErrors(bind)
			)).toString();
		}

		String contentType = attachment.getContentType();

		if (!MimeTypeUtilities.isImage(contentType)) {
			return new JSONObject(newObjectMap(
				"successful", false,
				"errors", messageHelper.getMessage("upload.invalid", contentType)
			)).toString();
		}

		try {
			File tmpFile = File.createTempFile(attachment.toString(), null);
			FileUtils.copyInputStreamToFile(attachment.getInputStream(), tmpFile);

			AssetDTO dto = new AssetDTO();
			dto.setSourceFilePath(tmpFile.getAbsolutePath());
			dto.setName(fileName);
			dto.setMimeType(contentType);
			dto.setAssociationType(UserAssetAssociationType.BACKGROUND_IMAGE);

			Asset asset = assetManagementService.storeAssetForUser(dto, getCurrentUser().getId());
			authenticationService.refreshSessionForUser(getCurrentUser().getId());

			return new JSONObject(CollectionUtilities.newObjectMap(
				"successful", true,
				"uri", asset.getUri()
			)).toString();
		} catch (Exception e) {
			// This new JSONObject nonsense is necessary in order to
			// make sure a 406 is not sent.
			// If someone knows a better way, please advise.
			return new JSONObject(CollectionUtilities.newObjectMap(
				"successful", false,
				"errors", messageHelper.getMessage("upload.IOException", contentType)
			)).toString();
		}
	}

	private int statusHasWorkCount(Map<String,DashboardStatus> statuses, String status) {
		return MapUtils.getInteger(statuses, status, 0);
	}
}
