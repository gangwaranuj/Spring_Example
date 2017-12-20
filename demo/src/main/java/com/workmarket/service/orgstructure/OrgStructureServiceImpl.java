package com.workmarket.service.orgstructure;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.api.v2.model.OrgUnitDTO;
import com.workmarket.business.OrgStructClient;
import com.workmarket.business.gen.Messages.AssignUsersFromBulkReq;
import com.workmarket.business.gen.Messages.FindUserMembershipReq;
import com.workmarket.business.gen.Messages.FindUserOrgUnitsReq;
import com.workmarket.business.gen.Messages.GetOrgChartUuidForCompanyUuidReq;
import com.workmarket.business.gen.Messages.GetOrgChartUuidForCompanyUuidResponse;
import com.workmarket.business.gen.Messages.GetOrgModePathsReq;
import com.workmarket.business.gen.Messages.GetOrgUnitMembersReq;
import com.workmarket.business.gen.Messages.GetSubtreePathsReq;
import com.workmarket.business.gen.Messages.MembersResponse;
import com.workmarket.business.gen.Messages.Membership;
import com.workmarket.business.gen.Messages.MembershipResponse;
import com.workmarket.business.gen.Messages.OrgChartResponse;
import com.workmarket.business.gen.Messages.OrgUnit;
import com.workmarket.business.gen.Messages.OrgUnitMembership;
import com.workmarket.business.gen.Messages.OrgUnitPath;
import com.workmarket.business.gen.Messages.OrgUnitPathsResponse;
import com.workmarket.business.gen.Messages.OrgUnitsResponse;
import com.workmarket.business.gen.Messages.UpdateOrgUnitMembershipReq;
import com.workmarket.business.gen.Messages.Status;
import com.workmarket.business.gen.Messages.UserIdentity;
import com.workmarket.business.gen.Messages.PublishOrgChartReq;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.setting.DimensionValuePair;
import com.workmarket.setting.SettingClient;
import com.workmarket.setting.gen.Common.Dimension;
import com.workmarket.setting.gen.Response;
import com.workmarket.setting.gen.Response.DimensionValue;
import com.workmarket.setting.vo.SingleValuesAndStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import rx.functions.Action1;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Collections2.filter;

@Service
public class OrgStructureServiceImpl implements OrgStructureService {
	private static final Log logger = LogFactory.getLog(OrgStructureServiceImpl.class);

	private static final String ORG_MODE_SETTING_KEY = "orgStructure.module.orgMode";
	private final Response.Status FAILED_STATUS = Response.Status.newBuilder().setSuccess(false).build();
	private final SingleValuesAndStatus DEFAULT_SINGLE_VALUES_AND_STATUS_RESPONSE =
			new SingleValuesAndStatus(Maps.<String, DimensionValue>newHashMap(), FAILED_STATUS);

	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private CompanyService companyService;
	@Autowired private UserService userService;
	@Autowired @Qualifier("OrgStructClient") OrgStructClient orgStructClient;
	@Autowired @Qualifier("SettingClient") SettingClient settingClient;

	@Override
	public String getOrgModeSetting(final long userId) {
		final SingleValuesAndStatus singleValuesAndStatus = getOrgModeSettingFromSettingService(userId);

		if (!singleValuesAndStatus.getStatus().getSuccess()
				|| !singleValuesAndStatus.get(ORG_MODE_SETTING_KEY).isPresent()) {
			logger.error("Error retrieving user's org mode setting");
			return getDefaultOrgModeOption(userId);
		}

		final DimensionValue dimensionValue = singleValuesAndStatus.get(ORG_MODE_SETTING_KEY).get();
		final Dimension dimension = dimensionValue.getDimension();

		final List<OrgUnitPath> orgModeOptions = getOrgModeOptions(userId);
		final List<String> orgModeOptionUuids = Lists.transform(orgModeOptions, new Function<OrgUnitPath, String>() {
			@Override
			public String apply(@Nullable final OrgUnitPath orgUnitPath) {
				return orgUnitPath == null ? null : orgUnitPath.getUuid();
			}
		});

		if (Dimension.USER.equals(dimension) && orgModeOptionUuids.contains(dimensionValue.getValue())) {
			return dimensionValue.getValue();
		} else if (Dimension.USER.equals(dimension) || Dimension.DEFAULT.equals(dimension)) {
			if (CollectionUtils.isNotEmpty(orgModeOptions)) {
				final String selectedOrgUnit = orgModeOptions.get(0).getUuid();
				setOrgModeSettingForUser(userId, selectedOrgUnit);
				return selectedOrgUnit;
			}
		} else {
			logger.error("Org Mode setting set for dimension other than USER and DEFAULT.");
		}

		return getDefaultOrgModeOption(userId);
	}

