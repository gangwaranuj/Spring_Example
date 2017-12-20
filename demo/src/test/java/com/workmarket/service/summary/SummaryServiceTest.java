package com.workmarket.service.summary;

import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.InvitationDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.dao.profile.ProfileDAO;
import com.workmarket.dao.recruiting.RecruitingCampaignDAO;
import com.workmarket.domains.payments.dao.AccountStatementDetailDAO;
import com.workmarket.dao.summary.HistorySummaryEntityDAO;
import com.workmarket.dao.summary.TimeDimensionDAO;
import com.workmarket.dao.summary.company.CompanySummaryDAO;
import com.workmarket.dao.summary.group.UserGroupSummaryDAO;
import com.workmarket.dao.summary.user.UserSummaryDAO;
import com.workmarket.dao.summary.work.WorkHistorySummaryDAO;
import com.workmarket.dao.summary.work.WorkStatusTransitionDAO;
import com.workmarket.data.solr.configuration.UserIndexerConfiguration;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupThroughputDTO;
import com.workmarket.domains.model.summary.company.CompanySummary;
import com.workmarket.domains.model.summary.group.UserGroupSummary;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.RecruitingService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.event.user.UserAverageRatingEvent;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.Calendar;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyBoolean;

@RunWith(MockitoJUnitRunner.class)
public class SummaryServiceTest {

	@Mock private CompanyDAO companyDAO;
	@Mock private InvitationDAO invitationDAO;
	@Mock private UserDAO userDAO;
	@Mock private WorkDAO workDAO;
	@Mock private RecruitingCampaignDAO recruitingCampaignDAO;
	@Mock private ProfileDAO profileDAO;
	@Qualifier("accountRegisterServicePrefundImpl")
	@Mock private AccountRegisterService accountRegisterServiceNetMoneyImpl;
	@Mock private RegistrationService registrationService;
	@Mock private RecruitingService recruitingService;
	@Mock private UserGroupService userGroupService;
	@Mock private WorkService workService;
	@Mock private CompanyService companyService;
	@Mock private AssessmentService assessmentService;
	@Mock private TimeDimensionDAO timeDimensionDAO;
	@Mock private AuthenticationService authenticationService;
	@Mock private WorkStatusTransitionDAO workStatusTransitionDAO;
	@Mock private PricingService pricingService;
	@Mock private AccountStatementDetailDAO accountStatementDetailDAO;
	@Mock private CompanySummaryDAO companySummaryDAO;
	@Mock private HistorySummaryEntityDAO historySummaryEntityDAO;
	@Mock private WorkHistorySummaryDAO workHistorySummaryDAO;
	@Mock private UserSummaryDAO userSummaryDAO;
	@Mock private UserGroupSummaryDAO userGroupSummaryDAO;
	@Mock private EventRouter eventRouter;
	@Mock private IndustryService industryService;
	@InjectMocks SummaryServiceImpl summaryService;

	private static final Long EXISTENT_USER_GROUP_SUMMARY_ID = 1L;
	private static final Long USER_GROUP_ID_FOR_EXISTENT_SUMMARY = 2L;
	private static final Long USER_GROUP_ID_FOR_NONEXISTENT_SUMMARY = 3L;

	private Company company, company2;
	private Work work;
	private User user, user2;
	private Rating rating;
	private Profile profile;
	private UserGroupSummary existentUserGroupSummary;
	private UserGroupThroughputDTO userGroupThroughputDTO;

