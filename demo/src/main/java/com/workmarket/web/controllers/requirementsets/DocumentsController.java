package com.workmarket.web.controllers.requirementsets;

import com.workmarket.domains.model.asset.CompanyAssetPagination;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.DocumentSerializationService;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/documents")
public class DocumentsController extends BaseController {
	@Autowired private AssetManagementService service;
	@Autowired private DocumentSerializationService jsonService;

	@ResponseBody
	@RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
	public String list() {
		CompanyAssetPagination companyAssetPagination = new CompanyAssetPagination(true);
		companyAssetPagination.addFilter(CompanyAssetPagination.FILTER_KEYS.ACTIVE, true);

		CompanyAssetPagination pagination = service.getCompanyLibrary(getCurrentUser().getCompanyId(), companyAssetPagination);
		return jsonService.toJson(pagination.getResults());
	}
}
