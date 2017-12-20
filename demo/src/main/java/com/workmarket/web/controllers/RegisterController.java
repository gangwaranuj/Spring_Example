package com.workmarket.web.controllers;

import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.CallingCode;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.session.ImpressionType;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.InvitationService;
import com.workmarket.service.business.RecruitingService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.ImpressionDTO;
import com.workmarket.service.business.dto.InvitationUserRegistrationDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.EncryptionService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.forms.RegisterUserForm;
import com.workmarket.web.forms.register.RecruitingRegistrationForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.FeedRequestParamsValidator;
import com.workmarket.web.validators.PasswordValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/register")
public class RegisterController extends BaseController {

	private static final Log LOGGER = LogFactory.getLog(RegisterController.class);

	@Autowired private EncryptionService encryptionService;
	@Autowired private AnalyticsService analyticsService;
	@Autowired private RecruitingService recruitingService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private RegistrationService registrationService;
	@Autowired private UserService userService;
	@Autowired private InvitationService invitationService;
	@Autowired private CompanyService companyService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private FormOptionsDataHelper formDataHelper;
	@Autowired private PasswordValidator passwordValidator;
	@Autowired private InvariantDataService invariantDataService;

	@ModelAttribute("industries")
	private Map<Long, String> getIndustries() {
		return formDataHelper.getIndustries();
	}

	/**
	 * Registration via campaign
	 */
	@RequestMapping(
		value = "/campaign/{encryptedId}",
		method = GET)
	public String showCampaign(
		@PathVariable String encryptedId,
		@ModelAttribute("form") RecruitingRegistrationForm form,
		Model model,
		HttpServletRequest request) {

		RecruitingCampaign campaign = loadAndValidateCampaign(encryptedId);

		model.addAttribute("campaign", campaign);
		model.addAttribute("type", "campaign");
		model.addAttribute("encryptedId", encryptedId);

		if (campaign.getCompany() != null) {
			model.addAttribute("company", companyService.findCompanyById(campaign.getCompany().getId()));
		}

		List<CallingCode> callingCodes = invariantDataService.findAllActiveCallingCodes();
		model.addAttribute("callingCodesList", callingCodes);

		// note the visit
		saveImpression(campaign.getId(), request);

		return "web/pages/register/index";
	}

	@RequestMapping(
		value = "/campaign/{encryptedId}",
		method = GET,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Map<String, Object> showCampaignJson(@PathVariable String encryptedId) {

		RecruitingCampaign campaign = loadAndValidateCampaign(encryptedId);

		if (campaign == null) {
			throw new HttpException404().setMessageKey("register.campaign.notfound");
		}
		return CollectionUtilities.newObjectMap(
			"id", encryptedId,
			"title", campaign.getTitle(),
			"description", campaign.getDescription());
	}

	@RequestMapping(
		value = "/campaign/{encryptedId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public  @ResponseBody AjaxResponseBuilder postCampaignForm(
		@PathVariable String encryptedId,
		@Valid @ModelAttribute("form") RecruitingRegistrationForm form,
		BindingResult bind,
		RedirectAttributes flash) throws Exception {

		Long id = encryptionService.decryptId(encryptedId);

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		AjaxResponseBuilder responseBody = new AjaxResponseBuilder().setRedirect("/register/campaign/{encryptedId}").setSuccessful(false);

		passwordValidator.validate(form.getPassword(), form.getEmail(), bind);
		if (StringUtils.isNotBlank(form.getEmail()) && !EmailValidator.getInstance().isValid(form.getEmail())) {
			bind.addError(new ObjectError("email", new String[]{"Pattern"}, new String[]{"Email"}, ""));
		}

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return responseBody.setMessages(bundle.getAllMessages());
		}

		// check if user already exists and redirect accordingly
		User invitedUser = userService.findUserByEmail(form.getEmail());
		if (invitedUser != null) {
			if (authenticationService.isSuspended(invitedUser)) {
				messageHelper.addNotice(bundle, "register.campaign.suspended");
			} else {
				messageHelper.addError(bundle, "register.emailuseerror", invitedUser.getEmail());
			}
			return responseBody.setMessages(bundle.getAllMessages());
		}
		Invitation invitation = invitationService.findInvitationRecruitingCampaign(id, form.getEmail());

		InvitationUserRegistrationDTO dto = new InvitationUserRegistrationDTO();
		dto.setAddressTypeCode(AddressType.COMPANY);
		dto.setCampaignId(id);
		dto.setIndustryId(form.getIndustryId());
		dto.setManageWork(false);
		dto.setFindWork(true);
		dto.setPassword(form.getPassword());
		dto.setWorkPhone(form.getWorkPhone());
		dto.setWorkPhoneExtension(form.getWorkPhoneExtension());
		dto.setPostalCode(form.getPostalCode());
		dto.setOperatingAsIndividualFlag(form.getOperatingAsIndividual());
		dto.setCountry(Country.valueOf(form.getCountry()).getId());
		dto.setCity(form.getCity());
		dto.setCompanyName(form.getCompanyName());
		dto.setFirstName(form.getFirstName());
		dto.setLastName(form.getLastName());
		dto.setEmail(form.getEmail());
		dto.setLongitude(form.getLongitude());
		dto.setLatitude(form.getLatitude());
		dto.setState(form.getState());
		dto.setAddress1(form.getAddress1());
		dto.setWorkPhoneInternationalCode(form.getWorkPhoneInternationalCode());
		dto.setLinkedin(false);
		dto.setOperatingAsIndividualFlag(form.getOperatingAsIndividual());
		if (invitation != null) {
			dto.setInvitationId(invitation.getId());
		}

		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		try {
			boolean isBuyer = RegisterUserForm.MANAGE_LABOR.equals(form.getRegistrationType());
			User result = checkNotNull(registrationService.registerUserSimple(dto, isBuyer));

			userService.saveOrUpdatePersonaPreference(new PersonaPreference()
				.setUserId(result.getId())
				.setBuyer(isBuyer)
				.setSeller(!isBuyer));
			return responseBody.setSuccessful(true).setRedirect(String.format("redirect:/register/thankyou?c=%s", encryptedId));

		} catch (Exception e) {
			LOGGER.error(e);
			messageHelper.addError(bundle, "register.campaign.exception");
		}

		return responseBody.setMessages(bundle.getAllMessages());
	}


