package com.workmarket.domains.work.service.workresource;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.report.kpi.KpiDAO;
import com.workmarket.dao.summary.work.WorkResourceHistorySummaryDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.dao.WorkResourceLabelDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.WorkResourceFeedbackPagination;
import com.workmarket.domains.work.model.WorkResourceLabel;
import com.workmarket.domains.work.model.WorkResourceLabelType;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.analytics.cache.ScorecardCache;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.summary.SummaryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkResourceServiceTest {

	@Mock private LookupEntityDAO lookupEntityDAO;
	@Mock private WorkResourceDAO workResourceDAO;
	@Mock private WorkResourceDetailCache workResourceDetailCache;
	@Mock private WorkResourceHistorySummaryDAO workResourceHistorySummaryDAO;
	@Mock private WorkResourceLabelDAO workResourceLabelDAO;
	@Mock private AuthenticationService authenticationService;
	@Mock private SummaryService summaryService;
	@Mock private KpiDAO kpiDAO;
	@Mock private WorkService workService;
	@Mock private UserService userService;
	@Mock private ScorecardCache scorecardCache;
	@Mock private EventRouter eventRouter;
	@Mock private WorkBundleService workBundleService;
	@Mock private UserRoleService userRoleService;

	@InjectMocks WorkResourceServiceImpl workResourceService;

	private WorkBundle workBundle;
	private Work work, work2;
	private User user, worker;
	private Company company;
	private PersonaPreference personaPreference;
	private Optional<PersonaPreference> personaPreferenceOptional;
	private WorkResource workResource;
	private CloseWorkDTO closeWorKDTO;
	private WorkResourceLabel workResourceLabelLate, workResourceLabelLateDeliverable, workResourceLabelCompletedOnTime;
	private WorkResourceLabelType workResourceLabelTypeLate, workResourceLabelTypeLateDeliverable, workResourceLabelTypeCompletedOnTime;

	private static Long USER_ID = 1L, WORKER_ID = 2L, WORK_ID = 2L, WORK_RESOURCE_ID = 2L;

	@Before
	public void setUp() throws Exception {
		workBundle = mock(WorkBundle.class);
		work = mock(Work.class);
		work2 = mock(Work.class);
		user = mock(User.class);
		worker = mock(User.class);
		company = mock(Company.class);
		personaPreference = new PersonaPreference();
		personaPreferenceOptional = Optional.of(personaPreference);
		workResource = mock(WorkResource.class);
		closeWorKDTO = mock(CloseWorkDTO.class);
		workResourceLabelLate = mock(WorkResourceLabel.class);
		workResourceLabelTypeLate = mock(WorkResourceLabelType.class);
		workResourceLabelLateDeliverable = mock(WorkResourceLabel.class);
		workResourceLabelTypeLateDeliverable = mock(WorkResourceLabelType.class);
		workResourceLabelCompletedOnTime = mock(WorkResourceLabel.class);
		workResourceLabelTypeCompletedOnTime = mock(WorkResourceLabelType.class);

		when(workBundle.getId()).thenReturn(1L);
		when(work.getId()).thenReturn(WORK_ID);
		when(work2.getId()).thenReturn(3L);
		when(work.isOpenable()).thenReturn(true);
		when(work.isRoutable()).thenReturn(true);
		when(user.getId()).thenReturn(USER_ID);
		when(worker.getId()).thenReturn(WORKER_ID);
		when(company.getId()).thenReturn(32343L);
		when(user.getCompany()).thenReturn(company);
		when(authenticationService.getCurrentUser()).thenReturn(user);
		when(authenticationService.getCurrentUserId()).thenReturn(USER_ID);
		when(workResourceDAO.findResourceFeedbackForUserVisibleToUserAtCompany(anyLong(), anyLong(), anyLong(), any(WorkResourceFeedbackPagination.class))).thenReturn(new WorkResourceFeedbackPagination());
		when(workService.findWork(1L)).thenReturn(workBundle);
		when(workService.findWork(2L)).thenReturn(work);
		when(workService.findWork(3L)).thenReturn(work2);
		when(workResourceDAO.findById(WORK_RESOURCE_ID)).thenReturn(workResource);
		when(workResource.getWork()).thenReturn(work);
		when(workResource.getId()).thenReturn(WORK_RESOURCE_ID);
		when(workService.findActiveWorkerId(anyLong())).thenReturn(USER_ID);
		when(workService.findWorkResource(anyLong(), anyLong())).thenReturn(workResource);
		when(userService.getPersonaPreference(user.getId())).thenReturn(personaPreferenceOptional);
		when(closeWorKDTO.isArrivedOnTime()).thenReturn(true);
		when(closeWorKDTO.isCompletedOnTime()).thenReturn(true);

		// Late Label
		when(workResourceLabelTypeLate.getCode()).thenReturn(WorkResourceLabelType.LATE);
		when(workResourceLabelLate.getWorkResourceLabelType()).thenReturn(workResourceLabelTypeLate);
		when(workResourceLabelLate.isIgnored()).thenReturn(false);
		when(workResourceLabelDAO.findByLabelCodeAndWorkResourceId(WorkResourceLabelType.LATE, WORK_RESOURCE_ID)).thenReturn(workResourceLabelLate);
		when(workResourceLabelLate.getWorkResourceId()).thenReturn(WORK_RESOURCE_ID);

		// Completed-On Time Label
		when(workResourceLabelTypeCompletedOnTime.getCode()).thenReturn(WorkResourceLabelType.COMPLETED_ONTIME);
		when(workResourceLabelCompletedOnTime.getWorkResourceLabelType()).thenReturn(workResourceLabelTypeCompletedOnTime);
		when(workResourceLabelCompletedOnTime.isIgnored()).thenReturn(false);
		when(workResourceLabelDAO.findByLabelCodeAndWorkResourceId(WorkResourceLabelType.COMPLETED_ONTIME, WORK_RESOURCE_ID)).thenReturn(workResourceLabelCompletedOnTime);
		when(workResourceLabelCompletedOnTime.getWorkResourceId()).thenReturn(WORK_RESOURCE_ID);

		// Deliverable Late Deliverable
		when(workResourceLabelTypeLateDeliverable.getCode()).thenReturn(WorkResourceLabelType.LATE_DELIVERABLE);
		when(workResourceLabelLateDeliverable.getWorkResourceLabelType()).thenReturn(workResourceLabelTypeLateDeliverable);
		when(workResourceLabelLateDeliverable.isIgnored()).thenReturn(false);
		when(workResourceLabelDAO.findByLabelCodeAndWorkResourceId(WorkResourceLabelType.LATE_DELIVERABLE, WORK_RESOURCE_ID)).thenReturn(workResourceLabelLateDeliverable);
		when(workResourceLabelLateDeliverable.getWorkResourceId()).thenReturn(WORK_RESOURCE_ID);
	}

	@Test
	public void saveAllResourcesFromWorkToWork_withWorkBundleAndNoResources_doNothing() {
		workResourceService.saveAllResourcesFromWorkToWork(workBundle.getId(), work.getId());

		verify(workResourceDAO).findResourcesInFromWorkNotInToWork(eq(1L), anyLong());
		verify(workResourceDAO, never()).saveAll(anyListOf(WorkResource.class));
	}

	@Test
	public void saveAllResourcesFromWorkToWork_withWorkAndNoResources_doNothing() {
		workResourceService.saveAllResourcesFromWorkToWork(work2.getId(), work.getId());

		verify(workResourceDAO).findResourcesInFromWorkNotInToWork(eq(3L), anyLong());
		verify(workResourceDAO, never()).saveAll(anyListOf(WorkResource.class));
	}

	@Test
	public void saveAllResourcesFromWorkToWork_success() {
		WorkResource workResource1 = mock(WorkResource.class);
		WorkResource workResource2 = mock(WorkResource.class);
		WorkResource workResource3 = mock(WorkResource.class);

		User user1 = mock(User.class);
		User user2 = mock(User.class);
		User user3 = mock(User.class);

		when(user1.getId()).thenReturn(1L);
		when(user2.getId()).thenReturn(2L);
		when(user3.getId()).thenReturn(3L);

		when(workResource1.getUser()).thenReturn(user1);
		when(workResource2.getUser()).thenReturn(user2);
		when(workResource3.getUser()).thenReturn(user3);

		when(workResource1.getScore()).thenReturn(27);
		when(workResource2.getScore()).thenReturn(45);
		when(workResource3.getScore()).thenReturn(45);

		List<WorkResource> existingWorkResources = Lists.newArrayList();
		existingWorkResources.add(workResource1);
		existingWorkResources.add(workResource2);
		existingWorkResources.add(workResource3);

		when(workResourceDAO.findResourcesInFromWorkNotInToWork(eq(3L), anyLong())).thenReturn(existingWorkResources);
		Set<WorkResource> saveWorkResources = workResourceService.saveAllResourcesFromWorkToWork(work2.getId(), work.getId());
		verify(workResourceDAO).findResourcesInFromWorkNotInToWork(eq(3L), anyLong());
		verify(workResourceDAO).saveAll(anyListOf(WorkResource.class));
		assertNotNull(saveWorkResources);
		assertTrue(saveWorkResources.size() == 3);
 	}

	@Test
	public void saveAll_withNullArguments_returnsEmptySet() {
		assertTrue(workResourceService.saveAll(null).isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void findAllResourcesForWork_withNullArguments_fail() {
		workResourceService.findAllResourcesForWork(null);
	}

	@Test
	public void findAllResourcesForWork_success() {
		workResourceService.findAllResourcesForWork(1L);
		verify(workResourceDAO).findAllResourcesForWork(eq(1L));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getAllWorkIdsByWorkResourceUserIdAndStatus_withNullArguments_fail() {
		workResourceService.getAllWorkIdsByWorkResourceUserIdAndStatus(1L, null);
	}

	@Test
	public void getAllWorkIdsByWorkResourceUserIdAndStatus_success() {
		workResourceService.getAllWorkIdsByWorkResourceUserIdAndStatus(1L, new WorkResourceStatusType(WorkResourceStatusType.ACCEPTED));
		verify(workResourceHistorySummaryDAO).getAllWorkIdsByWorkResourceUserIdAndStatus(anyLong(), any(WorkResourceStatusType.class));
	}

	@Test
	public void findUserIdsNotDeclinedForWork_success() {
		workResourceService.findUserIdsNotDeclinedForWork(1L);
		verify(workResourceDAO).findUserIdsNotDeclinedForWork(eq(1L));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findWorkResourceById_withNullArguments_fail() {
		workResourceService.findWorkResourceById(null);
	}

	@Test
	public void findWorkResourceById_success() {
		workResourceService.findWorkResourceById(1L);
		verify(workResourceDAO).findById(eq(1L));
	}

	@Test
	public void findActiveWorkResource_success() {
		workResourceService.findActiveWorkResource(1L);
		verify(workResourceDAO).findActiveWorkResource(eq(1L));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findResourceFeedbackForUser_withNullArguments_fail() {
		workResourceService.findResourceFeedbackForUser(null, null);
	}

	@Test
	public void findResourceFeedbackForUser_success() {
		workResourceService.findResourceFeedbackForUser(1L, new WorkResourceFeedbackPagination());
		verify(workResourceDAO).findResourceFeedbackForUserVisibleToUserAtCompany(anyLong(), anyLong(), anyLong(), any(WorkResourceFeedbackPagination.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void confirmWorkResourceLabel_withNullArguments_fail() {
		workResourceService.confirmWorkResourceLabel(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getAllDispatcherIdsForWorker_nullWorkerId_throwException() {
		workResourceService.getAllDispatcherIdsForWorker(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getDispatcherIdForWorkAndWorker_nullWorkId_throwException() {
		workResourceService.getDispatcherIdForWorkAndWorker(null, 1L);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getDispatcherIdForWorkAndWorker_nullWorkerId_throwException() {
		workResourceService.getDispatcherIdForWorkAndWorker(1L, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setDispatcherForWorkAndWorker_nullWorkId_throwException() {
		workResourceService.setDispatcherForWorkAndWorker(null, 1L);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setDispatcherForWorkAndWorker_nullWorkerId_throwException() {
		workResourceService.setDispatcherForWorkAndWorker(1L, null);
	}

	@Test
	public void setDispatcherForWorkAndWorker_currentUserIsWorker_doNotFetchPersonaPreference() {
		when(authenticationService.getCurrentUserId()).thenReturn(WORKER_ID);

		workResourceService.setDispatcherForWorkAndWorker(work.getId(), worker.getId());

		verify(userService, never()).getPersonaPreference(anyLong());
	}

	@Test
	public void setDispatcherForWorkAndWorker_currentUserIsNotDispatcher_doNotSetDispatcher() {
		personaPreference.setDispatcher(false);

		workResourceService.setDispatcherForWorkAndWorker(work.getId(), worker.getId());

		verify(workResourceDAO, never()).setDispatcherForWorkAndWorker(USER_ID, WORK_ID, WORKER_ID);
	}

	@Test
	public void setDispatcherForWorkAndWorker_currentUserIsDispatcher_doNotSetDispatcher() {
		personaPreference.setDispatcher(true);

		workResourceService.setDispatcherForWorkAndWorker(work.getId(), worker.getId());

		verify(workResourceDAO).setDispatcherForWorkAndWorker(USER_ID, WORK_ID, WORKER_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void onPostTransitionToClosed_nullCloseWorkDTO_exceptionThrown() {
		when(workService.findActiveWorkerId(anyLong())).thenReturn(null);

		workResourceService.onPostTransitionToClosed(USER_ID, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void onPostTransitionToClosed_nullActiveWorkResource_exceptionThrown() {
		when(workService.findWorkResource(anyLong(), anyLong())).thenReturn(null);

		workResourceService.onPostTransitionToClosed(USER_ID, closeWorKDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void onPostTransitionToClosed_nullActiveWorkResourceId_exceptionThrown() {
		when(workResource.getId()).thenReturn(null);

		workResourceService.onPostTransitionToClosed(USER_ID, closeWorKDTO);
	}

	@Test
	public void onPostTransitionToClosed_workerArrivedOnTime_lateWorkResourceLabelIgnored() {
		workResourceService.onPostTransitionToClosed(USER_ID, closeWorKDTO);

		verify(workResourceLabelLate).ignore(user);
	}

	@Test
	public void onPostTransitionToClosed_deliverablesWereSubmittedOnTime_lateDeliverableWorkResourceLabelIgnored() {
		workResourceService.onPostTransitionToClosed(USER_ID, closeWorKDTO);

		verify(workResourceLabelLateDeliverable).ignore(user);
	}

	@Test
	public void removeAutoAssign_setsAssignToFirstToAccept_toFalse() {
		WorkResource workResource = new WorkResource();
		workResource.setAssignToFirstToAccept(true);
		when(workService.findWorkResource(anyLong(), anyLong())).thenReturn(workResource);

		workResourceService.removeAutoAssign(WORKER_ID, WORK_ID);

		assertFalse(workResource.isAssignToFirstToAccept());
	}

	@Test
	public void removeAutoAssign_evictsWorkResourceFromCache() {
		workResourceService.removeAutoAssign(WORKER_ID, WORK_ID);

		verify(workResourceDetailCache).evict(WORK_ID);
	}
}
