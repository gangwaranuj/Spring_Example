
package com.workmarket.web.controllers.admin;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.certification.Certification;
import com.workmarket.domains.model.certification.CertificationPagination;
import com.workmarket.domains.model.certification.CertificationVendor;
import com.workmarket.domains.model.certification.CertificationVendorPagination;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.certification.UserCertificationAssociationPagination;
import com.workmarket.service.business.CertificationService;
import com.workmarket.service.business.CommentService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.MessagingService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CertificationDTO;
import com.workmarket.service.business.dto.CertificationVendorDTO;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.business.dto.UserCertificationDTO;
import com.workmarket.service.business.dto.UserCommentDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/admin/certifications")
public class CertificationsController extends BaseController {

	@Autowired private CertificationService certificationService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private MessagingService messagingService;
	@Autowired private CommentService commentService;
	@Autowired private UserService userService;
	@Autowired private IndustryService industryService;

	@RequestMapping(
		value="/add",
		method = GET)
	public String add(Model model) {
		model.addAttribute("industries", industryService.getAllIndustryDTOs());
		return "web/pages/admin/certifications/add";
	}

	@RequestMapping(
		value="/add",
		method = POST)
	public String addSubmit(
		Model model,
		HttpServletRequest httpRequest,
		RedirectAttributes redirectAttributes) {

		MessageBundle bundle = messageHelper.newBundle();

		if (httpRequest.getParameter("provider").equals("other")) {
			try {
				CertificationVendorDTO dto = new CertificationVendorDTO();
				dto.setName(httpRequest.getParameter("custom_provider"));

				certificationService.saveOrUpdateCertificationVendor(dto, Long.parseLong(httpRequest.getParameter("industry")));
			} catch (Exception e) {
				messageHelper.addError(bundle, "admin.certifications.vendor_add.error");
				model.addAttribute("bundle", bundle);

				return this.add(model);
			}
		}

		try {
			CertificationDTO dtoCert = new CertificationDTO();
			dtoCert.setCertificationVendorId(Long.parseLong(httpRequest.getParameter("provider")));
			dtoCert.setName(httpRequest.getParameter("name"));

			Certification cert = certificationService.saveOrUpdateCertification(dtoCert);
			certificationService.verifyCertification(cert.getId());

			messageHelper.addSuccess(bundle, "admin.certifications.add.success");
			redirectAttributes.addFlashAttribute("bundle", bundle);

			return "redirect:/admin/certifications/add";
		} catch (Exception e) {
			messageHelper.addError(bundle, "admin.certifications.add.error");
			model.addAttribute("bundle", bundle);

			return this.add(model);
		}
	}

	@RequestMapping(
		value="/review",
		method = GET)
	public String review() {

		return "web/pages/admin/certifications/review";
	}