	/**
	 * Registration via invitation
	 */
	@RequestMapping(
		value = "/invitation/{encryptedId}",
		method = GET)
	public String showInvitation(
		@PathVariable String encryptedId,
		@ModelAttribute("form") RecruitingRegistrationForm form,
		Model model) {

		Invitation invitation = loadAndValidateInvitation(encryptedId);
		form.setFirstName(invitation.getFirstName());
		form.setLastName(invitation.getLastName());
		form.setEmail(invitation.getEmail());

		model.addAttribute("invitation", invitation);
		model.addAttribute("type", "invitation");
		List<CallingCode> callingCodes = invariantDataService.findAllActiveCallingCodes();
		model.addAttribute("callingCodesList", callingCodes);

		if (invitation.getCompany() != null) {
			model.addAttribute("company", companyService.findCompanyById(invitation.getCompany().getId()));
		}

		return "web/pages/register/index";
	}


	@RequestMapping(
		value = "/invitation/{encryptedId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder submitInvitation(
		@PathVariable String encryptedId,
		@Valid @ModelAttribute("form") RecruitingRegistrationForm form,
		BindingResult bind,
		RedirectAttributes flash) throws Exception {

		Invitation invitation = loadAndValidateInvitation(encryptedId);

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		AjaxResponseBuilder responseBody = new AjaxResponseBuilder().setRedirect("/register/invitation/{encryptedId}").setSuccessful(false);

		passwordValidator.validate(form.getPassword(), form.getEmail(), bind);
		if (StringUtils.isNotBlank(form.getEmail()) && !EmailValidator.getInstance().isValid(form.getEmail())) {
			bind.addError(new ObjectError("email", new String[]{"Pattern"}, new String[]{"Email"}, ""));
		}

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return responseBody.setMessages(bundle.getAllMessages());
		}

		// check if user already exists and redirect accordingly
		User invitationUser = userService.findUserByEmail(form.getEmail());
		if (invitationUser != null) {
			if (authenticationService.isSuspended(invitationUser)) {
				messageHelper.addNotice(bundle, "register.invitation.suspended");
			} else {
				messageHelper.addError(bundle, "register.emailuseerror", invitationUser.getEmail());
			}
			return responseBody.setMessages(bundle.getAllMessages());
		}

