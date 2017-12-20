package com.workmarket.web.controllers.requirementsets;

import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.contract.Contract;
import com.workmarket.domains.model.contract.ContractVersion;
import com.workmarket.service.business.ContractSerializationService;
import com.workmarket.service.business.ContractService;
import com.workmarket.service.business.dto.ContractVersionUserSignatureDTO;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/agreements")
public class AgreementsController extends BaseController {
	@Autowired private ContractService service;
	@Autowired private ContractSerializationService jsonService;

	@ResponseBody
	@RequestMapping(value = "/fetch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String list() {
		List<Contract> agreements = service.findAllByCompanyId(getCurrentUser().getCompanyId());
		return jsonService.toJson(agreements);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getMostRecent(@PathVariable("id") Long id, Model model) {
		Set<Asset> assets = service.getMostRecentAssetsForContractId(id);
		ContractVersion version = service.findMostRecentContractVersionByContractId(id);

		model.addAttribute("assets", assets);
		model.addAttribute("version", version);
		return "web/pages/assignments/agreement";
	}

	@RequestMapping(value = "/accept", method = RequestMethod.POST)
	public @ResponseBody void accept(@RequestParam("versionId") Long versionId) throws Exception {
		service.findOrCreateContractVersionUserSignature(getContractVersionUserSignatureDTO(versionId));
	}

	@RequestMapping(value = "/accept/contract", method = RequestMethod.POST)
	public @ResponseBody void acceptByContractId(@RequestParam("contractId") Long contractId) throws Exception {
		ContractVersion version = service.findMostRecentContractVersionByContractId(contractId);
		service.findOrCreateContractVersionUserSignature(getContractVersionUserSignatureDTO(version.getId()));
	}

	private ContractVersionUserSignatureDTO getContractVersionUserSignatureDTO(Long versionId) {
		ContractVersionUserSignatureDTO dto = new ContractVersionUserSignatureDTO();
		dto.setUserId(getCurrentUser().getId());
		dto.setContractVersionId(versionId);
		dto.setSignature("1");
		return dto;
	}
}