	@Override
	public BaseStatus setOrgModeSettingForUser(final long userId, final String selectedOrgUnit) {
		final BaseStatus failedStatus = BaseStatus.FAILURE;

		if (StringUtils.isBlank(selectedOrgUnit)) {
			logger.error("selectedOrgUnit is blank!");
			return failedStatus;
		}

		final List<OrgUnitPath> possibleOrgModeOptions = getOrgModeOptions(userId);
		final List<String> possibleOrgModeOptionUuids = getOrgModeOptionsUuids(possibleOrgModeOptions);
		if (!possibleOrgModeOptionUuids.contains(selectedOrgUnit)) {
			logger.error("selectedOrgUnit is not valid!: " + selectedOrgUnit + " for userId: " + userId);
			return failedStatus;
		}

		final SingleValuesAndStatus singleValuesAndStatus = getOrgModeSettingFromSettingService(userId);
		if (!singleValuesAndStatus.getStatus().getSuccess()
				|| !singleValuesAndStatus.get(ORG_MODE_SETTING_KEY).isPresent()) {
			logger.error("Error retrieving user's org mode setting");
			return failedStatus;
		}

		final DimensionValue dimensionValue = singleValuesAndStatus.get(ORG_MODE_SETTING_KEY).get();
		final Dimension dimension = dimensionValue.getDimension();
		final DimensionValuePair dimensionValuePair = createUserDimensionValuePairForRequest(userId);
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		if (Dimension.USER.equals(dimension)) {
			settingClient.modifyOverride(ORG_MODE_SETTING_KEY, selectedOrgUnit, dimensionValuePair, null, requestContext)
					.toBlocking()
					.singleOrDefault(FAILED_STATUS);
		} else if (Dimension.DEFAULT.equals(dimension)) {
			settingClient.createOverride(ORG_MODE_SETTING_KEY, selectedOrgUnit, dimensionValuePair, requestContext)
					.toBlocking()
					.singleOrDefault(FAILED_STATUS);
		} else {
			logger.error("Org Mode setting set for dimension other than USER and DEFAULT.");
			return failedStatus;
		}

		return BaseStatus.SUCCESS;
	}

	@Override
	public List<OrgUnitPath> getOrgModeOptions(final long userId) {
		final UserIdentity userIdentity = buildUserIdentity();
		final String userUuid = userService.findUserUuidById(userId);
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		final GetOrgModePathsReq getOrgModePathsReq = GetOrgModePathsReq.newBuilder()
				.setUserIdentity(userIdentity)
				.setDraft(false)
				.setUserUuid(userUuid)
				.build();

		final OrgUnitPathsResponse orgUnitPathsResponse = orgStructClient.getOrgModePaths(getOrgModePathsReq, requestContext)
				.toBlocking()
				.singleOrDefault(OrgUnitPathsResponse.getDefaultInstance());

		if (orgUnitPathsResponse.hasStatus() && !orgUnitPathsResponse.getStatus().getSuccess()) {
			return Lists.newArrayList();
		}

		final List<OrgUnitPath> orgUnitPaths = Lists.newArrayList(orgUnitPathsResponse.getPathsList());

		Collections.sort(orgUnitPaths, new Comparator<OrgUnitPath>() {
			@Override
			public int compare(final OrgUnitPath o1, final OrgUnitPath o2) {
				final String orgOneNameAndUuid = o1.getName().concat(o1.getUuid());
				final String orgTwoNameAndUuid = o2.getName().concat(o2.getUuid());

				return orgOneNameAndUuid.compareToIgnoreCase(orgTwoNameAndUuid);
			}
		});

		return orgUnitPaths;
	}

