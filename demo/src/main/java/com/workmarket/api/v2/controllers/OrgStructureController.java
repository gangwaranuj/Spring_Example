package com.workmarket.api.v2.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.GenericApiException;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.OrgUnitDTO;
import com.workmarket.business.gen.Messages.OrgUnitPath;
import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.orgstructure.OrgStructureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Api(tags = {"OrgStructure"})
@Controller("orgStructureController")
@RequestMapping("/v2/orgStructure")
public class OrgStructureController extends ApiBaseController {
	private static final Logger logger = LoggerFactory.getLogger(OrgStructureController.class);

	@Autowired private FeatureEntitlementService featureEntitlementService;
	@Autowired private OrgStructureService orgStructureService;
	@Autowired private AuthenticationService authenticationService;

	@RequestMapping(
		value = "/{orgUnitUuid}/subTreePaths",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get all org units under the sub tree rooted at the given org unit")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	public ApiV2Response<OrgUnitDTO> getSubtreePaths(
		@PathVariable final String orgUnitUuid) {

		final long userId = getCurrentUser().getId();
		final long companyId = getCurrentUser().getCompanyId();
		final boolean hasOrgFeatureToggle = featureEntitlementService.hasFeatureToggle(userId, "org_structures");

		logger.info(String.format("Getting subtree paths for %s", orgUnitUuid));

		final List<OrgUnitDTO> orgUnitDTOS = (hasOrgFeatureToggle && !StringUtils.isBlank(orgUnitUuid))
			? orgStructureService.getSubtreePaths(userId, companyId, orgUnitUuid)
			: ImmutableList.<OrgUnitDTO>of();

		return ApiV2Response.valueWithResults(orgUnitDTOS);
	}

	@RequestMapping(
			value = "/orgMode",
			method = GET,
			produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get org mode for current user.")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	public ApiV2Response<String> getOrgMode() {

		final long userId = authenticationService.getCurrentUser().getId();
		final boolean hasOrgFeatureToggle = featureEntitlementService.hasFeatureToggle(userId, "org_structures");

		logger.info(String.format("Getting org mode for %s", userId));

		final String orgMode = (hasOrgFeatureToggle)
				? orgStructureService.getOrgModeSetting(userId)
				: StringUtils.EMPTY;

		return ApiV2Response.valueWithResult(orgMode);
	}

	@RequestMapping(
			value = "/orgMode",
			method = POST,
			produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Set org mode for current user.")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	public ApiV2Response<Boolean> setOrgMode(@RequestParam(value = "orgModeUuid") final String orgModeUuid) {

		final long userId = authenticationService.getCurrentUser().getId();
		final boolean hasOrgFeatureToggle = featureEntitlementService.hasFeatureToggle(userId, "org_structures");

		if (!hasOrgFeatureToggle) {
			throw new GenericApiException("Something went wrong.");
		}

		logger.info(String.format("Setting org mode UUID: %s for userId: %s", orgModeUuid, userId));

		final BaseStatus baseStatus = orgStructureService.setOrgModeSettingForUser(userId, orgModeUuid);

		return ApiV2Response.valueWithResult(baseStatus.isSuccessful());
	}

	@RequestMapping(
			value = "/orgModeOptions",
			method = GET,
			produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get org mode for current user.")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	public ApiV2Response<List<OrgUnitPath>> getOrgModeOptions() {

		final long userId = authenticationService.getCurrentUser().getId();
		final boolean hasOrgFeatureToggle = featureEntitlementService.hasFeatureToggle(userId, "org_structures");

		logger.info(String.format("Getting org mode options for %s", userId));

		final List<OrgUnitPath> orgMode = (hasOrgFeatureToggle)
				? orgStructureService.getOrgModeOptions(userId)
				: Lists.<OrgUnitPath>newArrayList();

		return ApiV2Response.valueWithResult(orgMode);
	}

	@RequestMapping(
			value = "/orgUnitMembers",
			method = POST,
			produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get all members of the org units specified")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	public ApiV2Response<UserDTO> getOrgUnitMembers(@RequestBody final List<String> orgUnitUuids) {

		logger.info(String.format("Getting members for org units, %s", orgUnitUuids));

		final boolean hasOrgFeatureToggle = featureEntitlementService.hasFeatureToggle(
				getCurrentUser().getId(), "org_structures");
		final List<UserDTO> userDTOs = (hasOrgFeatureToggle && CollectionUtils.isNotEmpty(orgUnitUuids))
				? orgStructureService.getOrgUnitMembers(orgUnitUuids)
				: ImmutableList.<UserDTO>of();

		return ApiV2Response.valueWithResults(userDTOs);
	}
}

