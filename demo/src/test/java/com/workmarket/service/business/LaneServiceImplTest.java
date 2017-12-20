package com.workmarket.service.business;

import com.google.common.collect.Sets;
import com.workmarket.dao.UserDAOImpl;
import com.workmarket.dao.lane.LaneAssociationDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.changelog.user.UserLaneRemovedChangeLog;
import com.workmarket.domains.model.changelog.user.UserLaneAddedChangeLog;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.event.EventFactoryImpl;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouterImpl;
import com.workmarket.service.infra.security.LaneContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LaneServiceImplTest {

	@InjectMocks @Spy LaneServiceImpl laneService = new LaneServiceImpl();
	@Mock AuthenticationService authenticationService;

	@Mock CompanyService companyService;
	@Mock EventRouterImpl eventRouter;
	@Mock EventFactoryImpl eventFactory;
	@Mock LaneAssociationDAO laneAssociationDAO;
	@Mock UserService userService;
	@Mock UserDAOImpl userDAO;
	@Mock UserChangeLogService userChangeLogService;
	@Mock UserNotificationService userNotificationService;
	@Mock UserGroupValidationService userGroupValidationService;
	@Mock UserIndexer userIndexer;
	@Mock WorkService workService;

	private static final long COMPANY_ID = 10L;
	private static final long OTHER_COMPANY_ID = 500L;
	private static final long USER_ID = 1001L;
	private static final String USER_NUMBER = "1001";
	private static final long WORKER_ID = 1005L;
	private static final String WORKER_NUMBER = "1005";
	private static final long MASQUERADE_USER_ID = 1010L;

	User user;
	User worker;
	Set<String> resourceUserNumbers = Sets.newHashSet();

	@Before
	public void setup() {
		user = mock(User.class);
		worker = mock(User.class);
		resourceUserNumbers.add("1");
		resourceUserNumbers.add("2");
		when(userDAO.findUserByUserNumber(USER_NUMBER, false)).thenReturn(user);
		when(worker.getId()).thenReturn(WORKER_ID);
		setupUserWithCompanyId(COMPANY_ID);
	}

	@Test
	public void addUsersToTest_eventFired() {
		laneService.addUsersToWorkerPool(USER_NUMBER, resourceUserNumbers);
		verify(eventFactory).buildAddToWorkerPoolEvent(COMPANY_ID, USER_NUMBER, resourceUserNumbers);
	}

	@Test
	public void addUserToWorkerPool_nullWorker_returnsEarly() {
		when(userService.findUserId(USER_NUMBER)).thenReturn(USER_ID);
		when(userService.findUserByUserNumber(WORKER_NUMBER)).thenReturn(null);

		laneService.addUserToWorkerPool(COMPANY_ID, USER_NUMBER, WORKER_NUMBER);

		verify(laneService, never()).getLaneContextForUserAndCompany(anyLong(), anyLong());
	}

	@Test
	public void addUserToWorkerPool_nullLaneContext_returnsEarly() {
		setupUserWithCompanyId(OTHER_COMPANY_ID);
		when(userService.findUserId(USER_NUMBER)).thenReturn(USER_ID);
		when(userService.findUserByUserNumber(WORKER_NUMBER)).thenReturn(worker);

		LaneAssociation laneAssociation = mock(LaneAssociation.class);
		when(laneAssociation.getLaneType()).thenReturn(LaneType.LANE_2);
		when(authenticationService.isLane2Active(user)).thenReturn(false);
		when(laneAssociationDAO.findActiveAssociationByUserIdAndCompanyId(anyLong(), anyLong())).thenReturn(laneAssociation);

		laneService.addUserToWorkerPool(COMPANY_ID, USER_NUMBER, WORKER_NUMBER);

		verify(laneService, never()).addUserToCompanyLane3(WORKER_ID, COMPANY_ID);
		verify(laneService, never()).updateLaneAssociationApprovalStatus(WORKER_ID, COMPANY_ID, ApprovalStatus.APPROVED);
	}

	@Test
	public void addUserToWorkerPool_lane4Context_addsToCompanyLane3() {
		setupUserWithCompanyId(OTHER_COMPANY_ID);
		when(userService.findUserByUserNumber(WORKER_NUMBER)).thenReturn(worker);
		when(userService.findUserId(USER_NUMBER)).thenReturn(USER_ID);
		when(laneAssociationDAO.findActiveAssociationByUserIdAndCompanyId(anyLong(), anyLong())).thenReturn(null);

		laneService.addUserToWorkerPool(COMPANY_ID, USER_NUMBER, WORKER_NUMBER);

		verify(laneService, times(1)).addUserToCompanyLane3(WORKER_ID, COMPANY_ID);
	}

	@Test
	public void addUserToWorkerPool_pendingLane2Context_updatesApprovalStatus() {
		setupUserWithCompanyId(OTHER_COMPANY_ID);
		when(authenticationService.isLane2Active(user)).thenReturn(true);
		when(userService.getUser(anyLong())).thenReturn(user);

		when(userService.findUserId(USER_NUMBER)).thenReturn(USER_ID);
		when(userService.findUserByUserNumber(WORKER_NUMBER)).thenReturn(worker);
		setupLaneAssociation(LaneType.LANE_2, ApprovalStatus.PENDING);

		laneService.addUserToWorkerPool(COMPANY_ID, USER_NUMBER, WORKER_NUMBER);

		verify(laneService, times(1)).updateLaneAssociationApprovalStatus(WORKER_ID, COMPANY_ID, ApprovalStatus.APPROVED);
	}

	@Test
	public void getLaneContext_lane1Active() {
		when(userService.getUser(anyLong())).thenReturn(user);
		when(authenticationService.isLane1Active(user)).thenReturn(true);
		final LaneType laneType = LaneType.LANE_1;
		final ApprovalStatus approvalStatus = ApprovalStatus.APPROVED;
		LaneAssociation laneAssociation = mockLaneAssociation(laneType, approvalStatus);

		LaneContext laneContext = laneService.getLaneContext(COMPANY_ID, user, laneAssociation, true);

		assertEquals(laneType, laneContext.getLaneType());
		assertEquals(approvalStatus, laneContext.getApprovalStatus());
	}

	@Test
	public void getLaneContext_lane1Active_doNotRequireActiveAcct() {
		when(userService.getUser(anyLong())).thenReturn(user);
		when(authenticationService.isLane1Active(user)).thenReturn(false);
		final LaneType laneType = LaneType.LANE_1;
		final ApprovalStatus approvalStatus = ApprovalStatus.APPROVED;
		LaneAssociation laneAssociation = mockLaneAssociation(laneType, approvalStatus);

		LaneContext laneContext = laneService.getLaneContext(COMPANY_ID, user, laneAssociation, false);

		assertEquals(laneType, laneContext.getLaneType());
		assertEquals(approvalStatus, laneContext.getApprovalStatus());
	}

	@Test
	public void getLaneContext_lane0Approved() {
		when(userService.getUser(anyLong())).thenReturn(user);
		when(laneAssociationDAO.findActiveAssociationByUserIdAndCompanyId(anyLong(), anyLong())).thenReturn(null);

		LaneContext laneContext = laneService.getLaneContext(COMPANY_ID, user, null, true);

		assertEquals(LaneType.LANE_0, laneContext.getLaneType());
		assertEquals(ApprovalStatus.APPROVED, laneContext.getApprovalStatus());
	}

	@Test
	public void getLaneContext_lane2InActive_null() {
		when(userService.getUser(anyLong())).thenReturn(user);
		when(authenticationService.isLane2Active(user)).thenReturn(false);
		setupUserWithCompanyId(OTHER_COMPANY_ID);
		LaneAssociation laneAssociation = mockLaneAssociation(LaneType.LANE_2, ApprovalStatus.APPROVED);

		assertNull(laneService.getLaneContext(COMPANY_ID, user, laneAssociation, true));
	}

	@Test
	public void getLaneContext_lane2Active_doNotRequireActiveAcct() {
		when(userService.getUser(anyLong())).thenReturn(user);
		when(authenticationService.isLane2Active(user)).thenReturn(false);
		setupUserWithCompanyId(OTHER_COMPANY_ID);
		LaneAssociation laneAssociation = mockLaneAssociation(LaneType.LANE_2, ApprovalStatus.APPROVED);

		final LaneContext laneContext = laneService.getLaneContext(COMPANY_ID, user, laneAssociation, false);
		assertEquals(LaneType.LANE_2, laneContext.getLaneType());
		assertEquals(ApprovalStatus.APPROVED, laneContext.getApprovalStatus());
	}

	@Test
	public void getLaneContext_lane3InActive_null() {
		when(userService.getUser(anyLong())).thenReturn(user);
		when(authenticationService.isLane3Active(user)).thenReturn(false);
		setupUserWithCompanyId(OTHER_COMPANY_ID);
		LaneAssociation laneAssociation = mockLaneAssociation(LaneType.LANE_3, ApprovalStatus.APPROVED);

		assertNull(laneService.getLaneContext(COMPANY_ID, user, laneAssociation, true));
	}

	@Test
	public void getLaneContext_lane4Active() {
		setupUserWithCompanyId(OTHER_COMPANY_ID);
		when(authenticationService.isLane4Active(user)).thenReturn(true);

		LaneContext laneContext = laneService.getLaneContext(COMPANY_ID, user, null, true);

		assertEquals(LaneType.LANE_4, laneContext.getLaneType());
		assertEquals(ApprovalStatus.APPROVED, laneContext.getApprovalStatus());
	}

	@Test
	public void getLaneContext_lane4Pending() {
		setupUserWithCompanyId(OTHER_COMPANY_ID);
		when(authenticationService.isLane4Active(user)).thenReturn(false);

		LaneContext laneContext = laneService.getLaneContext(COMPANY_ID, user, null, true);

		assertEquals(LaneType.LANE_4, laneContext.getLaneType());
		assertEquals(ApprovalStatus.PENDING, laneContext.getApprovalStatus());
	}

	@Test
	public void addUserToLane_logsUserLaneAdded() {
		setupUserWithCompanyId(OTHER_COMPANY_ID);
		setupAuthenticationServiceForUser(USER_ID, MASQUERADE_USER_ID);

		laneService.addUserToLane(WORKER_ID, COMPANY_ID, LaneType.LANE_3);

		verify(userChangeLogService, times(1)).createChangeLog(isA(UserLaneAddedChangeLog.class));
	}

	@Test
	public void removeUserFromCompanyLane_logsUserLaneRemoved() {
		setupAuthenticationServiceForUser(USER_ID, MASQUERADE_USER_ID);
		setupLaneAssociation(LaneType.LANE_3, ApprovalStatus.APPROVED);
		when(workService.doesWorkerHaveWorkWithCompany(anyLong(), anyLong(), anyListOf(String.class))).thenReturn(false);

		laneService.removeUserFromCompanyLane(WORKER_ID, COMPANY_ID);

		verify(userChangeLogService, times(1)).createChangeLog(isA(UserLaneRemovedChangeLog.class));
	}

	@Test
	public void updateUserCompanyLaneAssociation_sameLaneType_noUserChangeLogs() {
		setupLaneAssociation(LaneType.LANE_3, ApprovalStatus.APPROVED);
		setupAuthenticationServiceForUser(USER_ID, MASQUERADE_USER_ID);

		laneService.updateUserCompanyLaneAssociation(WORKER_ID, COMPANY_ID, LaneType.LANE_3);

		verifyZeroInteractions(userChangeLogService);
	}

	@Test
	public void updateUserCompanyLaneAssociation_differentLaneType_logsUserLaneRemovedAndLaneAdded() {
		setupLaneAssociation(LaneType.LANE_2, ApprovalStatus.APPROVED);
		setupAuthenticationServiceForUser(USER_ID, MASQUERADE_USER_ID);

		laneService.updateUserCompanyLaneAssociation(WORKER_ID, COMPANY_ID, LaneType.LANE_3);

		verify(userChangeLogService, times(1)).createChangeLog(isA(UserLaneRemovedChangeLog.class));
		verify(userChangeLogService, times(1)).createChangeLog(isA(UserLaneAddedChangeLog.class));
	}

	private void setupUserWithCompanyId(final long companyId) {
		Company company = mock(Company.class);
		when(company.getId()).thenReturn(companyId);
		when(user.getCompany()).thenReturn(company);
		when(userService.getUser(anyLong())).thenReturn(user);
	}

	private void setupLaneAssociation(final LaneType laneType, final ApprovalStatus approvalStatus) {
		LaneAssociation laneAssociation = mockLaneAssociation(laneType, approvalStatus);
		when(laneAssociationDAO.findActiveAssociationByUserIdAndCompanyId(anyLong(), anyLong())).thenReturn(laneAssociation);
	}

	private void setupAuthenticationServiceForUser(final Long userId, final Long masqueradeUserId) {
		when(authenticationService.getCurrentUserId()).thenReturn(userId);
		when(authenticationService.getMasqueradeUserId()).thenReturn(masqueradeUserId);
	}

	private LaneAssociation mockLaneAssociation(final LaneType laneType, final ApprovalStatus approvalStatus) {
		LaneAssociation laneAssociation = mock(LaneAssociation.class);
		when(laneAssociation.getDeleted()).thenReturn(false);
		when(laneAssociation.getLaneType()).thenReturn(laneType);
		when(laneAssociation.getApprovalStatus()).thenReturn(approvalStatus);
		when(laneAssociation.getUser()).thenReturn(worker);
		return laneAssociation;
	}

}
