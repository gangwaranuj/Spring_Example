
package com.workmarket.web.controllers.admin;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.insurance.UserInsuranceAssociationPagination;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.license.LicensePagination;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociationPagination;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CommentService;
import com.workmarket.service.business.LicenseService;
import com.workmarket.service.business.MessagingService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.business.dto.LicenseDTO;
import com.workmarket.service.business.dto.StateDTO;
import com.workmarket.service.business.dto.UserCommentDTO;
import com.workmarket.service.business.dto.UserLicenseDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.LicensesForm;
import com.workmarket.web.forms.UserLicenseForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping("/admin/licenses")
public class LicensesController extends BaseController {

	private static final Log logger = LogFactory.getLog(LicensesController.class);

	@Autowired private LicenseService licenseService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private MessagingService messagingService;
	@Autowired private CommentService commentService;
	@Autowired private UserService userService;

	@RequestMapping(
		value = "/add",
		method = RequestMethod.GET)
	public String add(ModelMap model) {
		List<StateDTO> statesList = invariantDataService.getStateDTOs();
		List<Map> states = new ArrayList<>();
		for (StateDTO s : statesList) {
			Map<String, String> table = new HashMap<>();
			table.put("id", s.getShortName());
			table.put("name", s.getName());
			states.add(table);
		}
		model.addAttribute("states", states);

		return "web/pages/admin/licenses/add";
	}

	@RequestMapping(
		value = "/add",
		method = RequestMethod.POST)
	public String add(@Valid @ModelAttribute("form_licensesadd") LicensesForm licensesForm,
	                  BindingResult binding, RedirectAttributes redirectAttributes) {

		MessageBundle bundle = MessageBundle.newInstance();

		if (binding.hasFieldErrors()) {
			messageHelper.setErrors(bundle, binding);

		} else {
			LicenseDTO dto = new LicenseDTO();
			dto.setName(licensesForm.getName());
			dto.setState(licensesForm.getState());

			try {
				licenseService.saveOrUpdateLicense(dto);

				messageHelper.addSuccess(bundle, "licenses.admin.lic.add.success");
			} catch (Exception e) {
				logger.error(e);
			}
		}
		redirectAttributes.addFlashAttribute("bundle", bundle);

		return "redirect:/admin/licenses/add";
	}

	@RequestMapping(value = "/review", method = RequestMethod.GET)
	public ModelAndView review() {
		return new ModelAndView("web/pages/admin/licenses/review");
	}