	@Override
	public List<OrgUnit> findUserOrgChildren(final long userId, final long companyId) {
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		final String userUuid = userService.findUserUuidById(userId);
		final List<String> companyUuids = companyService.getCompanyUuidsForCompanyIds(Lists.newArrayList(companyId));

		if (CollectionUtils.isNotEmpty(companyUuids) && userUuid != null) {
			final String companyUuid = companyUuids.get(0);
			final MembershipResponse failedDefaultResponse = MembershipResponse.newBuilder().build();
			final UserIdentity userIdentity = UserIdentity.newBuilder()
					.setUserUuid(userUuid)
					.setCompanyUuid(companyUuid)
					.build();
			final FindUserOrgUnitsReq request = FindUserOrgUnitsReq.newBuilder()
					.setUserIdentity(userIdentity)
					.setUserUuid(userUuid)
					.build();
			final MembershipResponse membershipResponse = orgStructClient.findUserOrgChildren(request, requestContext)
					.toBlocking()
					.singleOrDefault(failedDefaultResponse);

			final Map<String, List<OrgUnit>> membershipsMap = buildMembershipsMap(membershipResponse);
			return membershipsMap.containsKey(userUuid)
					? membershipsMap.get(userUuid)
					: Lists.<OrgUnit>newArrayList();
		}

		return Lists.newArrayList();
	}

	@Override
	public Map<String, List<OrgUnit>> findDirectMembershipsForUsersInCompany(final List<Long> userIds, final String companyUuid) {
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		if (CollectionUtils.isNotEmpty(userIds)) {
			final MembershipResponse failedDefaultResponse = MembershipResponse.getDefaultInstance();
			final Set<String> userUuids = userService.findAllUserUuidsByIds(userIds);
			final FindUserMembershipReq request = FindUserMembershipReq.newBuilder()
					.setUserIdentity(buildUserIdentityWithCompanyUuid(companyUuid))
					.setDraft(false)
					.addAllUserUuid(userUuids)
					.build();
			final MembershipResponse membershipResponse = orgStructClient.findUserMembership(request, requestContext)
					.toBlocking()
					.singleOrDefault(failedDefaultResponse);

			return buildMembershipsMap(membershipResponse);
		}

		return Maps.newHashMap();
	}

	@VisibleForTesting
	Map<String, List<OrgUnit>> buildMembershipsMap(final MembershipResponse membershipResponse) {
		final Map<String, List<OrgUnit>> membershipMap = Maps.newHashMap();
		final Status responseStatus = membershipResponse.getStatus();

		if (!responseStatus.getSuccess()) {
			final List<String> messages = responseStatus.getMessageList();
			if (CollectionUtils.isNotEmpty(messages)) {
				logger.info("Request returned with failed status: " + responseStatus.getMessage(0));
			} else {
				logger.info("Request returned with failed status.");
			}
			return membershipMap;
		}

		final List<Membership> memberships = membershipResponse.getMembershipList();

		if (CollectionUtils.isEmpty(memberships)) {
			logger.info("No memberships found.");
			return membershipMap;
		}

		for (final Membership membership : memberships) {
			final String userUuid = membership.getUserUuid();
			final List<OrgUnit> orgUnits = membership.getOrgUnitList();
			if (!membershipMap.containsKey(userUuid)) {
				membershipMap.put(userUuid, Lists.<OrgUnit>newArrayList());
			}
			membershipMap.get(userUuid).addAll(orgUnits);
		}
		return membershipMap;
	}

