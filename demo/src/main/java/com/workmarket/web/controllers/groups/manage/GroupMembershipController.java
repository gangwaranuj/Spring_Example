package com.workmarket.web.controllers.groups.manage;

import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.business.talentpool.TalentPoolClient;
import com.workmarket.business.talentpool.gen.Messages.ParticipantType;
import com.workmarket.business.talentpool.gen.Messages.Status;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolAddParticipantsRequest;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipant;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipation;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolUpdateParticipantsRequest;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolDeleteParticipantsRequest;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.groups.model.GroupMemberRequestType;
import com.workmarket.domains.groups.model.GroupMembershipPagination;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.service.business.UserGroupEvaluationScheduledRunService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.GroupMembershipDTO;
import com.workmarket.service.business.event.company.VendorSearchIndexEvent;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.views.CSVView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.function.matcher.AndMatcher.and;
import static com.workmarket.utility.StringUtilities.pluralize;
import static java.lang.Math.abs;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@PreAuthorize("hasAnyRole('ACL_ADMIN', 'ACL_MANAGER') AND !principal.companyIsLocked")
@RequestMapping("/groups")
public class GroupMembershipController extends BaseGroupsManageController {

	private static final Log logger = LogFactory.getLog(GroupMembershipController.class);

	@Autowired private UserGroupEvaluationScheduledRunService userGroupEvaluationScheduledRunService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private TalentPoolClient talentPoolClient;
	@Autowired private UserGroupService userGroupService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private UserService userService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	@Deprecated
	@RequestMapping(
		value = "/invite_workers/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder inviteWorkers(
		@PathVariable("id") Long groupId,
		@RequestParam("selected_workers[]") String[] selectedWorkers,
		RedirectAttributes redirectAttributes) throws Exception {

		UserGroup group = getGroupService().findGroupById(groupId);

		Set<String> selectedWorkersUserNumbers = new HashSet<>(Arrays.asList(selectedWorkers));
		List<RequestContext> authz = getGroupService().getRequestContext(groupId);
		if (!authz.contains(RequestContext.ADMIN)) {
			return new AjaxResponseBuilder().setSuccessful(false);
		}

		if (selectedWorkersUserNumbers.isEmpty())
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(Lists.newArrayList(getMessageHelper().getMessage("groups.manage.users.empty")));

		List<Long> eligibleUserIds = getGroupService().getEligibleUserIdsForInvitationToGroup(new ArrayList<>(userService.findAllUserIdsByUserNumbers(Arrays.asList(selectedWorkers))), group.getId());

		if (eligibleUserIds.isEmpty())
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(Lists.newArrayList(getMessageHelper().getMessage("groups.manage.users.all_invited", StringEscapeUtils.unescapeHtml4(group.getName()))));

		MessageBundle messages = getMessageHelper().newFlashBundle(redirectAttributes);
		int totalSentInvitations = selectedWorkersUserNumbers.size();

		logger.info("[addToGroup] - SEND EVENT");
		eventRouter.sendEvents(eventFactory.buildInviteToGroupEvent(eligibleUserIds, groupId, getCurrentUser().getId()));
		if (group.getOpenMembership()) {

			int skippedCount = abs(eligibleUserIds.size() - selectedWorkersUserNumbers.size());
			totalSentInvitations = selectedWorkersUserNumbers.size() - skippedCount;
			if (skippedCount > 0) {
				int sent = selectedWorkersUserNumbers.size() - skippedCount;
				getMessageHelper().addSuccess(messages, "groups.manage.users.invited", sent, pluralize("worker", sent), StringEscapeUtils.unescapeHtml4(group.getName()));
				getMessageHelper().addSuccess(messages, "groups.manage.users.not_invited", skippedCount, pluralize("worker", skippedCount),
					skippedCount > 1 ? "have" : "has",
					group.getName());
			} else {
				getMessageHelper().addSuccess(messages, "groups.manage.users.invited", selectedWorkersUserNumbers.size(), pluralize("worker", selectedWorkersUserNumbers.size()), StringEscapeUtils.unescapeHtml4(group.getName()));
			}
		} else {
			getMessageHelper().addSuccess(messages, "groups.manage.users.added", selectedWorkersUserNumbers.size(), pluralize("worker", selectedWorkersUserNumbers.size()), StringEscapeUtils.unescapeHtml4(group.getName()));
		}

		return new AjaxResponseBuilder()
			.setSuccessful(true)
			.setMessages(messages.getAllMessages());
	}