	@RequestMapping(value = "/unverified_userlicenses", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView unverifiedUserlicenses(HttpServletRequest httpRequest) {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, UserLicenseAssociationPagination.SORTS.VERIFICATION_STATUS.toString());
			put(1, UserLicenseAssociationPagination.SORTS.CREATED_DATE.toString());
			put(2, UserLicenseAssociationPagination.SORTS.USER_FIRST_NAME.toString());
			put(3, UserLicenseAssociationPagination.SORTS.STATE.toString());
			put(4, UserLicenseAssociationPagination.SORTS.LICENSE_NAME.toString());
			put(5, UserLicenseAssociationPagination.SORTS.LICENSE_NUMBER.toString());
			put(6, UserLicenseAssociationPagination.SORTS.LAST_ACTIVITY_DATE.toString());
		}});
		UserLicenseAssociationPagination pagination = new UserLicenseAssociationPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());

		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		String sSearch = httpRequest.getParameter("sSearch");
		if (!sSearch.isEmpty())
			filterBy(pagination, "user", sSearch);

		String status = (httpRequest.getParameter("status") != null) ? httpRequest.getParameter("status") : "";
		filterBy(pagination, "status", status);

		ModelAndView m = new ModelAndView("web/pages/licenses/unverified_userlicenses");

		UserLicenseAssociationPagination results = licenseService.findAllUserLicenseAssociations(pagination);
		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, results);
		List<UserLicenseAssociation> userLicenseAssocList = results.getResults();
		Map<Long, Map<String, Object>> creatorProps = userService.getProjectionMapByIds(CollectionUtilities.newListPropertyProjection(userLicenseAssocList, "creatorId"),
				"firstName", "lastName", "userNumber");

		for (UserLicenseAssociation item : userLicenseAssocList) {

			String timeZone = getCurrentUser().getTimeZoneId();

			List<String> data = Lists.newArrayList(
					item.getVerificationStatus().name(),
					DateUtilities.format("M/d/yyyy", item.getCreatedOn(), timeZone),
					StringUtilities.fullName((String) creatorProps.get(item.getCreatorId()).get("firstName"), (String) creatorProps.get(item.getCreatorId()).get("lastName")),
					item.getLicense().getState(),
					item.getLicense().getName(),
					item.getLicenseNumber(),
					((item.getLastActivityOn() != null) ? DateUtilities.format("M/d/yyyy", item.getLastActivityOn(), timeZone) : ""),
					""
			);

			List<Asset> assets = new ArrayList<Asset>();
			for (Asset a : item.getAssets()) {
				assets.add(a);
			}
			Asset asset = (!assets.isEmpty()) ? assets.get(0) : null;

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
					"id", item.getLicense().getId(),
					"user_id", item.getUser().getId(),
					"user_number", creatorProps.get(item.getCreatorId()).get("userNumber"),
					"association_id", item.getId(),
					"attachment_relative_uri", (asset != null) ? asset.getUri() : null
			);

			response.addRow(data, meta);
		}
		m.addObject("response", response);

		return m;
	}

	@RequestMapping(value = "/unverified_license", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView unverifiedLicense(HttpServletRequest httpRequest) {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, LicensePagination.SORTS.VERIFICATION_STATUS.toString());
			put(1, LicensePagination.SORTS.CREATED_DATE.toString());
			put(2, LicensePagination.SORTS.USER_FIRST_NAME.toString());
			put(3, LicensePagination.SORTS.STATE.toString());
			put(4, LicensePagination.SORTS.LICENSE_NAME.toString());
			put(5, LicensePagination.SORTS.LAST_ACTIVITY_DATE.toString());
		}});

		LicensePagination pagination = new LicensePagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());

		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		String status = (httpRequest.getParameter("status") != null) ? httpRequest.getParameter("status") : "";
		filterBy(pagination, "status", status);

		ModelAndView m = new ModelAndView("web/pages/licenses/unverified_license");

		LicensePagination results = licenseService.findAllLicenses(pagination);
		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, results);
		List<License> licenseList = results.getResults();

		Map<Long, Map<String, Object>> creatorProps = userService.getProjectionMapByIds(CollectionUtilities.newListPropertyProjection(licenseList, "creatorId"),
				"firstName", "lastName", "userNumber");

		for (License l : licenseList) {
			String timeZone = getCurrentUser().getTimeZoneId();

			Calendar createdDate = l.getCreatedOn();
			String createdDateStr = null;
			if (createdDate != null)
				createdDateStr = DateUtilities.format("M/d/yyyy", createdDate, timeZone);
			else
				createdDateStr = DateUtilities.format("M/d/yyyy", Calendar.getInstance(), timeZone);

			Calendar lastActivityOn = l.getLastActivityOn();
			String lastActivityOnStr = null;
			if (lastActivityOn != null)
				lastActivityOnStr = DateUtilities.format("M/d/yyyy", lastActivityOn, timeZone);
			else
				lastActivityOnStr = DateUtilities.format("M/d/yyyy", Calendar.getInstance(), timeZone);

			List<String> data = Lists.newArrayList(
					l.getVerificationStatus().name(),
					createdDateStr,
					StringUtilities.fullName((String) creatorProps.get(l.getCreatorId()).get("firstName"), (String) creatorProps.get(l.getCreatorId()).get("lastName")),
					l.getState(),
					l.getName(),
					lastActivityOnStr,
					""
			);
			Map<String, Object> meta = CollectionUtilities.newObjectMap(
					"id", l.getId(),
					"creator_user_number", creatorProps.get(l.getCreatorId()).get("userNumber")
			);
			response.addRow(data, meta);
		}
		m.addObject("response", response);

		return m;
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
		value = "/approvelicense",
		method = RequestMethod.POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map approvelicense(
		@RequestBody String id,
		HttpServletResponse response) {

		String[] strArr = id.split("_");
		String licenseId = strArr[1];

		if (!licenseId.isEmpty() && StringUtils.isNumeric(licenseId)) {
			try {
				licenseService.verifyLicense(Long.parseLong(licenseId));
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				logger.error(e);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		return CollectionUtilities.newObjectMap();
	}

	@RequestMapping(
		value = "/declinelicense",
		method = RequestMethod.POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map declinelicense(
		@RequestBody String id,
		HttpServletResponse response) {

		String[] strArr = id.split("_");
		String licenseId = strArr[1];

		if (!licenseId.isEmpty() && StringUtils.isNumeric(licenseId)) {
			try {
				licenseService.rejectLicense(Long.parseLong(licenseId));
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				logger.error(e);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		return CollectionUtilities.newObjectMap();
	}

	@RequestMapping(value = "/unverifiedlicense", method = RequestMethod.GET)
	public ModelAndView unverifiedlicense() {

		ModelAndView m = new ModelAndView("web/pages/licenses/unverifiedlicense");

		return m;
	}

	@RequestMapping(
		value = "/onholdlicense",
		method = RequestMethod.POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map onholdlicense(
		@RequestBody String id,
		HttpServletResponse response) {

		String[] strArr = id.split("_");
		String licenseId = strArr[1];

		if (!licenseId.isEmpty() && StringUtils.isNumeric(licenseId)) {
			try {
				licenseService.updateLicenseVerificationStatus(Long.parseLong(licenseId), VerificationStatus.ON_HOLD);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				logger.error(e);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		return CollectionUtilities.newObjectMap();
	}

	@RequestMapping(value = "/approve_userlicense", method = RequestMethod.GET)
	public ModelAndView approveUserlicense() {

		ModelAndView m = new ModelAndView("web/pages/licenses/approve_userlicense");

		return m;
	}

	@RequestMapping(value = "/decline_userlicense", method = RequestMethod.GET)
	public ModelAndView declineUserlicense() {

		ModelAndView m = new ModelAndView("web/pages/licenses/decline_userlicense");

		return m;
	}

	@RequestMapping(value = "/onhold_userlicense", method = RequestMethod.GET)
	public ModelAndView onholdUserlicense() {

		ModelAndView m = new ModelAndView("web/pages/licenses/onhold_userlicense");

		return m;
	}

	@RequestMapping(value = "/edit_userlicense", method = RequestMethod.GET)
	public String editUserlicense(@RequestParam(value = "id", required = false) Long id,
	                              @RequestParam(value = "user_id", required = false) Long userId, ModelMap model) {

		UserLicenseAssociation userLicense = licenseService.findAssociationByLicenseIdAndUserId(id, userId);

		setUserLicenseAttributes(id, userId, userLicense, model);

		return "web/pages/admin/licenses/edit_userlicense";
	}

	private void setUserLicenseAttributes(Long id, Long userId, UserLicenseAssociation userLicense, ModelMap model) {
		User user = userService.findUserById(userId);
		model.addAttribute("user_info", user);

		String timeZone = getCurrentUser().getTimeZoneId();
		String issueDate = DateUtilities.format("MM/dd/yyyy", userLicense.getIssueDate(), timeZone);
		model.addAttribute("issue_date", issueDate);
		String expDate = DateUtilities.format("MM/dd/yyyy", userLicense.getExpirationDate(), timeZone);
		model.addAttribute("expiration_date", expDate);

		model.addAttribute("user_license", userLicense);
		if (!userLicense.getAssets().isEmpty())
			model.addAttribute("assets", userLicense.getAssets());

		model.addAttribute("id", id);
		model.addAttribute("user_id", userId);
	}

	@RequestMapping(value = "/edit_userlicense", method = RequestMethod.POST)
	public String editUserlicense(@Valid @ModelAttribute("certs_form") UserLicenseForm userLicenseForm,
	                              RedirectAttributes redirectAttributes, ModelMap model) {

		MessageBundle bundle = MessageBundle.newInstance();

		UserLicenseAssociation userLicense = licenseService.findAssociationByLicenseIdAndUserId(userLicenseForm.getId(),
				userLicenseForm.getUserId());
		userLicense.setLicenseNumber(userLicenseForm.getLicenseNumber());
		userLicense.setIssueDate(userLicenseForm.getIssueDate());
		userLicense.setExpirationDate(userLicenseForm.getExpirationDate());

		try {
			UserLicenseAssociation results = licenseService.saveOrUpdateUserLicense(userLicense.getLicense().getId(),
					userLicense.getUser().getId(), toUserLicenseDTO(userLicense));

			if (!userLicenseForm.getFile().isEmpty()) {
				AssetDTO asset = createAssetDTO(userLicenseForm.getFile());
				assetManagementService.storeAssetForUserLicense(asset, results.getId());
			}

			updateUserLicenseStatus(userLicense.getLicense().getId(),
					userLicense.getUser().getId(), userLicenseForm.getAction());

			if ("decline".equals(userLicenseForm.getAction()) || "need_info".equals(userLicenseForm.getAction())) {

				if (userLicenseForm.getNote() != null && !"".equals(userLicenseForm.getNote())) {
					String subject = null;

					if ("decline".equals(userLicenseForm.getAction()))
						subject = messageHelper.getMessage("insurance.admin.userins.edit.decline");
					else
						subject = messageHelper.getMessage("insurance.admin.userins.edit.moreinfo");

					sendEmailToUsers(getCurrentUser().getId(), userLicenseForm.getUserId(), subject, userLicenseForm.getNote());

					UserCommentDTO dto = new UserCommentDTO();
					dto.setComment(userLicenseForm.getNote());
					dto.setUserId(userLicenseForm.getUserId());
					commentService.saveOrUpdateClientServiceUserComment(dto);
				}
			}

			messageHelper.addSuccess(bundle, "licenses.admin.userlic.edit.saved");
			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/licenses/review";

		} catch (Exception e) {
			logger.error(e);
		}

		setUserLicenseAttributes(userLicenseForm.getId(), userLicenseForm.getUserId(), userLicense, model);

		return "web/pages/admin/licenses/edit_userlicense";
	}

	private UserLicenseDTO toUserLicenseDTO(UserLicenseAssociation userLicense) {
		UserLicenseDTO dto = new UserLicenseDTO();
		if (userLicense.getIssueDate() != null)
			dto.setIssueDate(userLicense.getIssueDate());
		if (userLicense.getExpirationDate() != null)
			dto.setExpirationDate(userLicense.getExpirationDate());
		if (userLicense.getLicenseNumber() != null)
			dto.setLicenseNumber(userLicense.getLicenseNumber());
		return dto;
	}

	private AssetDTO createAssetDTO(MultipartFile file) throws IOException {
		File tempDest = File.createTempFile("license", ".dat");
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

	private void updateUserLicenseStatus(Long id, Long userId, String status) throws Exception {
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

		licenseService.updateUserLicenseAssociationStatus(id, userId, vstatus);
	}

	private void sendEmailToUsers(Long fromUserId, Long toUserId, String subject, String message) {
		EMailDTO dto = new EMailDTO();
		dto.setFromId(fromUserId);
		dto.setSubject(subject);
		dto.setText(message);

		messagingService.sendEmailToUsers(fromUserId, new Integer[]{toUserId.intValue()}, dto);
	}
}