		InvitationUserRegistrationDTO dto = new InvitationUserRegistrationDTO();
		dto.setAddressTypeCode(AddressType.COMPANY);
		dto.setInvitationId(invitation.getId());
		dto.setIndustryId(form.getIndustryId());
		dto.setManageWork(false);
		dto.setFindWork(true);
		dto.setPassword(form.getPassword());
		dto.setOperatingAsIndividualFlag(form.getOperatingAsIndividual());
		dto.setPostalCode(form.getPostalCode());
		dto.setWorkPhone(form.getWorkPhone());
		dto.setWorkPhoneExtension(form.getWorkPhoneExtension());
		dto.setLongitude(form.getLongitude());
		dto.setLatitude(form.getLatitude());
		dto.setCity(form.getCity());
		dto.setCountry(Country.valueOf(form.getCountry()).getId());
		dto.setWorkPhoneInternationalCode(form.getWorkPhoneInternationalCode());
		dto.setCompanyName(form.getCompanyName());
		dto.setState(form.getState());
		dto.setAddress1(form.getAddress1());
		dto.setFirstName(form.getFirstName());
		dto.setLastName(form.getLastName());
		dto.setEmail(form.getEmail());


		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		try {
			registrationService.registerUserSimple(dto, false);
			return responseBody.setSuccessful(true).setRedirect(String.format("redirect:/register/thankyou?i=%s", encryptedId));
		} catch (Exception e) {
			LOGGER.error(e);
			messageHelper.addError(bundle, "register.invitation.exception");
		}

		return responseBody.setMessages(bundle.getAllMessages());
	}


	/**
	 * Thank you
	 */
	@RequestMapping(
		value = "/thankyou",
		method = GET)
	public String thankyou(
		@RequestParam(value = "u", required = false) String encryptedUserId,
		@RequestParam(value = "c", required = false) String encryptedCampaignId,
		@RequestParam(value = "i", required = false) String encryptedInvitationId,
		Model model) {

		if (null != encryptedUserId) {
			User user = userService.findUserByEncryptedId(encryptedUserId);
			model.addAttribute("user", user);
		}

		if (encryptedCampaignId != null) {
			Long campaignId = encryptionService.decryptId(encryptedCampaignId);
			RecruitingCampaign campaign = recruitingService.findRecruitingCampaign(campaignId);
			if (campaign != null && campaign.getCompany() != null) {
				Company company = companyService.findCompanyById(campaign.getCompany().getId());
				model.addAttribute("company", company);
				model.addAttribute("campaign", campaign);
			}
		}

		if (encryptedInvitationId != null) {
			Long invitationId = encryptionService.decryptId(encryptedInvitationId);
			Invitation invitation = invitationService.findInvitationById(invitationId);
			if (invitation != null && invitation.getCompany() != null) {
				Company company = companyService.findCompanyById(invitation.getCompany().getId());
				model.addAttribute("company", company);
				model.addAttribute("invitation", invitation);
			}
		}

		model.addAttribute("feedValidationConstants", FeedRequestParamsValidator.VALIDATION_CONSTANTS);
		return "web/pages/register/thankyou";
	}

	private RecruitingCampaign loadAndValidateCampaign(String encryptedId) {

		RecruitingCampaign campaign;
		try {
			Long id = encryptionService.decryptId(encryptedId);
			campaign = recruitingService.findRecruitingCampaign(id);
		} catch (Exception e) {
			LOGGER.error(e);
			campaign = null;
		}

		if (campaign == null) {
			throw new HttpException404().setMessageKey("register.campaign.notfound").setRedirectUri("redirect:/register/campaign");
		}
		if (!campaign.isActive()) {
			throw new HttpException404().setMessageKey("register.campaign.expired").setRedirectUri("redirect:/register/campaign");
		}
		return campaign;
	}


	private Invitation loadAndValidateInvitation(String encryptedId) {
		Long id = encryptionService.decryptId(encryptedId);
		Invitation invitation = invitationService.findInvitationById(id);
		if (invitation == null) {
			throw new HttpException404().setMessageKey("register.invitation.notfound");
		}
		return invitation;
	}


	private void saveImpression(Long campaignId, HttpServletRequest request) {
		ImpressionDTO impressionDTO = new ImpressionDTO();
		impressionDTO.setCampaignId(campaignId);
		impressionDTO.setImpressionTypeId((long) ImpressionType.RECRUITING.ordinal());
		impressionDTO.setReferrer(request.getHeader("referer"));
		impressionDTO.setUserAgent(request.getHeader("User-Agent"));
		if (getCurrentUser() != null) {
			impressionDTO.setUserId(getCurrentUser().getId());
		}

		try {
			authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
			analyticsService.saveOrUpdateImpression(impressionDTO);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}
}
