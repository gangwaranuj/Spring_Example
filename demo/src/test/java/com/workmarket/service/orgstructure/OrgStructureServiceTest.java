package com.workmarket.service.orgstructure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.api.v2.model.OrgUnitDTO;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.workmarket.business.OrgStructClient;
import com.workmarket.business.gen.Messages.OrgChart;
import com.workmarket.business.gen.Messages.GetOrgModePathsReq;
import com.workmarket.business.gen.Messages.AssignUsersFromBulkReq;
import com.workmarket.business.gen.Messages.FindUserMembershipReq;
import com.workmarket.business.gen.Messages.FindUserOrgUnitsReq;
import com.workmarket.business.gen.Messages.GetOrgChartUuidForCompanyUuidReq;
import com.workmarket.business.gen.Messages.GetOrgChartUuidForCompanyUuidResponse;
import com.workmarket.business.gen.Messages.GetOrgUnitMembersReq;
import com.workmarket.business.gen.Messages.MembersResponse;
import com.workmarket.business.gen.Messages.Membership;
import com.workmarket.business.gen.Messages.MembershipResponse;
import com.workmarket.business.gen.Messages.OrgChartResponse;
import com.workmarket.business.gen.Messages.GetSubtreePathsReq;
import com.workmarket.business.gen.Messages.OrgUnit;
import com.workmarket.business.gen.Messages.OrgUnitPath;
import com.workmarket.business.gen.Messages.OrgUnitPathsResponse;
import com.workmarket.business.gen.Messages.OrgUnitsResponse;
import com.workmarket.business.gen.Messages.PublishOrgChartReq;
import com.workmarket.business.gen.Messages.Status;
import com.workmarket.business.gen.Messages.UpdateOrgUnitMembershipReq;
import com.workmarket.business.gen.Messages.UserIdentity;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.Matchers;