	@RequestMapping(
		value = "/invite_participants/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder inviteParticipants(
		@PathVariable("id") Long groupId,
		@RequestBody List<Participant> participants,
		RedirectAttributes redirectAttributes) throws Exception {

		List<RequestContext> authz = getGroupService().getRequestContext(groupId);
		if (!authz.contains(RequestContext.ADMIN)) {
			return new AjaxResponseBuilder().setSuccessful(false);
		}

		final UserGroup group = userGroupService.findGroupById(groupId);
		final MessageBundle messages = getMessageHelper().newFlashBundle(redirectAttributes);

		if (group == null) {
			getMessageHelper().addError(messages, "groups.message.groups.empty");
			return getAjaxResponseWithError(messages);
		}

		if (participants.isEmpty()) {
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(Lists.newArrayList(getMessageHelper().getMessage("groups.manage.users.empty")));
		}

		final Set<String> selectedVendorsCompanyNumbers = extractParticipantNumbersByParticipantType(participants, SolrUserType.VENDOR);
		if (!CollectionUtils.isEmpty(selectedVendorsCompanyNumbers)) {
			final List<String> vendorUuids =
					getCompanyService().getCompanyUuidsForCompanyNumbers(selectedVendorsCompanyNumbers);
			final TalentPoolAddParticipantsRequest talentPoolAddParticipantsRequest =
				buildTalentPoolAddParticipantsRequest(group, vendorUuids);
			final Long currentUserId = getCurrentUser().getId();

			talentPoolClient.addParticipants(talentPoolAddParticipantsRequest, webRequestContextProvider.getRequestContext())
						.subscribe(
								new Action1<Status>() {
									@Override
									public void call(final Status status) {
										messages.addSuccess(status.getMessage());
										final List<Long> companyIds = getCompanyService().getCompanyIdsByUuids(vendorUuids);
										userNotificationService.onUserGroupToVendorsInvitation(group.getId(), currentUserId, companyIds);
										eventRouter.sendEvent(new VendorSearchIndexEvent(companyIds));
									}
								},
								new Action1<Throwable>() {
									@Override
									public void call(Throwable throwable) {
										messages.addError(throwable.getMessage());
										logger.error("Failed to add vendors to talent pool: {}", throwable);
									}
								});
		}

		final Set<String> selectedWorkersUserNumbers = extractParticipantNumbersByParticipantType(participants, SolrUserType.WORKER);
		if(CollectionUtils.isEmpty(selectedWorkersUserNumbers)) {
			return new AjaxResponseBuilder()
					.setSuccessful(true)
					.setMessages(messages.getAllMessages());
		}

		final List<Long> eligibleUserIds = getUserIdToInvite(selectedWorkersUserNumbers, group);
		final String unescapedGroupName = StringEscapeUtils.unescapeHtml4(group.getName());
		if (selectedVendorsCompanyNumbers.isEmpty() && eligibleUserIds.isEmpty())
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(Lists.newArrayList(getMessageHelper().getMessage("groups.manage.users.all_invited", unescapedGroupName)));

		eventRouter.sendEvents(eventFactory.buildInviteToGroupEvent(eligibleUserIds, groupId, getCurrentUser().getId()));

		final ImmutableList<String> invitationMessages = getInvitationMessages(group, selectedWorkersUserNumbers, eligibleUserIds, unescapedGroupName);
		for (final String message : invitationMessages) {
			messages.addSuccess(message);
		}

		return new AjaxResponseBuilder()
			.setSuccessful(true)
			.setMessages(messages.getAllMessages());
	}

	private Set<String> extractParticipantNumbersByParticipantType(
		final List<Participant> participants,
		final SolrUserType participantType
	) {
		final List<Participant> participantsOfType =
				filter(having(on(Participant.class).getParticipantType(), equalTo(participantType)), participants);
		final List<String> participantNumbers = extract(participantsOfType, on(Participant.class).getNumber());
		return new HashSet<>(participantNumbers);
	}

