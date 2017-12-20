package com.workmarket.web.controllers.groups;

import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.api.v2.model.OrgUnitDTO;
import com.workmarket.business.talentpool.TalentPoolClient;
import com.workmarket.business.talentpool.gen.Messages.ParticipantType;
import com.workmarket.business.talentpool.gen.Messages.Status;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolAddParticipantsRequest;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipant;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipation;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolUpdateParticipantsRequest;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.groups.model.GroupMemberRequestType;
import com.workmarket.domains.groups.model.GroupMembershipPagination;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Message;
import com.workmarket.domains.model.MessagePagination;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.request.UserGroupInvitation;
import com.workmarket.domains.model.request.UserGroupInvitationPagination;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.guest.WebGuest;
import com.workmarket.domains.velvetrope.rope.ESignatureRope;
import com.workmarket.domains.velvetrope.rope.MarketplaceRope;
import com.workmarket.dto.TalentPoolMembershipDTO;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.ContractService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.MessagingService;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserGroupEvaluationScheduledRunService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.dto.ContractVersionUserSignatureDTO;
import com.workmarket.service.business.dto.GroupMembershipDTO;
import com.workmarket.service.business.event.company.VendorSearchIndexEvent;
import com.workmarket.service.business.requirementsets.EligibilityService;
import com.workmarket.service.business.wrapper.DownloadProfilePhotosResponse;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.network.NetworkService;
import com.workmarket.service.search.SearchPreferencesService;
import com.workmarket.service.talentpool.TalentPoolService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.forms.MessageForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.RatingStarsHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rx.functions.Action1;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/groups")
public class GroupsViewController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(GroupsViewController.class);

	@Autowired UserGroupService userGroupService;
	@Autowired ContractService contractService;
	@Autowired EligibilityService eligibilityService;
	@Autowired CompanyService companyService;
	@Autowired IndustryService industryService;
	@Autowired MessagingService messagingService;
	@Autowired RequestService requestService;
	@Autowired UserService userService;
	@Autowired MessageBundleHelper messageHelper;
	@Autowired NetworkService networkService;
	@Autowired UserGroupEvaluationScheduledRunService userGroupEvaluationScheduledRunService;
	@Autowired private EventRouter eventRouter;
	@Autowired private FormOptionsDataHelper formDataHelper;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private SearchPreferencesService searchPreferencesService;
	@Autowired private TalentPoolClient talentPoolClient;
	@Autowired private TalentPoolService talentPoolService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private VendorService vendorService;

	@Autowired @Qualifier("ESignatureDoorman")
	private Doorman ESignatureDoorman;
	@Autowired @Qualifier("marketplaceDoorman")
	private Doorman marketplaceDoorman;

	@RequestMapping(
		value = "/fetch_all",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder getBuyerGroups() {
		ManagedCompanyUserGroupRowPagination groupPagination = userGroupService.findCompanyGroupsOpenMembership(getCurrentUser().getCompanyId(), new ManagedCompanyUserGroupRowPagination(true));
		groupPagination.setSortColumn(ManagedCompanyUserGroupRowPagination.SORTS.GROUP_NAME);

		final List<Map<String, Object>> groups = Lists.newArrayListWithExpectedSize(groupPagination.getResults().size());
		for (ManagedCompanyUserGroupRow g : groupPagination.getResults()) {
			groups.add(ImmutableMap.<String, Object>of("id", g.getGroupId(), "name", g.getName(), "isPublic", g.isOpenMembership()));
		}

		return AjaxResponseBuilder.success().addData("groups", groups);
	}

	@RequestMapping(value={"", "/create"}, method = GET)
	public String index(Model model, HttpServletRequest httpRequest) {
		if (userService.belongsToWorkerCompany()) {
			return "redirect:/search-groups";
		}

		model.addAttribute("selected_navigation_link", "groups.view.index");
		model.addAttribute("pageId", "groupList");
		addGroupCounts(model);

		String avatarUri = "";
		if (companyService.findCompanyAvatars(getCurrentUser().getCompanyId()) != null) {
			avatarUri = companyService.findCompanyAvatars(getCurrentUser().getCompanyId()).getSmall().getUri();
		}

		MutableBoolean enableESignature = new MutableBoolean(false);
		ESignatureDoorman.welcome(new UserGuest(authenticationService.getCurrentUser()), new ESignatureRope(enableESignature));

		Map<String, Object> talentPoolMetaData = CollectionUtilities.newObjectMap(
			"type", "index",
			"hasVendorPoolsFeature", hasFeature(getCurrentUser().getCompanyId(), Constants.VENDOR_POOLS_FEATURE),
			"hasESignatureEnabled", enableESignature.booleanValue() ? true : false,
			"industries", industryService.getAllIndustries(),
			"owners", formDataHelper.getActiveUsers(getCurrentUser()),
			"avatar", avatarUri,
			"creator", getCurrentUser().getId(),
			"isMasquerading", getCurrentUser().isMasquerading());

		MutableBoolean hasMarketplace = new MutableBoolean(false);
		marketplaceDoorman.welcome(new WebGuest(getCurrentUser()), new MarketplaceRope(hasMarketplace));
		talentPoolMetaData.put("hasMarketplace", hasMarketplace.isTrue());

		Map<String, Object> orgStructuresData = getOrgStructuresData(httpRequest);

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "talentPools",
			"data", talentPoolMetaData,
			"features", CollectionUtilities.newObjectMap(
					"hasOrgStructures", orgStructuresData.get("isEnabled")
				)
		);
		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/groups/view/index";
	}

	@RequestMapping(
		value = "/memberships",
		method = GET)
	public String memberships(Model model) {
		model.addAttribute("selected_navigation_link", "groups.view.memberships");
		model.addAttribute("pageId", "groupMemberships");
		addGroupCounts(model);

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "talentPools",
			"data", CollectionUtilities.newObjectMap(
				"type", "memberships"
			),
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/groups/view/memberships";
	}

	@RequestMapping(
		value = "/invitations",
		method = GET)
	public String invitations(Model model) {
		model.addAttribute("selected_navigation_link", "groups.view.invitations");
		model.addAttribute("pageId", "groupInvitations");

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "talentPools",
			"data", CollectionUtilities.newObjectMap(
				"type", "invitations"
			),
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));
		addGroupCounts(model);

		return "web/pages/groups/view/invitations";
	}

	private void addGroupCounts(Model model) {
		if (getCurrentUser().isDispatcher()) {
			TalentPoolMembershipDTO dto = vendorService.getAllVendorUserGroupMemberships(getCurrentUser().getId());
			model.addAttribute("groupMembershipsCount", dto.getMemberships().size() + dto.getApplications().size());
			model.addAttribute("groupInvitationsCount", dto.getInvitations().size());
		} else {
			model.addAttribute("groupMembershipsCount", userGroupService.countUserGroupMemberships(getCurrentUser().getId()));
			model.addAttribute("groupInvitationsCount", requestService.countUserGroupInvitationsByInvitedUser(getCurrentUser().getId()));
		}
	}

	@RequestMapping(
		value = "/view/load_group_associations",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void loadGroups(
		Model model,
		HttpServletRequest httpRequest) {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(ImmutableMap.of(
			0, ManagedCompanyUserGroupRowPagination.SORTS.GROUP_NAME.toString(),
			2, ManagedCompanyUserGroupRowPagination.SORTS.OWNER_LAST_NAME.toString(),
			3, ManagedCompanyUserGroupRowPagination.SORTS.MEMBERS.toString(),
			4, ManagedCompanyUserGroupRowPagination.SORTS.PENDING_APPLICANT_COUNT.toString()
		));

		ManagedCompanyUserGroupRowPagination pagination = new ManagedCompanyUserGroupRowPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());
		pagination.setShowOnlyActiveGroups(false);

		DataTablesResponse<List<String>, Map<String, Object>> response;
		ExtendedUserDetails user = getCurrentUser();
		if (user.isDispatcher()) {
			response = loadVendorGroupMemberships(request, pagination);
		} else {
			response = loadUserGroupMemberships(request, pagination);
		}

		model.addAttribute("response", response);
	}

	private DataTablesResponse<List<String>, Map<String, Object>>
	loadUserGroupMemberships(DataTablesRequest request, ManagedCompanyUserGroupRowPagination pagination) {
		ExtendedUserDetails user = getCurrentUser();

		pagination = userGroupService.findMyGroupMembershipsAndApplications(user.getId(), pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (ManagedCompanyUserGroupRow group : pagination.getResults()) {
			List<String> row = Lists.newArrayList(
				group.getName(),
				String.valueOf(group.isOpenMembership()),
				String.valueOf(group.getActiveFlag()),
				String.valueOf(group.getMemberCount()),
				String.valueOf(group.getPendingApplicantCount()));

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", group.getGroupId(),
				"is_admin", isGroupAdmin(group.getCompanyId(), user),
				"open_membership", group.isOpenMembership(),
				"is_member", group.isMember(),
				"owner_company", group.getCompanyName(),
				"owner_user_number", group.getOwnerUserNumber(),
				"owner_full_name", group.getOwnerFullName(),
				"active", group.getActiveFlag(),
				"searchable", group.getSearchable(),
				"auto_generated", group.getAutoGenerated(),
				"is_shared_by_me", group.isSharedByMe(user.getCompanyId()),
				"is_shared_with_me", group.isSharedWithMe(user.getCompanyId()),
				"date_approved", group.getDateApproved() == null ? "" : DateUtilities.format("MM/dd/YYYY", group.getDateApproved())
			);

			response.addRow(row, meta);
		}
		return response;
	}

	private DataTablesResponse<List<String>, Map<String, Object>>
	loadVendorGroupMemberships(DataTablesRequest request, ManagedCompanyUserGroupRowPagination pagination) {
		TalentPoolMembershipDTO dto = vendorService.getAllVendorUserGroupMemberships(getCurrentUser().getId());

		if (dto.getMemberships().isEmpty() && dto.getApplications().isEmpty()) {
			return DataTablesResponse.newInstance(request);
		}

		Set<Long> groupIds = Sets.newHashSet(dto.getMemberships().keySet());
		groupIds.addAll(dto.getApplications().keySet());
		pagination = userGroupService.findVendorGroupMembershipsAndApplications(getCurrentUser().getId(), groupIds, pagination);
		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (ManagedCompanyUserGroupRow group : pagination.getResults()) {
			List<String> row = Lists.newArrayList(
				group.getName(),
				String.valueOf(group.isOpenMembership()),
				String.valueOf(group.getActiveFlag()),
				String.valueOf(group.getMemberCount()),
				String.valueOf(group.getPendingApplicantCount()));

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", group.getGroupId(),
				"is_admin", isGroupAdmin(group.getCompanyId(), getCurrentUser()),
				"open_membership", group.isOpenMembership(),
				"is_member", dto.getMemberships().containsKey(group.getGroupId()),
				"owner_company", group.getCompanyName(),
				"owner_user_number", group.getOwnerUserNumber(),
				"owner_full_name", group.getOwnerFullName(),
				"active", group.getActiveFlag(),
				"searchable", group.getSearchable(),
				"auto_generated", group.getAutoGenerated(),
				"is_shared_by_me", group.isSharedByMe(getCurrentUser().getCompanyId()),
				"is_shared_with_me", group.isSharedWithMe(getCurrentUser().getCompanyId()),
				"date_approved",
					dto.getMemberships().containsKey(group.getGroupId()) ?
						DateUtilities.format("MM/dd/YYYY", dto.getMemberships().get(group.getGroupId())) :
						""
			);
			response.addRow(row, meta);
		}

		return response;
	}

	@RequestMapping(
		value = "/view/list_groups",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder listGroups(
		@RequestParam(value = "type", required = false) String type) {

		ManagedCompanyUserGroupRowPagination pagination = new ManagedCompanyUserGroupRowPagination();
		pagination.setShowOnlyActiveGroups(false);
		pagination.setResultsLimit(1000);

		ExtendedUserDetails user = getCurrentUser();

		if ("memberships".equals(type)) {
			pagination = userGroupService.findMyGroupMembershipsAndApplications(user.getId(), pagination);
		}
		else {
			pagination = userGroupService.findSharedAndOwnedGroups(user.getId(), user.getCompanyId(), pagination);
		}

		List<Object> response = Lists.newArrayList();
		for (ManagedCompanyUserGroupRow group : pagination.getResults()) {
			final List<String> orgUnitNames = Lists.transform(group.getOrgUnits(), new Function<OrgUnitDTO, String>() {
				@Override
				public String apply(@Nullable OrgUnitDTO orgUnit) {
					return orgUnit == null ? null : orgUnit.getName();
				}
			});
			Map<String, Object> row = CollectionUtilities.newObjectMap(
				"requires_approval", group.getRequiresApproval(),
				"name", group.getName(),
				"description", group.getDescription(),
				"isActive", group.getActiveFlag(),
				"industry", group.getIndustry(),
				"count", String.valueOf(group.getMemberCount()),
				"pendingCount", String.valueOf(group.getPendingApplicantCount()),
				"invitedCount", String.valueOf(group.getInvitedApplicantCount()),
				"id", group.getGroupId(),
				"test", "test",
				"is_admin", isGroupAdmin(group.getCompanyId(), user),
				"open_membership", group.isOpenMembership(),
				"is_member", group.isMember(),
				"owner_company", group.getCompanyName(),
				"owner_id", group.getOwnerId(),
				"owner_user_number", group.getOwnerUserNumber(),
				"owner_full_name", group.getOwnerFullName(),
				"searchable", group.getSearchable(),
				"auto_generated", group.getAutoGenerated(),
				"is_shared_by_me", group.isSharedByMe(user.getCompanyId()),
				"is_shared_with_me", group.isSharedWithMe(user.getCompanyId()),
				"autoEnforce", userGroupEvaluationScheduledRunService.getNextFutureScheduledRunForActiveOrInactiveGroup(group.getGroupId()).isPresent(),
				"skills", userGroupService.findUserGroupSkills(group.getGroupId()),
				"org_units", group.getOrgUnits(),
				"org_unit_names", StringUtils.join(orgUnitNames, ", ")
			);
			response.add(row);
		}
		boolean readOnly = !user.hasAnyRoles("ACL_ADMIN", "ACL_MANAGER");
		return AjaxResponseBuilder.success().addData("groups", response).addData("readOnly", readOnly);
	}

	@RequestMapping(
		value = "/view/load_group_invitations",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void loadGroupInvitations(Model model, HttpServletRequest httpRequest) {
		DataTablesRequest dataTablesRequest = DataTablesRequest.newInstance(httpRequest);

		dataTablesRequest.setSortableColumnMapping(ImmutableMap.of(
			0, UserGroupInvitationPagination.SORTS.GROUP_NAME.toString(),
			1, UserGroupInvitationPagination.SORTS.INVITED_ON.toString()
		));

		DataTablesResponse<List<String>, Map<String, Object>> response;
		if (getCurrentUser().isDispatcher()) {
			response = loadVendorGroupInvitations(dataTablesRequest);
		} else {
			response = loadUserGroupInvitations(dataTablesRequest);
		}

		model.addAttribute("response", response);
	}

	private DataTablesResponse<List<String>, Map<String, Object>>
	loadUserGroupInvitations(DataTablesRequest dataTablesRequest) {
		UserGroupInvitationPagination pagination = new UserGroupInvitationPagination();
		pagination.setStartRow(dataTablesRequest.getStart());
		pagination.setResultsLimit(dataTablesRequest.getLimit());
		pagination.setSortColumn(dataTablesRequest.getSortColumn());
		pagination.setSortDirection(dataTablesRequest.getSortColumnDirection());

		pagination = requestService.findUserGroupInvitationsForInvitedUser(getCurrentUser().getId(), pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(dataTablesRequest, pagination);

		Set<UserGroup> distinctGroups = Sets.newHashSet();
		for (UserGroupInvitation invitation : pagination.getResults()) {
			UserGroup group = invitation.getUserGroup();

			// pagination includes duplicate invites, we only want to display one per group
			if (distinctGroups.contains(group)) continue;
			distinctGroups.add(group);

			List<String> row = Lists.newArrayList(
				group.getName(),
				DateUtilities.format("MM/dd/YYYY", invitation.getCreatedOn()));

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", group.getId(),
				"owner_company", invitation.getRequestor().getCompany().getName());

			response.addRow(row, meta);
		}
		return response;
	}

	private DataTablesResponse<List<String>, Map<String, Object>>
	loadVendorGroupInvitations(DataTablesRequest request) {
		TalentPoolMembershipDTO dto = vendorService.getAllVendorUserGroupMemberships(getCurrentUser().getId());

		if (dto.getInvitations().isEmpty()) {
			return DataTablesResponse.newInstance(request);
		}

		ManagedCompanyUserGroupRowPagination pagination = new ManagedCompanyUserGroupRowPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());
		pagination.setShowOnlyActiveGroups(false);

		pagination = userGroupService.findVendorGroupMembershipsAndApplications(
			getCurrentUser().getId(),
			dto.getInvitations().keySet(),
			pagination
		);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (ManagedCompanyUserGroupRow group : pagination.getResults()) {
			List<String> row = Lists.newArrayList(
				group.getName(),
				DateUtilities.format("MM/dd/yyyy", dto.getInvitations().get(group.getGroupId()))
			);
			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", group.getGroupId(),
				"owner_company", group.getCompanyName());

			response.addRow(row, meta);
		}

		return response;
	}

	@RequestMapping(
		value = "/{id}/members",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void loadGroupMembers(
		@PathVariable("id") Long id,
		Model model,
		HttpServletRequest httpRequest) {
		loadGroupMembers(id, GroupMemberRequestType.ALL.name(), model, httpRequest);
	}

	@RequestMapping(
		value = "/{id}/members/{status}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void loadGroupMembers(
		@PathVariable("id") Long id,
		@PathVariable(value = "status") String type,
		Model model,
		HttpServletRequest httpRequest) {

		UserGroup group = userGroupService.findGroupById(id);
		if (getCurrentUser().getCompanyId() != group.getCompany().getId()) {
			throw new HttpException401();
		}

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.of(
			0, GroupMembershipPagination.SORTS.USER_LAST_NAME.toString(),
			1, GroupMembershipPagination.SORTS.STATUS.toString()
		));

		GroupMemberRequestType requestType;
		try {
			requestType = GroupMemberRequestType.valueOf(type.toUpperCase());
		} catch (EnumConstantNotPresentException e) {
			requestType = GroupMemberRequestType.ALL;
		}

		GroupMembershipPagination pagination = new GroupMembershipPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());
		pagination = userGroupService.findGroupMembersByUserGroupId(id, requestType.name(), pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (GroupMembershipDTO groupMembership : pagination.getResults()) {
			List<String> row = Lists.newArrayList("", "", "");

			// TODO: validate if star rating falls within (0 - 5) range
			Integer starRating = (groupMembership.getStarRating() == null) ? 0 : groupMembership.getStarRating();

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", groupMembership.getUserId(),
				"user_id", groupMembership.getUserId(),
				"user_number", groupMembership.getUserNumber(),
				"first_name", groupMembership.getFirstName(),
				"last_name", groupMembership.getLastName(),
				"company_name", groupMembership.getCompanyName(),
				"lane_type", groupMembership.getLaneType().ordinal(),
				"city", groupMembership.getCity(),
				"state", groupMembership.getState(),
				"postal_code", groupMembership.getPostalCode(),
				"latitude", groupMembership.getLatitude(),
				"longitude", groupMembership.getLongitude(),
				"star_rating", groupMembership.getStarRating(),
				"star_rating_text", RatingStarsHelper.convertScaledRatingValueToText(starRating),
				"derived_status", groupMembership.getDerivedStatus(),
				"verification_status", groupMembership.getVerificationStatus(),
				"approval_status", groupMembership.getApprovalStatus()
			);

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/{id}",
		method = GET
	)
	public String details(@PathVariable("id") final Long id, final Model model, final HttpServletRequest httpRequest) {
		final UserGroup group = userGroupService.findGroupById(id);

		if (group == null)
			throw new HttpException404();

		final ExtendedUserDetails user = getCurrentUser();
		checkViewPrivileges(group, user);

		model.addAttribute("isGroupSharingActive", networkService.isGroupShared(id));
		model.addAttribute("isGroup", true);
		model.addAttribute("group", group);
		model.addAttribute("auto_generated", group.isAutoGenerated());
		// Default to TRUE, we'll check later if not yet a member
		model.addAttribute("authorized_to_join", Boolean.TRUE);
		model.addAttribute("pageId", "groupDetail");

		// Determine if the user can apply.
		final UserUserGroupAssociation association = userGroupService.findAssociationByGroupIdAndUserId(id, user.getId());
		model.addAttribute("association", association);

		if (association == null) {
			final List<UserGroupInvitation> invitations = requestService.findUserGroupInvitationRequestsByInvitedUserAndUserGroup(user.getId(), id);
			model.addAttribute("has_invitation", CollectionUtils.isNotEmpty(invitations));
			model.addAttribute("latest_invite", CollectionUtilities.last(invitations));
			model.addAttribute("authorized_to_join", userGroupService.authorizeUserForGroup(id, user.getId()));
		}

		final Eligibility validation_eligibility = isGroupMember(association) ? userGroupService.reValidateRequirementSets(id, user.getId()) : userGroupService.validateRequirementSets(id, user.getId());
		model.addAttribute("validation", validation_eligibility);
		model.addAttribute("missing_contract_versions", eligibilityService.getMissingContractVersions(id, user));
		model.addAttribute("hasNonAgreementRequirements", eligibilityService.hasNonAgreementRequirements(validation_eligibility));
		model.addAttribute("is_group_member", isGroupMember(association));
		model.addAttribute("is_group_admin", isGroupAdmin(group, user));
		model.addAttribute("is_group_company_viewer", isCompanyGroupViewer(group, user));
		model.addAttribute("isInternal", user.hasAnyRoles("ROLE_INTERNAL"));
		model.addAttribute("isSharedGroupViewable", networkService.userCanViewSharedGroup(id, user.getId()));
		model.addAttribute("userId", user.getId());
		model.addAttribute("memberCount", userGroupService.countAllActiveGroupMembers(id));
		model.addAttribute("messageCount", messagingService.countAllSentMessagesByUserGroup(id));
		model.addAttribute("messageForm", new MessageForm());
		model.addAttribute("authenticatedUserId", user.getId());
		model.addAttribute("preferences", searchPreferencesService.get(getCurrentUser().getId()).get("search_preferences"));
		model.addAttribute("is_dispatcher", user.isDispatcher());

		final Long userCompanyId = user.getCompanyId();
		model.addAttribute("companyBlocked", userService.isCompanyBlockedByUser(user.getId(), userCompanyId, group.getCompany().getId()));

		final Optional<String> memberStatus = getVendorTalentPoolStatus(group.getId(), userCompanyId);
		if (memberStatus.isPresent()) {
			model.addAttribute("vendorTalentPoolMembershipStatus", memberStatus.get());
		}

		final Company company = networkService.getSharingCompany(id);
		if (company != null) {
			model.addAttribute("sharingCompanyName", company.getName());
		}

		// Get company avatars.
		final CompanyAssetAssociation avatars = companyService.findCompanyAvatars(group.getCompany().getId());
		if (avatars != null && avatars.getTransformedLargeAsset() != null) {
			model.addAttribute("avatar_large", avatars.getTransformedLargeAsset().getUri());
		}

		final Map<String, Object> orgStructuresData = getOrgStructuresData(httpRequest);

		final Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "talentPools",
			"data", CollectionUtilities.newObjectMap(),
			"features", CollectionUtilities.newObjectMap(
					"hasOrgStructures", orgStructuresData.get("isEnabled")
			)
		);

		boolean isCurrentUserBuyer = getCurrentUser().isBuyer();

		MutableBoolean enableESignature = new MutableBoolean(false);
		ESignatureDoorman.welcome(new UserGuest(authenticationService.getCurrentUser()), new ESignatureRope(enableESignature));

		if (isCurrentUserBuyer) {
			final Map<String, Object> talentPoolMetaData = CollectionUtilities.newObjectMap(
				"type", "index",
				"industries", industryService.getAllIndustries(),
				"hasVendorPoolsFeature", hasFeature(getCurrentUser().getCompanyId(), Constants.VENDOR_POOLS_FEATURE),
				"hasESignatureEnabled", enableESignature.booleanValue() ? true : false,
				"owners", formDataHelper.getActiveUsers(getCurrentUser()),
				"creator", getCurrentUser().getId()
			);

			final MutableBoolean hasMarketplace = new MutableBoolean(false);
			marketplaceDoorman.welcome(new WebGuest(getCurrentUser()), new MarketplaceRope(hasMarketplace));
			talentPoolMetaData.put("hasMarketplace", hasMarketplace.isTrue());

			context.put("data", talentPoolMetaData);
		}

		model.addAttribute("is_admin", getCurrentUser().hasAnyRoles("ACL_ADMIN", "ACL_MANAGER"));
		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		if (isCurrentUserBuyer) {
			return "web/pages/groups/view/index";
		}
		return "web/pages/groups/view/details";
	}

	private Optional<String> getVendorTalentPoolStatus(final Long userGroupId, final Long userCompanyId) {
		final Map<Long, String> vendorMembershipStatuses =
				talentPoolService.getVendorGroupMemberRequestTypeStatuses(userGroupId, ImmutableList.of(userCompanyId));
		final Set<String> memberStatuses = ImmutableSet.of(
				GroupMemberRequestType.MEMBER.toString(),
				GroupMemberRequestType.MEMBER_PASSED.toString(),
				GroupMemberRequestType.MEMBER_OVERRIDE.toString());
		if (memberStatuses.contains(vendorMembershipStatuses.get(userCompanyId))) {
			return Optional.of("MEMBER");
		}

		final Set<String> appliedStatuses = ImmutableSet.of(
				GroupMemberRequestType.PENDING.toString(),
				GroupMemberRequestType.PENDING_FAILED.toString(),
				GroupMemberRequestType.PENDING_PASSED.toString()
		);
		if (appliedStatuses.contains(vendorMembershipStatuses.get(userCompanyId))) {
			return Optional.of("APPLIED");
		}

		return Optional.absent();
	}

	private void checkViewPrivileges(UserGroup group, ExtendedUserDetails user) {
		boolean isGroupAdmin = isGroupAdmin(group, user);
		boolean isCompanyGroupViewer = isCompanyGroupViewer(group, user);
		boolean isInternal = user.hasAnyRoles("ROLE_INTERNAL");
		boolean isSharedGroupViewable = networkService.userCanViewSharedGroup(group.getId(), user.getId());

		if (isGroupAdmin || isCompanyGroupViewer || isInternal) {
			return;
		}

		// You can't view the group if ...
		if (!group.getActiveFlag() // ... the group is inactive ...
			// ... or you're a buyer and the group is NOT shared and you're not a hybrid user (because hybrid users default to seller logic below) ...
			|| (user.isBuyer() && !isSharedGroupViewable && !user.isHybrid())
			// ... or you're a seller and the group is not open to the public
			|| (user.isSeller() && !group.getOpenMembership()) && !isSharedGroupViewable) {
			throw new HttpException401("groups.view.unauthorized").setRedirectUri("redirect:/groups");
		}
	}

	@RequestMapping(
		value = "/download_group_profile_photos/{id}.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder downloadGroupProfilePhotos(
		@PathVariable("id") Long groupId) throws IOException {

		AjaxResponseBuilder response = new AjaxResponseBuilder();
		Long userId = getCurrentUser().getId();
		if(groupId == null || userId == null){
			messageHelper.addMessage(response,"profile.photo.change.download.exception.fail");
			return response.setSuccessful(false);
		}
		DownloadProfilePhotosResponse profileResponse =  userGroupService.downloadGroupProfileImages(groupId,userId);
		return response.setSuccessful(profileResponse.isSuccessful()).setMessages(profileResponse.getMessages());
	}

	@RequestMapping(
		value = "/{id}/messages",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder loadGroupMessages(@PathVariable("id") Long groupId, HttpServletRequest httpRequest) {
		UserGroup group = getValidUserGroup(groupId);
		ExtendedUserDetails user = getCurrentUser();
		checkViewPermissions(groupId, group, user);
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		MessagePagination pagination = getMessagePagination(groupId, request);

		List<Map<String, Object>> messages = new ArrayList<>();

		for (Message message : pagination.getResults()) {
			String createdOn = DateUtilities.format("MM/dd/yyyy", message.getCreatedOn(), user.getTimeZoneId());
			Map<String, Object> meta = getMessageStringObjectMap(message, createdOn);
			messages.add(meta);
		}

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("messages", messages);
		return AjaxResponseBuilder.success().setData(responseData);
	}

	@RequestMapping(
		value = "/{id}/legacy_messages",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void legacyLoadGroupMessages(@PathVariable("id") Long groupId, Model model, HttpServletRequest httpRequest) {
		UserGroup group = getValidUserGroup(groupId);
		ExtendedUserDetails user = getCurrentUser();
		checkViewPermissions(groupId, group, user);
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		MessagePagination pagination = getMessagePagination(groupId, request);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (Message message : pagination.getResults()) {
			String createdOn = DateUtilities.format("MM/dd/yyyy", message.getCreatedOn(), user.getTimeZoneId());
			List<String> row = Lists.newArrayList("", createdOn);
			Map<String, Object> meta = getMessageStringObjectMap(message, createdOn);
			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}

	private void checkViewPermissions(final @PathVariable("id") Long groupId, final UserGroup group, final ExtendedUserDetails user) {
		// admins, company viewers, and members should be able to view messages
		if (!isGroupAdmin(group, user) && !isCompanyGroupViewer(group, user)) {
			UserUserGroupAssociation association = userGroupService.findAssociationByGroupIdAndUserId(groupId, user.getId());
			if (!isGroupMember(association))
				throw new HttpException401("groups.view.unauthorized").setRedirectUri("redirect:/groups");
		}
	}

	private UserGroup getValidUserGroup(final @PathVariable("id") Long groupId) {
		if (groupId == null)
			throw new HttpException400("groups.view.unauthorized").setRedirectUri("redirect:/groups");

		UserGroup group = userGroupService.findGroupById(groupId);

		if (group == null)
			throw new HttpException400("groups.view.unauthorized").setRedirectUri("redirect:/groups");
		return group;
	}

	private Map<String, Object> getMessageStringObjectMap(final Message message, final String createdOn) {
		String content = message.getContent();
		boolean hasShortContent = ((content != null) && (content.length() >= 80));
		String shortContent = (hasShortContent) ? StringUtils.abbreviate(content, 83) : content;
		String avatarUri = getAvatarUri(message.getSender().getId());

		// Need to add the shortened version of the message
		return CollectionUtilities.newObjectMap(
			"subject", message.getSubject(),
			"content", content,
			"short_content", shortContent,
			"has_short_content", hasShortContent,
			"date", createdOn,
			"sender_full_name", message.getSender().getFullName(),
			"sender_user_number", message.getSender().getUserNumber(),
			"avatarImage", avatarUri
		);
	}

	private String getAvatarUri(final Long senderId) {
		UserAssetAssociation assetAssociation = userService.findUserAvatars(senderId);

		if (assetAssociation == null) {
			return "";
		}

		Asset avatar = assetAssociation.getTransformedSmallAsset();
		if (avatar == null) {
			return "";
		}

		return assetAssociation.getTransformedSmallAsset().getUri();
	}

	private MessagePagination getMessagePagination(final long groupId, final DataTablesRequest request) {
		MessagePagination pagination = new MessagePagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(MessagePagination.SORTS.CREATED_ON);
		pagination.setSortDirection(request.getSortColumnDirection());
		pagination = messagingService.findAllSentMessagesByUserGroup(groupId, pagination);
		return pagination;
	}

	@RequestMapping(
		value = "/{id}/apply",
		method = POST)
	public String apply(
		@PathVariable("id") Long id,
		@RequestParam(
			value = "signed_contract_agreements[]",
			required = false) List<Long> versionIds,
		@RequestParam(
			value = "redirect_to",
			required = false) String redirectTo,
		RedirectAttributes redirectAttributes) {

		ExtendedUserDetails user = getCurrentUser();

		if (versionIds != null) {
			for (Long versionId : versionIds) {
				ContractVersionUserSignatureDTO dto = new ContractVersionUserSignatureDTO();
				dto.setUserId(user.getId());
				dto.setContractVersionId(versionId);
				dto.setSignature("1");

				try {
					contractService.saveOrUpdateContractVersionUserSignature(dto);
				} catch (Exception ex) {
					logger.error("There was a problem applying to groupId={}, versionIds={}, ex: {}", id, versionIds, ex);

					MessageBundle bundle = messageHelper.newFlashBundle(redirectAttributes);
					messageHelper.addError(bundle, "groups.apply.error");
					return getDefaultRedirectIfEmpty(redirectTo, "/groups/{id}");
				}
			}
		}

		Eligibility eligibility = userGroupService.validateRequirementSets(id, user.getId());

		// Apply to group.
		UserUserGroupAssociation association = userGroupService.applyToGroup(id, user.getId());
		MessageBundle bundle = messageHelper.newFlashBundle(redirectAttributes);

		if (association != null) {
			UserGroup group = userGroupService.findGroupById(id);
			if (!eligibility.isEligible() || group.getRequiresApproval()) {
				messageHelper.addSuccess(bundle, "groups.apply.pending", group.getName());
			} else {
				messageHelper.addSuccess(bundle, "groups.apply.approved", group.getName());
			}

			return getDefaultRedirectIfEmpty(redirectTo, "/search-groups");
		} else {
			messageHelper.addError(bundle, "groups.apply.error");
		}

		return getDefaultRedirectIfEmpty(redirectTo, "/groups/{id}");
	}

	private String getDefaultRedirectIfEmpty(String redirect, String defaultRedirect) {
		return "redirect:" + StringUtils.defaultIfEmpty(redirect, defaultRedirect);
	}

	@RequestMapping(
			value = "/{id}/vendor_apply",
			method = POST)
	public String vendorApply(
			@PathVariable("id") Long id,
			RedirectAttributes redirectAttributes) {

		final ExtendedUserDetails user = getCurrentUser();
		final MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);
		final List<String> companyUuids = companyService.getCompanyUuidsForCompanyIds(ImmutableSet.of(user.getCompanyId()));

		if (companyUuids.isEmpty()) {
			messageHelper.addError(messages, "groups.apply.error");
			return "redirect:/groups/{id}";
		}

		final UserGroup group = userGroupService.findGroupById(id);
		final List<String> vendorUuid = companyService.getCompanyUuidsForCompanyIds(ImmutableSet.of(user.getCompanyId()));

		final boolean alreadyParticipantInTalentPool =
				talentPoolService.isAlreadyParticipatingInTalentPool(group.getUuid(), vendorUuid.get(0));

		if (alreadyParticipantInTalentPool) {
			applyToTalentPoolFromInvitation(group, companyUuids, messages);
		} else {
			applyToTalentPool(messages, companyUuids, group);
		}

		return "redirect:/groups/{id}";
	}

	private void applyToTalentPoolFromInvitation(
			final UserGroup group,
			final List<String> companyUuids,
			final MessageBundle messages) {

		final TalentPoolUpdateParticipantsRequest talentPoolApplyRequest =
				buildTalentPoolUpdateApplyRequest(group, companyUuids.get(0));

		talentPoolClient.updateParticipants(talentPoolApplyRequest, webRequestContextProvider.getRequestContext())
				.subscribe(
						talentPoolApplySuccessAction(messages, group, companyUuids.get(0)),
						applyTalentPoolErrorAction(messages));
	}

	private TalentPoolUpdateParticipantsRequest buildTalentPoolUpdateApplyRequest(
			final UserGroup group,
			final String vendorUuid) {
		final TalentPoolParticipation talentPoolApplyParticipation = buildTalentPoolApplyParticipation(group);
		return TalentPoolUpdateParticipantsRequest.newBuilder()
				.setTalentPoolUuid(group.getUuid())
				.addParticipantUuid(vendorUuid)
				.setTalentPoolParticipation(talentPoolApplyParticipation)
				.build();
	}

	private void applyToTalentPool(final MessageBundle messages, final List<String> companyUuids, final UserGroup group) {
		final TalentPoolAddParticipantsRequest talentPoolApplyRequest =
				buildTalentPoolApplyRequest(group, companyUuids.get(0));

		talentPoolClient.addParticipants(talentPoolApplyRequest, webRequestContextProvider.getRequestContext())
				.subscribe(
						talentPoolApplySuccessAction(messages, group, companyUuids.get(0)),
						applyTalentPoolErrorAction(messages));
	}

	private TalentPoolAddParticipantsRequest buildTalentPoolApplyRequest(
			final UserGroup group,
			final String vendorUuid) {
		final TalentPoolParticipation talentPoolApplyParticipation = buildTalentPoolApplyParticipation(group);
		final TalentPoolParticipant talentPoolParticipant =
				buildTalentPoolParticipant(vendorUuid, talentPoolApplyParticipation);
		return buildTalentPoolApplyRequest(group.getUuid(), talentPoolParticipant);
	}

	private TalentPoolAddParticipantsRequest buildTalentPoolApplyRequest(
			final String talentPoolUuid,
			final TalentPoolParticipant talentPoolParticipant) {

		final TalentPoolAddParticipantsRequest.Builder talentPoolAddParticipantsRequestBuilder =
				TalentPoolAddParticipantsRequest.newBuilder()
						.setTalentPoolUuid(talentPoolUuid);
		talentPoolAddParticipantsRequestBuilder.addTalentPoolParticipant(talentPoolParticipant);
		return talentPoolAddParticipantsRequestBuilder.build();
	}

	private TalentPoolParticipant buildTalentPoolParticipant(
			final String vendorUuid,
			final TalentPoolParticipation talentPoolParticipation) {

		final TalentPoolParticipant.Builder talentPoolParticipantBuilder =
				TalentPoolParticipant.newBuilder()
						.setParticipantUuid(vendorUuid)
						.setTalentPoolParticipation(talentPoolParticipation);
		return talentPoolParticipantBuilder.build();
	}

	private TalentPoolParticipation buildTalentPoolApplyParticipation(final UserGroup group) {
		final String now = ISO8601Utils.format(DateUtilities.getNow());
		final String approvedOn = group.getRequiresApproval() ? "" : now;
		return TalentPoolParticipation.newBuilder()
				.setParticipantType(ParticipantType.VENDOR)
				.setAppliedOn(now)
				.setApprovedOn(approvedOn)
				.build();
	}

	private Action1<Status> talentPoolApplySuccessAction(
			final MessageBundle messages,
			final UserGroup group,
			final String vendorUuid) {
		return new Action1<Status>() {
			@Override
			public void call(final Status status) {
				final Company company = companyService.findByUUID(vendorUuid);

				if(group.getRequiresApproval()) {
					messageHelper.addSuccess(messages, "groups.apply.pending", group.getName());
				} else {
					messageHelper.addSuccess(messages, "groups.apply.approved", group.getName());
				}

				eventRouter.sendEvent(new VendorSearchIndexEvent(company.getId()));
			}
		};
	}

	private Action1<Throwable> applyTalentPoolErrorAction(final MessageBundle messages) {
		return new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				messageHelper.addError(messages, "groups.apply.error");
				logger.error("Failed to apply to talent pool: {}", throwable);
			}
		};
	}

	@RequestMapping(
		value = "/{id}/leave",
		method = GET)
	public String leave(
		@PathVariable("id") Long id,
		RedirectAttributes flash) {

		ExtendedUserDetails user = getCurrentUser();
		UserUserGroupAssociation association = userGroupService.findAssociationByGroupIdAndUserId(id, user.getId());

		// Leave group.
		try {
			userGroupService.removeAssociation(id, user.getId());
			UserGroup group = userGroupService.findGroupById(id);
			MessageBundle bundle = messageHelper.newFlashBundle(flash);

			// Tailor message for if the person has a membership to leave, or if they are cancelling a pending membership
			if (association.isApproved()) {
				messageHelper.addSuccess(bundle, "groups.leave.approved", group.getName());
			} else {
				messageHelper.addSuccess(bundle, "groups.leave.unapproved", group.getName());
			}

			return "redirect:/search-groups";
		} catch (Exception ex) {
			logger.error("There was a problem leaving this group for userGroupId={} and userId={}, ex: {}", id, user.getId(), ex);
		}

		return "redirect:/groups/{id}";
	}

	@RequestMapping(
		value = "/{id}/decline",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String declineHtml(@PathVariable("id") Long groupId, RedirectAttributes flash) {
		final ExtendedUserDetails user = getCurrentUser();
		final MessageBundle bundle = messageHelper.newFlashBundle(flash);
		final UserGroup group = userGroupService.findGroupById(groupId);
		final List<String> vendorUuid = companyService.getCompanyUuidsForCompanyIds(ImmutableSet.of(user.getCompanyId()));
		final String participantUuid = vendorUuid.get(0);
		final boolean alreadyParticipatingInTalentPool =
				talentPoolService.isAlreadyParticipatingInTalentPool(group.getUuid(), participantUuid);

		if (alreadyParticipatingInTalentPool) {
			declineTalentPoolParticipation(bundle, participantUuid, group.getUuid());
			return "redirect:/search-groups";
		}

		try {
			requestService.declineInvitationToGroup(user.getId(), groupId);
			messageHelper.addNotice(bundle, "groups.decline.notice");
		} catch (Exception ex) {
			logger.error("There was a problem declining the invitation for groupId=" + groupId, ex);
			messageHelper.addError(bundle, "groups.decline.error");
		}

		return "redirect:/search-groups";
	}

	private void declineTalentPoolParticipation(
			final MessageBundle bundle,
			final String participantUuid,
			final String talentPoolUuid) {

		final TalentPoolUpdateParticipantsRequest talentPoolDeclineRequest =
				buildTalentPoolDeclineParticipationRequest(talentPoolUuid, participantUuid);

		talentPoolClient.updateParticipants(talentPoolDeclineRequest, webRequestContextProvider.getRequestContext())
				.subscribe(
						new Action1<Status>() {
							@Override
							public void call(final Status status) {
								messageHelper.addNotice(bundle, "groups.decline.notice");
								final Company company = companyService.findByUUID(participantUuid);
								eventRouter.sendEvent(new VendorSearchIndexEvent(company.getId()));
							}
						},
						new Action1<Throwable>() {
							@Override
							public void call(Throwable throwable) {
								messageHelper.addError(bundle, "groups.decline.error");
								logger.error("Failed to apply to talent pool: {}", throwable);
							}
						});
	}

	private TalentPoolUpdateParticipantsRequest buildTalentPoolDeclineParticipationRequest(
			final String talentPoolUuid,
			final String participationUuid) {

		final String now = ISO8601Utils.format(DateUtilities.getNow());
		final TalentPoolParticipation talentPoolDeclineParticipationRequest = TalentPoolParticipation.newBuilder()
				.setParticipantType(ParticipantType.VENDOR)
				.setDeclinedOn(now)
				.build();

		return TalentPoolUpdateParticipantsRequest.newBuilder()
				.setTalentPoolUuid(talentPoolUuid)
				.addParticipantUuid(participationUuid)
				.setTalentPoolParticipation(talentPoolDeclineParticipationRequest)
				.build();
	}

	@RequestMapping(
		value = "/{id}/decline",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder declineJson(@PathVariable("id") Long groupId) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);
		try {
			requestService.declineInvitationToGroup(getCurrentUser().getId(), groupId);
			messageHelper.addMessage(response, "groups.decline.notice");

			return response.setSuccessful(true);

		} catch (Exception ex) {
			logger.error("There was a problem declining the invitation for groupId=" + groupId, ex);
			messageHelper.addMessage(response, "groups.decline.error");
		}

		return response;
	}

	protected boolean isGroupAdmin(UserGroup group, ExtendedUserDetails user) {
		Long companyId = (group.getCompany() != null) ? group.getCompany().getId() : null;
		return isGroupAdmin(companyId, user);
	}

	protected boolean isGroupAdmin(Long companyId, ExtendedUserDetails user) {
		return user.hasAnyRoles("ROLE_INTERNAL") || (user.getCompanyId().equals(companyId) && user.hasAnyRoles("ACL_ADMIN", "ACL_MANAGER"));
	}

	protected boolean isCompanyGroupViewer(UserGroup group, ExtendedUserDetails user) {
		return (group.getCompany() != null) && user.getCompanyId().equals(group.getCompany().getId());
	}

	protected boolean isGroupMember(UserUserGroupAssociation association) {
		return association != null && association.isApproved() && association.getVerificationStatus().isVerified();
	}
}
