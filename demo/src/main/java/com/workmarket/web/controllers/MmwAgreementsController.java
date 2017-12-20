package com.workmarket.web.controllers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.contract.Contract;
import com.workmarket.domains.model.contract.ContractPagination;
import com.workmarket.domains.model.contract.ContractVersion;
import com.workmarket.domains.model.contract.ContractVersionPagination;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.ContractService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.ContractDTO;
import com.workmarket.service.business.dto.ContractVersionAssetDTO;
import com.workmarket.service.business.dto.ContractVersionDTO;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.forms.filemanager.AddAgreementForm;
import com.workmarket.web.forms.filemanager.EditAgreementForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/agreements")
public class MmwAgreementsController extends BaseController {

	private static final Log logger = LogFactory.getLog(FilemanagerController.class);
	private static final String baseRedirect = "redirect:/agreements";

	@Autowired AssetManagementService assetService;
	@Autowired ContractService contractService;
	@Autowired MessageBundleHelper messageHelper;
	@Autowired UploadService uploadService;
	@Autowired JsonSerializationService jsonService;
	@Autowired UserService userService;

	public static final Map<Integer, String> contractListColumnSortMap = ImmutableMap.of(
		0, ContractPagination.SORTS.NAME.toString(),
		1, ContractPagination.SORTS.MODIFICATION_DATE.toString(),
		2, ContractPagination.SORTS.CREATOR.toString());


	@RequestMapping(method = GET)
	public String index() {

		return "web/pages/agreements/index";
	}

	@RequestMapping(
		value = "/available",
		method = GET)
	public void availableAgreements(HttpServletRequest httpRequest, Model model) {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(contractListColumnSortMap);

		ContractPagination pagination = new ContractPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());
		pagination.addFilter(ContractPagination.FILTER_KEYS.ACTIVE.toString(), "true");

		pagination = contractService.findAllContractsByCompanyId(getCurrentUser().getCompanyId(), pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		model.addAttribute("response", response);
		List<Long> ids = CollectionUtilities.newListPropertyProjection(pagination.getResults(), "creatorId");
		Map<Long, Map<String, Object>> props = userService.getProjectionMapByIds(ids, "firstName", "lastName");

		for (Contract contract : pagination.getResults()) {
			Long creatorId = contract.getCreatorId();
			String fullName = props.containsKey(contract.getCreatorId())
				? StringUtilities.fullName((String)props.get(creatorId).get("firstName"), (String)props.get(creatorId).get("lastName"))
				: "";

			List<String> row = Lists.newArrayList(
				contract.getName(),
				DateUtilities.format("MMM dd, yyyy", contract.getContractVersionCount() > 0 ? contractService.findMostRecentContractVersionByContractId(contract.getId()).getModifiedOn() : contract.getModifiedOn()),
				fullName,
				Integer.toString(contract.getContractVersionCount())
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", contract.getId(),
				"asset", contract.getMostRecentContractVersionId());

			response.addRow(row, meta);
		}
	}


	@RequestMapping(
		value = "/deactivate",
		method = GET)
	public String deactivateAgreement(
		@RequestParam(required = false) Long id,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (id == null) {
			messageHelper.addError(bundle, "filemanager.delete_agreement.no_selection");
			return baseRedirect;
		}

		Contract contract = contractService.findContractByIdAndCompany(id, getCurrentUser().getCompanyId());

		if (contract != null) {
			Contract result = contractService.updateContractActiveFlag(id, false);
			if (result != null)
				messageHelper.addSuccess(bundle, "filemanager.delete_agreement.success");
			else
				messageHelper.addError(bundle, "filemanager.delete_agreement.exception");
		} else {
			messageHelper.addError(bundle, "filemanager.delete_agreement.no_selection");
		}

		return baseRedirect;
	}


	@RequestMapping(
		value = "/add",
		method = GET)
	public String addAgreementShow(Model model) {

		model.addAttribute("addagreement_form", new AddAgreementForm());

		return "web/pages/agreements/add";
	}


	@RequestMapping(
		value = "/add",
		method = POST)
	private String addAgreementSubmit(
		@Valid @ModelAttribute("addagreement_form") AddAgreementForm form,
		BindingResult bind,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasFieldErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/agreements/add";
		}

		if (contractService.activeContractExists(form.getName(), getCurrentUser().getCompanyId())) {
			messageHelper.addError(bundle, "filemanager.new_agreement.duplicate");
			return "redirect:/agreements/add";
		}

		ContractDTO contractDTO = new ContractDTO();
		contractDTO.setCompanyId(getCurrentUser().getCompanyId());
		contractDTO.setName(form.getName());

