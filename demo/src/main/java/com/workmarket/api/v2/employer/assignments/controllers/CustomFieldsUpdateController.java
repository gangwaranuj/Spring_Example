package com.workmarket.api.v2.employer.assignments.controllers;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import com.google.common.collect.Lists;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.exceptions.ResourceValidationException;
import com.workmarket.api.v2.ApiV2xResponse;
import com.workmarket.api.v2.employer.assignments.models.CustomFieldsUpdateRequest;
import com.workmarket.api.v2.employer.assignments.models.validator.CustomFieldsUpdateRequestValidator;
import com.workmarket.api.v2.employer.assignments.services.WorkCustomFieldsDTOFormatterService;
import com.workmarket.api.v2.model.CustomFieldDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.RestCode;
import com.workmarket.web.exceptions.NotFoundException;
import com.workmarket.web.exceptions.ValidationException;
import com.workmarket.web.helpers.MessageBundleHelper;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// TODO API - Change to ApiV2Response
@Api(tags = "Custom Fields")
@Controller
@RequestMapping(value = {"/v2/assignments"})
public class CustomFieldsUpdateController extends ApiBaseController {
	private static final Logger logger = LoggerFactory.getLogger(CustomFieldsUpdateController.class);
	@Autowired private CustomFieldsUpdateRequestValidator validator;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private CustomFieldService customFieldService;
	@Autowired protected WorkService workService;
	@Autowired protected MessageBundleHelper messageHelper;
	@Autowired protected WorkCustomFieldsDTOFormatterService workCustomFieldsDTOFormatterService;

	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@ApiOperation(value = "Update a custom field group's custom fields by position")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = "{workNumber}/custom_fields/{position}",
		method = RequestMethod.PUT,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE,
		headers = "Accept=application/json"
	)
	@ResponseBody
	public ApiV2xResponse putUpdateCustomFieldGroupByPosition(
		@ApiParam(name = "workNumber", required = true) @PathVariable String workNumber,
		@ApiParam(name = "position", required = true) @PathVariable Integer position,
		@RequestBody @Valid CustomFieldsUpdateRequest request,
		BindingResult result
	) throws Exception {
		List<WorkCustomFieldDTO> dtos = null;
		User currentUser = authenticationService.getCurrentUser();
		Long companyId = currentUser.getCompany().getId();
		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		if (work == null) {
			throw new NotFoundException(RestCode.ASSIGNMENT_NOT_FOUND);
		}

		boolean isActiveResource = workService.isUserActiveResourceForWork(currentUser.getId(), work.getId());
		boolean isAdmin = workService.isAuthorizedToAdminister(work.getId(), currentUser.getId());

		if (!isActiveResource && !isAdmin) {
			throw new ResourceValidationException(RestCode.BAD_REQUEST.getDescription());
		}

		if (request.getGroupId() != 0 && CollectionUtils.isNotEmpty(request.getFields())) {
			List<CustomFieldDTO.Builder> fields = Lists.transform(request.getFields(),
				new Function<CustomFieldDTO, CustomFieldDTO.Builder>() {
					@Nullable
					@Override
					public CustomFieldDTO.Builder apply(@Nullable CustomFieldDTO input) {
						return new CustomFieldDTO.Builder(input);
					}
				});

			dtos =  workCustomFieldsDTOFormatterService.verifyAndPackageCustomFields(
				companyId,
				new CustomFieldGroupDTO.Builder().setId(request.getGroupId()).setFields(ImmutableSet.copyOf(fields)).build(),
				result
			);

		} else {
			result.reject(messageHelper.getMessage("api.v2.validation.error.groupAndFieldsMissing"));
		}

		if (result.hasErrors() && CollectionUtilities.isEmpty(dtos)) {
			throw new ValidationException(result.getAllErrors());
		}

		customFieldService.replaceCustomFieldGroupForWorkByPosition(work.getId(), request.getGroupId(), dtos, position);

		// TODO API - not done here
		return buildApiResponse(dtos, result, HttpStatus.OK);
	}

	@ApiOperation(value = "Update multiple custom fields")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = "{workNumber}/custom_fields",
		method = RequestMethod.PUT,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE,
		headers = "Accept=application/json"
	)
	@ResponseBody
	public ApiV2xResponse putUpdateBulkCustomFields(
			@ApiParam(name = "workNumber", required = true)
			@PathVariable("workNumber") String workNumber,
			@RequestBody @Valid CustomFieldsUpdateRequest request,
			BindingResult result
	) throws Exception {
		List<WorkCustomFieldDTO> dtos = null;
		User currentUser = authenticationService.getCurrentUser();
		Long companyId = currentUser.getCompany().getId();
		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		if (work == null) {
			throw new NotFoundException(RestCode.ASSIGNMENT_NOT_FOUND);
		}

		boolean isActiveResource = workService.isUserActiveResourceForWork(currentUser.getId(), work.getId());
		boolean isAdmin = workService.isAuthorizedToAdminister(work.getId(), currentUser.getId());

		if (!isActiveResource && !isAdmin){
			throw new ResourceValidationException(RestCode.BAD_REQUEST.getDescription());
		}

		for(CustomFieldGroupDTO groupBuilder : request.getGroups()){
			dtos =  workCustomFieldsDTOFormatterService.verifyAndPackageCustomFields(companyId, groupBuilder, result);
			customFieldService.replaceCustomFieldGroupForWork(work.getId(), groupBuilder.getId(), dtos);
		}

		if (result.hasErrors()) {
			throw new ValidationException(result.getAllErrors());
		}

		// TODO API - not done here
		return buildApiResponse(dtos, result, HttpStatus.OK);
	}

	@ApiOperation(value = "Create custom fields")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = "{workNumber}/custom_fields/{position}",
		method = RequestMethod.POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE,
		headers = "Accept=application/json"
	)
	@ResponseBody
	public ApiV2xResponse postCreateCustomFields(
		@ApiParam(name = "workNumber", required = true)
		@PathVariable("workNumber") String workNumber,
		@ApiParam(name = "position", required = true)
		@PathVariable("position") Integer position,
		@RequestBody @Valid CustomFieldsUpdateRequest request,
		BindingResult result
	) throws Exception {
		List<WorkCustomFieldDTO> dtos = null;
		User currentUser = authenticationService.getCurrentUser();
		Long companyId = currentUser.getCompany().getId();

		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		if (work == null) {
			throw new NotFoundException(RestCode.ASSIGNMENT_NOT_FOUND);
		}

		boolean isActiveResource = workService.isUserActiveResourceForWork(currentUser.getId(), work.getId());

		boolean isAdmin = workService.isAuthorizedToAdminister(work.getId(), currentUser.getId());

		if (!isActiveResource && !isAdmin) {
			throw new ResourceValidationException(RestCode.BAD_REQUEST.getDescription());
		}

		WorkCustomFieldGroup group = customFieldService.findWorkCustomFieldGroup(work.getId(), companyId, position);

		if (group != null && group.getId() != null && CollectionUtils.isNotEmpty(request.getFields())) {
			List<CustomFieldDTO.Builder> fields = Lists.transform(request.getFields(),
				new Function<CustomFieldDTO, CustomFieldDTO.Builder>() {
					@Nullable
					@Override
					public CustomFieldDTO.Builder apply(@Nullable CustomFieldDTO input) {
						return new CustomFieldDTO.Builder(input);
					}
				});

			dtos =  workCustomFieldsDTOFormatterService.verifyAndPackageCustomFields(
				companyId,
				new CustomFieldGroupDTO.Builder().setId(request.getGroupId()).setFields(ImmutableSet.copyOf(fields)).build(),
				result
			);
		} else {
			result.reject(messageHelper.getMessage("api.v2.validation.error.groupAndFieldsMissing"));
		}

		// TODO API - not done here
		if (result.hasErrors() && CollectionUtilities.isEmpty(dtos)) {
			return buildApiResponse(dtos,result,HttpStatus.BAD_REQUEST);
		}

		customFieldService.saveWorkCustomFieldsForWorkAndIndex(dtos.toArray(new WorkCustomFieldDTO[dtos.size()]), work.getId());

		return  buildApiResponse(dtos, result, HttpStatus.OK);
	}


	private ApiV2xResponse buildApiResponse(List<WorkCustomFieldDTO> dtos, BindingResult result, final HttpStatus httpStatus) {
		ApiV2xResponse apiV2xResponse = new ApiV2xResponse();
		ApiJSONPayloadMap meta = new ApiJSONPayloadMap();
		meta.setStatusCode(httpStatus.value());

		Map<String, Object> response = new HashMap<>();
		response.put("data", dtos);
		response.put("success", HttpStatus.OK.equals(httpStatus));

		apiV2xResponse.setMeta(meta);
		apiV2xResponse.setResponse(response);
		if (result.hasErrors()) {
			apiV2xResponse.setErrors(extract(result.getAllErrors(), on(ObjectError.class).getDefaultMessage()));
		} else {
			apiV2xResponse.setErrors(new ArrayList<String>());
		}
		// TODO API - not done here
		return apiV2xResponse;
	}

}