	@Before
	public void setUp() throws Exception {
		company = mock(Company.class);
		work = mock(Work.class);
		user = mock(User.class);

		when(companyDAO.findById(anyLong())).thenReturn(company);
		when(userDAO.get(anyLong())).thenReturn(user);
		when(user.getCompany()).thenReturn(company);
		when(work.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(1L);
		when(work.getScheduleFrom()).thenReturn(Calendar.getInstance());
		when(workHistorySummaryDAO.countWork(anyLong(), anyString(), any(DateRange.class))).thenReturn(10);
		when(workHistorySummaryDAO.countWorkWithLatePayment(anyLong(), any(DateRange.class))).thenReturn(5);

		rating = mock(Rating.class);
		user2 = mock(User.class);
		company2 = mock(Company.class);
		profile = mock(Profile.class);
		when(user2.getCompany()).thenReturn(company2);
		when(rating.getRatedUser()).thenReturn(user);
		when(rating.getRatingUser()).thenReturn(user2);
		when(user.getProfile()).thenReturn(profile);

		existentUserGroupSummary = mock(UserGroupSummary.class);
		when(existentUserGroupSummary.getId()).thenReturn(EXISTENT_USER_GROUP_SUMMARY_ID);
		when(existentUserGroupSummary.getTotalThroughput()).thenReturn(BigDecimal.ZERO);
		UserGroup userGroup = mock(UserGroup.class);
		when(userGroup.getId()).thenReturn(USER_GROUP_ID_FOR_EXISTENT_SUMMARY);
		when(userGroupService.findUserGroupSummaryByUserGroup(USER_GROUP_ID_FOR_EXISTENT_SUMMARY)).thenReturn(existentUserGroupSummary);

		userGroupThroughputDTO = mock(UserGroupThroughputDTO.class);
		when(userGroupThroughputDTO.getUserGroupId()).thenReturn(USER_GROUP_ID_FOR_NONEXISTENT_SUMMARY);
	}

	@Test
	public void updateCompanySummary_withNewCompany_success() throws Exception {
		when(companySummaryDAO.findByCompany(anyLong())).thenReturn(null);
		CompanySummary companySummary = summaryService.updateCompanySummary(1L);
		assertNotNull(companySummary);
		assertTrue(companySummary.getTotalLatePaidAssignments() == 5);
		assertTrue(companySummary.getTotalPaidAssignments() == 10);
		verify(workHistorySummaryDAO, times(1)).countWork(anyLong(), eq(WorkStatusType.PAID), any(DateRange.class));
		verify(workHistorySummaryDAO, times(1)).countWork(anyLong(), eq(WorkStatusType.DRAFT), any(DateRange.class));
		verify(workHistorySummaryDAO, times(1)).countWorkWithLatePayment(anyLong(), any(DateRange.class));
		verify(workHistorySummaryDAO, times(1)).countWork(anyLong(), eq(WorkStatusType.CANCELLED), any(DateRange.class));
	}

	@Test
	public void saveWorkStatusTransitionHistorySummary_withWorkAndStatuses_success() throws Exception {
		assertNotNull(summaryService.saveWorkStatusTransitionHistorySummary(work, new WorkStatusType(WorkStatusType.DRAFT), new WorkStatusType(WorkStatusType.SENT), 0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveWorkStatusTransitionHistorySummary_withNullWorkAndStatuses_fail() throws Exception {
		assertNotNull(summaryService.saveWorkStatusTransitionHistorySummary(null, null, null, 0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveWorkStatusTransitionHistorySummary_withNullWork_fail() throws Exception {
		assertNotNull(summaryService.saveWorkStatusTransitionHistorySummary(null, new WorkStatusType(WorkStatusType.DRAFT), new WorkStatusType(WorkStatusType.SENT), 0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveWorkStatusTransitionHistorySummary_withNullStatuses_fail() throws Exception {
		assertNotNull(summaryService.saveWorkStatusTransitionHistorySummary(work, null, null, 0));
	}

	@Test
	public void saveWorkStatusTransitionHistorySummary_withSent() throws Exception {
		assertNotNull(summaryService.saveWorkStatusTransitionHistorySummary(work, new WorkStatusType(WorkStatusType.DRAFT), new WorkStatusType(WorkStatusType.SENT), 0));
		verify(historySummaryEntityDAO, times(2)).saveOrUpdate(any(HistorySummaryEntityDAO.class));
	}

	@Test
	public void saveWorkStatusTransitionHistorySummary_withComplete() throws Exception {
		assertNotNull(summaryService.saveWorkStatusTransitionHistorySummary(work, new WorkStatusType(WorkStatusType.ACTIVE), new WorkStatusType(WorkStatusType.COMPLETE), 0));
		verify(historySummaryEntityDAO, times(2)).saveOrUpdate(any(HistorySummaryEntityDAO.class));
	}

	@Test
	public void findAllUsersWithLastAssignedDateBetweenDates_success() {
		summaryService.findAllUsersWithLastAssignedDateBetweenDates(UserIndexerConfiguration.getOnTimePercentageThresholdDate(), Calendar.getInstance());
		verify(userSummaryDAO).findAllUsersWithLastAssignedDateBetweenDates(any(Calendar.class), any(Calendar.class));
	}

	@Test
	public void getTotalUpcomingDueIn24Hours() {
		summaryService.getTotalUpcomingDueIn24Hours(1L);
		when(authenticationService.hasAccessToAllInvoicesAndStatementsAtCompany(any(User.class))).thenReturn(true);
		verify(accountStatementDetailDAO).sumTotalUpcomingDueIn24Hours(anyLong(), anyLong(), anyBoolean(), anyBoolean());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateUserGroupSummary_WithUserGroupThroughputDTO_Null() {
		UserGroupThroughputDTO dto = null;
		summaryService.updateUserGroupSummary(dto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateUserGroupSummary_WithUserGroupThroughputDTO_UserGroupIdNull() {
		when(userGroupThroughputDTO.getUserGroupId()).thenReturn(null);
		summaryService.updateUserGroupSummary(userGroupThroughputDTO);
	}


	@Test
	public void updateUserGroupSummary_WithUserGroupThroughputDTO_ThroughputNull() {
		summaryService.updateUserGroupSummary(userGroupThroughputDTO);
		verify(userGroupService, never()).findUserGroupSummaryByUserGroup(any(Long.class));
	}

	@Test
	public void updateUserGroupSummary_WithUserGroupThroughputDTO_ThroughputZero() {
		when(userGroupThroughputDTO.getThroughput()).thenReturn(BigDecimal.ZERO);

		summaryService.updateUserGroupSummary(userGroupThroughputDTO);
		verify(userGroupService, never()).findUserGroupSummaryByUserGroup(any(Long.class));
	}

	@Test
	public void updateUserGroupSummary_WithUserGroupThroughputDTO_NonexistentUserGroupSummary() {
		when(userGroupThroughputDTO.getThroughput()).thenReturn(new BigDecimal(20));

		summaryService.updateUserGroupSummary(userGroupThroughputDTO);

		ArgumentCaptor<UserGroupSummary> ugsCaptor = ArgumentCaptor.forClass(UserGroupSummary.class);
		verify(userGroupSummaryDAO).saveOrUpdate(ugsCaptor.capture());

		UserGroupSummary ugs = ugsCaptor.getValue();
		assertThat(ugs.getId(), is(nullValue()));
	}

	@Test
	public void updateUserGroupSummary_WithUserGroupThroughputDTO_ExistentUserGroupSummary() {
		when(userGroupThroughputDTO.getThroughput()).thenReturn(new BigDecimal(20));
		when(userGroupThroughputDTO.getUserGroupId()).thenReturn(USER_GROUP_ID_FOR_EXISTENT_SUMMARY);

		summaryService.updateUserGroupSummary(userGroupThroughputDTO);

		ArgumentCaptor<UserGroupSummary> ugsCaptor = ArgumentCaptor.forClass(UserGroupSummary.class);
		verify(userGroupSummaryDAO).saveOrUpdate(ugsCaptor.capture());

		UserGroupSummary ugs = ugsCaptor.getValue();
		assertThat(ugs.getId(), is(EXISTENT_USER_GROUP_SUMMARY_ID));
	}

	@Test
	public void saveUserRatingHistorySummary_verifyUserAverageRatingEventIsSent() {
		summaryService.saveUserRatingHistorySummary(rating);
		ArgumentCaptor<UserAverageRatingEvent> captor = ArgumentCaptor.forClass(UserAverageRatingEvent.class);

		verify(eventRouter).sendEvent(captor.capture());

		assertThat(captor.getValue().getRatedUserId(), is(rating.getRatedUser().getId()));
		assertThat(captor.getValue().getRaterCompanyId(), is(rating.getRatingUser().getId()));
	}
}
