package com.workmarket.web.controllers.users;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserLaneRelationship;
import com.workmarket.domains.model.UserLaneRelationshipPagination;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.user.CompanyUser;
import com.workmarket.domains.model.user.CompanyUserPagination;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.MessagingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/users")
public class UsersController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

	@Autowired RegistrationService registrationService;
	@Autowired MessageBundleHelper messageHelper;
	@Autowired UserService userService;
	@Autowired ProfileService profileService;
	@Autowired AuthenticationService authenticationService;
	@Autowired MessagingService messagingService;
	@Autowired private LaneService laneService;
	@Autowired private TaxService taxService;

	@RequestMapping(method = GET)
	public String index(Model model) {
		AbstractTaxEntity taxEntity = taxService.findActiveTaxEntity(getCurrentUser().getId());

		model.addAttribute("hasBusinessTaxInfo", taxEntity != null && taxEntity.getBusinessFlag());

		return "web/pages/users/index";
	}

	@RequestMapping(
		value = {"/list"},
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void list(
		Model model,
		HttpServletRequest httpRequest) throws Exception {

		Calendar isOnlineStart = DateUtilities.subtractTime(DateUtilities.getCalendarNow(), 30, Constants.MINUTE);
		Calendar isOnlineEnd = DateUtilities.getCalendarNow();

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(CollectionUtilities.<Integer,String>newTypedObjectMap(
			0, CompanyUserPagination.SORTS.LAST_NAME.toString(),
			1, CompanyUserPagination.SORTS.EMAIL.toString(),
			2, CompanyUserPagination.SORTS.ROLES_STRING.toString(),
			3, CompanyUserPagination.SORTS.LATEST_ACTIVITY.toString()
		));
		request.setFilterMapping(CollectionUtilities.<String,Enum<?>>newTypedObjectMap(
			"inactive", CompanyUserPagination.FILTER_KEYS.IS_INACTIVE
		));
		CompanyUserPagination pagination = request.newPagination(CompanyUserPagination.class);
		pagination = userService.findAllCompanyUsers(getCurrentUser().getCompanyId(), pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (CompanyUser user : pagination.getResults()) {
			List<String> row = Lists.newArrayList(
				StringUtilities.fullName(user.getFirstName(), user.getLastName()),
				user.getRolesString(),
				user.getLatestActivityOn() != null ? DateUtilities.format("MMM d, yyyy h:mma z", user.getLatestActivityOn(), getCurrentUser().getTimeZoneId()) : "-",
				null
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", user.getUserNumber(),
				"status", user.getUserStatusType(),
				"email", user.getEmail(),
				"emailConfirmed", user.getEmailConfirmed(),
				"latestActivityAddress", user.getLatestActivityInetAddress(),
				"isOnline", DateUtilities.intervalContains(isOnlineStart, isOnlineEnd, user.getLatestActivityOn()),
				"statsSentCount", user.getStats().getSentCount(),
				"statsSentValue", user.getStats().getSentValue(),
				"statsApprovedCount", user.getStats().getApprovedCount(),
				"statsApprovedValue", user.getStats().getApprovedValue()
			);
			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}


	// TODO API - To be removed and replaced by /v2/resend_confirmation_email endpoint
	@RequestMapping(
		value = "/resend_confirmation_email/{userNumber}",
		method = GET)
	public String resend(
		@PathVariable("userNumber") String userNumber,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		try {
			User user = userService.findUserByUserNumber(userNumber);
			if (user == null) {
				messageHelper.addError(bundle, "users.resend_confirmation_email.user_not_found");
			} else {
				registrationService.sendRemindConfirmationWithPasswordResetEmail(user.getId());
				messageHelper.addSuccess(bundle, "users.resend_confirmation_email.success", userNumber);
			}
		} catch (Exception ex) {
			messageHelper.addError(bundle, "any", ex.getMessage());
		}

		return "redirect:/users";
	}


	private Long getRole(List<AclRole> companyRoles, String roleName) {
		if (roleName != null) {
			for (AclRole availableCompanyRole : companyRoles) {
				if (availableCompanyRole.getName().equals(roleName)) {
					return availableCompanyRole.getId();
				}
			}
		}
		return null;
	}


	private String getField(String[] line, Map<String, Integer> fieldIndex, String fieldName) {
		if (fieldIndex.containsKey(fieldName)) {
			return line[fieldIndex.get(fieldName)].trim();
		}
		return null;
	}



	@RequestMapping(
		value = "/send_message",
		method = POST)
	public String sendMessage(
		@RequestParam("id") Long toUserId,
		@RequestParam("subject") String subject,
		@RequestParam("return_to") String returnTo,
		@RequestParam("message") String message, RedirectAttributes redirectAttributes) {

		EMailDTO dto = new EMailDTO();
		dto.setFromId(getCurrentUser().getId());
		dto.setSubject(subject);
		dto.setText(message);

		messagingService.sendEmailToUser(getCurrentUser().getId(), toUserId, dto);

		MessageBundle bundle = messageHelper.newFlashBundle(redirectAttributes);
		messageHelper.addSuccess(bundle, "users.message-sent");

		return "redirect:" + returnTo;
	}

	@RequestMapping(
		value = "/worker_pool_data/{userNumber}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void workerPoolData(
		Model model,
		@PathVariable("userNumber") String userNumber,
		HttpServletRequest httpRequest) {

		logger.debug("requesting worker pool data for " + userNumber);

		User user = userService.findUserByUserNumber(userNumber);

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		UserLaneRelationshipPagination pagination = new UserLaneRelationshipPagination(true);
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());
		pagination = laneService.findAllLaneRelationshipsByUserId(user.getId(), pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		SimpleDateFormat sdf = new SimpleDateFormat("MM d, yyyy");

		for (UserLaneRelationship relationship : pagination.getResults()) {
			List<String> row = Lists.newArrayList(
				relationship.getCompanyName(),
				String.valueOf(relationship.getLaneType()),
				relationship.getLastAssignmentDate() != null ? sdf.format(relationship.getLastAssignmentDate().getTime()) : "",
				String.valueOf(relationship.getTotalAssignments())
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"company_name", relationship.getCompanyName(),
				"company_id", relationship.getCompanyId(),
				"last_assignment_id", String.valueOf(relationship.getLastAssignmentId()),
				"lane", String.valueOf(relationship.getLaneType())
			);
			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}


	@RequestMapping(
		value = "/decline_lane3",
		method = GET)
	public ModelAndView declineLane3() {

		ModelAndView m = new ModelAndView("web/pages/users/decline_lane3");

		return m;
	}


	@RequestMapping(
		value = "/message",
		method = GET)
	public ModelAndView message() {

		ModelAndView m = new ModelAndView("web/pages/users/message");

		return m;
	}


	@RequestMapping(
		value = "/suspended",
		method = GET)
	public ModelAndView suspended() {

		ModelAndView m = new ModelAndView("web/pages/users/suspended");

		return m;
	}


	@RequestMapping(
		value = "/recent",
		method = GET)
	public ModelAndView recent() {

		ModelAndView m = new ModelAndView("web/pages/users/recent");

		return m;
	}


	@RequestMapping(
		value = "/remove_from_lane/{userNumber}",
		method = GET)
	public String removeFromLane(
		@PathVariable("userNumber") String userNumber,
		@RequestParam(value = "company_id", required = false) Long companyId,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		// Find the user id based on the user number
		Long userId = userService.findUserId(userNumber);
		boolean status = false;

		if (companyId != null) {
			try {
				laneService.removeUserFromCompanyLane(userId, companyId);
				messageHelper.addSuccess(bundle, "users.remove_from_lane.success");
				status = true;
			} catch (Exception ex) {
				logger.error("error removing user from company lane: userId={} and companyId={}", new Object[]{userId, companyId}, ex);
			}
		}
		if (!status) {
			messageHelper.addError(bundle, "users.remove_from_lane.failure");
		}

		return "redirect:/profile/" + userNumber;
	}


	class UserInfo {
		public UserDTO user;
		public Map<String, String> profile;
		public Long[] roles;


		public UserInfo(UserDTO user, Map<String, String> profile, Set<Long> roles) {
			this.user = user;
			this.profile = profile;
			this.roles = roles != null ? roles.toArray(new Long[roles.size()]) : null;
		}
	}
}