		try {
			Contract savedContract = contractService.saveOrUpdateContract(contractDTO);

			if (savedContract != null) {
				ContractVersionDTO versionDTO = new ContractVersionDTO();
				versionDTO.setContractId(savedContract.getId());

				ContractVersion version = contractService.saveOrUpdateContractVersion(versionDTO);

				if (version != null) {
					ContractVersionAssetDTO assetDTO = new ContractVersionAssetDTO();
					assetDTO.setName(form.getName());
					assetDTO.setDescription(form.getDescription());
					assetDTO.setContent(form.getAgreement());
					assetDTO.setDisplayable(true);

					Asset result = assetService.storeAssetForContractVersion(assetDTO, version.getId());

					if (result != null) {
						messageHelper.addSuccess(bundle, "filemanager.new_agreement.success");
						return baseRedirect;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		messageHelper.addError(bundle, "filemanager.new_agreement.exception");

		return baseRedirect;
	}


	@RequestMapping(
		value = "/edit",
		method = GET)
	public String editAgreementShow(
		@RequestParam(required = false) Long id, Long version, Model model, RedirectAttributes flash) {

		if (id == null) {
			return baseRedirect;
		}

		ContractVersion contractVersion = contractService.findContractVersionByIdAndCompany(version, getCurrentUser().getCompanyId());

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (contractVersion == null) {
			messageHelper.addError(bundle, "filemanager.edit_agreement.notfound");
			return baseRedirect;
		}

		Contract contract = contractService.findContractById(id);
		ContractVersionPagination pagination = new ContractVersionPagination();
		pagination.setStartRow(0);
		pagination.setResultsLimit(25);
		ContractVersionPagination versions = contractService.findAllContractVersionsByContractId(id, pagination);

		model.addAttribute("contract", contract);
		model.addAttribute("versions", versions);
		model.addAttribute("contract_version_asset", getContractVersionAsset(contractVersion));
		model.addAttribute("editagreement_form", new EditAgreementForm());

		return "web/pages/agreements/edit";
	}


	@RequestMapping(
		value = "/edit",
		method = POST)
	public String editAgreementSubmit(
		@Valid @ModelAttribute("editagreement_form") EditAgreementForm form,
		BindingResult bind,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return baseRedirect;
		}

		Contract contract = contractService.findContractById(form.getId());
		if (contract == null) {
			messageHelper.addError(bundle, "filemanager.edit_agreement.notfound");
			return baseRedirect;
		}

		if (!contract.getName().equals(form.getName())) {
			if (contractService.activeContractExists(form.getName(), getCurrentUser().getCompanyId())) {
				messageHelper.addError(bundle, "filemanager.new_agreement.duplicate");
				return baseRedirect;
			}
		}

		ContractDTO contractDTO = new ContractDTO();

		contractDTO.setCompanyId(getCurrentUser().getCompanyId());
		contractDTO.setName(form.getName());
		contractDTO.setContractId(form.getId());
		contractDTO.setEntityId(form.getId());

		try {
			Contract savedContract = contractService.saveOrUpdateContract(contractDTO);

			if (savedContract != null) {
				ContractVersionDTO versionDTO = new ContractVersionDTO();
				versionDTO.setContractId(contract.getId());

				ContractVersion version = contractService.saveOrUpdateContractVersion(versionDTO);

				if (version != null) {
					ContractVersionAssetDTO assetDTO = new ContractVersionAssetDTO();
					assetDTO.setName(form.getName());
					assetDTO.setDescription(form.getDescription());
					assetDTO.setContent(form.getContent());
					assetDTO.setDisplayable(true);

					Asset result = assetService.storeAssetForContractVersion(assetDTO, version.getId());

					if (result == null)
						messageHelper.addError(bundle, "filemanager.edit_agreement.exception");
					else
						messageHelper.addSuccess(bundle, "filemanager.edit_agreement.success");
				}
			}
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "filemanager.edit_agreement.exception");
		}

		return baseRedirect;
	}


	@RequestMapping(
		value = "/get_agreement_text",
		method = GET)
	public @ResponseBody String getAgreementText(
		@RequestParam(required = false) Long id,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		ContractVersion contractVersion = contractService.findContractVersionByIdAndCompany(id, getCurrentUser().getCompanyId());

		if (contractVersion != null) {
			Asset asset = getContractVersionAsset(contractVersion);
			return (asset != null) ? asset.getContent() : "";
		}

		messageHelper.addError(bundle, "filemanager.get_agreement_text.notfound");

		return "";
	}

	private Asset getContractVersionAsset(ContractVersion contractVersion) {
		List<Asset> assets = Lists.newArrayList(contractVersion.getContractVersionAssets());
		if (assets.isEmpty()) {
			return null;
		}
		return assets.get(0);
	}

}