	private Set<String> extractParticipantNumbersByParticipantTypeAndRequestType(
		final List<Participant> participants,
		final SolrUserType participantType,
		final GroupMemberRequestType requestType
	) {
		final List<Participant> ofParticipantTypeAndRequestType =
			filter(
				and(
					having(on(Participant.class).getParticipantType(), equalTo(participantType)),
					having(on(Participant.class).getRequestType(), equalTo(requestType))),
				participants);
		final List<String> participantNumbers = extract(ofParticipantTypeAndRequestType, on(Participant.class).getNumber());
		return new HashSet<>(participantNumbers);
	}

	private ImmutableList<String> getInvitationMessages(
			final UserGroup group,
			final Set<String> selectedWorkersUserNumbers,
			final List<Long> eligibleUserIds,
			final String unescapedGroupName) {

		ImmutableList.Builder<String> messages = ImmutableList.builder();

		if (group.getOpenMembership()) {
			final int skippedCount = abs(eligibleUserIds.size() - selectedWorkersUserNumbers.size());
			final int numSent = skippedCount > 0 ? selectedWorkersUserNumbers.size() - skippedCount : selectedWorkersUserNumbers.size();
			final String haveOrHasSkipped = skippedCount > 1 ? "have" : "has";
			getMessageHelper().getMessage("groups.manage.users.not_invited", skippedCount, pluralize("worker", skippedCount), haveOrHasSkipped, unescapedGroupName);
			messages.add(getMessageHelper().getMessage("groups.manage.users.invited", numSent, pluralize("worker", numSent), unescapedGroupName));
		} else {
			messages.add(getMessageHelper().getMessage("groups.manage.users.added", selectedWorkersUserNumbers.size(), pluralize("worker", selectedWorkersUserNumbers.size()), unescapedGroupName));
		}

		return messages.build();
	}

	private List<Long> getUserIdToInvite(final Collection<String> workerNumbers, final UserGroup group) {
		if (CollectionUtils.isEmpty(workerNumbers)) {
			return Collections.emptyList();
		}
		final ArrayList<Long> userIds = new ArrayList<>(userService.findAllUserIdsByUserNumbers(workerNumbers));
		return userGroupService.getEligibleUserIdsForInvitationToGroup(userIds, group.getId());
	}

	private TalentPoolUpdateParticipantsRequest buildTalentPoolUpdateParticipantsRequest(
			final UserGroup group,
			final List<String> vendorUuids,
			final TalentPoolParticipation talentPoolParticipation) {

		final TalentPoolUpdateParticipantsRequest.Builder talentPoolUpdateParticipantsRequestBuilder =
				TalentPoolUpdateParticipantsRequest.newBuilder()
						.setTalentPoolUuid(group.getUuid());
		talentPoolUpdateParticipantsRequestBuilder.addAllParticipantUuid(vendorUuids);
		talentPoolUpdateParticipantsRequestBuilder.setTalentPoolParticipation(talentPoolParticipation);
		return talentPoolUpdateParticipantsRequestBuilder.build();
	}

	private TalentPoolParticipation getTalentPoolParticipationApproval() {
		final String now = ISO8601Utils.format(DateUtilities.getNow());
		return TalentPoolParticipation.newBuilder()
				.setParticipantType(ParticipantType.VENDOR)
				.setApprovedOn(now)
				.build();
	}

	private TalentPoolAddParticipantsRequest buildTalentPoolAddParticipantsRequest(
			final UserGroup group,
			final List<String> vendorUuids) {

		final TalentPoolAddParticipantsRequest.Builder talentPoolAddParticipantsRequestBuilder =
			TalentPoolAddParticipantsRequest.newBuilder()
				.setTalentPoolUuid(group.getUuid());
		final TalentPoolParticipation talentPoolParticipation = getTalentPoolParticipation(group);
		final ImmutableList<TalentPoolParticipant> talentPoolParticipants =
			buildTalentPoolParticipants(vendorUuids, talentPoolParticipation);
		talentPoolAddParticipantsRequestBuilder.addAllTalentPoolParticipant(talentPoolParticipants);
		return talentPoolAddParticipantsRequestBuilder.build();
	}

