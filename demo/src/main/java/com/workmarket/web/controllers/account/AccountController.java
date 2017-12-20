package com.workmarket.web.controllers.account;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.asset.type.CompanyAssetAssociationType;
import com.workmarket.domains.model.company.CustomerType;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.builders.CompanyDetailsFormBuilder;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.converters.AddressFormToAddressDTOConverter;
import com.workmarket.web.forms.account.CompanyDetailsForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.UploadHelper;
import com.workmarket.web.models.BaseResponse;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.AddressValidator;
import com.workmarket.web.validators.FilenameValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.workmarket.utility.CollectionUtilities.newObjectMap;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping("/account")
public class AccountController extends BaseController {

	private static final Log logger = LogFactory.getLog(AccountController.class);
	private static final int TEN_MB = 10 * 1024 * 1024;
	private static final int TWO_MB = 2 * 1024 * 1024;

	@Autowired private ProfileService profileService;
	@Autowired private CompanyService companyService;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private FormOptionsDataHelper formDataHelper;
	@Autowired private FilenameValidator filenameValidator;
	@Autowired private AddressValidator addressValidator;
	@Autowired private EventRouter eventRouter;
	@Autowired private CompanyDetailsFormBuilder companyDetailsFormBuilder;
	@Autowired private AddressFormToAddressDTOConverter addressFormToAddressDTOConverter;

	@RequestMapping(method = RequestMethod.GET)
	public String indexShow(Model model) {
		Long id = getCurrentUser().getId();

		Company company = profileService.findCompany(id);
		Address companyAddress = profileService.findCompanyAddress(id);
		CompanyDetailsForm form = companyDetailsFormBuilder.build(company, companyAddress);

		if (company != null) {
			model.addAttribute("companyNumber", company.getCompanyNumber());
		}

		model.addAttribute("company_form", form);
		model.addAttribute("statesCountries", formDataHelper.getStatesAsOptgroup());
		model.addAttribute("countries", formDataHelper.getCountries());
		model.addAttribute("isMbo", getCurrentUser().isMbo());

		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/account"));
		return "web/pages/account/index";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String indexSubmit(
			@Valid @ModelAttribute("company_form") CompanyDetailsForm form,
			BindingResult bind,
			RedirectAttributes flash,
			Model model) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		AddressDTO dto = addressFormToAddressDTOConverter.convert(form);
		addressValidator.validate(dto, bind);

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/account";
		}
		Long userId = getCurrentUser().getId();
		Company company = profileService.findCompany(userId);

		company.setName(form.getName());
		company.setWebsite(form.getWebsite());
		company.setEmployees(form.getEmployees());
		company.setYearFounded(form.getYearfounded());
		company.setEmployedProfessionals(form.getEmployedprofessionals());

		Company updatedCompany = profileService.saveOrUpdateCompany(company);
		Address updatedCompanyAddress = profileService.saveOrUpdateCompanyAddress(userId, dto);

		companyService.setOverview(company.getId(), form.getOverview());

		if (updatedCompany != null && updatedCompanyAddress != null) {
			List<String> blacklistedCodes = profileService.findBlacklistedZipcodesForUser(userId);
			if (CollectionUtils.isNotEmpty(blacklistedCodes) && blacklistedCodes.contains(form.getPostalCode())) {
				blacklistedCodes.remove(form.getPostalCode());
				profileService.setBlacklistedZipcodesForUser(userId, blacklistedCodes);
			}

			messageHelper.addSuccess(bundle, "account.update.success");
			return "redirect:/account";
		} else {
			messageHelper.setErrors(bundle, bind);
		}

		Address companyAddress = profileService.findCompanyAddress(userId);
		model.addAttribute("company_form", companyDetailsFormBuilder.build(company, companyAddress));
		model.addAttribute("isMbo", getCurrentUser().isMbo());

