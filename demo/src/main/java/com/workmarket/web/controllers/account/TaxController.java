package com.workmarket.web.controllers.account;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.TaxEntityDTO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.vault.services.VaultServerService;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.TaxEntityValidator;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@RequestMapping("/account/tax")
@Controller
public class TaxController extends BaseController {

	@Autowired private JsonSerializationService jsonService;
	@Autowired private TaxService taxService;
	@Autowired private TaxEntityValidator taxEntityValidator;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private FormOptionsDataHelper formDataHelper;
	@Autowired private CompanyService companyService;
	@Autowired private VaultHelper vaultHelper;
	@Autowired VaultServerService vaultServerService;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private ProfileService profileService;

	private static final Log logger = LogFactory.getLog(TaxController.class);

	@RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
	public String showTax(@RequestParam(value = "return_to", required = false) String returnTo, Model model) {

		List<? extends AbstractTaxEntity> taxEntities = taxService.findAllTaxEntities(getCurrentUser().getId());

		List<TaxEntityDTO> dtos = Lists.newArrayList();
		if (taxEntities != null) {
			for (AbstractTaxEntity taxEntity : taxEntities) {
				TaxEntityDTO dto = TaxEntityDTO.toDTO(taxEntity);
				final String taxNumber = vaultHelper.get(taxEntity, "taxNumber", taxEntity.getTaxNumberSanitized()).getValue();
        taxEntity.setTaxNumber(taxNumber);
				dto.setTaxNumber(taxEntity.getSecureTaxNumber());

				// use user timezone for display
				if (dto.getSignatureDateAsCalendar() != null) {
					Calendar signatureStringUserTz = DateUtilities.changeTimeZone(dto.getSignatureDateAsCalendar(), getCurrentUser().getTimeZoneId());
					dto.setSignatureDateStringFromCalendar(signatureStringUserTz);
				}
				dtos.add(dto);
			}
		}

		model.addAttribute("taxEntities", jsonService.toJson(dtos));
		model.addAttribute("returnTo", returnTo);
		Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());
		String defaultCountry = "";
		if (company.getAddress() != null && company.getAddress().getCountry() != null) {
			defaultCountry = AbstractTaxEntity.getCountryFromCountryId(company.getAddress().getCountry().getId());
			model.addAttribute("defaultCountry", defaultCountry);
		}