	@RequestMapping(
		value="/pending_vendors",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void pendingVendors(
		Model model,
		HttpServletRequest httpRequest) {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, CertificationVendorPagination.SORTS.VERIFICATION_STATUS.toString());
			put(1, CertificationVendorPagination.SORTS.CREATED_DATE.toString());
			put(2, CertificationVendorPagination.SORTS.USER_LAST_NAME.toString());
			put(3, CertificationVendorPagination.SORTS.VENDOR_NAME.toString());
			put(4, CertificationVendorPagination.SORTS.INDUSTRY.toString());
			put(5, CertificationVendorPagination.SORTS.LAST_ACTIVITY_DATE.toString());
		}});

		CertificationVendorPagination pagination = new CertificationVendorPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		if (StringUtilities.isNotEmpty(httpRequest.getParameter("status"))) {
			int val = Integer.parseInt(httpRequest.getParameter("status"));
			VerificationStatus enumValues[] = VerificationStatus.values();
			switch (enumValues[val])
			{
				case PENDING:
					pagination.addFilter(CertificationVendorPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.PENDING);
					break;
				case PENDING_INFORMATION:
					pagination.addFilter(CertificationVendorPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.PENDING_INFORMATION);
					break;
				case ON_HOLD:
					pagination.addFilter(CertificationVendorPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.ON_HOLD);
					break;
			}
		}

		pagination = certificationService.findAllCertificationVendors(pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);
		List<CertificationVendor> vendors = pagination.getResults();
		List<Long> ids = CollectionUtilities.newListPropertyProjection(vendors, "creatorId");
		Map<Long, Map<String, Object>> props = userService.getProjectionMapByIds(ids, "firstName", "lastName", "userNumber");

		for (CertificationVendor vendor : vendors) {
			String createdOn = DateUtilities.format("MM/dd/yyyy h:mm a", vendor.getCreatedOn(), getCurrentUser().getTimeZoneId());
			String lastActivityOn = DateUtilities.format("MM/dd/yyyy h:mm a", vendor.getLastActivityOn(), getCurrentUser().getTimeZoneId());
			Map<String, Object> creatorProps = props.get(vendor.getCreatorId());

			List<String> row = Lists.newArrayList(
				vendor.getVerificationStatus().name(),
				createdOn,
				StringUtilities.fullName((String)creatorProps.get("firstName"), (String)creatorProps.get("lastName")),
				vendor.getName(),
				vendor.getCertificationType().getName(),
				lastActivityOn
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", vendor.getId(),
				"user_id", vendor.getCreatorId(),
				"user_number", creatorProps.get("userNumber")
			);

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/editvendor",
		method = GET)
	public String editVendor(
		Model model,
		HttpServletRequest httpRequest,
		RedirectAttributes redirectAttributes) {

		if (httpRequest.getParameter("id").isEmpty()) {
			MessageBundle bundle = messageHelper.newBundle();
			messageHelper.addError(bundle, "admin.certifications.vendor_id.required");

			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/certifications/review";
		}

		CertificationVendor vendor = certificationService.findCertificationVendorById(Long.parseLong(httpRequest.getParameter("id")));

		if (vendor == null) {
			MessageBundle bundle = messageHelper.newBundle();
			messageHelper.addError(bundle, "admin.certifications.vendor_id.required");

			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/certifications/review";
		}

		model.addAttribute("id", vendor.getId())
			.addAttribute("vendor", vendor);

		return "web/pages/admin/certifications/editvendor";
	}

	@RequestMapping(
		value = "/editvendor",
		method = POST)
	public String editVendorSubmit(
		Model model,
		HttpServletRequest httpRequest,
		RedirectAttributes redirectAttributes) {

		MessageBundle bundle = messageHelper.newBundle();

		if (httpRequest.getParameter("id") == null || httpRequest.getParameter("id").isEmpty()) {
			messageHelper.addError(bundle, "admin.certifications.vendor_id.required");
			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/certifications/review";
		}

		CertificationVendor vendor = certificationService.findCertificationVendorById(Long.parseLong(httpRequest.getParameter("id")));

		if (vendor == null) {
			messageHelper.addError(bundle, "admin.certifications.vendor_id.required");
			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/certifications/review";
		}

		if (httpRequest.getParameter("currentvendor") == null || httpRequest.getParameter("currentvendor").isEmpty()) {
			bundle.addError(messageHelper.getMessage("admin.certifications.currentvendor.required"));
			model.addAttribute("bundle", bundle)
				.addAttribute("id", vendor.getId())
				.addAttribute("vendor", vendor);
			return "web/pages/admin/certifications/editvendor";
		}

		if (!httpRequest.getParameter("currentvendor").equals(vendor.getName())) {
			try {
				CertificationVendorDTO dto = new CertificationVendorDTO();
				dto.setCertificationVendorId(vendor.getId());
				dto.setName(httpRequest.getParameter("currentvendor"));

				vendor = certificationService.saveOrUpdateCertificationVendor(dto, vendor.getCertificationType().getId());
			} catch (Exception e) {
				messageHelper.addError(bundle, "admin.certifications.vendor_add.error");
				model.addAttribute("bundle", bundle)
					.addAttribute("id", vendor.getId())
					.addAttribute("vendor", vendor);
				return "web/pages/admin/certifications/editvendor";
			}
		}

		boolean sendEmail = false;
		VerificationStatus status = null;

		if (httpRequest.getParameter("action").equals("approve")) {
			status = VerificationStatus.VERIFIED;
		} else if (httpRequest.getParameter("action").equals("decline")) {
			status = VerificationStatus.FAILED;
			sendEmail = true;
		} else if (httpRequest.getParameter("action").equals("need_info")) {
			status = VerificationStatus.PENDING_INFORMATION;
			sendEmail = true;
		} else if (httpRequest.getParameter("action").equals("unverified")) {
			status = VerificationStatus.UNVERIFIED;
		} else if (httpRequest.getParameter("action").equals("on_hold")) {
			status = VerificationStatus.ON_HOLD;
		}

		try {
			certificationService.updateCertificationVendorStatus(vendor.getId(), status);
		} catch (Exception e) {
			messageHelper.addError(bundle, "admin.certifications.user_certification.error");
			model.addAttribute("bundle", bundle)
				.addAttribute("id", vendor.getId())
				.addAttribute("vendor", vendor);
			return "web/pages/admin/certifications/editvendor";
		}

		if (sendEmail && httpRequest.getParameter("note") != null && !httpRequest.getParameter("note").isEmpty()) {
			EMailDTO dtoEmail = new EMailDTO();
			if (status == VerificationStatus.FAILED) {
				dtoEmail.setSubject("Your certification was declined");
			} else {
				dtoEmail.setSubject("More info needed regarding your Work Market profile");
			}
			dtoEmail.setText(httpRequest.getParameter("note"));
			messagingService.sendEmailToUsers(getCurrentUser().getId(), Lists.newArrayList(vendor.getCreatorId()), dtoEmail);

			UserCommentDTO dtoComment = new UserCommentDTO();
			dtoComment.setComment(httpRequest.getParameter("note"));
			dtoComment.setUserId(vendor.getCreatorId());
			commentService.saveOrUpdateClientServiceUserComment(dtoComment);
		}
		return "redirect:/admin/certifications/review";
	}

	@RequestMapping(
		value = "/editcertifications",
		method = GET)
	public String editCertifications(
		Model model,
		HttpServletRequest httpRequest,
		RedirectAttributes redirectAttributes) {

		MessageBundle bundle = messageHelper.newBundle();

		if (httpRequest.getParameter("id").isEmpty()) {
			messageHelper.addError(bundle, "admin.certifications.certification_id.required");

			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/certifications/review";
		}

		Certification certification = certificationService.findCertificationById(Long.parseLong(httpRequest.getParameter("id")));

		if (certification == null) {
			messageHelper.addError(bundle, "admin.certifications.certification_id.required");

			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/certifications/review";
		}

		CertificationVendor vendor = certificationService.findCertificationVendorById(certification.getCertificationVendor().getId());

		model.addAttribute("vendor", vendor)
			.addAttribute("id", certification.getId())
			.addAttribute("certification", certification);

		return "web/pages/admin/certifications/editcertifications";
	}

	@RequestMapping(
		value = "/editcertifications",
		method = POST)
	public String editCertificationsSubmit(
		Model model,
		HttpServletRequest httpRequest,
		RedirectAttributes redirectAttributes) throws Exception {

		MessageBundle bundle = messageHelper.newBundle();

		if (httpRequest.getParameter("id") == null || httpRequest.getParameter("id").isEmpty()) {
			messageHelper.addError(bundle, "admin.certifications.certification_id.required");
		}

		if (httpRequest.getParameter("currentcertification") == null || httpRequest.getParameter("currentcertification").isEmpty()) {
			messageHelper.addError(bundle, "admin.certifications.currentcertification.required");
		}

		if (bundle.hasErrors()) {
			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/certifications/review";
		}

		Certification certification = certificationService.findCertificationById(Long.parseLong(httpRequest.getParameter("id")));

		if (certification == null) {
			messageHelper.addError(bundle, "admin.certifications.certification_id.required");
			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/certifications/review";
		}

		CertificationVendor vendor = certificationService.findCertificationVendorById(certification.getCertificationVendor().getId());

		CertificationDTO dto = new CertificationDTO();
		dto.setName(httpRequest.getParameter("currentcertification"));

		if (httpRequest.getParameter("provider") != null && !httpRequest.getParameter("provider").isEmpty()) {
			vendor = certificationService.findCertificationVendorById(Long.parseLong(httpRequest.getParameter("provider")));
		}
		dto.setCertificationVendorId(vendor.getId());

		boolean sendEmail = false;
		VerificationStatus status = null;
		try {
			certification = certificationService.saveOrUpdateCertification(dto);

			if (httpRequest.getParameter("action").equals("approve")) {
				status = VerificationStatus.VERIFIED;
			} else if (httpRequest.getParameter("action").equals("decline")) {
				status = VerificationStatus.FAILED;
				sendEmail = true;
			} else if (httpRequest.getParameter("action").equals("need_info")) {
				status = VerificationStatus.PENDING_INFORMATION;
				sendEmail = true;
			} else if (httpRequest.getParameter("action").equals("unverified")) {
				status = VerificationStatus.UNVERIFIED;
			} else if (httpRequest.getParameter("action").equals("on_hold")) {
				status = VerificationStatus.ON_HOLD;
			}

			certificationService.updateCertificationStatus(certification.getId(), status);
		} catch (Exception e) {
			messageHelper.addError(bundle, "admin.certifications.user_certification.error");
			redirectAttributes.addFlashAttribute("bundle", bundle);
		}

		if (sendEmail && httpRequest.getParameter("note") != null && !httpRequest.getParameter("note").isEmpty()) {
			EMailDTO dtoEmail = new EMailDTO();
			if (httpRequest.getParameter("action").equals("decline")) {
				dtoEmail.setSubject("Your certification was declined");
			} else {
				dtoEmail.setSubject("More info needed regarding your Work Market profile");
			}
			dtoEmail.setText(httpRequest.getParameter("note"));
			messagingService.sendEmailToUsers(getCurrentUser().getId(), Lists.newArrayList(vendor.getCreatorId()), dtoEmail);

			UserCommentDTO dtoComment = new UserCommentDTO();
			dtoComment.setComment(httpRequest.getParameter("note"));
			dtoComment.setUserId(certification.getCreatorId());
			commentService.saveOrUpdateClientServiceUserComment(dtoComment);
		}

		return "redirect:/admin/certifications/review";
	}

	@RequestMapping(
		value = "/edit_usercertification",
		method = GET)
	public String editUserCertification(
		Model model,
		HttpServletRequest httpRequest,
		RedirectAttributes redirectAttributes) {

		MessageBundle bundle = messageHelper.newBundle();

		if (httpRequest.getParameter("id").isEmpty() || httpRequest.getParameter("user_id").isEmpty()) {
			messageHelper.addError(bundle, "admin.certifications.certification_id_user_id.required");

			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/certifications/review";
		}

		UserCertificationAssociation userCertification = certificationService.findAssociationByCertificationIdAndUserId(
			Long.parseLong(httpRequest.getParameter("id")),
			Long.parseLong(httpRequest.getParameter("user_id"))
		);

		if (userCertification == null) {
			messageHelper.addError(bundle, "admin.certifications.certification_id_user_id.required");

			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/certifications/review";
		}

		Certification certification = certificationService.findCertificationById(userCertification.getCertification().getId());
		CertificationVendor vendor = certificationService.findCertificationVendorById(certification.getCertificationVendor().getId());

		User user_info = userService.getUser(Long.parseLong(httpRequest.getParameter("user_id")));
		model.addAttribute("user_certification", userCertification)
			.addAttribute("certification", certification)
			.addAttribute("vendor", vendor)
			.addAttribute("id", httpRequest.getParameter("id"))
			.addAttribute("user_id", httpRequest.getParameter("user_id"))
			.addAttribute("user_info", user_info);
		if (!userCertification.getAssets().isEmpty()) {
			model.addAttribute("assets", userCertification.getAssets());
		}

		return "web/pages/admin/certifications/edit_usercertification";
	}

	@RequestMapping(
		value = "/edit_usercertification",
		method = POST)
	public String editUserCertificationSubmit(
		Model model,
		HttpServletRequest httpRequest,
		RedirectAttributes redirectAttributes) {

		MessageBundle bundle = messageHelper.newBundle();

		if (httpRequest.getParameter("id").isEmpty() || httpRequest.getParameter("user_id").isEmpty()) {
			messageHelper.addError(bundle, "admin.certifications.certification_id_user_id.required");
			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/certifications/review";
		}

		UserCertificationAssociation userCertification = certificationService.findAssociationByCertificationIdAndUserId(
			Long.parseLong(httpRequest.getParameter("id")),
			Long.parseLong(httpRequest.getParameter("user_id"))
		);

		if (userCertification == null) {
			messageHelper.addError(bundle, "admin.certifications.certification_id_user_id.required");

			redirectAttributes.addFlashAttribute("bundle", bundle);
			return "redirect:/admin/certifications/review";
		}

		Certification certification = certificationService.findCertificationById(userCertification.getCertification().getId());
		CertificationVendor vendor = certificationService.findCertificationVendorById(certification.getCertificationVendor().getId());
		String expiration_date = httpRequest.getParameter("expiration_date") != null  ? httpRequest.getParameter("expiration_date") : "";
		String issue_date = httpRequest.getParameter("issue_date") != null  ? httpRequest.getParameter("issue_date") : "";

		boolean sendEmail = false;
		VerificationStatus status = null;

		try {
			UserCertificationDTO dto = new UserCertificationDTO();
			if (StringUtilities.isNotEmpty(expiration_date)) {
				dto.setExpirationDate(DateUtilities.getCalendarFromDateString(expiration_date, getCurrentUser().getTimeZoneId()));
			}
			if (StringUtilities.isNotEmpty(issue_date)) {
				dto.setIssueDate(DateUtilities.getCalendarFromDateString(issue_date, getCurrentUser().getTimeZoneId()));
			}
			dto.setCertificationNumber(httpRequest.getParameter("number"));

			certificationService.saveOrUpdateUserCertification(
				Long.parseLong(httpRequest.getParameter("id")),
				Long.parseLong(httpRequest.getParameter("user_id")),
				dto
			);

			if (httpRequest.getParameter("action").equals("approve")) {
				status = VerificationStatus.VERIFIED;
			} else if (httpRequest.getParameter("action").equals("decline")) {
				status = VerificationStatus.FAILED;
				sendEmail = true;
			} else if (httpRequest.getParameter("action").equals("need_info")) {
				status = VerificationStatus.PENDING_INFORMATION;
				sendEmail = true;
			} else if (httpRequest.getParameter("action").equals("unverified")) {
				status = VerificationStatus.UNVERIFIED;
			} else if (httpRequest.getParameter("action").equals("on_hold")) {
				status = VerificationStatus.ON_HOLD;
			}

			certificationService.updateUserCertificationAssociationStatus(
				certification.getId(),
				Long.parseLong(httpRequest.getParameter("user_id")),
				status
			);
		} catch (Exception e) {
			messageHelper.addError(bundle, "admin.certifications.user_certification.error");
			User user_info = userService.getUser(Long.parseLong(httpRequest.getParameter("user_id")));
			model.addAttribute("bundle", bundle)
				.addAttribute("certification", certification)
				.addAttribute("vendor", vendor)
				.addAttribute("id", httpRequest.getParameter("id"))
				.addAttribute("user_id", httpRequest.getParameter("user_id"))
				.addAttribute("user_certification", userCertification)
				.addAttribute("user_info", user_info);

			if (!userCertification.getAssets().isEmpty()) {
				model.addAttribute("assets", userCertification.getAssets());
			}

			return "web/pages/admin/certifications/edit_usercertification";
		}

		if (sendEmail && httpRequest.getParameter("note") != null && !httpRequest.getParameter("note").isEmpty()) {
			if (httpRequest.getParameter("action").equals("decline") || httpRequest.getParameter("action").equals("need_info")) {
				EMailDTO dtoEmail = new EMailDTO();
				if (status == VerificationStatus.FAILED) {
					dtoEmail.setSubject("Your certification was declined");
				} else {
					dtoEmail.setSubject("More info needed regarding your Work Market profile");
				}
				dtoEmail.setText(httpRequest.getParameter("note"));
				messagingService.sendEmailToUsers(getCurrentUser().getId(), Lists.newArrayList(StringUtilities.parseLong(httpRequest.getParameter("user_id"))), dtoEmail);

				UserCommentDTO dtoComment = new UserCommentDTO();
				dtoComment.setComment(httpRequest.getParameter("note"));
				dtoComment.setUserId(certification.getCreatorId());
				commentService.saveOrUpdateClientServiceUserComment(dtoComment);
			}
		}

		return "redirect:/admin/certifications/review";
	}

	@RequestMapping(
		value="/unverified_certifications",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void unverifiedCertifications(
		Model model,
		HttpServletRequest httpRequest) {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, CertificationPagination.SORTS.VERIFICATION_STATUS.toString());
			put(1, CertificationPagination.SORTS.CREATED_DATE.toString());
			put(2, CertificationPagination.SORTS.USER_LAST_NAME.toString());
			put(3, CertificationPagination.SORTS.VENDOR_NAME.toString());
			put(4, CertificationPagination.SORTS.CERTIFICATION_NAME.toString());
			put(5, CertificationPagination.SORTS.LAST_ACTIVITY_DATE.toString());
		}});

		CertificationPagination pagination = new CertificationPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		if (StringUtilities.isNotEmpty(httpRequest.getParameter("status"))) {
			int val = Integer.parseInt(httpRequest.getParameter("status"));
			VerificationStatus enumValues[] = VerificationStatus.values();
			switch (enumValues[val])
			{
				case PENDING:
					pagination.addFilter(CertificationPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.PENDING);
					break;
				case PENDING_INFORMATION:
					pagination.addFilter(CertificationPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.PENDING_INFORMATION);
					break;
				case ON_HOLD:
					pagination.addFilter(CertificationPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.ON_HOLD);
					break;
			}
		}

		pagination = certificationService.findAllCertifications(pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);
		List<Certification> certifications = pagination.getResults();
		List<Long> ids = CollectionUtilities.newListPropertyProjection(certifications, "creatorId");
		Map<Long, Map<String, Object>> props = userService.getProjectionMapByIds(ids, "firstName", "lastName", "userNumber");

		for (Certification cert : certifications) {
			String createdOn = DateUtilities.format("MM/dd/yyyy h:mm a", cert.getCreatedOn(), getCurrentUser().getTimeZoneId());
			String lastActivityOn = DateUtilities.format("MM/dd/yyyy h:mm a", cert.getLastActivityOn(), getCurrentUser().getTimeZoneId());
			Map<String, Object> creatorProps = props.get(cert.getCreatorId());

			List<String> row = Lists.newArrayList(
				cert.getVerificationStatus().name(),
				createdOn,
				StringUtilities.fullName((String) creatorProps.get("firstName"), (String) creatorProps.get("lastName")),
				cert.getCertificationVendor().getName(),
				cert.getName(),
				lastActivityOn
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", cert.getId(),
				"user_id", cert.getCreatorId(),
				"user_number", creatorProps.get("userNumber")
			);

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/unverified_usercertifications",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void unverifiedUserCertifications(
		Model model,
		HttpServletRequest httpRequest) {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, UserCertificationAssociationPagination.SORTS.VERIFICATION_STATUS.toString());
			put(1, UserCertificationAssociationPagination.SORTS.CREATED_DATE.toString());
			put(2, UserCertificationAssociationPagination.SORTS.USER_LAST_NAME.toString());
			put(3, UserCertificationAssociationPagination.SORTS.VENDOR_NAME.toString());
			put(5, UserCertificationAssociationPagination.SORTS.ISSUE_DATE.toString());
			put(6, UserCertificationAssociationPagination.SORTS.EXPIRATION_DATE.toString());
			put(7, UserCertificationAssociationPagination.SORTS.LAST_ACTIVITY_DATE.toString());
		}});

		UserCertificationAssociationPagination pagination = new UserCertificationAssociationPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		String searchString = httpRequest.getParameter("sSearch");
		if (StringUtilities.isNotEmpty(searchString)) {
			pagination.addFilter(UserCertificationAssociationPagination.FILTER_KEYS.USER_NAME, searchString);
		}

		if (StringUtilities.isNotEmpty(httpRequest.getParameter("status"))) {
			int val = Integer.parseInt(httpRequest.getParameter("status"));
			VerificationStatus enumValues[] = VerificationStatus.values();
			switch (enumValues[val])
			{
				case PENDING:
					pagination.addFilter(UserCertificationAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.PENDING);
					break;
				case PENDING_INFORMATION:
					pagination.addFilter(UserCertificationAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.PENDING_INFORMATION);
					break;
				case ON_HOLD:
					pagination.addFilter(UserCertificationAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS, VerificationStatus.ON_HOLD);
					break;
			}
		}

		pagination = certificationService.findAllUserCertifications(pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);
		List<UserCertificationAssociation> associations = pagination.getResults();
		List<Long> ids = CollectionUtilities.newListPropertyProjection(associations, "creatorId");
		Map<Long, Map<String, Object>> props = userService.getProjectionMapByIds(ids, "firstName", "lastName", "userNumber");

		for (UserCertificationAssociation cert : associations) {
			String createdOn = DateUtilities.format("MM/dd/yyyy h:mm a", cert.getCreatedOn(), getCurrentUser().getTimeZoneId());
			String issueDate = DateUtilities.format("MM/dd/yyyy h:mm a", cert.getIssueDate(), getCurrentUser().getTimeZoneId());
			String expirationDate = DateUtilities.format("MM/dd/yyyy h:mm a", cert.getExpirationDate(), getCurrentUser().getTimeZoneId());
			String lastActivityOn = DateUtilities.format("MM/dd/yyyy h:mm a", cert.getLastActivityOn(), getCurrentUser().getTimeZoneId());
			Long creatorId = cert.getCreatorId();
			Map<String, Object> creatorProps = props.get(creatorId);

			List<String> row = Lists.newArrayList(
				cert.getVerificationStatus().name(),
				createdOn,
				StringUtilities.fullName((String) creatorProps.get("firstName"), (String) creatorProps.get("lastName")),
				cert.getCertification().getCertificationVendor().getName(),
				"",
				issueDate,
				expirationDate,
				lastActivityOn
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", cert.getCertification().getId(),
				"user_id", creatorId,
				"user_number", creatorProps.get("userNumber"),
				"attachment_relative_uri", ((CollectionUtilities.isEmpty(cert.getAssets())) ? null : cert.getAssets().iterator().next().getUri()),
				"name", cert.getCertification().getName(),
				"number", cert.getCertificationNumber()
			);

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/vendor_instructions",
		method = GET)
	public String vendorInstructions(Model model) {

		CertificationVendorPagination pagination = new CertificationVendorPagination();
		pagination.setReturnAllRows();
		pagination = certificationService.findAllCertificationVendors(pagination);

		List<CertificationVendor> vendors = pagination.getResults();
		model.addAttribute("vendors", vendors);

		return "web/pages/admin/certifications/vendor_instructions";
	}
}