		return "web/pages/account/index";
	}

	@RequestMapping(
		value = "/logoupload",
		method = RequestMethod.POST,
		produces = MediaType.APPLICATION_JSON_VALUE,
		consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE
	)
	public @ResponseBody Map<String, Object> logoUpload(HttpServletRequest request) throws IOException, HostServiceException {

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
		MessageBundle messages = messageHelper.newBundle();
		if (!UploadHelper.isValidMimeType(contentType)) {
			messageHelper.addError(messages, "upload.invalid", contentType);
			return newObjectMap(
					"successful", false,
					"errors", messages.getErrors()
			);
		}

		Long companyId = getCurrentUser().getCompanyId();
		File file = File.createTempFile("company_logo_" + companyId, ".dat");
		int bytesCopied;
		try (FileOutputStream fos = new FileOutputStream(file)) {
			bytesCopied = IOUtils.copy(request.getInputStream(), fos);
		}

		if (bytesCopied > TEN_MB) {
			messageHelper.addError(messages, "upload.sizelimit", TEN_MB / (1024 * 1024));
			return newObjectMap(
				"successful", false,
				"errors", messages.getErrors()
			);
		}

		AssetDTO dto = new AssetDTO();
		dto.setMimeType(MimeTypeUtilities.guessMimeType(fileName));
		dto.setName(fileName);
		dto.setDescription("Company logo");
		dto.setFileByteSize(bytesCopied);
		dto.setSourceFilePath(file.getAbsolutePath());
		dto.setAssociationType(CompanyAssetAssociationType.AVATAR);
		dto.setLargeTransformation(true);
		dto.setSmallTransformation(true);

		try {
			assetManagementService.storeAssetForCompany(dto, companyId);
		} catch (AssetTransformationException e) {
			messageHelper.addError(messages, "account.logoupload.exception", contentType);
			return newObjectMap(
				"successful", false,
				"errors", messages.getErrors()
			);
		}

		CompanyAssetAssociation avatars = companyService.findCompanyAvatars(companyId);

		return newObjectMap(
			"successful", true,
			"asset_id", avatars.getAsset().getId(),
			"asset", newObjectMap(
				"id", avatars.getSmall().getId(),
				"uuid", avatars.getSmall().getUUID(),
				"uri", avatars.getSmall().getUri()
			)
		);
	}

	@RequestMapping(
		value = "/logoupload",
		method = RequestMethod.POST,
		produces = MediaType.TEXT_HTML_VALUE,
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public @ResponseBody String logoUploadForIE(
		@RequestParam("qqfile") MultipartFile attachment) throws IOException, HostServiceException {

		String fileName = attachment.getOriginalFilename();
		String contentType = attachment.getContentType();
		MessageBundle messages = messageHelper.newBundle();
		if (!UploadHelper.isValidMimeType(contentType)) {
			messageHelper.addError(messages, "upload.invalid", contentType);
			return new JSONObject(newObjectMap(
					"successful", false,
					"errors", messages.getErrors()
			)).toString();
		}

		Long companyId = getCurrentUser().getCompanyId();
		File file = File.createTempFile("company_logo_" + companyId, ".dat");

		int bytesCopied;
		try (FileOutputStream fos = new FileOutputStream(file)) {
			bytesCopied = IOUtils.copy(attachment.getInputStream(), fos);
		}

		if (bytesCopied > TWO_MB) {
			messageHelper.addError(messages, "upload.sizelimit", TWO_MB / (1024 * 1024));
			return new JSONObject(newObjectMap(
					"successful", false,
					"errors", messages.getErrors()
			)).toString();
		}

		AssetDTO dto = new AssetDTO();
		dto.setMimeType(MimeTypeUtilities.guessMimeType(fileName));
		dto.setName(fileName);
		dto.setDescription("Company logo");
		dto.setFileByteSize(bytesCopied);
		dto.setSourceFilePath(file.getAbsolutePath());
		dto.setAssociationType(CompanyAssetAssociationType.AVATAR);
		dto.setLargeTransformation(true);
		dto.setSmallTransformation(true);

		try {
			assetManagementService.storeAssetForCompany(dto, companyId);
		} catch (AssetTransformationException e) {
			messageHelper.addError(messages, "account.logoupload.exception", contentType);
			return new JSONObject(newObjectMap(
					"successful", false,
					"errors", messages.getErrors()
			)).toString();
		}

		CompanyAssetAssociation avatars = companyService.findCompanyAvatars(companyId);

		return new JSONObject(newObjectMap(
				"successful", true,
				"asset_id", avatars.getAsset().getId(),
				"asset", newObjectMap(
				"id", avatars.getSmall().getId(),
				"uuid", avatars.getSmall().getUUID(),
				"uri", avatars.getSmall().getUri()
		))).toString();
	}

	@RequestMapping(
		value = "/logodelete",
		method = RequestMethod.POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map logodelete() {
		Long companyId = getCurrentUser().getCompanyId();
		CompanyAssetAssociation avatars = companyService.findCompanyAvatars(companyId);

		assetManagementService.removeAssetFromCompany(avatars.getAsset().getId(), companyId);

		return CollectionUtilities.newObjectMap();
	}

	@RequestMapping(value = "/updateoverview", method = RequestMethod.POST)
	public @ResponseBody BaseResponse updateOverview(
		@RequestParam(value = "overview", required = false) String overview) {

		BaseResponse response = new BaseResponse();
		MessageBundle bundle = messageHelper.newBundle();

		if (StringUtils.isBlank(overview)) {
			bundle.addError(messageHelper.getMessage("account.updateoverview.empty"));
		} else if (overview.length() > Company.COMPANY_OVERVIEW_MAX_LENGTH) {
			bundle.addError(messageHelper.getMessage("account.updateoverview.max.length"));
		}

		if (bundle.hasErrors()) {
			response.setErrors(bundle.getErrors());
			response.setSuccessful(false);
			return response;
		}

		Company company = profileService.findCompanyById(getCurrentUser().getCompanyId());
		if (company == null) {
			bundle.addError(messageHelper.getMessage("account.updateoverview.exception"));
			response.setErrors(bundle.getErrors());
			response.setSuccessful(false);
		} else {
			companyService.setOverview(company.getId(), overview);
			response.setSuccessful(true);
		}

		return response;
	}

	@RequestMapping(
		value = "/vendor_search_status",
		method = RequestMethod.POST)
	public @ResponseBody AjaxResponseBuilder updateListInVendorSearch(
		@RequestBody Map<String, Boolean> listToggle) {

		companyService.updateListInVendorSearch(getCurrentUser().getCompanyId(), listToggle.get("isInVendorSearch"));
		return AjaxResponseBuilder.success();
	}

	@RequestMapping(
		value = "/vendor_search_status",
		method = RequestMethod.GET)
	@ResponseBody
	public AjaxResponseBuilder getVendorSearchStatus() {
		AjaxResponseBuilder ajaxResponseBuilder = AjaxResponseBuilder.fail();
		Company company = companyService.findById(getCurrentUser().getCompanyId());
		if (company == null) {
			return ajaxResponseBuilder;
		}

		boolean isBuyer = companyService.getCustomerType(company.getId()).equals(CustomerType.BUYER.value());
		return ajaxResponseBuilder.setData(
			ImmutableMap.<String, Object>of(
				"isInVendorSearch", company.isInVendorSearch(),
				"hasAtLeastOneDispatcher", companyService.hasAtLeastOneActiveDispatcher(company.getId()),
				"hasAtLeastOneWorker", companyService.hasAtLeastOneActiveWorker(company.getId()),
				/*
				added the isBuyer flag to differentiate worker and buyer side to support the new business logic
				for buyer company to be able to opt into vendor search and be backward compatible with the worker company.
				see account_index.js for the corresponding change in FE.
				*/
				"isBuyer", isBuyer
			)
		).setSuccessful(true);
	}

}
