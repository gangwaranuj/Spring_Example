package com.workmarket.web.controllers.settings;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.IndustryPagination;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.option.CompanyOption;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.ManageMyWorkMarketDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.option.OptionsService;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException403;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.MmwValidator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.lang.Object;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/settings/manage")
public class SettingsManageFormController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(SettingsManageFormController.class);

	@Autowired private ProfileService profileService;
	@Autowired private CompanyService companyService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private MmwValidator validator;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private InvariantDataService invariantDataService;
	@Qualifier("companyOptionsService") @Autowired protected OptionsService<Company> companyOptionsService;

	private static String DEFAULT_CODE_OF_CONDUCT = null;
	private final static String DEFAULT_CODE_OF_CONDUCT_KEY = "code.of.conduct";
	private static final String WORK_MARKET_LOGO_ON_PRINT_OUT_KEY = "wm";
	private static final String COMPANY_LOGO_ON_PRINT_OUT_KEY = "company";
	private static final String NO_LOGO_ON_PRINT_OUT_KEY = "none";

	static {
		try {
			Properties properties = PropertiesLoaderUtils.loadAllProperties("codeOfConduct.properties");
			DEFAULT_CODE_OF_CONDUCT = properties.getProperty(DEFAULT_CODE_OF_CONDUCT_KEY);
		} catch (IOException e) {
			logger.error("Failed initial load of default code of conduct", e);
		}
	}

	@ModelAttribute("mmw")
	public ManageMyWorkMarket createModel() {
		return MoreObjects.firstNonNull(companyService.getManageMyWorkMarket(getCurrentUser().getCompanyId()), new ManageMyWorkMarket());
	}

	@RequestMapping(
		method = GET)
	public String showAssignmentSettings(
		@ModelAttribute("mmw")
		ManageMyWorkMarket mmw,
		Model model,
		HttpServletRequest request) {

		unescapeMMWTextFields(mmw);
		String customSignatureLine;
		String assignmentAgingEmail = StringUtils.EMPTY;

		Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());

		if ("POST".equalsIgnoreCase(request.getMethod())) {
			customSignatureLine = request.getParameter("custom_signature_line");
			assignmentAgingEmail = request.getParameter("assignment_aging_email");
		} else {
			customSignatureLine = company.getCustomSignatureLine();
			List<Email> emails = Lists.newArrayList(company.getAgingAlertEmails());
			if (CollectionUtils.isNotEmpty(emails)) {
				assignmentAgingEmail = emails.get(0).getEmail();
			}
		}

		if (StringUtils.isEmpty(mmw.getStandardInstructions())) {
			mmw.setStandardInstructions(DEFAULT_CODE_OF_CONDUCT);
		}

		CompanyPreference companyPreference = company.getCompanyPreference();
		model.addAttribute("mboEnabled", companyOptionsService.hasOption(company, CompanyOption.MBO_ENABLED, "true"));
		model.addAttribute("mboUsageRequired", companyOptionsService.hasOption(company, CompanyOption.MBO_REQUIRED, "true"));
		model.addAttribute("assignment_aging_email", assignmentAgingEmail);
		model.addAttribute("custom_signature_line", customSignatureLine);
		model.addAttribute("printedForm", getPrintoutTypeForAssignment(mmw));
		model.addAttribute("printedFormLogo", getPrintedFormLogo(mmw));
		model.addAttribute("enabled", Boolean.TRUE);
		if (companyPreference != null) {
			model.addAttribute("requireUniqueIdFlag", companyPreference.isExternalIdActive());
			model.addAttribute("uniqueIdName", companyPreference.getExternalIdDisplayName());
			model.addAttribute("uniqueIdNameVersion", companyPreference.getExternalIdVersion());
		}
		model.addAttribute("hideContactEnabled", companyOptionsService.hasOption(company, CompanyOption.HIDE_CONTACT_ENABLED, "true"));

		model.addAttribute("documentsEnabled", !companyOptionsService.hasOption(company, CompanyOption.DOCUMENTS_ENABLED, "false"));

		return "web/pages/settings/manage/index";
	}

	@RequestMapping(method = POST)
	public String saveAssignmentSettings(
		@RequestParam(value = "printedForm", required = true) String printedForm,
		@RequestParam(value = "mboUsageRequired", required = false) boolean mboUsageRequired,
		@RequestParam(value = "mboEnabledFlag", required = false) boolean mboEnabledFlag,
		@RequestParam(value = "requireUniqueIdFlag", required = false) boolean requireUniqueIdFlag,
		@RequestParam(value = "documentsEnabled", required = false) boolean documentsEnabled,
		@RequestParam(value = "hideContactEnabled", required = false) boolean hideContactEnabled,
		@ModelAttribute("mmw") ManageMyWorkMarket mmw,
		BindingResult result,
		Model model,
		HttpServletRequest request) {

		Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());
		if (getCurrentUser().isMboServiceType()) {
			companyOptionsService.setOption(company, CompanyOption.MBO_REQUIRED, String.valueOf(mboUsageRequired));
			companyOptionsService.setOption(company, CompanyOption.MBO_ENABLED, String.valueOf(mboEnabledFlag));
		}

		companyOptionsService.setOption(company, CompanyOption.HIDE_CONTACT_ENABLED, String.valueOf(hideContactEnabled));

		validator.validate(mmw, result);

		MessageBundle bundle = messageHelper.newBundle(model);
		String agingAlertEmail = request.getParameter("assignment_aging_email");
		if (mmw.getAgingAssignmentAlertEnabled()) {
			if (StringUtils.isBlank(agingAlertEmail)) {
				messageHelper.addError(bundle, "mmw.manage.aging_email.empty");
			} else if (!EmailValidator.getInstance().isValid(agingAlertEmail)) {
				messageHelper.addError(bundle, "mmw.manage.aging_email.invalid");
			}
		}

		companyOptionsService.setOption(company, CompanyOption.DOCUMENTS_ENABLED, String.valueOf(documentsEnabled));

		String uniqueIdName = request.getParameter("uniqueIdName");
		if (requireUniqueIdFlag) {
			if (StringUtils.isBlank(uniqueIdName)) {
				messageHelper.addError(bundle, "mmw.manage.unique_id_name.empty");
			} else if (uniqueIdName.length() > 50) {
				messageHelper.addError(bundle, "mmw.manage.unique_id_name.max_length");
			}
		}

		if(result.hasErrors() || bundle.hasErrors()) {
			messageHelper.setErrors(bundle, result);
			return showAssignmentSettings(mmw, model, request);
		}

		if (StringUtils.equals(printedForm, WORK_MARKET_LOGO_ON_PRINT_OUT_KEY)) {
			mmw.setEnableAssignmentPrintout(true);
		}

		else if (StringUtils.equals(printedForm, COMPANY_LOGO_ON_PRINT_OUT_KEY)) {
			mmw.setEnableAssignmentPrintout(false);
		}

		String printedFormLogo = request.getParameter("printedFormLogo");

		if (StringUtils.equals(printedFormLogo, COMPANY_LOGO_ON_PRINT_OUT_KEY)) {
			mmw.setHideWorkMarketLogoFlag(true);
			mmw.setUseCompanyLogoFlag(true);
		}
		else if (StringUtils.equals(printedFormLogo, WORK_MARKET_LOGO_ON_PRINT_OUT_KEY)) {
			mmw.setHideWorkMarketLogoFlag(false);
			mmw.setUseCompanyLogoFlag(false);
		}
		else if (StringUtils.equals(printedFormLogo, NO_LOGO_ON_PRINT_OUT_KEY)) {
			mmw.setHideWorkMarketLogoFlag(true);
			mmw.setUseCompanyLogoFlag(false);
		}

		mmw.setStandardTermsFlag(StringUtils.isNotEmpty(mmw.getStandardTerms()));
		mmw.setStandardTermsEndUserFlag(StringUtils.isNotEmpty(mmw.getStandardTermsEndUser()));

		if (StringUtils.isEmpty(mmw.getStandardInstructions())) {
			mmw.setStandardInstructions(DEFAULT_CODE_OF_CONDUCT);
		}

		try {
			CompanyPreference companyPreference = company.getCompanyPreference();
			if (uniqueIdName != null && !uniqueIdName.equals(companyPreference.getExternalIdDisplayName())) {
				companyPreference.setExternalIdDisplayName(uniqueIdName);
				companyPreference.setExternalIdActive(requireUniqueIdFlag);
			}
			companyService.updateCompanyPreference(companyPreference);
		} catch (Exception ex) {
			logger.error("error updating CompanyPreference for company id={}", getCurrentUser().getCompanyId(), ex);
			messageHelper.addError(bundle, "mmw.manage.update.failure");
		}

		try {
			ManageMyWorkMarketDTO dto = new ManageMyWorkMarketDTO();
			BeanUtils.copyProperties(mmw, dto);
			dto.setCustomSignatureLine(request.getParameter("custom_signature_line"));

			Long companyId = getCurrentUser().getCompanyId();
			profileService.updateManageMyWorkMarket(companyId, dto);

			EmailAddressDTO emailAddressDTO = new EmailAddressDTO();
			emailAddressDTO.setEmail(agingAlertEmail);
			companyService.saveAssignmentAlertEmailToCompany(companyId, emailAddressDTO);
			messageHelper.addSuccess(bundle, "mmw.manage.update.success");
		}
		catch (Exception ex) {
			logger.error("error updating ManageMyWorkMarket for company id={}", getCurrentUser().getCompanyId(), ex);
			messageHelper.addError(bundle, "mmw.manage.update.failure");
		}

		return showAssignmentSettings(mmw, model, request);
	}

	@PreAuthorize("hasAnyRole('ACL_ADMIN','ACL_MANAGER')")
	@RequestMapping(
			value = "/approvals",
			method = GET)
	public String approvals(Model model) {
		if (getCurrentUser().isSeller()) {
			throw new HttpException403("You do not have access to the requested template.");
		}
		if (!hasFeature("MultipleApprovals")) {
			return "web/pages/settings/manage/index";
		}
		return "web/pages/settings/manage/approvals/index";
	}

	@RequestMapping(
		value = "/feed",
		method = GET)
	public String work(Model model) {

		IndustryPagination pagination = new IndustryPagination();
		pagination.setReturnAllRows();
		pagination = invariantDataService.findAllIndustries(pagination);
		List<Industry> industries = pagination.getResults();

		model.addAttribute("companyId", getCurrentUser().getCompanyId());
		model.addAttribute("companyName", getCurrentUser().getCompanyName());
		model.addAttribute("industries", industries);

		return "web/pages/settings/manage/feed";
	}

	protected String getPrintedFormLogo(ManageMyWorkMarket mmw) {
		if (mmw.getHideWorkMarketLogoFlag() && mmw.getUseCompanyLogoFlag()) {
			return COMPANY_LOGO_ON_PRINT_OUT_KEY;
		} else if (!mmw.getHideWorkMarketLogoFlag() && !mmw.getUseCompanyLogoFlag()) {
			return WORK_MARKET_LOGO_ON_PRINT_OUT_KEY;
		} else if (mmw.getHideWorkMarketLogoFlag() && !mmw.getUseCompanyLogoFlag()) {
			return NO_LOGO_ON_PRINT_OUT_KEY;
		}

		return null;
	}

	protected String getPrintoutTypeForAssignment(ManageMyWorkMarket mmw) {
		if (!mmw.isEnableAssignmentPrintout()) {
			return COMPANY_LOGO_ON_PRINT_OUT_KEY;
		}

		return WORK_MARKET_LOGO_ON_PRINT_OUT_KEY;
	}

	private void unescapeMMWTextFields(ManageMyWorkMarket mmw){
		if (mmw != null) {
			if (mmw.getStandardTermsFlag()) {
				mmw.setStandardTerms(StringEscapeUtils.unescapeHtml4(mmw.getStandardTerms()));
			}

			if (mmw.isStandardTermsEndUserFlag()) {
				mmw.setStandardTermsEndUser(StringEscapeUtils.unescapeHtml4(mmw.getStandardTermsEndUser()));
			}

			if (mmw.getStandardInstructionsFlag()) {
				mmw.setStandardInstructions(StringEscapeUtils.unescapeHtml4(mmw.getStandardInstructions()));
			}

		}
	}
}