	@Override
	public boolean assignUsersFromBulk(
		final List<String> orgUnitPaths,
		final String userUuid,
		final String companyUuid,
		final String userEmail,
		final String orgChartUuid) {

		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		final List<OrgUnitMembership> orgUnitMemberships = new ArrayList<>();
		for (final String orgUnitPath : orgUnitPaths) {
			final OrgUnitMembership orgUnitMembership = OrgUnitMembership.newBuilder()
				.setOrgUnitPath(orgUnitPath)
				.setUserUuid(userUuid)
				.build();
			orgUnitMemberships.add(orgUnitMembership);
		}
		final AssignUsersFromBulkReq assignUsersFromBulkReq = AssignUsersFromBulkReq.newBuilder()
			.setOrgChartUuid(orgChartUuid)
			.addAllMembership(orgUnitMemberships)
			.setUserIdentity(UserIdentity.newBuilder()
				.setCompanyUuid(companyUuid)
				.setUserUuid(userUuid)
				.build())
			.build();
		final boolean[] status = new boolean[1];
		orgStructClient.assignUsersFromBulk(assignUsersFromBulkReq, requestContext)
			.doOnError(new Action1<Throwable>() {
				@Override
				public void call(final Throwable throwable) {
					throw new RuntimeException(
						String.format("Failed to bulk upload userUuid:%s, userEmail:%s, orgChartUuid:%s",
							userUuid,
							userEmail,
							orgChartUuid),
						throwable);
				}
			})
			.subscribe(new Action1<OrgChartResponse>() {
				@Override
				public void call(final OrgChartResponse orgChartResponse) {
					status[0] = orgChartResponse.getStatus().getSuccess();
					if (!status[0]) {
						logger.error(
							String.format("Assign users from bulk failed for userUuid:%s, userEmail:%s, orgChartUuid:%s",
							userUuid,
							userEmail,
							orgChartUuid));
					}
				}
			});
		return status[0];
	}

	@Override
	public String getOrgChartUuidFromCompanyUuid(final String companyUuid) {
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();


		final GetOrgChartUuidForCompanyUuidReq request = GetOrgChartUuidForCompanyUuidReq.newBuilder()
			.setCompanyUuid(companyUuid)
			.build();

		final StringBuilder stringBuilder = new StringBuilder();
		orgStructClient.getOrgChartUuid(request, requestContext)
			.doOnError(new Action1<Throwable>() {
				@Override
				public void call(final Throwable throwable) {
					throw new RuntimeException(
						String.format("Failed to get orgChartUuid for companyUuid:%s", companyUuid),
						throwable);
				}
			})
			.subscribe(new Action1<GetOrgChartUuidForCompanyUuidResponse>() {
				@Override
				public void call(final GetOrgChartUuidForCompanyUuidResponse getOrgChartUuidForCompanyUuidResponse) {
					stringBuilder.append(getOrgChartUuidForCompanyUuidResponse.getOrgChartUuid());
				}
			});
		return stringBuilder.toString();
	}

	@Override
	public List<UserDTO> getOrgUnitMembers(final List<String> orgUnitUuids) {
		if (CollectionUtils.isEmpty(orgUnitUuids)) {
			return Lists.newArrayList();
		}

		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		final GetOrgUnitMembersReq request = GetOrgUnitMembersReq.newBuilder()
			.setUserIdentity(buildUserIdentity())
			.setDraft(false)
			.addAllOrgUnitUuid(orgUnitUuids)
			.build();

		final MembersResponse defaultResponse = MembersResponse.getDefaultInstance();
		final MembersResponse response = orgStructClient.getOrgUnitMembers(request, requestContext)
			.toBlocking()
			.singleOrDefault(defaultResponse);

		return buildUserList(response);
	}



	public List<String> getUserOrgUnitUuids(final String userUuid) {
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();

		// Get user's current list of memberships
		final FindUserMembershipReq request = FindUserMembershipReq.newBuilder()
			.setUserIdentity(buildUserIdentity())
			.addUserUuid(userUuid)
			.setDraft(false)
			.build();

		final MembershipResponse membershipResponse = orgStructClient.findUserMembership(request, requestContext)
			.toBlocking()
			.singleOrDefault(MembershipResponse.getDefaultInstance());

		List<String> currentOrgUnitUuids = Lists.newArrayList();

		if (!membershipResponse.getStatus().getSuccess()) {
			final List<String> messages = membershipResponse.getStatus().getMessageList();
			if (CollectionUtils.isNotEmpty(messages)) {
				logger.info(String.format("Request to find org unit memberships for user '%s' resulted in failed status: %s", userUuid, messages.get(0)));
			} else {
				logger.info(String.format("Request to find org unit memberships for user '%s' resulted in failed status", userUuid));
			}
			return currentOrgUnitUuids;
		}

		if (CollectionUtils.isEmpty(membershipResponse.getMembershipList())) {
			return currentOrgUnitUuids;
		}

		final Membership userMembership = membershipResponse.getMembership(0);

		// Extract just the org unit UUIDs as a list
		if (userMembership != null) {
			currentOrgUnitUuids = Lists.transform(userMembership.getOrgUnitList(), new Function<OrgUnit, String>() {
				@Nullable
				@Override
				public String apply(@Nullable OrgUnit orgUnit) {
					return orgUnit.getUuid();
				}
			});
		}

		return currentOrgUnitUuids;
	}
	
