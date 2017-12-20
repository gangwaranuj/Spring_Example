
package com.workmarket.web.controllers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.BlockedCompanyUserAssociationPagination;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/relationships")
public class RelationshipsController extends BaseController {

	@Autowired private LaneService laneService;
	@Autowired private UserService userService;
	@Autowired private VendorService vendorService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private MessageSource messageSource;

	private static final Logger logger = LoggerFactory.getLogger(RelationshipsController.class);

	@RequestMapping(
		value = "/addtolane3",
		method = RequestMethod.POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map addToLane3(
		@RequestBody Map<String, String> user,
		HttpServletResponse response) {

		String message;
		Long uid = checkNotNull(userService.findUserId(user.get("userNumber")));

		try {
			laneService.addUsersToCompanyLane3(Lists.newArrayList(uid), getCurrentUser().getCompanyId());
			message = messageSource.getMessage("relationships.addtolane3.success", null, null);
		} catch (Exception e) {
			message = messageSource.getMessage("relationships.addtolane3.exception", null, null);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("failed to remove from lane for user id: %s", uid), e);
		}

		return CollectionUtilities.newObjectMap(
			"message", message
		);
	}

	@RequestMapping(
		value = "/removefromlane",
		method = RequestMethod.POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map removeFromLane(
		@RequestBody Map<String, String> user,
		HttpServletResponse response) {

		String message;
		Long uid = checkNotNull(userService.findUserId(user.get("userNumber")));

		try {
			laneService.removeUserFromCompanyLane(uid, getCurrentUser().getCompanyId());
			message = messageSource.getMessage("relationships.removefromlane.success", null, null);
		} catch (Exception e) {
			message = messageSource.getMessage("relationships.removefromlane.exception", null, null);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("failed to remove from lane for user id: %s", uid), e);
		}

		return CollectionUtilities.newObjectMap(
			"message", message
		);
	}

	@RequestMapping(
		value = "/blocked_resources",
		method = GET)
	public String blockedResources(Model model) {

		List<Map<String, Object>> list = Lists.newArrayList();
		try {
			Set<User> users = userService.findBlockedUsers(getCurrentUser().getId());
			for (User u : users) {
				list.add(ImmutableMap.<String, Object>of(
					"id", u.getUserNumber(),
					"name",u.getFullName(),
					"actualId", u.getId(),
					"createdOn", userService.findDateWhenUserBlocked(u.getId(),getCurrentUser().getCompanyId()).getTime(),
					"blockingUser", userService.getFullName(userService.findBlockingUserId(u.getId(),getCurrentUser().getCompanyId()))));
			}
		} catch (Exception e) {
			logger.error("error creating /blocked_resources list", e);
		}

		model.addAttribute("blocked_resources", list);

		return "web/pages/relationships/blocked_resources";
	}

	@RequestMapping(
		value = "/blocked_clients",
		method = GET)
	public String blockedClients(Model model) {

		BlockedCompanyUserAssociationPagination companyPagination = new BlockedCompanyUserAssociationPagination();

		try {
			companyPagination = userService.findAllBlockedCompanies(getCurrentUser().getId(), companyPagination);
		} catch (Exception e) {
			logger.error("error creating /blocked_clients list", e);
		}

		model.addAttribute("blocked_clients", companyPagination.getResults());

		return "web/pages/relationships/blocked_clients";
	}

	@RequestMapping(
		value = "/unblocked_clients",
		method = POST)
	public @ResponseBody  AjaxResponseBuilder unblockedClients(
		@RequestParam("clientNumber") Long clientNumber) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		try {
			ExtendedUserDetails user = getCurrentUser();
			if (user.isBuyer()) {
				vendorService.unblockVendor(user.getId(), clientNumber);
			} else {
				userService.unblockCompany(user.getId(), clientNumber);
			}
			messageHelper.addMessage(response, "unblockclient.success");
		} catch (Exception e) {
			logger.error("error creating /unblocked_clients list", e);
			messageHelper.addMessage(response, "unblockclient.exception");
			return response;
		}

		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/unblocked_resources",
		method = POST)
	public @ResponseBody AjaxResponseBuilder unblockedResources(
		@RequestParam("resourceNumber") Long resourceNumber) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		try {
			userService.unblockUser(getCurrentUser().getId(), resourceNumber);
			messageHelper.addMessage(response, "unblockresource.success");
		} catch (Exception e) {
			logger.error("error occurred while unblocking user", resourceNumber, e);
			messageHelper.addMessage(response, "unblockresource.error");
			return response;
		}

		return response.setSuccessful(true);
	}

}