	private ImmutableList<TalentPoolParticipant> buildTalentPoolParticipants(
		final List<String> vendorUuids,
		final TalentPoolParticipation talentPoolParticipation) {

		final ImmutableList.Builder<TalentPoolParticipant> talentPoolParticipants = ImmutableList.builder();
		for (String vendorUuid : vendorUuids) {
			final TalentPoolParticipant.Builder talentPoolParticipantBuilder =
				TalentPoolParticipant.newBuilder()
					.setParticipantUuid(vendorUuid)
					.setTalentPoolParticipation(talentPoolParticipation);
			talentPoolParticipants.add(talentPoolParticipantBuilder.build());
		}
		return talentPoolParticipants.build();
	}

	private TalentPoolParticipation getTalentPoolParticipation(final UserGroup group) {
		final String now = ISO8601Utils.format(DateUtilities.getNow());
		final String approvedOn = group.getOpenMembership() ? "" : now;
		return TalentPoolParticipation.newBuilder()
				.setParticipantType(ParticipantType.VENDOR)
				.setInvitedOn(now)
				.setApprovedOn(approvedOn)
				.build();
	}

	@RequestMapping(
		value = "/manage/add_to_group/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder addToGroup(
		@PathVariable("id") Long groupId,
		@RequestParam("userId") Long userId) throws Exception {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		List<Long> resources = Lists.newArrayList(userId);
		List<RequestContext> authz = getGroupService().getRequestContext(groupId);

		if (!authz.contains(RequestContext.ADMIN)) {
			messageHelper.addMessage(response, "profile.group.invite.error");
			return response;
		}

		getGroupService().addUsersToGroup(resources, groupId, getCurrentUser().getId());
		messageHelper.addMessage(response, "profile.group.invite.success");
		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/{id}/export_members",
		method = GET)
	public CSVView exportMembers(
		@PathVariable("id") Long groupId,
		Model model) {

		List<RequestContext> authz = getGroupService().getRequestContext(groupId);
		if (!authz.contains(RequestContext.ADMIN)) {
			return null;
		}

		List<String[]> data = Lists.newArrayList();
		data.add(CollectionUtilities.newArray(
			"Resource ID",
			"First Name",
			"Last Name",
			"Company",
			"City",
			"State",
			"Zip",
			"Latitude",
			"Longitude",
			"Resource Type"
		));

		GroupMembershipPagination pagination = new GroupMembershipPagination();
		pagination.setReturnAllRows(true);

		pagination = getGroupService().findGroupMembersByUserGroupId(groupId, GroupMemberRequestType.MEMBER.name(), pagination);

		for (GroupMembershipDTO dto : pagination.getResults()) {
			data.add(CollectionUtilities.newArray(
				dto.getUserNumber(),
				dto.getFirstName(),
				dto.getLastName(),
				dto.getCompanyName(),
				dto.getCity(),
				dto.getState(),
				dto.getPostalCode(),
				(dto.getLatitude() != null) ? dto.getLatitude().toPlainString() : null,
				(dto.getLatitude() != null) ? dto.getLongitude().toPlainString() : null,
				dto.getLaneType().getDescription()
			));
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, data);

		CSVView view = new CSVView();
		view.setFilename(String.format("group-members-%d.csv", groupId));
		return view;
	}

