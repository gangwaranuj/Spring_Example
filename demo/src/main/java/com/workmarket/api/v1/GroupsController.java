package com.workmarket.api.v1;

import com.google.api.client.repackaged.com.google.common.base.Objects;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v1.model.ApiApplyToTalentPoolOnBehalfRequestDTO;
import com.workmarket.api.v1.model.ApiApplyToTalentPoolOnBehalfResponseDTO;
import com.workmarket.api.v1.model.ApiTalentPoolDTO;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.exceptions.HttpException403;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = "Talent Pools")
@Controller("apiGroupsController")
@RequestMapping(value = {"/v1/employer/groups", "/api/v1/groups"})
public class GroupsController extends ApiBaseController {

	@Autowired private UserGroupService userGroupService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserService userService;

	/**
	 * Restrict request methods.
	 * @param iDisplayStart is start row
	 * @param iDisplayLength is limit
	 * @return ApiResponse
	 */
	@ApiOperation(value = "List talent pools")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/list", method=RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<List<ApiTalentPoolDTO>> listTalentPoolsAction(
		@RequestParam(value="iDisplayStart", required=false, defaultValue="0") Integer iDisplayStart,
		@RequestParam(value="iDisplayLength", required=false, defaultValue="25") Integer iDisplayLength,
		@RequestParam(value = "start", required = false, defaultValue = "0") int start,
		@RequestParam(value = "limit", required = false, defaultValue = "100") int limit,
		@RequestParam(value = "activeOnly", required = false, defaultValue = "false") boolean activeOnly) {
		
		Long userId = authenticationService.getCurrentUser().getId();
		
		ManagedCompanyUserGroupRowPagination pagination = new ManagedCompanyUserGroupRowPagination();
		pagination.setStartRow(Objects.firstNonNull(start, iDisplayStart));
		pagination.setResultsLimit(Objects.firstNonNull(limit, iDisplayLength));
		pagination.setSortColumn(ManagedCompanyUserGroupRowPagination.SORTS.GROUP_NAME);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.ASC);
		pagination.setShowOnlyActiveGroups(activeOnly);

		ManagedCompanyUserGroupRowPagination results = userGroupService.findMyCompanyGroups(userId, pagination);
		List<ApiTalentPoolDTO> talentPoolDTOs = Lists.transform(results.getResults(), new Function<ManagedCompanyUserGroupRow, ApiTalentPoolDTO>() {
			@Override
			public ApiTalentPoolDTO apply(ManagedCompanyUserGroupRow group) {
				return new ApiTalentPoolDTO.Builder()
					.withId(group.getGroupId())
					.withName(group.getName())
					.withDescription(group.getDescription())
					.withMembers(group.getMemberCount())
					.withActive(group.getActiveFlag())
					.withDeleted(group.getDeleted())
					.build();
			}
		});

		ApiV1Response<List<ApiTalentPoolDTO>> apiResponse = new ApiV1Response<>();
		apiResponse.setResponse(talentPoolDTOs);
		return apiResponse;
	}

	@ApiOperation(value = "Add or invite workers to talent pool")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/{groupId}/add_workers", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> addOrInviteWorkersAction(
		@RequestBody List<String> userNumbers, @PathVariable(value = "groupId") Long groupId) {

		if (CollectionUtils.isEmpty(userNumbers) || groupId == null) {
			return ApiV1Response.of(false, HttpStatus.SC_BAD_REQUEST);
		}

		List<RequestContext> contexts = userGroupService.getRequestContext(groupId);
		if (!contexts.contains(RequestContext.ADMIN) && !contexts.contains(RequestContext.OWNER)) {
			return ApiV1Response.of(false, HttpStatus.SC_FORBIDDEN);
		}

		userGroupService.addUsersToGroupByUserNumberAsync(userNumbers, groupId, authenticationService.getCurrentUserId());

		return ApiV1Response.of(true);
	}

	@ApiOperation(value = "Add workers to talent pool")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/{groupId}/apply_on_behalf", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV1Response<ApiApplyToTalentPoolOnBehalfResponseDTO> applyToTalentPoolOnBehalfAction(
		@RequestBody ApiApplyToTalentPoolOnBehalfRequestDTO applyToTalentPoolOnBehalfRequestDto, @PathVariable(value = "groupId") Long groupId) {

		if (CollectionUtils.isEmpty(applyToTalentPoolOnBehalfRequestDto.getUserIds()) || groupId == null) {
			throw new HttpException400("Must provide at least one user ID");
		}

		List<RequestContext> contexts = userGroupService.getRequestContext(groupId);
		if (!contexts.contains(RequestContext.ADMIN) && !contexts.contains(RequestContext.OWNER)) {
			throw new HttpException403();
		}

		Map<String, List<String>> results = userGroupService.applyOnBehalfOfUsers(
			applyToTalentPoolOnBehalfRequestDto.getUserIds(),
			groupId,
			getCurrentUser().getId(),
			applyToTalentPoolOnBehalfRequestDto.isSuppressNotification(),
			applyToTalentPoolOnBehalfRequestDto.isOverride());

		return new ApiV1Response<>(
			new ApiApplyToTalentPoolOnBehalfResponseDTO(results.get("success"), results.get("failure")));
	}

	@ApiOperation(value = "Remove workers from talent pool")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/{groupId}/remove_workers", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> removeWorkersAction(@RequestBody List<String> userNumbers, @PathVariable(value = "groupId") Long groupId) {

		if (CollectionUtils.isEmpty(userNumbers) || groupId == null) {
			return ApiV1Response.of(false, HttpStatus.SC_BAD_REQUEST);
		}

		List<RequestContext> contexts = userGroupService.getRequestContext(groupId);
		if (!contexts.contains(RequestContext.ADMIN) && !contexts.contains(RequestContext.OWNER)) {
			return ApiV1Response.of(false, HttpStatus.SC_FORBIDDEN);
		}

		Set<Long> allUserIdsByUserNumbers = userService.findAllUserIdsByUserNumbers(userNumbers);

		userGroupService.removeAssociations(groupId, allUserIdsByUserNumbers.toArray(new Long[allUserIdsByUserNumbers.size()]), authenticationService.getCurrentUserCompanyId());
		return ApiV1Response.of(true);
	}
}
