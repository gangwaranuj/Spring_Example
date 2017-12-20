
package com.workmarket.web.controllers.admin;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.insurance.Insurance;
import com.workmarket.domains.model.insurance.InsurancePagination;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.insurance.UserInsuranceAssociationPagination;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CommentService;
import com.workmarket.service.business.InsuranceService;
import com.workmarket.service.business.MessagingService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.business.dto.InsuranceDTO;
import com.workmarket.service.business.dto.UserCommentDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.UserInsuranceForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Controller
@RequestMapping("/admin/insurance")
public class InsuranceController extends BaseController {

	private static final Log logger = LogFactory.getLog(InsuranceController.class);

	@Autowired private InsuranceService insuranceService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private MessagingService messagingService;
	@Autowired private CommentService commentService;
	@Autowired private UserService userService;

	@RequestMapping(
		value = "/review",
		method = GET)
	public String review() {

		return "web/pages/admin/insurance/review";

	}

	@RequestMapping(
		value = "/unverified_userinsurance",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public ModelAndView unverifiedUserinsurance(HttpServletRequest httpRequest) {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, UserInsuranceAssociationPagination.SORTS.VERIFICATION_STATUS.toString());
			put(1, UserInsuranceAssociationPagination.SORTS.CREATED_DATE.toString());
			put(2, UserInsuranceAssociationPagination.SORTS.USER_FIRST_NAME.toString());
			put(3, UserInsuranceAssociationPagination.SORTS.PROVIDER.toString());
			put(6, UserInsuranceAssociationPagination.SORTS.ISSUE_DATE.toString());
			put(7, UserInsuranceAssociationPagination.SORTS.EXPIRATION_DATE.toString());
			put(8, UserInsuranceAssociationPagination.SORTS.LAST_ACTIVITY_DATE.toString());
		}});
		UserInsuranceAssociationPagination pagination = new UserInsuranceAssociationPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());

		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		String sSearch = httpRequest.getParameter("sSearch");
		if (!sSearch.isEmpty())
			filterBy(pagination, "user", sSearch);

		String status = (httpRequest.getParameter("status") != null) ? httpRequest.getParameter("status") : "";
		filterBy(pagination, "status", status);

		ModelAndView modelAndView = new ModelAndView("web/pages/insurance/unverified_userinsurance");
		try {
			UserInsuranceAssociationPagination results = insuranceService.findAllUserInsuranceAssociations(pagination);
			DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, results);
			List<UserInsuranceAssociation> userInsuranceAssocList = results.getResults();
			for (UserInsuranceAssociation item : userInsuranceAssocList) {
				Insurance insurance = item.getInsurance();
				User user = item.getUser();

				String timeZone = getCurrentUser().getTimeZoneId();

				List<String> data = Lists.newArrayList(
					item.getVerificationStatus().name(),
					DateUtilities.format("M/d/yyyy", item.getCreatedOn(), timeZone),
					user.getFullName(),
					item.getProvider(),
					insurance.getName(),
					"",
					((item.getIssueDate() != null) ? DateUtilities.format("M/d/yyyy", item.getIssueDate()) : ""),
					((item.getExpirationDate() != null) ? DateUtilities.format("M/d/yyyy", item.getExpirationDate()) : ""),
					((item.getLastActivityOn() != null) ? DateUtilities.format("M/d/yyyy", item.getLastActivityOn(), timeZone) : ""),
					""
				);

				List<Asset> assets = new ArrayList<>();
				for (Asset a : item.getAssets()) {
					assets.add(a);
				}
				Asset asset = (!assets.isEmpty()) ? assets.get(0) : null;

				Map<String, Object> meta = CollectionUtilities.newObjectMap(
					"id", item.getId(),
					"user_id", user.getId(),
					"user_number", user.getUserNumber(),
					"attachment_relative_uri", (asset != null) ? asset.getUri() : null,
					"number", item.getPolicyNumber(),
					"coverage", item.getCoverage()
				);
				response.addRow(data, meta);
			}
			modelAndView.addObject("response", response);
		} catch (Exception e) {
			logger.error(e);
		}
		return modelAndView;
	}

	@RequestMapping(
		value = "/unverified_insurance",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public ModelAndView unverifiedInsurance(HttpServletRequest httpRequest) {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, InsurancePagination.SORTS.VERIFICATION_STATUS.toString());
			put(1, InsurancePagination.SORTS.CREATED_DATE.toString());
			put(2, InsurancePagination.SORTS.USER_FIRST_NAME.toString());
			put(3, InsurancePagination.SORTS.INSURANCE_NAME.toString());
			put(4, InsurancePagination.SORTS.INDUSTRY.toString());
			put(5, InsurancePagination.SORTS.LAST_ACTIVITY_DATE.toString());
		}});
		InsurancePagination pagination = new InsurancePagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());

		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		String status = (httpRequest.getParameter("status") != null) ? httpRequest.getParameter("status") : "";
		filterBy(pagination, "status", status);

		ModelAndView modelAndView = new ModelAndView("web/pages/insurance/unverified_insurance");

		try {
			InsurancePagination results = insuranceService.findAllInsurances(pagination);
			DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, results);
			List<Insurance> insuranceList = results.getResults();
			Map<Long, Map<String, Object>> creatorProps = userService.getProjectionMapByIds(CollectionUtilities.newListPropertyProjection(insuranceList, "creatorId"),
				"firstName", "lastName", "userNumber");

			for (Insurance i : insuranceList) {

				String timeZone = getCurrentUser().getTimeZoneId();

				Calendar createdDate = i.getCreatedOn();
				String createdDateStr = null;
				if (createdDate != null)
					createdDateStr = DateUtilities.format("M/d/yyyy", createdDate, timeZone);
				else
					createdDateStr = DateUtilities.format("M/d/yyyy", Calendar.getInstance(), timeZone);

				Calendar lastActivityOn = i.getLastActivityOn();
				String lastActivityOnStr = null;
				if (lastActivityOn != null)
					lastActivityOnStr = DateUtilities.format("M/d/yyyy", lastActivityOn, timeZone);
				else
					lastActivityOnStr = DateUtilities.format("M/d/yyyy", Calendar.getInstance(), timeZone);

				List<String> data = Lists.newArrayList(
					i.getVerificationStatus().name(),
					createdDateStr,
					StringUtilities.fullName((String) creatorProps.get(i.getCreatorId()).get("firstName"), (String) creatorProps.get(i.getCreatorId()).get("lastName")),
					i.getName(),
					i.getIndustry().getName(),
					lastActivityOnStr,
					""
				);
				Map<String, Object> meta = CollectionUtilities.newObjectMap(
					"id", i.getId(),
					"user_number", creatorProps.get(i.getCreatorId()).get("userNumber")
				);
				response.addRow(data, meta);
			}
			modelAndView.addObject("response", response);
		} catch (Exception e) {
			logger.error(e);
		}

		return modelAndView;
	}

	public void filterBy(AbstractPagination pagination, String key, String value) {
		if ("status".equals(key)) {
			int val = (!"".equals(value)) ? Integer.parseInt(value) : VerificationStatus.UNVERIFIED.ordinal();
			VerificationStatus enumValues[] = VerificationStatus.values();
			switch (enumValues[val]) {
				case PENDING:
					pagination.addFilter(UserInsuranceAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.PENDING);
					break;
				case VERIFIED:
					pagination.addFilter(UserInsuranceAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.VERIFIED);
					break;
				case FAILED:
					pagination.addFilter(UserInsuranceAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.FAILED);
					break;
				case PENDING_INFORMATION:
					pagination.addFilter(UserInsuranceAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.PENDING_INFORMATION);
					break;
				case ON_HOLD:
					pagination.addFilter(UserInsuranceAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.ON_HOLD);
					break;
				default:
					pagination.addFilter(UserInsuranceAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.UNVERIFIED);
			}
		} else {    //key must be "user"
			if (pagination instanceof UserInsuranceAssociationPagination)    //only implemented for UserInsuranceAssociationPagination
				pagination.addFilter(UserInsuranceAssociationPagination.FILTER_KEYS.USER_NAME, value);
		}
	}

	@RequestMapping(
		value = "/approveinsurance",
		method = GET)
	public void approveinsurance(@RequestParam("id") String id, Model model) {

		String[] strArr = id.split("_");
		String insuranceId = strArr[1];
		boolean isSuccess = false;

		if (!insuranceId.isEmpty() && StringUtils.isNumeric(insuranceId)) {
			try {
				insuranceService.verifyInsurance(Long.parseLong(insuranceId));

				isSuccess = true;
			} catch (Exception e) {
				logger.error(e);
			}
		}

		model.addAttribute("response", CollectionUtilities.newObjectMap(
			"success", isSuccess
		));
	}

	@RequestMapping(
		value = "/declineinsurance",
		method = GET)
	public void declineinsurance(@RequestParam("id") String id, Model model) {

		String[] strArr = id.split("_");
		String insuranceId = strArr[1];
		boolean isSuccess = false;

		if (!insuranceId.isEmpty() && StringUtils.isNumeric(insuranceId)) {
			try {
				insuranceService.rejectInsurance(Long.parseLong(insuranceId));

				isSuccess = true;
			} catch (Exception e) {
				logger.error(e);
			}
		}

		model.addAttribute("response", CollectionUtilities.newObjectMap(
			"success", isSuccess
		));
	}

	@RequestMapping(
		value = "/onholdinsurance",
		method = GET)
	public void onholdinsurance(@RequestParam("id") String id, Model model) {

		String[] strArr = id.split("_");
		String insuranceId = strArr[1];
		boolean isSuccess = false;

		if (!insuranceId.isEmpty() && StringUtils.isNumeric(insuranceId)) {
			try {
				insuranceService.updateInsuranceVerificationStatus(Long.parseLong(insuranceId), VerificationStatus.ON_HOLD);

				isSuccess = true;
			} catch (Exception e) {
				logger.error(e);
			}
		}

		model.addAttribute("response", CollectionUtilities.newObjectMap(
			"success", isSuccess
		));
	}

	@RequestMapping(
		value = "/approve_userinsurance",
		method = GET)
	public ModelAndView approveUserinsurance() {

		ModelAndView m = new ModelAndView("web/pages/insurance/approve_userinsurance");

		return m;
	}

	@RequestMapping(
		value = "/decline_userinsurance",
		method = GET)
	public ModelAndView declineUserinsurance() {

		ModelAndView m = new ModelAndView("web/pages/insurance/decline_userinsurance");

		return m;
	}

	@RequestMapping(
		value = "/onhold_userinsurance",
		method = GET)
	public ModelAndView onholdUserinsurance() {

		ModelAndView m = new ModelAndView("web/pages/insurance/onhold_userinsurance");

		return m;
	}

	@RequestMapping(
		value = "/edit_userinsurance",
		method = GET)
	public String editUserinsurance(
		@RequestParam(value = "id", required = false) Long id,
		@RequestParam(value = "user_id", required = false) Long userId, ModelMap model
	) {
		try {
			UserInsuranceAssociation userInsurance = insuranceService.findUserInsuranceAssociation(id);

			setUserInsuranceAttributes(id, userId, userInsurance, model);

		} catch (Exception e) {
			logger.error(e);
		}

		return "web/pages/admin/insurance/edit_userinsurance";
	}

	private void setUserInsuranceAttributes(Long id, Long userId, UserInsuranceAssociation userInsurance, ModelMap model) {
		User user = userService.findUserById(userId);
		model.addAttribute("user", user);

		String timeZone = getCurrentUser().getTimeZoneId();
		String issueDate = DateUtilities.format("MM/dd/yyyy", userInsurance.getIssueDate(), timeZone);
		model.addAttribute("issue_date", issueDate);
		String expDate = DateUtilities.format("MM/dd/yyyy", userInsurance.getExpirationDate(), timeZone);
		model.addAttribute("expiration_date", expDate);

		model.addAttribute("user_insurance", userInsurance);
		if (!userInsurance.getAssets().isEmpty())
			model.addAttribute("assets", userInsurance.getAssets());

		model.addAttribute("id", id);
		model.addAttribute("user_id", userId);
		model.addAttribute("workers_comp_insurance_id", Constants.WORKERS_COMP_INSURANCE_ID);
	}

	@RequestMapping(value = "/edit_userinsurance", method = RequestMethod.POST)
	public String editUserinsurance(
		@Valid @ModelAttribute("certs_form") UserInsuranceForm userInsuranceForm,
		BindingResult binding, RedirectAttributes redirectAttributes, ModelMap model
	) {

		MessageBundle bundle = messageHelper.newFlashBundle(redirectAttributes);
		if (!userInsuranceForm.isNotApplicableOverride()) {
			if (StringUtils.isEmpty(userInsuranceForm.getProvider())) {
				messageHelper.addError(bundle, "Provider is required.");
			}
			if (StringUtils.isEmpty(userInsuranceForm.getCoverage())) {
				messageHelper.addError(bundle, "Coverage is required.");
			}
			if (StringUtils.isEmpty(userInsuranceForm.getPolicyNumber())) {
				messageHelper.addError(bundle, "Policy Number is required.");
			}
		}

		if (bundle.hasErrors()) {
			return "redirect:/admin/insurance/edit_userinsurance?id=" + userInsuranceForm.getId() + "&user_id=" + userInsuranceForm.getUserId();
		}

		UserInsuranceAssociation userInsurance = null;
		try {
			userInsurance = insuranceService.findUserInsuranceAssociation(userInsuranceForm.getId());

			if (userInsurance != null) {
				userInsurance.setPolicyNumber(userInsuranceForm.getPolicyNumber());
				userInsurance.setIssueDate(userInsuranceForm.getIssueDate());
				userInsurance.setExpirationDate(userInsuranceForm.getExpirationDate());
				userInsurance.setProvider(userInsuranceForm.getProvider());
				userInsurance.setCoverage(userInsuranceForm.getCoverage());
				userInsurance.setNotApplicableOverride(userInsurance.isNotApplicableOverride());
			} else {
				messageHelper.addError(bundle, "insurance.admin.userins.edit.cantfind");
				redirectAttributes.addFlashAttribute("bundle", bundle);
				return "redirect:/admin/insurance/review";
			}
			UserInsuranceAssociation results = insuranceService.updateUserInsuranceAssociation(userInsurance.getId(), toUserInsuranceDTO(userInsurance));

			if (!userInsuranceForm.getFile().isEmpty()) {
				AssetDTO asset = createAssetDTO(userInsuranceForm.getFile());
				assetManagementService.storeAssetForUserInsurance(asset, results.getId());
			}

			updateUserInsuranceStatus(results.getId(), userInsuranceForm.getAction());

			if (
				("decline".equals(userInsuranceForm.getAction()) || "need_info".equals(userInsuranceForm.getAction())) &&
					StringUtils.isNotEmpty(userInsuranceForm.getNote())
				) {
				String subject = null;

				if ("decline".equals(userInsuranceForm.getAction()))
					subject = messageHelper.getMessage("insurance.admin.userins.edit.decline");
				else
					subject = messageHelper.getMessage("insurance.admin.userins.edit.moreinfo");

				sendEmailToUsers(getCurrentUser().getId(), userInsuranceForm.getUserId(), subject, userInsuranceForm.getNote());

				UserCommentDTO dto = new UserCommentDTO();
				dto.setComment(userInsuranceForm.getNote());
				dto.setUserId(userInsuranceForm.getUserId());
				commentService.saveOrUpdateClientServiceUserComment(dto);
			}

			messageHelper.addSuccess(bundle, "insurance.admin.userins.edit.saved");
			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/insurance/review";
		} catch (Exception e) {
			logger.error(e);
		}

		setUserInsuranceAttributes(userInsuranceForm.getId(), userInsuranceForm.getUserId(), userInsurance, model);

		return "web/pages/admin/insurance/edit_userinsurance";
	}

	private InsuranceDTO toUserInsuranceDTO(UserInsuranceAssociation userInsurance) {
		InsuranceDTO dto = new InsuranceDTO();
		dto.setInsuranceId(userInsurance.getInsurance().getId());
		dto.setProvider(userInsurance.getProvider());
		dto.setPolicyNumber(userInsurance.getPolicyNumber());
		dto.setCoverage(userInsurance.getCoverage());
		if (userInsurance.getIssueDate() != null)
			dto.setIssueDate(userInsurance.getIssueDate());
		if (userInsurance.getExpirationDate() != null)
			dto.setExpirationDate(userInsurance.getExpirationDate());
		dto.setNotApplicableOverride(userInsurance.isNotApplicableOverride());
		return dto;
	}

	private AssetDTO createAssetDTO(MultipartFile file) throws Exception {
		File tempDest = File.createTempFile("insurance", ".dat");
		file.transferTo(tempDest);

		AssetDTO asset = new AssetDTO();
		asset.setSourceFilePath(tempDest.getPath());
		asset.setName(file.getOriginalFilename());
		asset.setMimeType(file.getContentType());
		asset.setAssociationType("none");
		asset.setLargeTransformation(false);
		asset.setSmallTransformation(false);
		asset.setActive(true);
		return asset;
	}

	private void updateUserInsuranceStatus(Long id, String status) throws Exception {
		VerificationStatus vstatus = null;

		if ("approve".equals(status)) {
			vstatus = VerificationStatus.VERIFIED;
		} else if ("decline".equals(status)) {
			vstatus = VerificationStatus.FAILED;
		} else if ("need_info".equals(status)) {
			vstatus = VerificationStatus.PENDING_INFORMATION;
		} else if ("unverified".equals(status)) {
			vstatus = VerificationStatus.UNVERIFIED;
		} else if ("on_hold".equals(status)) {
			vstatus = VerificationStatus.ON_HOLD;
		}

		insuranceService.updateUserInsuranceVerificationStatus(id, vstatus);
	}

	private void sendEmailToUsers(Long fromUserId, Long toUserId, String subject, String message) {
		EMailDTO dto = new EMailDTO();
		dto.setFromId(fromUserId);
		dto.setSubject(subject);
		dto.setText(message);

		messagingService.sendEmailToUsers(fromUserId, new Integer[]{toUserId.intValue()}, dto);
	}
}