		model.addAttribute("states", formDataHelper.getStates(Country.USA));
		model.addAttribute("provinces", formDataHelper.getStates(Country.CANADA));
		model.addAttribute("countries", formDataHelper.getCountries());
		model.addAttribute("allCountries", formDataHelper.getAllCountries());

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "tax",
			"data", CollectionUtilities.newObjectMap(
				"defaultCounty", defaultCountry,
				"taxEntities", dtos,
				"isMasquerading", getCurrentUser().isMasquerading()
			),
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/account/tax";
	}


	@RequestMapping(value = {"", "/"}, method = { RequestMethod.POST, RequestMethod.PUT }, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("!principal.isMasquerading()")
	public @ResponseBody AjaxResponseBuilder submitTax(@RequestBody String modelStr) {
		// find our latest tax entity - this is done to help in the handling of the masked ssn
		// our validation doesn't support the masked ssn so we pull this and determine if we have
		// a new tax id entered and if so then we validate it, otherwise we keep what was there
		List<? extends AbstractTaxEntity> taxEntities = taxService.findAllTaxEntities(getCurrentUser().getId());
		AbstractTaxEntity latest = null;
		if (isNotEmpty(taxEntities)) {
			for (AbstractTaxEntity ate : taxEntities) {
				if ((latest == null) || (latest.getId() < ate.getId())) {
					latest = ate;
				}
			}
		}

		String model = StringEscapeUtils.unescapeHtml4(modelStr);
		String returnTo = (String) jsonService.fromJson(model, Map.class).get("return");
		TaxEntityDTO dto = jsonService.fromJson(model, TaxEntityDTO.class);

		if (latest != null) {
			if (dto.getTaxNumber() != null) {
				if (StringUtils.equals(latest.getSecureFormattedTaxNumber(), dto.getTaxNumber())) {
					dto.setTaxNumber(latest.getTaxNumberSanitized());
				}
			}
		}

		if (dto.getBusinessFlag()) {
			if (AbstractTaxEntity.COUNTRY_CANADA.equals(dto.getTaxCountry()) ||
				AbstractTaxEntity.COUNTRY_OTHER.equals(dto.getTaxCountry()) ) {
				dto.setBusinessName(dto.getTaxName());
			} else{
				dto.setTaxName(dto.getLastName());
			}
		} else {
			dto.setTaxName(dto.getFullName());
		}

		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setSuccessful(false)
			.setData(CollectionUtilities.newObjectMap(
				"tax_entity", jsonService.toJson(dto)));

		BindingResult bind = new BeanPropertyBindingResult(dto, "tax_entity");
		taxEntityValidator.validate(dto, bind);

		MessageBundle bundle = messageHelper.newBundle();

		if (bind.hasFieldErrors()) {
			messageHelper.setErrors(bundle, bind);
			return response.setMessages(bundle.getAllMessages());
		}

		if (taxService.hasTaxEntityPendingApproval(getCurrentUser().getId(), dto.getTaxNumber())) {
			messageHelper.addError(bundle, "account.tax.pending_approval");
			return response.setMessages(bundle.getAllMessages());
		}

		try {
			Long userId = getCurrentUser().getId();

			AbstractTaxEntity existingTaxEntity = taxService.findActiveTaxEntity(userId);

			/* For US tax info, user needs to include effectiveDate for the IRS if:
			 * - entity type changes from business -> individual
			 * - SSN changes
			 * - EIN changes
			 * - tax name changes
			 * - existing TIN is in 'approved' status
			 */
			if (existingTaxEntity != null) {
				if (existingTaxEntity.getStatus() != null) {
					dto.setTaxVerificationStatusCode(existingTaxEntity.getStatus().getCode());
				}

				if (AbstractTaxEntity.COUNTRY_USA.equals(dto.getCountry())) {
					UsaTaxEntity usaEntity = (UsaTaxEntity) existingTaxEntity;
					TaxVerificationStatusType existingTaxEntityStatus = TaxVerificationStatusType.newInstance(usaEntity.getStatus().getCode());

					// if any of the IRS Match fields change, they must include effective_date
					if (existingTaxEntityStatus.isApproved() && taxService.areIrsMatchFieldsDifferent(dto, usaEntity)) {

						if (isBlank(dto.getEffectiveDateString())) {
							bind.rejectValue("effectiveDateString", "account.tax.effective_date.empty");
						} else if (dto.getEffectiveDateAsCalendar() == null) {
							bind.rejectValue("effectiveDateString", "account.tax.effective_date.format");
						}

						// make a new tax entity
						dto.setTaxVerificationStatusCode(TaxVerificationStatusType.UNVERIFIED);
					}

					if (BooleanUtils.isTrue(dto.getVerificationPending())) {
						bind.rejectValue("effectiveDateString", "account.tax.verification_pending.true");
					}
				}
			}else{
				//Once TIN/BN/SIN is validated by the system, set status to the VALIDATED for Canadian users and SIGNED_FORM_W8 for international users.
				if(AbstractTaxEntity.COUNTRY_CANADA.equalsIgnoreCase(dto.getTaxCountry()))
					dto.setTaxVerificationStatusCode(TaxVerificationStatusType.VALIDATED);
				else if(AbstractTaxEntity.COUNTRY_OTHER.equalsIgnoreCase(dto.getTaxCountry()))
					dto.setTaxVerificationStatusCode(TaxVerificationStatusType.SIGNED_FORM_W8);
			}

			if (bind.hasErrors()) {
				messageHelper.setErrors(bundle, bind);
				return response.setMessages(bundle.getAllMessages());
			}

			// search for this tax number in the system and reject if it belongs to another company
			String rawTaxNumber = dto.getTaxNumber().replaceAll("[^\\p{Alnum}]", "");
			if (isNotBlank(rawTaxNumber)) {
				if (hasGlobalFeature("checkDuplicateTin")) {
					if (vaultHelper.isDuplicateOutsideCompany(dto.getTaxCountry(), rawTaxNumber, getCurrentUser().getCompanyNumber())) {
						messageHelper.addError(bundle, "account.tax.tax_number.exists");
						return response.setMessages(bundle.getAllMessages());
					}
				} else {
					List<? extends AbstractTaxEntity> existingTins = taxService.findTaxEntitiesByTinAndCountry(rawTaxNumber, dto.getTaxCountry());
					if (isNotEmpty(existingTins)) {
						Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());
						for (AbstractTaxEntity tin : existingTins) {
							if (!company.getId().equals(tin.getCompany().getId())) {
								messageHelper.addError(bundle, "account.tax.tax_number.exists");
								return response.setMessages(bundle.getAllMessages());
							}
						}
					}

				}
			}

			// if it's not signed yet, return the result of the validation
			if (!dto.isSigned()) {
				if (bind.hasErrors()) {
					messageHelper.setErrors(bundle, bind);
				} else {
					// return a "validated but not signed" error to the sign page
					messageHelper.addError(bundle, "account.tax.signing.empty");
					response.setSuccessful(true);
				}
				return response.setMessages(bundle.getAllMessages());
			}

			AbstractTaxEntity saveResult = taxService.saveTaxEntity(userId, dto);

			if (saveResult != null) {
				profileService.sendProfileUpdateEmail(userId, "Tax information");
				return response
					.setSuccessful(true)
					.setRedirect(defaultIfEmpty(returnTo, "/account/tax"));
			} else {
				messageHelper.addError(bundle, "account.tax.exception");
			}
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "account.tax.exception");
		}

		return response.setMessages(bundle.getAllMessages());
	}
}
