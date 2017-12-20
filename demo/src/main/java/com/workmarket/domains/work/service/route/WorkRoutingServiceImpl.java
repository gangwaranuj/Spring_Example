package com.workmarket.domains.work.service.route;

import ch.lambdaj.function.convert.PropertyExtractor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.rope.AvoidScheduleConflictsRoutingRope;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.service.WorkActionRequestFactory;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.resource.WorkResourceAddOptions;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.velvetrope.Doorman;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections.CollectionUtils.subtract;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class WorkRoutingServiceImpl implements WorkRoutingService {

	private static final Log logger = LogFactory.getLog(WorkRoutingServiceImpl.class);
	@Autowired private WorkService workService;
	@Autowired private UserService userService;
	@Autowired private EventFactory eventFactory;
	@Autowired private WorkRoutingValidator workRoutingValidator;
	@Autowired private WorkStatusService workStatusService;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private SummaryService summaryService;
	@Autowired private LaneService laneService;
	@Autowired private EventRouter eventRouter;
	@Autowired private WorkActionRequestFactory workActionRequestFactory;
	@Autowired private AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Autowired private AuthenticationService authenticationService;

	@Qualifier("avoidScheduleConflictsRoutingDoorman")
	@Autowired private Doorman doorman;
	@Autowired private WorkResourceDAO workResourceDAO;

	@SuppressWarnings("unchecked")
	@Override
	public WorkRoutingResponseSummary addToWorkResources(Work work, Set<Long> potentialWorkResourcesUserIds, WorkResourceAddOptions options)
		throws WorkNotFoundException {
		return addToWorkResources(work, potentialWorkResourcesUserIds, options, Collections.EMPTY_SET, null, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public WorkRoutingResponseSummary addToWorkResources(Work work, Set<Long> potentialWorkResourcesUserIds, WorkResourceAddOptions options, boolean assignToFirstToAccept)
		throws WorkNotFoundException {

		return addToWorkResources(work, potentialWorkResourcesUserIds, options, Collections.EMPTY_SET, null, assignToFirstToAccept);
	}

	private WorkRoutingResponseSummary addToWorkResources(
		Work work,
		Set<Long> potentialWorkersUserIds,
		WorkResourceAddOptions options,
		Collection<String> potentialWorkersUserNumbers,
		Long dispatcherId,
		boolean assignToFirstToAccept
	) throws WorkNotFoundException {

		WorkRoutingResponseSummary responseSummary = new WorkRoutingResponseSummary();
		if (work == null) {
			throw new WorkNotFoundException("There was a problem finding work ");
		}

		Set<WorkResource> invitedWorkResources = Sets.newHashSetWithExpectedSize(potentialWorkersUserIds.size());

		final Set<WorkAuthorizationResponse> workAuthorizationResponses = workRoutingValidator.validateWorkForRouting(work);
		if (!workAuthorizationResponses.contains(WorkAuthorizationResponse.SUCCEEDED)) {
			WorkAuthorizationResponse authorizationResponse = CollectionUtilities.first(workAuthorizationResponses);
			responseSummary.addToWorkAuthorizationResponse(authorizationResponse, potentialWorkersUserNumbers);
			return responseSummary;
		}

		if (isNotEmpty(potentialWorkersUserIds)) {
			final List<WorkResource> resourcesAlreadyOnWork = workResourceService.findAllResourcesForWork(work.getId());

			//First find all the users that could be eligible to take the work and add them to workResponseSummary
			findEligibleWorkResourcesByUserLaneType(work, responseSummary, potentialWorkersUserIds, resourcesAlreadyOnWork, dispatcherId != null);
			//Authorize account register and
			Set<User> authorizedWorkResources = authorizeWorkResources(work, responseSummary);
			//Save the resources
			User workCreator = userService.findUserById(work.getCreatorId());
			//Don't let the job creator assign himself the job unless it's an internal assignment
			if (!(work.getPricingStrategyType().isInternal()) && authorizedWorkResources.contains(workCreator)) {
				authorizedWorkResources.remove(workCreator);
			}

			invitedWorkResources = saveWorkResources(work, responseSummary, potentialWorkersUserIds, options, authorizedWorkResources, resourcesAlreadyOnWork, dispatcherId, assignToFirstToAccept);
			if (isEmpty(invitedWorkResources)) {
				return responseSummary;
			}
		} else {
			//Work feed allows zero resources
			responseSummary.addToWorkAuthorizationResponse(WorkAuthorizationResponse.SUCCEEDED, StringUtils.EMPTY);
		}

		if (!responseSummary.isSuccessful()) {
			return responseSummary;
		}

		if (work.isWorkBundle()) {
			return responseSummary;
		}

		openWork(work);
		if (options.isNotifyUsers()) {
			notifyWorkResources(work.getId(), invitedWorkResources);
		}
		return responseSummary;
	}

	@Override
	public void openBundle(long workId, Collection<Long> invitedWorkResources) {
		openWork(workId);
		Set<WorkResource> workResources = Sets.newHashSet(workResourceService.findWorkResources(invitedWorkResources, workId));
		notifyWorkResources(workId, workResources);
	}

	@Override
	public void openWork(long workId) {
		Work work = workService.findWork(workId);
		openWork(work);
	}

	@Override
	public WorkAuthorizationResponse openWork(String workNumber) {
		Work work = workService.findWorkByWorkNumber(workNumber);
		if (work.isWorkBundle()) {
			return WorkAuthorizationResponse.UNKNOWN;
		}

		WorkAuthorizationResponse authorizationResponse = accountRegisterAuthorizationService.authorizeWork(work.getId());
		if (authorizationResponse.fail()) {
			return authorizationResponse;
		}

		openWork(work);

		return authorizationResponse.SUCCEEDED;
	}


	@Override
	public void openWork(Work work) {
		if (!work.isOpenable()) {
			eventRouter.sendEvent(eventFactory.buildWorkResourceCache(work.getId()));
			return;
		}

		if (work.isBundleOrInBundle()) {
			work.getManageMyWorkMarket().setShowInFeed(false);
		}

		workStatusService.transitionToSend(work, workActionRequestFactory.create(work, WorkAuditType.OPEN_WORK));
		eventRouter.sendEvent(eventFactory.buildWorkResourceCache(work.getId()));
		eventRouter.sendEvent(
			new WorkUpdateSearchIndexEvent(work.getId())
				.setScheduledDate(DateUtilities.addTime(Calendar.getInstance(), 1, Constants.SECOND)));
		workService.saveOrUpdateWork(work);
	}

	private void notifyWorkResources(long workId, Set<WorkResource> invitedWorkResources) {
		if (CollectionUtils.isEmpty(invitedWorkResources)) {
			return;
		}

		summaryService.saveWorkResourceHistorySummary(invitedWorkResources);
		List<Long> usersToNotify = Lists.newArrayListWithExpectedSize(invitedWorkResources.size());
		for (WorkResource successfulUser : invitedWorkResources) {
			usersToNotify.add(successfulUser.getUser().getId());
		}
		eventRouter.sendEvent(eventFactory.buildWorkResourceInvitation(workId, usersToNotify, false));
	}

	private void findEligibleWorkResourcesByUserLaneType(Work work, WorkRoutingResponseSummary responseSummary, Set<Long> potentialWorkResourcesUserIds, List<WorkResource> resourcesAlreadyOnWork, Boolean isDispatched) {

		Set<Long> selectedUserIds = Sets.newLinkedHashSet(potentialWorkResourcesUserIds);

		//Filter users who are already a resource of this assignment
		filterUsersAlreadyInvitedToWork(selectedUserIds, responseSummary, resourcesAlreadyOnWork);
		if (isEmpty(selectedUserIds)) {
			return;
		}

		Map<LaneType, Set<User>> companyUserLaneAssociationMap = buildCompanyUserLaneTypeRelationship(responseSummary, work, selectedUserIds, isDispatched);
		responseSummary.addToCompanyUserLaneAssociationMap(companyUserLaneAssociationMap);

		if (isEmpty(companyUserLaneAssociationMap)) {
			logger.error("All users are bunk for this request. workNumber: " + work.getWorkNumber() + " user numbers to add" + potentialWorkResourcesUserIds);
			return;
		}

		WorkRoutingWorkResources<Long> combinedWorkResources =
			new WorkRoutingWorkResources<>(potentialWorkResourcesUserIds, resourcesAlreadyOnWork);
		if (combinedWorkResources.exceedsMaxResourceLimit()) {
			addUsersToResponseMap(responseSummary, createResourcesToAdd(companyUserLaneAssociationMap, false), WorkAuthorizationResponse.MAX_RESOURCES_EXCEEDED);
		}
	}

	private Set<User> authorizeWorkResources(Work work, WorkRoutingResponseSummary responseSummary) {
		boolean addOnlyLane1 = false;
		Assert.notNull(responseSummary.getCompanyUserLaneAssociationMap());

		Set<User> contractors = responseSummary.getCompanyUserLaneAssociationMap().get(LaneType.LANE_3);
		//We just need a user to authorize against.
		User firstContractor = CollectionUtilities.first(contractors);
		if (firstContractor == null) {
			addOnlyLane1 = true;
		} else {
			WorkAuthorizationResponse responseType = accountRegisterAuthorizationService.authorizeWork(work);
			if (responseType.fail()) {
				addOnlyLane1 = true;
				addUsersToResponseMap(responseSummary, contractors, responseType);
			}
		}

		return createResourcesToAdd(responseSummary.getCompanyUserLaneAssociationMap(), addOnlyLane1);
	}

	private Set<WorkResource> saveWorkResources(
		Work work,
		WorkRoutingResponseSummary responseSummary,
		Set<Long> potentialWorkResourcesUserIds,
		WorkResourceAddOptions options,
		Set<User> resourcesToAdd,
		List<WorkResource> resourcesAlreadyOnWork,
		Long dispatcherId,
		boolean assignToFirstToAccept
	) {
		Assert.notNull(work);
		Assert.notNull(options);
		Assert.notNull(resourcesAlreadyOnWork);
		if (responseSummary == null) {
			responseSummary = new WorkRoutingResponseSummary();
		}

		Set<WorkResource> invitedWorkResources = Sets.newHashSetWithExpectedSize(potentialWorkResourcesUserIds.size());
		if (isNotEmpty(potentialWorkResourcesUserIds) && isEmpty(resourcesToAdd)) {
			return invitedWorkResources;
		}

		// Cap the # of resources per assignment but add those who can still make it
		if (options.isEnforceMaxResource()) {
			WorkRoutingWorkResources<User> combinedWorkResources =
				new WorkRoutingWorkResources<>(resourcesToAdd, resourcesAlreadyOnWork);
			if (combinedWorkResources.exceedsMaxResourceLimit()) {
				List<User> resourcesToRemove = combinedWorkResources.getExcess();
				resourcesToAdd.removeAll(resourcesToRemove);
				addUsersToResponseMap(responseSummary, resourcesToRemove, WorkAuthorizationResponse.MAX_RESOURCES_EXCEEDED);
			}
		}

		for (User resourceToAdd : resourcesToAdd) {
			WorkResource workResource = new WorkResource(work, resourceToAdd);
			workResource.setDispatcherId(dispatcherId);
			workResource.setAssignToFirstToAccept(assignToFirstToAccept);
			invitedWorkResources.add(workResource);
			responseSummary.addToWorkAuthorizationResponse(WorkAuthorizationResponse.SUCCEEDED, resourceToAdd.getUserNumber());
		}

		doorman.welcome(
			new UserGuest(work.getBuyer()),
			new AvoidScheduleConflictsRoutingRope(work, responseSummary, invitedWorkResources, workResourceDAO, workService)
		);

		workResourceService.saveAll(invitedWorkResources);
		return invitedWorkResources;
	}

	@SuppressWarnings("unchecked")
	private Map<LaneType, Set<User>> buildCompanyUserLaneTypeRelationship(WorkRoutingResponseSummary response, Work work, Set<Long> userIds, Boolean isDispatched) {
		Set<LaneAssociation> contractorsForCompany = laneService.findAllAssociationsWhereUserIdIn(work.getCompany().getId(), userIds);
		Map<LaneType, Set<User>> companyUserLaneAssociationMap = filterUsersByLaneAssociation(contractorsForCompany, response, work);

		List<Long> usersInCompanyWorkerPool = convert(contractorsForCompany, new PropertyExtractor("user.id"));
		Set<Long> potentialLanes1And3Users = Sets.newLinkedHashSet(subtract(userIds, usersInCompanyWorkerPool));

		if (isNotEmpty(potentialLanes1And3Users)) {
			Set<User> eligibleLane3Users = getEligibleLane3Users(response, potentialLanes1And3Users, work.getCompany());
			for (User user : eligibleLane3Users) {
				WorkAuthorizationResponse resourceResponseType = workRoutingValidator.validateUser(user, work, LaneType.LANE_3);
				if (resourceResponseType.success()) {
					companyUserLaneAssociationMap.get(LaneType.LANE_3).add(user);
					potentialLanes1And3Users.remove(user.getId());
				} else {
					response.addToWorkAuthorizationResponse(resourceResponseType, user.getUserNumber());
				}
			}
		}
		if (isDispatched && isNotEmpty(potentialLanes1And3Users)) {
			Set<User> eligibleLane1Users = getEligibleLane1Users(potentialLanes1And3Users);
			for (User user : eligibleLane1Users) {
				companyUserLaneAssociationMap.get(LaneType.LANE_1).add(user);
			}
		}
		return companyUserLaneAssociationMap;
	}

	private void filterUsersAlreadyInvitedToWork(Set<Long> selectedUserIds, WorkRoutingResponseSummary response, List<WorkResource> resourcesAlreadyOnWork) {
		if (isEmpty(resourcesAlreadyOnWork)) {
			return;
		}

		for (WorkResource resource : resourcesAlreadyOnWork) {
			User user = resource.getUser();
			if (selectedUserIds.remove(user.getId())) {
				response.addToWorkAuthorizationResponse(WorkAuthorizationResponse.ALREADY_ADDED, user.getUserNumber());
			}
		}
	}

	private Map<LaneType, Set<User>> filterUsersByLaneAssociation(Set<LaneAssociation> contractorsForCompany, WorkRoutingResponseSummary response, Work work) {
		final Map<LaneType, Set<User>> companyUserLaneAssociationMap = Maps.newHashMap();
		final Set<User> lane1 = Sets.newLinkedHashSet();
		final Set<User> lane234 = Sets.newLinkedHashSet();

		for (LaneAssociation laneAssociation : contractorsForCompany) {
			WorkAuthorizationResponse resourceResponseType = workRoutingValidator.validateUser(laneAssociation.getUser(), work, laneAssociation.getLaneType());
			if (resourceResponseType.success()) {
				if (LaneType.LANE_1.equals(laneAssociation.getLaneType())) {
					lane1.add(laneAssociation.getUser());
				} else {
					lane234.add(laneAssociation.getUser());
				}
			} else {
				response.addToWorkAuthorizationResponse(resourceResponseType, laneAssociation.getUser().getUserNumber());
			}
		}
		companyUserLaneAssociationMap.put(LaneType.LANE_1, lane1);
		companyUserLaneAssociationMap.put(LaneType.LANE_3, lane234);
		return companyUserLaneAssociationMap;
	}

	@SuppressWarnings("unchecked")
	private void addUsersToResponseMap(WorkRoutingResponseSummary response, Collection<User> resources, WorkAuthorizationResponse responseType) {
		List<String> userNumbers = convert(resources, new PropertyExtractor("userNumber"));
		response.addToWorkAuthorizationResponse(responseType, userNumbers);
	}

	private Set<User> getEligibleLane1Users(Collection<Long> potentialLane1Users) {
		Collection<User> users = userService.findAllUsersByIds(potentialLane1Users);
		final Set<User> result = Sets.newHashSet();
		for (final User user: users) {
			if (authenticationService.isLane1Active(user)) {
				result.add(user);
			}
		}
		return result;
	}

	@Override
	public Set<User> getEligibleLane3Users(WorkRoutingResponseSummary response, Collection<Long> potentialLane4Users, Company company) {
		if (isEmpty(potentialLane4Users)) {
			return Collections.emptySet();
		}

		if (response == null) {
			response = new WorkRoutingResponseSummary();
		}

		Set<User> lane4ActiveUsers = Sets.newHashSetWithExpectedSize(potentialLane4Users.size());
		Collection<User> users = userService.findAllUsersByIds(potentialLane4Users);

		boolean instantWorkerPool = company.getManageMyWorkMarket().isInstantWorkerPoolEnabled();
		if (!instantWorkerPool) {
			response.addToWorkAuthorizationResponse(WorkAuthorizationResponse.DISABLED_WORKER_POOL, convert(users, new PropertyExtractor("userNumber")));
			return Collections.emptySet();
		}

		for (User user : users) {
			//Being lane 4 active, means you can become a lane 3
			if (authenticationService.isLane4Active(user)) {
				lane4ActiveUsers.add(user);
			} else {
				response.addToWorkAuthorizationResponse(WorkAuthorizationResponse.INVALID_USER, user.getUserNumber());
			}
		}
		return lane4ActiveUsers;
	}

	@SuppressWarnings("unchecked")
	private Set<User> createResourcesToAdd(Map<LaneType, Set<User>> companyUserLaneAssociationMap, boolean addOnlyLane1) {
		Set<User> resourcesToAdd = Sets.newHashSet();
		resourcesToAdd.addAll((Set<User>) MapUtils.getObject(companyUserLaneAssociationMap, LaneType.LANE_1, Collections.EMPTY_SET));

		if (!addOnlyLane1) {
			resourcesToAdd.addAll((Set<User>) MapUtils.getObject(companyUserLaneAssociationMap, LaneType.LANE_3, Collections.EMPTY_SET));
		}
		return resourcesToAdd;
	}

	@Override
	public WorkRoutingResponseSummary addToWorkResources(long workId, Set<Long> userIds, WorkResourceAddOptions options, boolean assignToFirstToAccept) throws WorkNotFoundException {
		Work work = workService.findWork(workId);

		if (work == null) {
			throw new WorkNotFoundException("There was a problem finding work by work id " + workId);
		}

		return addToWorkResources(work, userIds, options, assignToFirstToAccept);
	}

	@Override
	public WorkRoutingResponseSummary addToWorkResources(Long workId, Set<Long> userIds, boolean assignToFirstToAccept) throws WorkNotFoundException {
		Work work = workService.findWork(workId);

		if (work == null) {
			throw new WorkNotFoundException("There was a problem finding work by work id " + workId);
		}

		return addToWorkResources(work, userIds, new WorkResourceAddOptions(true), assignToFirstToAccept);
	}

	@Override
	public WorkRoutingResponseSummary addToWorkResourcesAsDispatcher(String workNumber, Set<String> userNumbers, Long dispatcherId, boolean assignToFirstToAccept) throws WorkNotFoundException {
		Work work = workService.findWorkByWorkNumber(workNumber);

		if (work == null) {
			throw new WorkNotFoundException("There was a problem finding work by work number " + workNumber);
		}

		return addToWorkResources(work, userService.findAllUserIdsByUserNumbers(userNumbers), new WorkResourceAddOptions(true), userNumbers, dispatcherId, assignToFirstToAccept);
	}

	@Override
	public WorkRoutingResponseSummary addToWorkResources(String workNumber, Set<String> userNumbers, boolean assignToFirstToAccept) throws WorkNotFoundException {
		Work work = workService.findWorkByWorkNumber(workNumber);

		if (work == null) {
			throw new WorkNotFoundException("There was a problem finding work by work number " + workNumber);
		}

		return addToWorkResources(work, userService.findAllUserIdsByUserNumbers(userNumbers), new WorkResourceAddOptions(true), userNumbers, null, assignToFirstToAccept);
	}

	@Override
	public WorkRoutingResponseSummary addToWorkResources(String workNumber, Set<String> userNumbers) throws WorkNotFoundException {
		Work work = workService.findWorkByWorkNumber(workNumber);

		if (work == null) {
			throw new WorkNotFoundException("There was a problem finding work by work number " + workNumber);
		}

		return addToWorkResources(work, userService.findAllUserIdsByUserNumbers(userNumbers), new WorkResourceAddOptions(true), userNumbers, null, false);
	}

	@Override
	public WorkRoutingResponseSummary addToWorkResources(long workId, long userId) throws WorkNotFoundException {
		Work work = workService.findWork(workId);
		if (work == null) {
			throw new WorkNotFoundException("There was a problem finding work by work id " + workId);
		}
		return addToWorkResources(work, Sets.newHashSet(userId), new WorkResourceAddOptions(true), false);
	}

	@Override
	public WorkRoutingResponseSummary addToWorkResources(long workId, long userId, boolean assignToFirstToAccept) throws WorkNotFoundException {
		Work work = workService.findWork(workId);
		if (work == null) {
			throw new WorkNotFoundException("There was a problem finding work by work id " + workId);
		}
		return addToWorkResources(work, Sets.newHashSet(userId), new WorkResourceAddOptions(true), assignToFirstToAccept);
	}

	// TODO [Lu]: consider remove this method as it is a convenient method to handle search results. We should just extract a list of user ids from search results and call the other method.
	@SuppressWarnings("unchecked")
	@Override
	public WorkRoutingResponseSummary addToWorkResources(
		long workId,
		List<PeopleSearchResult> selectedUsers,
		WorkResourceAddOptions options,
		boolean assignToFirstAccept
	) throws WorkNotFoundException {
		Work work = workService.findWork(workId);
		if (work == null) {
			throw new WorkNotFoundException("Invalid assignment");
		}
		if (work.isBundleOrInBundle()) {
			work.getManageMyWorkMarket().setShowInFeed(false);
		}

		WorkRoutingResponseSummary responseSummary = new WorkRoutingResponseSummary();
		if (isEmpty(selectedUsers)) {
			return responseSummary;
		}

		final Set<WorkAuthorizationResponse> workAuthorizationResponses = workRoutingValidator.validateWorkForRouting(work);
		if (!workAuthorizationResponses.contains(WorkAuthorizationResponse.SUCCEEDED)) {
			WorkAuthorizationResponse authorizationResponse = CollectionUtilities.first(workAuthorizationResponses);
			addUsersToResponseMap(responseSummary, Collections.EMPTY_LIST, authorizationResponse);
			return responseSummary;
		}

		final Set<WorkResource> invitedWorkResources = Sets.newHashSetWithExpectedSize(selectedUsers.size());
		List<PeopleSearchResult> potentialWorkResources = Lists.newArrayList(selectedUsers);
		List<Long> resourcesAlreadyOnWork = workService.findWorkerIdsForWork(work.getId());

		Set<PeopleSearchResult> contractors = Sets.newLinkedHashSet();

		if (resourcesAlreadyOnWork.size() >= Constants.MAX_RESOURCES_PER_ASSIGNMENT) {
			responseSummary.addToWorkAuthorizationResponse(WorkAuthorizationResponse.MAX_RESOURCES_EXCEEDED, convert(potentialWorkResources, new PropertyExtractor("userNumber")));
			return responseSummary;
		}

		for (PeopleSearchResult user : selectedUsers) {
			WorkAuthorizationResponse response = workRoutingValidator.validateSearchResult(user, resourcesAlreadyOnWork, work);
			if (response.fail()) {
				responseSummary.addToWorkAuthorizationResponse(response, user.getUserNumber());
				potentialWorkResources.remove(user);
			} else if (!LaneType.LANE_1.equals(user.getLane())) {
				contractors.add(user);
			}
		}

		if (isEmpty(potentialWorkResources)) {
			return responseSummary;
		}

		// TODO: the naming of "authorizeContractors" is confusing. It really has nothing to do with authorizing users.
		// TODO: it authorizes a work, and if failed, append a list of user numbers to responseSummary. Should consider remove.
		WorkAuthorizationResponse contractorsAuthorization = accountRegisterAuthorizationService.authorizeContractors(contractors, work, responseSummary);
		if (contractorsAuthorization.fail()) {
			potentialWorkResources.removeAll(contractors);
		}

		if (isEmpty(potentialWorkResources)) {
			return responseSummary;
		}

		// Cap the # of resources per assignment but add those who can still make it
		if (options.isEnforceMaxResource()) {
			WorkRoutingWorkResources<PeopleSearchResult> combinedWorkResources =
				new WorkRoutingWorkResources<>(potentialWorkResources, resourcesAlreadyOnWork);
			if (combinedWorkResources.exceedsMaxResourceLimit()) {
				List<PeopleSearchResult> resourcesToRemove = combinedWorkResources.getExcess();
				potentialWorkResources.removeAll(resourcesToRemove);
				responseSummary.addToWorkAuthorizationResponse(WorkAuthorizationResponse.MAX_RESOURCES_EXCEEDED, convert(resourcesToRemove, new PropertyExtractor("userNumber")));
			}
		}

		for (PeopleSearchResult resourceToAdd : potentialWorkResources) {
			User user = userService.getUser(resourceToAdd.getUserId());
			if (user != null) {
				WorkResource workResource = new WorkResource(work, user);
				workResource.setScore((int) resourceToAdd.getScore());
				workResource.setAssignToFirstToAccept(assignToFirstAccept);
				invitedWorkResources.add(workResource);
				responseSummary.addToWorkAuthorizationResponse(WorkAuthorizationResponse.SUCCEEDED, user.getUserNumber());
			}
		}

		doorman.welcome(
			new UserGuest(work.getBuyer()),
			new AvoidScheduleConflictsRoutingRope(work, responseSummary, invitedWorkResources, workResourceDAO, workService)
		);

		workResourceService.saveAll(invitedWorkResources);

		if (isEmpty(invitedWorkResources)) {
			return responseSummary;
		}

		if (work.isWorkBundle()) {
			return responseSummary;
		}

		openWork(work);
		if (options.isNotifyUsers()) {
			notifyWorkResources(work.getId(), invitedWorkResources);
		}
		return responseSummary;
	}
}
