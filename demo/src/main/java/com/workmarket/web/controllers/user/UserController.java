package com.workmarket.web.controllers.user;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.EncryptionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.StringUtils;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired private AuthenticationService authn;
	@Autowired private RegistrationService registrationService;
	@Autowired private UserService userService;
	@Autowired private CompanyService companyService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired protected JsonSerializationService jsonService;

	private static final String WM_LOGIN_MESSAGE = "WM_LOGIN_MESSAGE";

	@RequestMapping(
		value = "/confirm_account/{userNumber}",
		method = GET)
	public String confirmAccount(
		@PathVariable("userNumber") String encryptedId,
		RedirectAttributes redirectAttributes,
		HttpSession session) throws UnsupportedEncodingException {

		MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);
		String userNumber = "";
		Long userId = 0L;

		try {
			userId = EncryptionUtilities.decryptLong(encryptedId);
			userNumber = userService.findUserNumber(userId);
		}
		catch (EncryptionOperationNotPossibleException e) {
			logger.error("Error decrypting key [" + encryptedId + "]", e);
		}

		if (StringUtils.isBlank(userNumber)) {
			messageHelper.addError(messages, "user.account.confirm.error");
			if (isAuthenticated()) { return "redirect:/home"; }
			session.setAttribute(WM_LOGIN_MESSAGE, messages);
			return "redirect:/login";
		}

		authn.setCurrentUser(userNumber);
		User user = registrationService.confirmAndApproveAccount(userId);

		if (user == null) {
			messageHelper.addError(messages, "user.account.confirm.error_email_not_found");
			if (isAuthenticated()) { return "redirect:/home"; }
			session.setAttribute(WM_LOGIN_MESSAGE, messages);
			return "redirect:/login";
		}

		messageHelper.addSuccess(messages, "user.account.confirm.success");
		session.setAttribute(WM_LOGIN_MESSAGE, messages);
		return String.format("redirect:/login?login=%s", StringUtilities.urlEncode(user.getEmail(), "utf-8"));
	}

	// TODO API - To be removed and replaced by /v2/resend_confirmation_email endpoint
	@RequestMapping(
		value = "/resend_confirmation_email/{userNumber}",
		method = GET)
	public String resendConfirmationEmail(
		@PathVariable("userNumber") String userNumber,
		@ModelAttribute("bundle") MessageBundle messages,
		Model model) {

		// TODO PHP provides an AJAX handler as well... required?
		// TODO And is this for authenticated users? Hmm.

		Long uid = userService.findUserId(userNumber);
		try {
			registrationService.sendRemindConfirmationEmail(uid);
			messageHelper.addSuccess(messages, "user.confirmation.resend.success");
		} catch (Exception e) {
			messageHelper.addError(messages, "user.confirmation.resend.failure");
		}

		model.addAttribute("un", userNumber);
		return "web/pages/error/not_confirmed";
	}

	@RequestMapping(
		value = "/not_my_account/{userNumber}",
		method = GET)
	public String notMyAccount(@PathVariable("userNumber") String userNumber, Model model) {

		User user = userService.findUserByUserNumber(userNumber);

		model.addAttribute("user", user);

		return "web/pages/user/not_my_account";
	}

	@RequestMapping(
		value = "/not_my_account_confirmed/{userNumber}",
		method = GET)
	public String notMyAccountConfirmed(
		@PathVariable("userNumber") String userNumber,
		@ModelAttribute("bundle") MessageBundle messages) {

		// TODO Flow from PHP seems odd. Review.

		Long uid = userService.findUserId(userNumber);
		try {
			authn.setCurrentUser(userNumber);
			if(userService.deleteUserIfNotConfirmed(uid)){
				messageHelper.addSuccess(messages, "user.not_my_account.success");
			}else{
				messageHelper.addError(messages, "user.not_my_account.failure");
			}
		} catch (Exception e) {
			messageHelper.addError(messages, "user.not_my_account.failure");
		}

		return "web/pages/user/not_my_account_confirmed";
	}

	@RequestMapping(
		value = "/optout",
		method = GET)
	public String optout(@RequestParam("email") String email, Model model) {
		model.addAttribute("email", email);
		return "web/pages/user/optout";
	}

	@RequestMapping(
		value = "/optout",
		method = POST)
	public String doOptout(@RequestParam("email") String email, MessageBundle messages,
						   RedirectAttributes redirectAttributes, HttpSession session) {

		registrationService.blacklistEmail(email);

		messageHelper.addSuccess(messages, "user.optout.success");

		redirectAttributes.addFlashAttribute("bundle", messages);

		return "redirect:/";
	}

	@RequestMapping(
		value = "/blockclient",
		method = GET)
	public String blockClient(
		@RequestParam("user") String user,
		@RequestParam("client") String client,
		RedirectAttributes redirectAttributes,
		HttpSession session) {

		MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);

		Long userId = null;
		Long clientId = null;
		String clientName = null;

		try{
			userId = userService.findUserByEncryptedId(user).getId();
			Company company = companyService.findCompanyByEncryptedId(client);
			clientId = company.getId();
			clientName = company.getName();
		} catch(Exception e){
			messageHelper.addError(messages, "blockclient.exception");
			return "redirect:/user/block_client";
		}

		try{
			authn.setCurrentUser(userId);
			userService.blockCompany(userId, clientId);
			messageHelper.addSuccess(messages, "blockclient.success", clientName , "/relationships/blocked_clients");
		} catch(IllegalArgumentException i){
			messageHelper.addError(messages, i.getMessage(), clientName);
		} catch (Exception e){
			messageHelper.addError(messages, "blockclient.error", clientName);
		}

		return "redirect:/user/block_client";

	}

	@RequestMapping(
		value = "/block_client",
		method = GET)
	public String blockclientPage(Model model) {
		return "web/pages/user/block_client";
	}

	@RequestMapping(
		value = "/get_session",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getSession(Model model) {
		model.addAttribute("response", getCurrentUser());
	}

	@RequestMapping(
		value = "/block_client/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder blockClient(@PathVariable("id") Long clientId) {
		AjaxResponseBuilder result = new AjaxResponseBuilder();
		MessageBundle bundle = messageHelper.newBundle();

		Long userId = getCurrentUser().getId();
		Company company = companyService.findCompanyById(clientId);

		if (userId == null || company == null) {
			messageHelper.addError(bundle, "blockclient.exception");

			return result
				.setSuccessful(false)
				.setMessages(bundle.getErrors());
		}

		try {
			userService.blockCompany(userId, company.getId());
		} catch(IllegalArgumentException e) {
			messageHelper.addError(bundle, messageHelper.getMessage(e.getMessage(), company.getName()));

			return result
				.setSuccessful(false)
				.setMessages(bundle.getErrors());
		}

		messageHelper.addSuccess(bundle, "blockclient.success", company.getName(), messageHelper.getMessage("blocked_clients.url"));
		result.addData("status", "OK");

		return result
			.setSuccessful(true)
			.setMessages(bundle.getSuccess());
	}

	@RequestMapping(
		value = "/unblock_client/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder unblockClient(@PathVariable("id") Long clientId) {
		AjaxResponseBuilder result = new AjaxResponseBuilder();
		MessageBundle bundle = messageHelper.newBundle();

		Long userId = getCurrentUser().getId();
		Company company =  companyService.findCompanyById(clientId);

		if (userId == null || company == null) {
			messageHelper.addError(bundle, "unblockclient.exception");

			return result
				.setSuccessful(false)
				.setMessages(bundle.getErrors());
		}

		try {
			userService.unblockCompany(userId, company.getId());
		} catch(IllegalArgumentException e) {
			messageHelper.addError(bundle, messageHelper.getMessage(e.getMessage()));

			return result
				.setSuccessful(false)
				.setMessages(bundle.getErrors());
		}

		messageHelper.addSuccess(bundle, "unblockclient.success");

		return result
			.setSuccessful(true)
			.addData("status", "OK")
			.setMessages(bundle.getSuccess());
	}

	@RequestMapping(
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody
	List<Map<String, Object>> getUsers(@RequestParam List<String> userNumbers) {
		Set<User> users = userService.findAllUsersByUserNumbers(userNumbers);
		List<Map<String, Object>> results = Lists.newArrayListWithExpectedSize(users.size());

		for (User user : users) {
			UserAssetAssociation avatar = userService.findUserAvatars(user.getId());
			Optional<PersonaPreference> preference = userService.getPersonaPreference(user.getId());
			results.add(new ImmutableMap.Builder<String, Object>()
				.put("id", user.getUserNumber())
				.put("firstName", user.getFirstName())
				.put("lastName", user.getLastName())
				.put("fullName", user.getFullName())
				.put("thumbnail", avatar != null && avatar.getAsset() != null && avatar.getAsset().getDownloadableUri() != null ? avatar.getAsset().getDownloadableUri() : "")
				.put("isCurrentUser", user.getId().equals(getCurrentUser().getId()))
				.put("isWorker", preference.isPresent() && preference.get().isSeller())
				.build());
		}

		return results;
	}

	@RequestMapping(
		value = "/{userNumber}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getUser(@PathVariable String userNumber, HttpServletResponse response) {
		Map<String, Object> result;
		User user = userService.findUserByUserNumber(userNumber);

		if (user != null) {
			UserAssetAssociation avatar = userService.findUserAvatars(user.getId());
			Optional<PersonaPreference> preference = userService.getPersonaPreference(user.getId());
			result = new ImmutableMap.Builder<String, Object>()
				.put("id", user.getUserNumber())
				.put("firstName", user.getFirstName())
				.put("lastName", user.getLastName())
				.put("fullName", user.getFullName())
				.put("thumbnail", avatar != null && avatar.getAsset() != null && avatar.getAsset().getDownloadableUri() != null ? avatar.getAsset().getDownloadableUri() : "")
				.put("isCurrentUser", user.getId().equals(getCurrentUser().getId()))
				.put("isWorker", preference.isPresent() && preference.get().isSeller())
				.build();
		} else {
			result = Maps.newHashMap();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("Error fetching user %s", userNumber));
		}

		return result;
	}
}