	public List<String> setUserMemberships(final List<String> orgUnitUuids, final String userUuid) {
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();

		final User user = userService.findUserByUuid(userUuid);
		Assert.notNull(user);
		
		final List<String> currentOrgUnitUuids = getUserOrgUnitUuids(userUuid);
		List<String> orgUnitsToRemove = Lists.newArrayList();

		if (!CollectionUtils.isEmpty(currentOrgUnitUuids)) {
			// Filter to create list of org units to remove
			orgUnitsToRemove = currentOrgUnitUuids;
			orgUnitsToRemove.removeAll(orgUnitUuids);

			// Remove one by one
			for (String orgUnitUuid : orgUnitsToRemove) {
				OrgUnitsResponse removeResponse = removeUserFromOrgUnit(requestContext, userUuid, orgUnitUuid);
				if(!removeResponse.getStatus().getSuccess()) {
					final List<String> messages = removeResponse.getStatus().getMessageList();
					if (CollectionUtils.isNotEmpty(messages)) {
						logger.info(String.format("Request to remove user '%s' from org unit '%s' resulted in failed status: %s", userUuid, orgUnitUuid, messages.get(0)));
					} else {
						logger.info(String.format("Request to remove user '%s' from org unit '%s' resulted in failed status.", userUuid, orgUnitUuid));
					}
				}
			}
		}

		// Filter to create list of org units to add
		List<String> orgUnitsToAdd = Lists.newArrayList(orgUnitUuids);
		orgUnitsToAdd.removeAll(currentOrgUnitUuids);

		// Add one by one
		for (String orgUnitUuid : orgUnitsToAdd) {
			OrgUnitsResponse addResponse = addUserToOrgUnit(requestContext, userUuid, orgUnitUuid);
			if (!addResponse.getStatus().getSuccess()) {
				final List<String> messages = addResponse.getStatus().getMessageList();
				if (CollectionUtils.isNotEmpty(messages)) {
					logger.info(String.format("Request to add user '%s' from org unit '%s' resulted in failed status: %s", userUuid, orgUnitUuid, messages.get(0)));
				} else {
					logger.info(String.format("Request to add user '%s' to org unit '%s' resulted in failed status.", userUuid, orgUnitUuid));
				}
			}
		}

		// If we added or removed anything, we gotta publish those changes
		if (CollectionUtils.isNotEmpty(orgUnitsToRemove) || CollectionUtils.isNotEmpty(orgUnitsToAdd)) {
			OrgChartResponse orgChartResponse = publishOrgChart(requestContext, user);
			if (!orgChartResponse.getStatus().getSuccess()) {
				logger.info(String.format("Request to publish org chart for user '%s' resulted in failed status.", user.getUuid()));
			}
		}

		return getUserOrgUnitUuids(userUuid);
	}

	private OrgUnitsResponse addUserToOrgUnit(RequestContext requestContext, String userUuid, String orgUnit) {
		// pull the whole list
		List<String> memberUuids = Lists.transform(getOrgUnitMembers(ImmutableList.of(orgUnit)), new Function<UserDTO, String>() {
			@Override
			public String apply(UserDTO userDTO) {
				return userDTO.getUuid();
			}
		});

		// add the user to the list
		final List<String> updatedMemberList = ImmutableList.<String>builder()
			.addAll(memberUuids)
			.add(userUuid)
			.build();

		// update the membership
		final UpdateOrgUnitMembershipReq updateOrgUnitMembershipReq = UpdateOrgUnitMembershipReq.newBuilder()
			.setUserIdentity(buildUserIdentity())
			.setOrgUnitUuid(orgUnit)
			.addAllUserUuid(updatedMemberList)
			.build();
		
		return orgStructClient.updateOrgUnitMembership(updateOrgUnitMembershipReq, requestContext)
			.toBlocking()
			.singleOrDefault(OrgUnitsResponse.getDefaultInstance());
	}