	@Deprecated
	@RequestMapping(
		value = "/{id}/approve_users",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder approveUsers(
		@PathVariable("id") Long groupId,
		@RequestParam("userNumbers[]") String[] selectedWorkers,
		RedirectAttributes redirectAttributes) {

		MessageBundle messages = getMessageHelper().newFlashBundle(redirectAttributes);

		UserGroup group = userGroupService.findGroupById(groupId);
		if (group != null && !isGroupAdmin(group, getCurrentUser())) {
			getMessageHelper().addError(messages, "groups.manage.users.unable_to_edit_not_group_owner");
			return getAjaxResponseWithError(messages);
		}

		Set<Long> userIds = userService.findAllUserIdsByUserNumbers(Arrays.asList(selectedWorkers));

		try {
			getGroupService().approveUsers(groupId, userIds.toArray(new Long[userIds.size()]));

			getMessageHelper().addSuccess(messages, "groups.manage.users.approve.success");
			return new AjaxResponseBuilder()
				.setSuccessful(true)
				.setMessages(messages.getSuccess());

		} catch (Exception e) {
			getMessageHelper().addError(messages, "groups.manage.users.approve.error");
			return getAjaxResponseWithError(messages);
		}
	}

	@RequestMapping(
			value = "/{id}/approve_participants",
			method = POST,
			produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder approveParticipants(
			@PathVariable("id") Long groupId,
			@RequestBody List<Participant> participants,
			RedirectAttributes redirectAttributes) {

		final MessageBundle messages = getMessageHelper().newFlashBundle(redirectAttributes);

		UserGroup group = userGroupService.findGroupById(groupId);
		if (group == null) {
			getMessageHelper().addError(messages, "groups.message.groups.empty");
			return getAjaxResponseWithError(messages);
		}

		if (!isGroupAdmin(group, getCurrentUser())) {
			getMessageHelper().addError(messages, "groups.manage.users.unable_to_edit_not_group_owner");
			return getAjaxResponseWithError(messages);
		}

		if (participants.isEmpty()) {
			return new AjaxResponseBuilder()
					.setSuccessful(false)
					.setMessages(Lists.newArrayList(getMessageHelper().getMessage("groups.manage.participants.empty")));
		}

		final Set<String> selectedVendorsCompanyNumbers = extractParticipantNumbersByParticipantType(participants, SolrUserType.VENDOR);

		if (!CollectionUtils.isEmpty(selectedVendorsCompanyNumbers)) {
			final List<String> vendorUuids =
					getCompanyService().getCompanyUuidsForCompanyNumbers(selectedVendorsCompanyNumbers);
			final TalentPoolParticipation talentPoolParticipationApproval = getTalentPoolParticipationApproval();
			final TalentPoolUpdateParticipantsRequest talentPoolApproveParticipantsRequest =
					buildTalentPoolUpdateParticipantsRequest(group, vendorUuids, talentPoolParticipationApproval);

			talentPoolClient.updateParticipants(talentPoolApproveParticipantsRequest, webRequestContextProvider.getRequestContext())
					.subscribe(
							new Action1<Status>() {
								@Override
								public void call(final Status status) {
									messages.addSuccess(status.getMessage());
									final List<Long> companyIds = getCompanyService().getCompanyIdsByUuids(vendorUuids);
									eventRouter.sendEvent(new VendorSearchIndexEvent(companyIds));
								}
							},
							new Action1<Throwable>() {
								@Override
								public void call(Throwable throwable) {
									messages.addError(throwable.getMessage());
									logger.error("Failed to approve vendors for talent pool: {}", throwable);
								}
							});
		}

		final Set<String> selectedWorkersUserNumbers = extractParticipantNumbersByParticipantType(participants, SolrUserType.WORKER);
		if (selectedWorkersUserNumbers.isEmpty()) {
			return new AjaxResponseBuilder()
					.setSuccessful(true)
					.setMessages(messages.getAllMessages());
		}

		final Set<Long> userIds = userService.findAllUserIdsByUserNumbers(selectedWorkersUserNumbers);
		try {
			getGroupService().approveUsers(groupId, userIds.toArray(new Long[userIds.size()]));

			getMessageHelper().addSuccess(messages, "groups.manage.users.approve.success");
			return new AjaxResponseBuilder()
					.setSuccessful(true)
					.setMessages(messages.getAllMessages());

		} catch (Exception e) {
			getMessageHelper().addError(messages, "groups.manage.users.approve.error");
			return getAjaxResponseWithError(messages);
		}
	}

	@RequestMapping(
		value = "/{id}/decline_users",
		method = POST)
	public @ResponseBody AjaxResponseBuilder declineUsers(
		@PathVariable("id") Long groupId,
		@RequestParam("userNumbers[]") String[] selectedWorkers,
		RedirectAttributes redirectAttributes) {

		MessageBundle messages = getMessageHelper().newFlashBundle(redirectAttributes);

		UserGroup group = userGroupService.findGroupById(groupId);
		if (group != null && !isGroupAdmin(group, getCurrentUser())) {
			getMessageHelper().addError(messages, "groups.manage.users.unable_to_edit_not_group_owner");
			return getAjaxResponseWithError(messages);
		}

		Set<Long> userIds = userService.findAllUserIdsByUserNumbers(Arrays.asList(selectedWorkers));

		try {
			getGroupService().declineUsers(groupId, userIds.toArray(new Long[userIds.size()]));

			getMessageHelper().addSuccess(messages, "groups.manage.users.decline.success");
			return new AjaxResponseBuilder()
				.setSuccessful(true)
				.setMessages(messages.getSuccess());

		} catch (Exception e) {
			getMessageHelper().addError(messages, "groups.manage.users.decline.error");
			return getAjaxResponseWithError(messages);
		}
	}

	@Deprecated
	@RequestMapping(
		value = "/{id}/remove_users",
		method = POST)
	public @ResponseBody AjaxResponseBuilder removeUsers(
		@PathVariable("id") Long groupId,
		@RequestParam("userNumbers[]") String[] selectedWorkers,
		RedirectAttributes redirectAttributes) {

		MessageBundle messages = getMessageHelper().newFlashBundle(redirectAttributes);

		UserGroup group = userGroupService.findGroupById(groupId);
		if (group != null && !isGroupAdmin(group, getCurrentUser())) {
			getMessageHelper().addError(messages, "groups.manage.users.unable_to_edit_not_group_owner");
			return getAjaxResponseWithError(messages);
		}
		Set<Long> userIds = userService.findAllUserIdsByUserNumbers(Arrays.asList(selectedWorkers));

		try {
			getGroupService().removeAssociations(groupId, userIds.toArray(new Long[userIds.size()]), getCurrentUser().getCompanyId());
			getMessageHelper().addSuccess(messages, "groups.manage.users.remove.success");
			return new AjaxResponseBuilder()
				.setSuccessful(true)
				.setMessages(messages.getSuccess());

		} catch (Exception e) {
			getMessageHelper().addError(messages, "groups.manage.users.remove.error");
			return getAjaxResponseWithError(messages);
		}
	}

	@RequestMapping(
			value = "/{id}/remove_participants",
			method = POST,
			produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder removeParticipants(
			@PathVariable("id") Long groupId,
			@RequestBody List<Participant> participants,
			RedirectAttributes redirectAttributes) {

		final MessageBundle messages = getMessageHelper().newFlashBundle(redirectAttributes);

		UserGroup group = userGroupService.findGroupById(groupId);
		if (group == null) {
			getMessageHelper().addError(messages, "groups.message.groups.empty");
			return getAjaxResponseWithError(messages);
		}

		if (!isGroupAdmin(group, getCurrentUser())) {
			getMessageHelper().addError(messages, "groups.manage.users.unable_to_edit_not_group_owner");
			return getAjaxResponseWithError(messages);
		}

		final Set<String> selectedVendorsCompanyNumbers = extractParticipantNumbersByParticipantType(participants, SolrUserType.VENDOR);
		if (!CollectionUtils.isEmpty(selectedVendorsCompanyNumbers)) {
			final List<String> vendorUuids =
					getCompanyService().getCompanyUuidsForCompanyNumbers(selectedVendorsCompanyNumbers);
			final TalentPoolDeleteParticipantsRequest talentPoolRemoveParticipantsRequest =
					TalentPoolDeleteParticipantsRequest.newBuilder()
							.setTalentPoolUuid(group.getUuid())
							.addAllParticipantUuid(vendorUuids)
							.build();

			talentPoolClient.deleteParticipants(talentPoolRemoveParticipantsRequest, webRequestContextProvider.getRequestContext())
					.subscribe(
							new Action1<Status>() {
								@Override
								public void call(final Status status) {
									messages.addSuccess(status.getMessage());
									final List<Long> companyIds = getCompanyService().getCompanyIdsByUuids(vendorUuids);
									eventRouter.sendEvent(new VendorSearchIndexEvent(companyIds));
								}
							},
							new Action1<Throwable>() {
								@Override
								public void call(Throwable throwable) {
									messages.addError(throwable.getMessage());
									logger.error("Failed to remove vendors from talent pool: {}", throwable);
								}
							});
		}

		final Set<String> workersUserNumbersToDecline = extractParticipantNumbersByParticipantType(participants, SolrUserType.WORKER);
		final Set<String> workersUserNumbersToUninvite =
			extractParticipantNumbersByParticipantTypeAndRequestType(participants, SolrUserType.WORKER, GroupMemberRequestType.INVITED);
		workersUserNumbersToDecline.removeAll(workersUserNumbersToUninvite);
		if (workersUserNumbersToUninvite.isEmpty() && workersUserNumbersToDecline.isEmpty()) {
			return new AjaxResponseBuilder()
					.setSuccessful(true)
					.setMessages(messages.getAllMessages());
		}

		if (!workersUserNumbersToDecline.isEmpty()) {
			Set<Long> userIds = userService.findAllUserIdsByUserNumbers(workersUserNumbersToDecline);

			try {
				getGroupService().removeAssociations(groupId, userIds.toArray(new Long[userIds.size()]), getCurrentUser().getCompanyId());
				getMessageHelper().addSuccess(messages, "groups.manage.users.remove.success");
			} catch (Exception e) {
				getMessageHelper().addError(messages, "groups.manage.users.remove.error");
			}
		}
		if (!workersUserNumbersToUninvite.isEmpty()) {
			Set<Long> userIds = userService.findAllUserIdsByUserNumbers(workersUserNumbersToUninvite);
			try {
				getRequestService().deleteInvitationsToGroup(new ArrayList<>(userIds), groupId);
				getMessageHelper().addSuccess(messages, "groups.manage.users.cancel.success");
			} catch (Exception e) {
				getMessageHelper().addError(messages, "groups.manage.users.cancel.error");
			}
		}

		if (messages.hasErrors()) {
			return getAjaxResponseWithError(messages);
		} else {
			return new AjaxResponseBuilder()
				.setSuccessful(true)
				.setMessages(messages.getSuccess());
		}
	}

	@RequestMapping(
		value = "/{id}/cancel_invitations",
		method = POST)
	public @ResponseBody AjaxResponseBuilder cancelInvitations(
		@PathVariable("id") Long groupId,
		@RequestParam("userNumbers[]") String[] selectedWorkers,
		RedirectAttributes redirectAttributes) {

		MessageBundle messages = getMessageHelper().newFlashBundle(redirectAttributes);

		UserGroup group = userGroupService.findGroupById(groupId);
		if (group != null && !isGroupAdmin(group, getCurrentUser())) {
			getMessageHelper().addError(messages, "groups.manage.users.unable_to_edit_not_group_owner");
			return getAjaxResponseWithError(messages);
		}

		try {
			getRequestService().deleteInvitationsToGroup(new ArrayList<>(userService.findAllUserIdsByUserNumbers(Arrays.asList(selectedWorkers))), groupId);

			getMessageHelper().addSuccess(messages, "groups.manage.users.cancel.success");
			return new AjaxResponseBuilder()
				.setSuccessful(true)
				.setMessages(messages.getSuccess());

		} catch (Exception e) {
			getMessageHelper().addError(messages, "groups.manage.users.cancel.error");
			return getAjaxResponseWithError(messages);
		}
	}

	@RequestMapping(
		value = "/{id}/validateRequirements/{userNumber}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder validateRequirementsVoid(
		@PathVariable Long id,
		@PathVariable String userNumber) {

		UserGroup group = userGroupService.findGroupById(id);
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		Long userId = userService.findUserId(userNumber);
		Map<String, Object> data;

		data = getMissingRequirements(group, userId);

		return response.addData("data", data).setSuccessful(true);
	}

	private Map<String, Object> getMissingRequirements(UserGroup userGroup, Long userId) {
		Eligibility eligibility = userGroupService.validateRequirementSets(userGroup.getId(), userId);
		List<Criterion> missingRequirementCriteria =
			select(eligibility.getCriteria(), having(on(Criterion.class).isMet(), is(false)));
		Map<String, Object> missingRequirementData = new HashMap<>();
		missingRequirementData.put("criteria", missingRequirementCriteria);
		return missingRequirementData;
	}

	@RequestMapping(
		value = "/{id}/revalidate_membership_requirements",
		method = GET)
	public String revalidateMembershipRequirements(
		@PathVariable("id") Long groupId,
		RedirectAttributes redirectAttributes) {

		MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);

		try {
			userGroupService.revalidateAllAssociations(groupId);
			messageHelper.addSuccess(messages, "groups.manage.revalidate.success");
			return String.format("redirect:/groups/%d", groupId);
		} catch (Exception e) {
			messageHelper.addError(messages, "groups.manage.revalidate.error");
			return String.format("redirect:/groups/%d/requirements", groupId);
		}
	}

	@RequestMapping(
		value = "/{id}/automatic_evaluation/on",
		produces = APPLICATION_JSON_VALUE,
		method = POST)
	public @ResponseBody AjaxResponseBuilder turnAutomaticEvaluationOn(
		@PathVariable("id") Long userGroupId,
		RedirectAttributes redirectAttributes) {

		MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);
		UserGroup userGroup = userGroupService.findGroupById(userGroupId);
		messages = getPermissionErrors(messages, userGroup);
		if (messages.hasErrors()) {
			return new AjaxResponseBuilder()
					.setSuccessful(false)
					.setMessages(messages.getErrors());
		}

		messages = getHasFeatureErrors(messages, userGroup);
		if (messages.hasErrors()) {
			return new AjaxResponseBuilder()
					.setSuccessful(false)
					.setMessages(messages.getErrors());
		}

		int defaultValidateDaysInterval = 1;
		userGroupEvaluationScheduledRunService.turnOnAutomaticEvaluation(userGroupId, defaultValidateDaysInterval);

		String message = getToggleSuccessMessage("on");
		messages.addSuccess(message);
		return new AjaxResponseBuilder()
				.setSuccessful(true)
				.setMessages(messages.getAllMessages());
	}

	@RequestMapping(
		value = "/{id}/automatic_evaluation/off",
		produces = APPLICATION_JSON_VALUE,
		method = POST)
	public @ResponseBody AjaxResponseBuilder turnAutomaticEvaluationOff(
		@PathVariable("id") Long userGroupId,
		RedirectAttributes redirectAttributes) {

		MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);
		UserGroup userGroup = userGroupService.findGroupById(userGroupId);

		messages = getPermissionErrors(messages, userGroup);
		if (messages.hasErrors()) {
			return new AjaxResponseBuilder()
					.setSuccessful(false)
					.setMessages(messages.getErrors());
		}

		messages = getHasFeatureErrors(messages, userGroup);
		if (messages.hasErrors()) {
			return new AjaxResponseBuilder()
					.setSuccessful(false)
					.setMessages(messages.getErrors());
		}

		userGroupEvaluationScheduledRunService.turnOffAutomaticEvaluation(userGroupId);

		String message = getToggleSuccessMessage("off");
		messages.addSuccess(message);
		return new AjaxResponseBuilder()
				.setSuccessful(true)
				.setMessages(messages.getAllMessages());
	}

