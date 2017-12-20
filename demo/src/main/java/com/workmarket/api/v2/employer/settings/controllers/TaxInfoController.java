package com.workmarket.api.v2.employer.settings.controllers;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.settings.models.TaxInfoDTO;
import com.workmarket.api.v2.employer.settings.services.TaxInfoService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Tax"})
@Controller("employerTaxInfoController")
@RequestMapping(value = {"/v2/employer/settings/tax", "/employer/v2/settings/tax"})
public class TaxInfoController extends ApiBaseController {

	@Autowired TaxInfoService taxInfoService;
	@Autowired InvariantDataService invariantDataService;

	@ApiOperation(value = "Save tax info")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		method = POST,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<TaxInfoDTO> postSaveTaxInfo(@RequestBody TaxInfoDTO builder) throws ValidationException {
		TaxInfoDTO result = taxInfoService.save(builder);
		return ApiV2Response.valueWithResult(result);
	}
}