	private OrgUnitsResponse removeUserFromOrgUnit(RequestContext requestContext, String userUuid, String orgUnitToRemove) {
		// pull the whole list
		final List<String> memberUuids = Lists.transform(getOrgUnitMembers(ImmutableList.of(orgUnitToRemove)), new Function<UserDTO, String>() {
				@Override
				public String apply(UserDTO userDTO) {
					return userDTO.getUuid();
				}
		});

		// remove user from list
		final List<String> updatedMemberUuids = Lists.newArrayList(filter(memberUuids, not(equalTo(userUuid))));

		// update the membership with new list
		final UpdateOrgUnitMembershipReq updateOrgUnitMembershipReq = UpdateOrgUnitMembershipReq.newBuilder()
			.setUserIdentity(buildUserIdentity())
			.setOrgUnitUuid(orgUnitToRemove)
			.addAllUserUuid(updatedMemberUuids)
			.build();

		return orgStructClient.updateOrgUnitMembership(updateOrgUnitMembershipReq, requestContext)
			.toBlocking()
			.singleOrDefault(OrgUnitsResponse.getDefaultInstance());
	}

	private OrgChartResponse publishOrgChart(RequestContext requestContext, User user) {
		final List<Long> companyIds = companyService.findCompanyIdsForUsers(ImmutableList.of(user.getId()));
		Assert.isTrue(companyIds.size() == 1);
		final Company company = companyService.findById(companyIds.get(0));
		
		final String orgChartUuidFromCompanyUuid = getOrgChartUuidFromCompanyUuid(company.getUuid());

		final PublishOrgChartReq publishOrgChartReq = PublishOrgChartReq.newBuilder()
			.setUserIdentity(buildUserIdentity())
			.setOrgChartUuid(orgChartUuidFromCompanyUuid)
			.build();

		return orgStructClient.publishOrgChart(publishOrgChartReq, requestContext)
			.toBlocking()
			.singleOrDefault(OrgChartResponse.getDefaultInstance());
	}


	@Override
	public List<OrgUnitDTO> getSubtreePaths(final long userId, final long companyId, final String orgUnitUuid) {
		final ImmutableList.Builder<OrgUnitDTO> subtreePathsBuilder = ImmutableList.builder();
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		final String userUuid = userService.findUserUuidById(userId);
		final List<String> companyUuids = companyService.getCompanyUuidsForCompanyIds(Lists.newArrayList(companyId));

		if (CollectionUtils.isEmpty(companyUuids)
				|| companyUuids.contains("")
				|| StringUtils.isBlank(userUuid)
				|| StringUtils.isBlank(orgUnitUuid)) {
			return subtreePathsBuilder.build();
		}

		final String companyUuid = companyUuids.get(0);
		final UserIdentity userIdentity = UserIdentity.newBuilder()
				.setUserUuid(userUuid)
				.setCompanyUuid(companyUuid)
				.build();
		final GetSubtreePathsReq request = GetSubtreePathsReq.newBuilder()
				.setUserIdentity(userIdentity)
				.setOrgUnitUuid(orgUnitUuid)
				.setDraft(false)
				.build();

		orgStructClient.getSubtreePaths(request, requestContext)
				.doOnError(new Action1<Throwable>() {
					@Override
					public void call(final Throwable throwable) {
						final String errorMessage = String.format("Failed to get org units rooted at current org unit: %s", orgUnitUuid);
						logger.error(errorMessage);
						throw new RuntimeException(errorMessage, throwable);
					}
				})
				.subscribe(new Action1<OrgUnitPathsResponse>() {
					@Override
					public void call(final OrgUnitPathsResponse orgUnitPathsResponse) {
						if (orgUnitPathsResponse.getStatus().getSuccess()) {
							for(OrgUnitPath path: orgUnitPathsResponse.getPathsList()) {
								final OrgUnitDTO.Builder builder = new OrgUnitDTO.Builder(path);
								subtreePathsBuilder.add(builder.build());
				}}}});

		return subtreePathsBuilder.build();
	}

