package com.workmarket.api.v2.controllers;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.GetSignableEsignatureRequestDTO;
import com.workmarket.api.v2.model.GetSignedEsignatureRequestDTO;
import com.workmarket.api.v2.model.SignableEsignatureResponseDTO;
import com.workmarket.api.v2.model.SignedEsignatureResponseDTO;
import com.workmarket.biz.esignature.gen.Messages.GetDocumentByStatusResp;
import com.workmarket.biz.esignature.gen.Messages.GetTemplatesResp;
import com.workmarket.biz.esignature.gen.Messages.Template;
import com.workmarket.domains.model.Company;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.esignature.EsignatureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Api(tags = {"Esignature"})
@Controller("EsignatureController")
@RequestMapping("/")
public class EsignatureController extends ApiBaseController {

	@Autowired private CompanyService companyService;
	@Autowired private EsignatureService esignatureService;

	@RequestMapping(
			value = "/v2/esignature/template/list",
			method = GET,
			produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@ApiOperation(value = "List esignature templates.")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	public ApiV2Response<Template> list() {
		final Company company = companyService.findById(getCurrentUser().getCompanyId());
		final GetTemplatesResp esignatureTemplates = esignatureService.getTemplates(company.getUuid());
		return ApiV2Response.valueWithResults(esignatureTemplates.getTemplateList());
	}

	@RequestMapping(
			value = "/v2/esignature/get_signable",
			method = GET,
			produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@ApiOperation(value = "Get signable.")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	public ApiV2Response<SignableEsignatureResponseDTO> getSignable(GetSignableEsignatureRequestDTO request) {
		final SignableEsignatureResponseDTO signable =
				esignatureService.getOrCreateSignable(
						request.getCompanyUuid(),
						request.getTemplateUuid(),
						getCurrentUser().getUuid(),
						getCurrentUser().getEmail(),
						getCurrentUser().getFullName());

		return ApiV2Response.valueWithResult(signable);
	}

	@RequestMapping(
			value = "/v2/esignature/get_signed",
			method = GET,
			produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@ApiOperation(value = "Get signed.")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	public ApiV2Response<SignedEsignatureResponseDTO> getSigned(GetSignedEsignatureRequestDTO request) {
		final GetDocumentByStatusResp signable =
				esignatureService.getSignableByUserNumber(request.getTemplateUuid(), request.getuserNumber());
		final SignedEsignatureResponseDTO responseDTO =
				SignedEsignatureResponseDTO.newBuilder().withExecutedUrl(signable.getExecutedUrl()).build();
		return ApiV2Response.valueWithResult(responseDTO);
	}
}