import com.workmarket.setting.DimensionValuePair;
import com.workmarket.setting.SettingClient;
import com.workmarket.setting.gen.Common.Dimension;
import com.workmarket.setting.gen.Response;
import com.workmarket.setting.gen.Response.DimensionValue;
import com.workmarket.setting.vo.SingleValuesAndStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrgStructureServiceTest {

	@Mock WebRequestContextProvider webRequestContextProvider;
	@Mock AuthenticationService authenticationService;
	@Mock CompanyService companyService;
	@Mock UserService userService;
	@Mock OrgStructClient orgStructClient;
	@Mock SettingClient settingClient;

	@InjectMocks OrgStructureServiceImpl orgStructureService = spy(new OrgStructureServiceImpl());

	private final String

			ORG_UNIT_NAME = "orgUnitNameOne",
			ORG_UNIT_UUID = "orgUnitUuidOne",
			ORG_UNIT_NAME_TWO = "orgUnitNameTwo",
			ORG_UNIT_UUID_TWO = "orgUnitUuidTwo",
			USER_UUID = "userUuid",
			COMPANY_UUID = "companyUuid",
			ORG_CHART_UUID = "orgChatUuid",
			USER_EMAIL = "user@email.com",
			EMPTY_STRING = "",
			ORG_SETTING_KEY = orgStructureService.getOrgModeSettingKey(),
			ORG_STRUCTURE_UUID = "orgStructUuid";

	final UserIdentity userIdentity = UserIdentity.newBuilder()
			.setUserUuid(USER_UUID)
			.setCompanyUuid(COMPANY_UUID)
			.build();

	final GetSubtreePathsReq getSubtreePathsReq = GetSubtreePathsReq.newBuilder()
			.setUserIdentity(userIdentity)
			.setOrgUnitUuid(ORG_UNIT_UUID)
			.setDraft(false)
			.build();

	private final long
			USER_ID = 1l,
			COMPANY_ID = 1l;
	private final Response.Status
			SUCCESS_RESPONSE_STATUS = Response.Status.newBuilder()
			.setSuccess(true)
			.build(),
			FAIL_RESPONSE_STATUS = Response.Status.newBuilder()
			.setSuccess(false)
			.build();

	private DimensionValue dimensionValue;
	private SingleValuesAndStatus responseValuesAndStatus, defaultSingleValuesAndStatusResponse;
	private MembershipResponse membershipResponse;
	private MembersResponse membersResponse;
	private OrgUnitPathsResponse orgUnitPathsResponse;
	private GetOrgChartUuidForCompanyUuidResponse getOrgChartUuidForCompanyUuidResponse;
	private OrgChartResponse orgChartResponse;
	private OrgUnitPath orgUnitPathOne, orgUnitPathTwo;
	private Membership membership;
	private Status status;
	private List<OrgUnit> orgUnits;
	private RequestContext requestContext;
	private User user;
	private Company company;
	private OrgChart orgChart;

	@Before
	public void setup() {
		orgUnits = Lists.newArrayList(OrgUnit.newBuilder()
			.setName(ORG_UNIT_NAME)
			.setUuid(ORG_UNIT_UUID)
			.build());

		membership = Membership.newBuilder()
			.setUserUuid(USER_UUID)
			.addAllOrgUnit(orgUnits)
			.build();

		status = Status.newBuilder()
			.setSuccess(true)
			.build();

		membersResponse = MembersResponse.newBuilder()
			.setStatus(status)
			.addMemberUuid(USER_UUID)
			.build();

		membershipResponse = MembershipResponse.newBuilder()
			.setStatus(status)
			.addMembership(membership)
			.build();

		dimensionValue = DimensionValue.newBuilder()
				.setDimension(Dimension.USER)
				.setValue(ORG_UNIT_UUID)
				.build();

		orgUnitPathOne = OrgUnitPath.newBuilder()
				.setName(ORG_UNIT_NAME)
				.setUuid(ORG_UNIT_UUID)
				.addAllPath(Lists.newArrayList(ORG_UNIT_NAME))
				.build();

		orgUnitPathTwo = OrgUnitPath.newBuilder()
				.setName(ORG_UNIT_NAME_TWO)
				.setUuid(ORG_UNIT_UUID_TWO)
				.addAllPath(Lists.newArrayList(ORG_UNIT_NAME_TWO))
				.build();

		orgUnitPathsResponse = OrgUnitPathsResponse.newBuilder()
				.setStatus(status)
				.addAllPaths(Lists.newArrayList(orgUnitPathOne, orgUnitPathTwo))
				.build();

		OrgChart orgChart = OrgChart.newBuilder()
			.setOrgStructureId(ORG_STRUCTURE_UUID)
			.setRootOrgUnit(orgUnits.get(0))
			.build();


		getOrgChartUuidForCompanyUuidResponse = GetOrgChartUuidForCompanyUuidResponse.newBuilder()
			.setStatus(status)
			.setOrgChartUuid(ORG_CHART_UUID)
			.build();

		orgChartResponse = OrgChartResponse.newBuilder()
			.setStatus(status)
			.setOrgChart(orgChart)
			.build();

		responseValuesAndStatus = mock(SingleValuesAndStatus.class);

		user = mock(User.class);
		company = mock(Company.class);

		when(user.getCompany()).thenReturn(company);
		when(user.getUuid()).thenReturn(USER_UUID);
		when(company.getUuid()).thenReturn(COMPANY_UUID);
		when(userService.findUserByUuid(anyString())).thenReturn(user);

		when(responseValuesAndStatus.getStatus())
				.thenReturn(SUCCESS_RESPONSE_STATUS);
		when(responseValuesAndStatus.get(ORG_SETTING_KEY))
				.thenReturn(Optional.of(dimensionValue));

		defaultSingleValuesAndStatusResponse = mock(SingleValuesAndStatus.class);
		when(defaultSingleValuesAndStatusResponse.getStatus())
				.thenReturn(FAIL_RESPONSE_STATUS);
		when(defaultSingleValuesAndStatusResponse.getSettings())
				.thenReturn(Maps.<String, DimensionValue>newHashMap());

		requestContext = mock(RequestContext.class);
		when(requestContext.getCompanyId())
				.thenReturn(COMPANY_UUID);
		when(requestContext.getUserId())
				.thenReturn(USER_UUID);

		when(webRequestContextProvider.getRequestContext())
				.thenReturn(requestContext);
		when(authenticationService.getCurrentUserId())
				.thenReturn(0L);
		when(authenticationService.getCurrentUserCompanyId())
				.thenReturn(0L);
		when(userService.findUserUuidById(anyLong()))
				.thenReturn(USER_UUID);
		when(userService.findUserDTOsByUuids(anyListOf(String.class)))
				.thenReturn(Lists.newArrayList(new UserDTO()));
		when(companyService.getCompanyUuidsForCompanyIds(Lists.newArrayList(anyLong())))
				.thenReturn(Lists.newArrayList(COMPANY_UUID));
		when(companyService.findCompanyIdsForUsers(Lists.newArrayList(anyLong())))
			.thenReturn(Lists.newArrayList(COMPANY_ID));
		when(companyService.findById(COMPANY_ID))
			.thenReturn(company);
		when(orgStructClient.findUserOrgChildren(any(FindUserOrgUnitsReq.class), any(RequestContext.class)))
				.thenReturn(Observable.just(membershipResponse));
		when(orgStructClient.getOrgUnitMembers(any(GetOrgUnitMembersReq.class), any(RequestContext.class)))
				.thenReturn(Observable.just(membersResponse));
		when(orgStructClient.getOrgModePaths(any(GetOrgModePathsReq.class), any(RequestContext.class)))
				.thenReturn(Observable.just(orgUnitPathsResponse));
		when(settingClient.getSetting(anyListOf(DimensionValuePair.class), anyListOf(String.class) , eq(requestContext)))
				.thenReturn(Observable.just(responseValuesAndStatus));
		when(settingClient.createOverride(anyString(), anyString() , any(DimensionValuePair.class), eq(requestContext)))
				.thenReturn(Observable.just(SUCCESS_RESPONSE_STATUS));
		when(settingClient.modifyOverride(anyString(), anyString() , any(DimensionValuePair.class), anyMap(), eq(requestContext)))
				.thenReturn(Observable.just(SUCCESS_RESPONSE_STATUS));
		when(orgStructClient.getOrgChartUuid(any(GetOrgChartUuidForCompanyUuidReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(getOrgChartUuidForCompanyUuidResponse));
		when(orgStructClient.publishOrgChart(any(PublishOrgChartReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(orgChartResponse));
	}

	@Test
	public void findUserOrgChildren_happyPath_orgClientCalled() {
		orgStructureService.findUserOrgChildren(USER_ID, COMPANY_ID);

		verify(orgStructClient).findUserOrgChildren(any(FindUserOrgUnitsReq.class), any(RequestContext.class));
	}

	@Test
	public void findUserOrgChildren_noMappingFromCompanyIdToUuid_orgClientNotCalled() {
		when(companyService.getCompanyUuidsForCompanyIds(Lists.newArrayList(anyLong())))
			.thenReturn(Lists.<String>newArrayList());

		orgStructureService.findUserOrgChildren(USER_ID, COMPANY_ID);

		verify(orgStructClient, never())
			.findUserOrgChildren(any(FindUserOrgUnitsReq.class), any(RequestContext.class));
	}

	@Test
	public void findUserOrgChildren_noMappingFromUserIdToUuid_orgClientNotCalled() {
		when(userService.findUserUuidById(anyLong()))
				.thenReturn(null);

		orgStructureService.findUserOrgChildren(USER_ID, COMPANY_ID);

		verify(orgStructClient, never())
			.findUserOrgChildren(any(FindUserOrgUnitsReq.class), any(RequestContext.class));
	}


	@Test
	public void buildMembershipsMap_properOrgUnitDataHappyPath_orgUnitList() {
		final Map<String, List<OrgUnit>> builtOrgUnits = orgStructureService.buildMembershipsMap(membershipResponse);
		final OrgUnit orgUnit = builtOrgUnits.get(USER_UUID).get(0);

		assertEquals(ORG_UNIT_UUID, orgUnit.getUuid());
		assertEquals(ORG_UNIT_NAME, orgUnit.getName());
	}

	@Test
	public void buildMembershipsMap_properOrgUnitDataHappyPath_multipleUsers() {
		status = Status.newBuilder()
				.setSuccess(true)
				.build();
		final String secondInstance = "_2";
		membershipResponse = MembershipResponse.newBuilder()
				.setStatus(status)
				.addMembership(membership)
				.addMembership(Membership.newBuilder()
						.setUserUuid(USER_UUID + secondInstance)
						.addOrgUnit(OrgUnit.newBuilder()
								.setUuid(ORG_UNIT_UUID + secondInstance)
								.setName(ORG_UNIT_NAME + secondInstance)
								.build()))
				.build();

		final Map<String, List<OrgUnit>> builtOrgUnits = orgStructureService.buildMembershipsMap(membershipResponse);

		assertEquals(2, builtOrgUnits.size());

		final OrgUnit orgUnit = builtOrgUnits.get(USER_UUID).get(0);
		assertEquals(ORG_UNIT_UUID, orgUnit.getUuid());
		assertEquals(ORG_UNIT_NAME, orgUnit.getName());

		final OrgUnit orgUnit2 = builtOrgUnits.get(USER_UUID + secondInstance).get(0);
		assertEquals(ORG_UNIT_UUID + secondInstance, orgUnit2.getUuid());
		assertEquals(ORG_UNIT_NAME + secondInstance, orgUnit2.getName());
	}

	@Test
	public void buildMembershipsMap_failedReturnStatus_orgUnitListIsEmpty() {
		status = Status.newBuilder()
				.setSuccess(false)
				.build();
		membershipResponse = MembershipResponse.newBuilder()
				.setStatus(status)
				.build();

		final Map<String, List<OrgUnit>> builtOrgUnits = orgStructureService.buildMembershipsMap(membershipResponse);

		assertTrue(builtOrgUnits.isEmpty());
	}

	@Test
	public void buildMembershipsMap_failedReturnStatusAndMessage_orgUnitListIsEmpty() {
		status = Status.newBuilder()
				.setSuccess(false)
				.addMessage("Fail")
				.build();
		membershipResponse = MembershipResponse.newBuilder()
				.setStatus(status)
				.build();

		final Map<String, List<OrgUnit>> builtOrgUnits = orgStructureService.buildMembershipsMap(membershipResponse);

		assertTrue(builtOrgUnits.isEmpty());
	}

	@Test
	public void buildMembershipsMap_listOfMembershipsIsEmpty_orgUnitListIsEmpty() {
		final List<Membership> emptyList = Lists.newArrayList();
		membershipResponse = MembershipResponse.newBuilder()
				.setStatus(status)
				.addAllMembership(emptyList)
				.build();

		final Map<String, List<OrgUnit>> builtOrgUnits = orgStructureService.buildMembershipsMap(membershipResponse);

		assertTrue(builtOrgUnits.isEmpty());
	}

	@Test
	public void testGetOrgChartUuidFromCompanyUuid_Success() {
		final GetOrgChartUuidForCompanyUuidResponse response = GetOrgChartUuidForCompanyUuidResponse.newBuilder()
			.setOrgChartUuid(ORG_CHART_UUID)
			.build();
		when(orgStructClient.getOrgChartUuid(any(GetOrgChartUuidForCompanyUuidReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(response));
		final String actualUuid = orgStructureService.getOrgChartUuidFromCompanyUuid(COMPANY_UUID);
		assertThat(actualUuid, equalTo(ORG_CHART_UUID));
	}

	@Test
	public void testGetOrgChartUuidFromCompanyUuid_Failure() {
		final GetOrgChartUuidForCompanyUuidResponse response = GetOrgChartUuidForCompanyUuidResponse.newBuilder()
			.setStatus(Status.newBuilder().addMessage("Multiple org charts defined for company_uuid").build())
			.build();
		when(orgStructClient.getOrgChartUuid(
			any(GetOrgChartUuidForCompanyUuidReq.class),
			any(RequestContext.class)))
			.thenReturn(Observable.just(response));
		final String actualUuid = orgStructureService.getOrgChartUuidFromCompanyUuid(ORG_CHART_UUID);
		assertThat(actualUuid, isEmptyString());
	}

	@Test
	public void testGetOrgChartUuidFromCompanyUuid_OnError() {
		when(orgStructClient.getOrgChartUuid(any(GetOrgChartUuidForCompanyUuidReq.class), any(RequestContext.class)))
			.thenReturn(Observable.<GetOrgChartUuidForCompanyUuidResponse>error(new Exception()));

		try {
			orgStructureService.getOrgChartUuidFromCompanyUuid(COMPANY_UUID);
		} catch (final Exception e) {
			assertThat(e.getMessage(), equalTo(String.format("Failed to get orgChartUuid for companyUuid:%s",
				COMPANY_UUID)));
		}

	}

	@Test
	public void testAssignUsersFromBulk_Success() {
		final List<String> orgUnitPaths = Lists.newArrayList("WM/1");
		final OrgChartResponse orgChartResponse = OrgChartResponse.newBuilder()
			.setStatus(Status.newBuilder().setSuccess(true).build())
			.build();
		when(orgStructClient.assignUsersFromBulk(any(AssignUsersFromBulkReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(orgChartResponse));

		final boolean status = orgStructureService.assignUsersFromBulk(
			orgUnitPaths,
			USER_UUID,
			COMPANY_UUID,
			USER_EMAIL,
			ORG_CHART_UUID
		);
		assertTrue(status);
	}

	@Test
	public void testAssignUsersFromBulk_Failure() {
		final List<String> orgUnitPaths = Lists.newArrayList("WM/1");
		final OrgChartResponse orgChartResponse = OrgChartResponse.newBuilder()
			.setStatus(Status.newBuilder().setSuccess(false).build())
			.build();
		when(orgStructClient.assignUsersFromBulk(any(AssignUsersFromBulkReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(orgChartResponse));

		final boolean status = orgStructureService.assignUsersFromBulk(
			orgUnitPaths,
			USER_UUID,
			COMPANY_UUID,
			USER_EMAIL,
			ORG_CHART_UUID
		);
		assertFalse(status);
	}

	@Test
	public void testAssignUsersFromBulk_OnError() {
		final List<String> orgUnitPaths = Lists.newArrayList("WM/1");
		when(orgStructClient.assignUsersFromBulk(any(AssignUsersFromBulkReq.class), any(RequestContext.class)))
				.thenReturn(Observable.<OrgChartResponse>error(new Exception()));

		try {
			orgStructureService.assignUsersFromBulk(orgUnitPaths, USER_UUID, COMPANY_UUID, USER_EMAIL, ORG_CHART_UUID);
		} catch (final Exception e) {
			assertThat(e.getMessage(), equalTo(
				String.format("Failed to bulk upload userUuid:%s, userEmail:%s, orgChartUuid:%s",
						USER_UUID,
						USER_EMAIL,
						ORG_CHART_UUID
				)));
		}
	}

	@Test
	public void getSubTreePaths_companyUuidIsEmpty_returnsEmptyList() {
		when(companyService.getCompanyUuidsForCompanyIds(Lists.newArrayList(anyLong())))
			.thenReturn(Lists.newArrayList(EMPTY_STRING));

		final Collection<OrgUnitDTO> orgUnitPaths = orgStructureService.getSubtreePaths(USER_ID, COMPANY_ID, ORG_UNIT_UUID);

		assertEquals(0, orgUnitPaths.size());
		verify(orgStructClient, never()).getSubtreePaths(any(GetSubtreePathsReq.class), any(RequestContext.class));
	}

	@Test
	public void getSubTreePaths_userUuidIsEmpty_returnsEmptyList() {
		when(userService.findUserUuidById(anyLong()))
			.thenReturn(EMPTY_STRING);

		final Collection<OrgUnitDTO> orgUnitPaths = orgStructureService.getSubtreePaths(USER_ID, COMPANY_ID, ORG_UNIT_UUID);

		assertEquals(0, orgUnitPaths.size());
		verify(orgStructClient, never()).getSubtreePaths(any(GetSubtreePathsReq.class), any(RequestContext.class));
	}


	@Test
	public void getSubTreePaths_orgUnitUudIsEmpty_returnsEmptyList() {
		final Collection<OrgUnitDTO> orgUnitPaths = orgStructureService.getSubtreePaths(USER_ID, COMPANY_ID, EMPTY_STRING);

		assertEquals(0, orgUnitPaths.size());
		verify(orgStructClient, never()).getSubtreePaths(any(GetSubtreePathsReq.class), any(RequestContext.class));
	}

	@Test
	public void getSubTreePaths_success_returnPathsList() {
		final OrgUnitPath photoPath = buildOrgUnitPath("photo-uuid", "Photo", ImmutableList.of("COMPANY B"));
		final OrgUnitPath localPath = buildOrgUnitPath("local-uuid", "Local", ImmutableList.of("COMPANY B","Magazine","Magazine Supervision"));
		final OrgUnitPath magTPath = buildOrgUnitPath("magt-uuid", "Mag-T", ImmutableList.of("COMPANY B","Magazine","Magazine Supervision","Mag"));
		final List<OrgUnitPath> expectedPaths = ImmutableList.of(photoPath, localPath, magTPath);
		final OrgUnitPathsResponse expectedResp = OrgUnitPathsResponse.newBuilder()
			.setStatus(Status.newBuilder().setSuccess(true).build())
			.addAllPaths(expectedPaths)
			.build();
		when(orgStructClient.getSubtreePaths(eq(getSubtreePathsReq), any(RequestContext.class)))
			.thenReturn(Observable.just(expectedResp));

		final Collection<OrgUnitDTO> actualPaths = orgStructureService.getSubtreePaths(USER_ID, COMPANY_ID, ORG_UNIT_UUID);

		assertEquals(expectedPaths.size(), actualPaths.size());
		verifyPaths(photoPath, actualPaths);
		verifyPaths(localPath, actualPaths);
		verifyPaths(magTPath, actualPaths);
	}

	@Test
	public void getSubTreePaths_exception_returnEmptyList() {
		when(orgStructClient.getSubtreePaths(eq(getSubtreePathsReq), any(RequestContext.class)))
			.thenReturn(Observable.<OrgUnitPathsResponse>error(new Exception()));

		try {
			orgStructureService.getSubtreePaths(USER_ID, COMPANY_ID, ORG_UNIT_UUID);
		} catch (final Exception e) {
			assertThat(e.getMessage(), equalTo(String.format("Failed to get org units rooted at current org unit: %s", ORG_UNIT_UUID)));
		}
	}

	@Test
	public void getSubtreePathOrgUnitUuidsForCurrentOrgMode_noOrgUnitUuids_returnsEmptyList() {
		final List<OrgUnitPath> expectedPaths = ImmutableList.of();
		final OrgUnitPathsResponse expectedResp = OrgUnitPathsResponse.newBuilder()
				.setStatus(Status.newBuilder().setSuccess(true).build())
				.addAllPaths(expectedPaths)
				.build();
		when(orgStructClient.getSubtreePaths(eq(getSubtreePathsReq), any(RequestContext.class)))
				.thenReturn(Observable.just(expectedResp));

		final List<String> orgUnitUuids = orgStructureService.getSubtreePathOrgUnitUuidsForCurrentOrgMode(USER_ID, COMPANY_ID);

		assertEquals(0, orgUnitUuids.size());
	}

	@Test
	public void getSubtreePathOrgUnitUuidsForCurrentOrgMode_orgUnitUuids_returnsOrgUnitUuids() {
		final OrgUnitPath photoPath = buildOrgUnitPath("photo-uuid", "Photo", ImmutableList.of("COMPANY B"));
		final OrgUnitPath localPath = buildOrgUnitPath("local-uuid", "Local", ImmutableList.of("COMPANY B","Magazine","Magazine Supervision"));
		final OrgUnitPath magTPath = buildOrgUnitPath("magt-uuid", "Mag-T", ImmutableList.of("COMPANY B","Magazine","Magazine Supervision","Mag"));
		final List<OrgUnitPath> expectedPaths = ImmutableList.of(photoPath, localPath, magTPath);
		final OrgUnitPathsResponse expectedResp = OrgUnitPathsResponse.newBuilder()
				.setStatus(Status.newBuilder().setSuccess(true).build())
				.addAllPaths(expectedPaths)
				.build();
		when(orgStructClient.getSubtreePaths(eq(getSubtreePathsReq), any(RequestContext.class)))
				.thenReturn(Observable.just(expectedResp));

		final List<String> orgUnitUuids = orgStructureService.getSubtreePathOrgUnitUuidsForCurrentOrgMode(USER_ID, COMPANY_ID);

		assertEquals(expectedPaths.size(), orgUnitUuids.size());
		assertTrue(orgUnitUuids.containsAll(Lists.newArrayList(photoPath.getUuid(), localPath.getUuid(), magTPath.getUuid())));
	}

	@Test
	public void getOrgUnitMembers_happyPath_orgClientCalled() {
		orgStructureService.getOrgUnitMembers(ImmutableList.of(ORG_UNIT_UUID));

		verify(orgStructClient).getOrgUnitMembers(any(GetOrgUnitMembersReq.class), any(RequestContext.class));
	}

	@Test
	public void getOrgUnitMembers_happyPath_NullListOrgClientNotCalled() {
		orgStructureService.getOrgUnitMembers(null);

		verify(orgStructClient, never()).getOrgUnitMembers(any(GetOrgUnitMembersReq.class), any(RequestContext.class));
	}

	@Test
	public void getOrgUnitMembers_happyPath_EmptyListOrgClientNotCalled() {
		orgStructureService.getOrgUnitMembers(ImmutableList.<String>of());

		verify(orgStructClient, never()).getOrgUnitMembers(any(GetOrgUnitMembersReq.class), any(RequestContext.class));
	}

	@Test
	public void getOrgUnitMembers_happyPath_userServiceCalled() {
		orgStructureService.getOrgUnitMembers(ImmutableList.of(ORG_UNIT_UUID));

		verify(userService).findUserDTOsByUuids(anyListOf(String.class));
	}

	@Test
	public void getOrgUnitMembers_noMembersFound_userServiceNotCalled() {
		membersResponse = MembersResponse.newBuilder()
			.setStatus(status)
			.build();
		when(orgStructClient.getOrgUnitMembers(any(GetOrgUnitMembersReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(membersResponse));

		final List<UserDTO> actual = orgStructureService.getOrgUnitMembers(ImmutableList.of(ORG_UNIT_UUID));

		assertTrue(actual.isEmpty());
		verify(userService, never()).findUserDTOsByUuids(anyListOf(String.class));
	}

	@Test
	public void getOrgUnitMembers_failureStatus_userServiceNotCalled() {
		status = Status.newBuilder()
				.setSuccess(false)
				.addMessage("Fail")
				.build();
		membersResponse = MembersResponse.newBuilder()
				.setStatus(status)
				.build();
		when(orgStructClient.getOrgUnitMembers(any(GetOrgUnitMembersReq.class), any(RequestContext.class)))
				.thenReturn(Observable.just(membersResponse));

		final List<UserDTO> actual = orgStructureService.getOrgUnitMembers(ImmutableList.of(ORG_UNIT_UUID));

		assertTrue(actual.isEmpty());
		verify(userService, never()).findUserDTOsByUuids(anyListOf(String.class));

	}

	@Test
	public void getUserOrgUnitMemberships_happyPath_properResponse() {
		status = Status.newBuilder()
			.setSuccess(true)
			.addMessage("Success")
			.build();

		final MembershipResponse membershipResponse = MembershipResponse.newBuilder()
			.setStatus(status)
			.addMembership(Membership.newBuilder()
				.addOrgUnit(orgUnits.get(0))
				.setUserUuid(USER_UUID)
				.build())
			.build();

		when(orgStructClient.findUserMembership(any(FindUserMembershipReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(membershipResponse));

		final List<String> actual = orgStructureService.getUserOrgUnitUuids(USER_UUID);

		assertTrue(actual.size() == 1);
		assertTrue(Objects.equals(actual.get(0), orgUnits.get(0).getUuid()));

		verify(orgStructClient).findUserMembership(any(FindUserMembershipReq.class), any(RequestContext.class));
	}

	@Test
	public void setUserMemberships_happyPath_additiveOnly_verifyCalls() {
		status = Status.newBuilder()
			.setSuccess(true)
			.addMessage("Success")
			.build();

		final OrgUnitsResponse orgUnitsResponse = OrgUnitsResponse.newBuilder()
			.setStatus(status)
			.addOrgUnit(orgUnits.get(0))
			.build();

		ArgumentCaptor<UpdateOrgUnitMembershipReq> updateMembersReqCaptor = ArgumentCaptor.forClass(UpdateOrgUnitMembershipReq.class);

		when(orgStructClient.getOrgUnitMembers(any(GetOrgUnitMembersReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(MembersResponse.getDefaultInstance()));

		// initially empty
		when(orgStructClient.findUserMembership(any(FindUserMembershipReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(MembershipResponse.getDefaultInstance()));

		when(orgStructClient.updateOrgUnitMembership(any(UpdateOrgUnitMembershipReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(orgUnitsResponse));

		final List<String> actual = orgStructureService.setUserMemberships(ImmutableList.of(ORG_UNIT_UUID), USER_UUID);

		verify(orgStructClient, times(2)).findUserMembership(any(FindUserMembershipReq.class), any(RequestContext.class));
		verify(orgStructClient).updateOrgUnitMembership(updateMembersReqCaptor.capture(), any(RequestContext.class));
	}

	@Test
	public void setUserMemberships_happyPath_addAndRemove() {
		final String NEW_ORG_UNIT_UUID = "newOrgUnitUuid";

		status = Status.newBuilder()
			.setSuccess(true)
			.addMessage("Success")
			.build();

		final OrgUnitsResponse orgUnitsResponse = OrgUnitsResponse.newBuilder()
			.setStatus(status)
			.addOrgUnit(orgUnits.get(0))
			.build();

		final MembershipResponse membershipResponse = MembershipResponse.newBuilder()
			.setStatus(status)
			.addMembership(Membership.newBuilder()
				.addOrgUnit(orgUnits.get(0))
				.setUserUuid(USER_UUID)
				.build())
			.build();

		when(orgStructClient.getOrgUnitMembers(any(GetOrgUnitMembersReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(MembersResponse.getDefaultInstance()));

		when(orgStructClient.findUserMembership(any(FindUserMembershipReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(membershipResponse));

		when(orgStructClient.updateOrgUnitMembership(any(UpdateOrgUnitMembershipReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(orgUnitsResponse));

		final List<String> actual = orgStructureService.setUserMemberships(ImmutableList.of(NEW_ORG_UNIT_UUID), USER_UUID);

		verify(orgStructClient, times(2)).findUserMembership(any(FindUserMembershipReq.class), any(RequestContext.class));
		verify(orgStructClient, times(2)).updateOrgUnitMembership(any(UpdateOrgUnitMembershipReq.class), any(RequestContext.class));
	}

	@Test
	public void setUserMemberships_happyPath_removeAllMemberships() {
		status = Status.newBuilder()
			.setSuccess(true)
			.addMessage("Success")
			.build();

		final OrgUnitsResponse orgUnitsResponse = OrgUnitsResponse.newBuilder()
			.setStatus(status)
			.addOrgUnit(orgUnits.get(0))
			.build();

		final MembershipResponse membershipResponse = MembershipResponse.newBuilder()
			.setStatus(status)
			.addMembership(Membership.newBuilder()
				.addOrgUnit(orgUnits.get(0))
				.setUserUuid(USER_UUID)
				.build())
			.build();

		when(orgStructClient.getOrgUnitMembers(any(GetOrgUnitMembersReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(MembersResponse.getDefaultInstance()));

		when(orgStructClient.findUserMembership(any(FindUserMembershipReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(membershipResponse));

		when(orgStructClient.updateOrgUnitMembership(any(UpdateOrgUnitMembershipReq.class), any(RequestContext.class)))
			.thenReturn(Observable.just(orgUnitsResponse));

		final List<String> actual = orgStructureService.setUserMemberships(ImmutableList.<String>of(), USER_UUID);

		verify(orgStructClient, times(2)).findUserMembership(any(FindUserMembershipReq.class), any(RequestContext.class));
		verify(orgStructClient).updateOrgUnitMembership(any(UpdateOrgUnitMembershipReq.class), any(RequestContext.class));
	}

	private OrgUnitPath buildOrgUnitPath(final String uuid,
		final String name, final List<String> paths) {
		return OrgUnitPath.newBuilder()
			.setUuid(uuid)
			.setName(name)
			.addAllPath(paths)
			.build();
	}

	private void verifyPaths(final OrgUnitPath expectedPath, final Collection<OrgUnitDTO> actualPaths) {
		assertThat(actualPaths,
				hasItem(Matchers.<OrgUnitDTO>hasProperty("uuid", equalTo(expectedPath.getUuid()))));
		assertThat(actualPaths,
				hasItem(Matchers.<OrgUnitDTO>hasProperty("name", equalTo(expectedPath.getName()))));
		assertThat(actualPaths,
				hasItem(Matchers.<OrgUnitDTO>hasProperty("paths", equalTo(expectedPath.getPathList()))));
	}

	@Test
	public void createUserDimensionValuePairForRequest_userId_userDimensionValuePairCreated() {
		final DimensionValuePair dimensionValuePair = orgStructureService.createUserDimensionValuePairForRequest(USER_ID);

		assertEquals(USER_UUID, dimensionValuePair.getObjectId());
		assertEquals(Dimension.USER, dimensionValuePair.getDimension());
	}

	@Test
	public void getOrgModeSettingFromSettingService_userIdHappyPath_getSettingCalled() {
		orgStructureService.getOrgModeSettingFromSettingService(USER_ID);

		verify(settingClient).getSetting(anyListOf(DimensionValuePair.class), anyListOf(String.class), eq(requestContext));
	}

	@Test
	public void getOrgModeSetting_settingClientFailsToGetSetting_firstPathReturned() {
		when(settingClient.getSetting(anyListOf(DimensionValuePair.class), anyListOf(String.class) , eq(requestContext)))
				.thenReturn(Observable.just(defaultSingleValuesAndStatusResponse));

		final String result = orgStructureService.getOrgModeSetting(USER_ID);

		assertEquals(ORG_UNIT_UUID, result);
	}

	@Test
	public void getOrgModeSetting_userDimensionFound_preferenceReturned() {
		final String result = orgStructureService.getOrgModeSetting(USER_ID);

		assertEquals(ORG_UNIT_UUID, result);
	}

	@Test
	public void getOrgModeSetting_unrecognizedDimensionFound_firstPathReturned() {
		dimensionValue = DimensionValue.newBuilder()
				.setDimension(Dimension.COMPANY)
				.setValue(ORG_UNIT_UUID)
				.build();
		when(responseValuesAndStatus.get(ORG_SETTING_KEY))
				.thenReturn(Optional.of(dimensionValue));

		final String result = orgStructureService.getOrgModeSetting(USER_ID);

		assertEquals(ORG_UNIT_UUID, result);
	}

	@Test
	public void getOrgModeSetting_defaultDimensionFound_firstPreferenceSetAndReturned() {
		dimensionValue = DimensionValue.newBuilder()
				.setDimension(Dimension.DEFAULT)
				.setValue(ORG_UNIT_UUID)
				.build();
		when(responseValuesAndStatus.get(ORG_SETTING_KEY))
				.thenReturn(Optional.of(dimensionValue));

		final String resultOrgUnitUuid = orgStructureService.getOrgModeSetting(USER_ID);

		assertEquals(resultOrgUnitUuid, ORG_UNIT_UUID);
		verify(settingClient).createOverride(anyString(), anyString(), any(DimensionValuePair.class), eq(requestContext));
	}

	@Test
	public void setOrgModeSettingForUser_selectedOrgModeDoesNotExistAsAPossibleOrgModeOption_setFailed() {
		final String invalidOrgModeOptionUuid = "!!!PostOffice!!!";

		final BaseStatus status = orgStructureService.setOrgModeSettingForUser(USER_ID, invalidOrgModeOptionUuid);

		assertTrue(status.isFailure());
	}

	@Test
	public void setOrgModeSettingForUser_selectedOrgUnitIsNull_setFailed() {
		final BaseStatus status = orgStructureService.setOrgModeSettingForUser(USER_ID, null);

		assertTrue(status.isFailure());
	}

	@Test
	public void setOrgModeSettingForUser_selectedOrgUnitIsBlank_setFailed() {
		final BaseStatus status = orgStructureService.setOrgModeSettingForUser(USER_ID, "");

		assertTrue(status.isFailure());
	}

	@Test
	public void setOrgModeSettingForUser_settingServiceCanNotReturnUserSetting_setFailed() {
		when(settingClient.getSetting(anyListOf(DimensionValuePair.class), anyListOf(String.class) , eq(requestContext)))
				.thenReturn(Observable.just(defaultSingleValuesAndStatusResponse));

		final BaseStatus status = orgStructureService.setOrgModeSettingForUser(USER_ID, ORG_UNIT_UUID);

		assertTrue(status.isFailure());
	}

	@Test
	public void setOrgModeSettingForUser_unrecognizedDimensionReturned_setFailed() {
		dimensionValue = DimensionValue.newBuilder()
				.setDimension(Dimension.COMPANY)
				.setValue(ORG_UNIT_UUID)
				.build();
		when(responseValuesAndStatus.get(ORG_SETTING_KEY))
				.thenReturn(Optional.of(dimensionValue));

		final BaseStatus status = orgStructureService.setOrgModeSettingForUser(USER_ID, ORG_UNIT_UUID);

		assertTrue(status.isFailure());
	}

	@Test
	public void setOrgModeSettingForUser_defaultDimensionReturned_settingCreated() {
		dimensionValue = DimensionValue.newBuilder()
				.setDimension(Dimension.DEFAULT)
				.setValue(ORG_UNIT_UUID)
				.build();
		when(responseValuesAndStatus.get(ORG_SETTING_KEY))
				.thenReturn(Optional.of(dimensionValue));

		final BaseStatus status = orgStructureService.setOrgModeSettingForUser(USER_ID, ORG_UNIT_UUID);

		assertTrue(status.isSuccessful());
		verify(settingClient).createOverride(anyString(), anyString(), any(DimensionValuePair.class), eq(requestContext));
	}

	@Test
	public void setOrgModeSettingForUser_userDimensionReturned_settingModified() {
		final BaseStatus status = orgStructureService.setOrgModeSettingForUser(USER_ID, ORG_UNIT_UUID);

		assertTrue(status.isSuccessful());
		verify(settingClient).modifyOverride(anyString(), anyString(), any(DimensionValuePair.class), anyMap(), eq(requestContext));
	}

	@Test
	public void getOrgModeOptionsUuids_nullOrgUnitPathsList_emptyListReturned() {
		final List<String> result = orgStructureService.getOrgModeOptionsUuids(null);

		assertTrue(CollectionUtils.isEmpty(result));
	}

	@Test
	public void getOrgModeOptionsUuids_emptyOrgUnitPathsList_emptyListReturned() {
		final List<String> result = orgStructureService.getOrgModeOptionsUuids(Lists.<OrgUnitPath>newArrayList());

		assertTrue(CollectionUtils.isEmpty(result));
	}

	@Test
	public void getOrgModeOptionsUuids_orgUnitPathsList_orgUnitUuidsReturned() {
		final List<OrgUnitPath> orgUnitPathsList = orgUnitPathsResponse.getPathsList();
		final List<String> result = orgStructureService.getOrgModeOptionsUuids(orgUnitPathsList);

		final String orgUnitUuidOne = orgUnitPathsList.get(0).getUuid();
		final String orgUnitUuidTwo = orgUnitPathsList.get(1).getUuid();
		assertEquals(orgUnitUuidOne, result.get(0));
		assertEquals(orgUnitUuidTwo, result.get(1));
	}

	@Test
	public void getOrgModeOptions_failedStatus_emptyListReturned() {
		final Status failedStatus = Status.newBuilder().setSuccess(false).build();
		orgUnitPathsResponse = OrgUnitPathsResponse.newBuilder()
				.setStatus(failedStatus)
				.addPaths(orgUnitPathOne)
				.addPaths(orgUnitPathTwo)
				.build();
		when(orgStructClient.getOrgModePaths(any(GetOrgModePathsReq.class), any(RequestContext.class)))
				.thenReturn(Observable.just(orgUnitPathsResponse));

		final List<OrgUnitPath> result = orgStructureService.getOrgModeOptions(USER_ID);

		assertTrue(CollectionUtils.isEmpty(result));
	}

	@Test
	public void getOrgModeOptions_optionOneAlphabeticallyFirst_optionOneFirstInList() {
		final String orgOneName = "aaaaaaa";
		final String orgTwoName = "zzzzzzz";
		orgUnitPathOne = OrgUnitPath.newBuilder()
				.setName(orgOneName)
				.setUuid("")
				.addAllPath(Lists.newArrayList(ORG_UNIT_NAME))
				.build();
		orgUnitPathTwo = OrgUnitPath.newBuilder()
				.setName(orgTwoName)
				.setUuid("")
				.addAllPath(Lists.newArrayList(ORG_UNIT_NAME_TWO))
				.build();
		orgUnitPathsResponse = OrgUnitPathsResponse.newBuilder()
				.setStatus(status)
				.addAllPaths(Lists.newArrayList(orgUnitPathTwo, orgUnitPathOne))
				.build();
		when(orgStructClient.getOrgModePaths(any(GetOrgModePathsReq.class), any(RequestContext.class)))
				.thenReturn(Observable.just(orgUnitPathsResponse));

		final List<OrgUnitPath> result = orgStructureService.getOrgModeOptions(USER_ID);

		final OrgUnitPath firstOption = result.get(0);
		final OrgUnitPath secondOption = result.get(1);
		assertEquals(orgOneName, firstOption.getName());
		assertEquals(orgTwoName, secondOption.getName());
	}

	@Test
	public void getOrgModeOptions_bothOptionsHaveSameNameButOptionOneUuidIsAlphabeticallyFirst_optionOneFirstInList() {
		final String orgOneUuid = "aaaaaaa";
		final String orgTwoUuid = "zzzzzzz";
		orgUnitPathOne = OrgUnitPath.newBuilder()
				.setName(ORG_UNIT_NAME)
				.setUuid(orgOneUuid)
				.addAllPath(Lists.newArrayList(ORG_UNIT_NAME))
				.build();
		orgUnitPathTwo = OrgUnitPath.newBuilder()
				.setName(ORG_UNIT_NAME)
				.setUuid(orgTwoUuid)
				.addAllPath(Lists.newArrayList(ORG_UNIT_NAME_TWO))
				.build();
		orgUnitPathsResponse = OrgUnitPathsResponse.newBuilder()
				.setStatus(status)
				.addAllPaths(Lists.newArrayList(orgUnitPathTwo, orgUnitPathOne))
				.build();
		when(orgStructClient.getOrgModePaths(any(GetOrgModePathsReq.class), any(RequestContext.class)))
				.thenReturn(Observable.just(orgUnitPathsResponse));

		final List<OrgUnitPath> result = orgStructureService.getOrgModeOptions(USER_ID);

		final OrgUnitPath firstOption = result.get(0);
		final OrgUnitPath secondOption = result.get(1);
		assertEquals(orgOneUuid, firstOption.getUuid());
		assertEquals(orgTwoUuid, secondOption.getUuid());
	}

	@Test
	public void getOrgModeOptions_optionOneAlphabeticallyFirstIgnoreCase_optionOneFirstInList() {
		final String orgOneName = "aaaaaaa";
		final String orgTwoName = "ZZZZZZZ";
		orgUnitPathOne = OrgUnitPath.newBuilder()
				.setName(orgOneName)
				.setUuid("")
				.addAllPath(Lists.newArrayList(ORG_UNIT_NAME))
				.build();
		orgUnitPathTwo = OrgUnitPath.newBuilder()
				.setName(orgTwoName)
				.setUuid("")
				.addAllPath(Lists.newArrayList(ORG_UNIT_NAME_TWO))
				.build();
		orgUnitPathsResponse = OrgUnitPathsResponse.newBuilder()
				.setStatus(status)
				.addAllPaths(Lists.newArrayList(orgUnitPathTwo, orgUnitPathOne))
				.build();
		when(orgStructClient.getOrgModePaths(any(GetOrgModePathsReq.class), any(RequestContext.class)))
				.thenReturn(Observable.just(orgUnitPathsResponse));

		final List<OrgUnitPath> result = orgStructureService.getOrgModeOptions(USER_ID);

		final OrgUnitPath firstOption = result.get(0);
		final OrgUnitPath secondOption = result.get(1);
		assertEquals(orgOneName, firstOption.getName());
		assertEquals(orgTwoName, secondOption.getName());
	}
}