	@Override
	public List<String> getSubtreePathOrgUnitUuidsForCurrentOrgMode(final long userId, final long companyId) {
		final String rootOrgUnit = getOrgModeSetting(userId);
		final List<OrgUnitDTO> orgUnits = getSubtreePaths(userId, companyId, rootOrgUnit);
		List<String> orgUnitUuids = Lists.newArrayList();

		if (CollectionUtils.isNotEmpty(orgUnits)) {
			orgUnitUuids = Lists.transform(orgUnits, new Function<OrgUnitDTO, String>() {
				public String apply(OrgUnitDTO orgUnit) { return orgUnit.getUuid(); }
			});
		}

		return orgUnitUuids;
	}

	@VisibleForTesting
	protected List<String> getOrgModeOptionsUuids(final List<OrgUnitPath> orgUnitPaths) {
		final List<String> result = Lists.newArrayList();
		if (CollectionUtils.isEmpty(orgUnitPaths)) {
			return result;
		}

		for (OrgUnitPath orgUnitPath : orgUnitPaths) {
			result.add(orgUnitPath.getUuid());
		}

		return result;
	}

	@VisibleForTesting
	protected SingleValuesAndStatus getOrgModeSettingFromSettingService(final long userId) {
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		final DimensionValuePair dimensionValuePair = createUserDimensionValuePairForRequest(userId);

		final SingleValuesAndStatus singleValuesAndStatus =
			settingClient.getSetting(Lists.newArrayList(dimensionValuePair), Lists.newArrayList(ORG_MODE_SETTING_KEY), requestContext)
				.toBlocking()
				.singleOrDefault(DEFAULT_SINGLE_VALUES_AND_STATUS_RESPONSE);

		return singleValuesAndStatus;
	}

	@VisibleForTesting
	protected DimensionValuePair createUserDimensionValuePairForRequest(final long userId) {
		final String userUuid = userService.findUserUuidById(userId);

		return new DimensionValuePair(userUuid, Dimension.USER);
	}

	@VisibleForTesting
	protected String getOrgModeSettingKey() {
		return ORG_MODE_SETTING_KEY;
	}

	private List<UserDTO> buildUserList(final MembersResponse response) {
		final Status responseStatus = response.getStatus();
		if (!responseStatus.getSuccess()) {
			final List<String> messages = responseStatus.getMessageList();
			if (CollectionUtils.isNotEmpty(messages)) {
				logger.info("Request returned with failed status: " + messages.get(0));
			} else {
				logger.info("Request returned with failed status.");
			}
			return Lists.newArrayList();
		}

		final List<String> memberUuids = response.getMemberUuidList();
		if (CollectionUtils.isEmpty(memberUuids)) {
			return Lists.newArrayList();
		}

		return userService.findUserDTOsByUuids(memberUuids);
	}

	@VisibleForTesting
	protected String getDefaultOrgModeOption(final Long userId) {
		final List<OrgUnitPath> orgModeOptions = getOrgModeOptions(userId);
		if (CollectionUtils.isEmpty(orgModeOptions)) {
			return null;
		}
		// Default value is the first (alphabetically sorted) org mode options.
		return orgModeOptions.get(0).getUuid();
	}

	private UserIdentity buildUserIdentity() {
		final Long companyId = authenticationService.getCurrentUserCompanyId();
		final List<String> companyUuids = companyService.getCompanyUuidsForCompanyIds(Lists.newArrayList(companyId));
		final String companyUuid = CollectionUtils.isNotEmpty(companyUuids) ? companyUuids.get(0) : "";

		return buildUserIdentityWithCompanyUuid(companyUuid);
	}

	private UserIdentity buildUserIdentityWithCompanyUuid(final String companyUuid) {
		final Long userId = authenticationService.getCurrentUserId();
		final String userUuid = userService.findUserUuidById(userId);
		return UserIdentity.newBuilder()
				.setUserUuid(userUuid)
				.setCompanyUuid(companyUuid)
				.build();
	}
}