	private MessageBundle getPermissionErrors(MessageBundle messages, UserGroup userGroup) {
		ExtendedUserDetails user = getCurrentUser();
		if (userGroup == null || isGroupAdmin(userGroup, user)) {
			return messages;
		}

		messageHelper.addError(messages, "groups.manage.users.unable_to_edit_not_group_owner");
		return messages;
	}

	private MessageBundle getHasFeatureErrors(MessageBundle messages, UserGroup userGroup) {
		if(hasFeature(userGroup.getCompany().getId(), "talent-pool-requirements-automatic-evaluation")) {
			return messages;
		}

		messageHelper.getMessage("groups.manage.automatic.validate.feature.unavailable");
		return messages;
	}

	private String getToggleSuccessMessage(String toggleState) {
		return messageHelper.getMessage("groups.manage.automatic.validate.toggle.success", toggleState);
	}

	protected boolean isGroupAdmin(UserGroup group, ExtendedUserDetails user) {
		Long companyId = (group.getCompany() != null) ? group.getCompany().getId() : null;
		return isGroupAdmin(companyId, user);
	}

	protected boolean isGroupAdmin(Long companyId, ExtendedUserDetails user) {
		return user.hasAnyRoles("ROLE_INTERNAL") || (user.getCompanyId().equals(companyId) && user.hasAnyRoles("ACL_ADMIN", "ACL_MANAGER"));
	}

	static class Participant {
		private String number;
		private SolrUserType participantType;
		private GroupMemberRequestType requestType;

		public String getNumber() {
			return number;
		}

		public void setNumber(final String number) {
			this.number = number;
		}

		public SolrUserType getParticipantType() {
			return participantType;
		}

		public void setParticipantType(final SolrUserType participantType) {
			this.participantType = participantType;
		}

		public GroupMemberRequestType getRequestType() {
			return requestType;
		}

		public void setRequestType(final GroupMemberRequestType requestType) {
			this.requestType = requestType;
		}
	}

}
