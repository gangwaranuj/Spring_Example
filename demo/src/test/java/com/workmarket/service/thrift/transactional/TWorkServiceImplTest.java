package com.workmarket.service.thrift.transactional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.common.template.BulkUploadFailedNotificationTemplate;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.velvetrope.doorman.AssignmentsDoorman;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.service.WorkActionRequestFactory;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.dto.AssessmentDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.service.thrift.TWorkUploadService;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.RoutingStrategy;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.thrift.work.uploader.WorkUploadException;
import com.workmarket.thrift.work.uploader.WorkUploadInvalidFileTypeException;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;
import com.workmarket.thrift.work.uploader.WorkUploadResponse;
import com.workmarket.thrift.work.uploader.WorkUploadRowLimitExceededException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.doNothing;

@RunWith(MockitoJUnitRunner.class)
public class TWorkServiceImplTest {

	@Mock AuthenticationService authenticationService;
	@Mock TWorkUploadService uploader;
	@Mock NotificationDispatcher notificationDispatcher;
	@Mock NotificationTemplateFactory notificationTemplateFactory;
	@Mock AssessmentService assessmentService;
	@Mock WorkAuditService workAuditService;
	@Mock WorkActionRequestFactory workActionRequestFactory;
	@Mock AssignmentsDoorman assignmentsDoorman;
	@Mock WorkService workService;
	@InjectMocks TWorkServiceImpl tWorkService = spy(new TWorkServiceImpl());

	WorkUploadResponse workUploadResponse = mock(WorkUploadResponse.class);
	BulkUploadFailedNotificationTemplate notificationTemplate = mock(BulkUploadFailedNotificationTemplate.class);
	WorkCustomFieldDAO customFieldDAO = mock(WorkCustomFieldDAO.class);
	WorkSaveRequest workSaveRequest;
	Work work;
	AssessmentDTO assessmentDTO;
	List<Assessment> assessments;
	Assessment assessment;
	RoutingStrategy routingStrategy;
	PeopleSearchRequest peopleSearchRequest;
	Set<Long> groupIds = ImmutableSet.of(GROUP_ID);

	private static long WORK_ID = 1L, ASSESMENT_ID = 2L, GROUP_ID = 3L;

	@Before
	public void setUp() throws WorkUploadInvalidFileTypeException, WorkUploadRowLimitExceededException, WorkUploadException, HostServiceException {
		when(uploader.uploadWork(any(WorkUploadRequest.class))).thenReturn(workUploadResponse);
		doNothing().when(authenticationService).setCurrentUser(anyLong());
		//noinspection unchecked
		when(notificationTemplateFactory.buildBulkUploadFailedNotificationTemplate(anyLong(), anyList())).thenReturn(notificationTemplate);
		when(workUploadResponse.getErrorUploadsSize()).thenReturn(0);

		peopleSearchRequest = mock(PeopleSearchRequest.class);
		when(peopleSearchRequest.getGroupFilter()).thenReturn(groupIds);

		routingStrategy = mock(RoutingStrategy.class);
		when(routingStrategy.getFilter()).thenReturn(peopleSearchRequest);

		work = mock(Work.class);
		when(work.isSetAssessments()).thenReturn(true);

		workSaveRequest = mock(WorkSaveRequest.class);
		when(workSaveRequest.getWork()).thenReturn(work);
		when(workSaveRequest.getRoutingStrategies()).thenReturn(ImmutableList.of(routingStrategy));

		assessments = Lists.newArrayList();
		assessment = mock(Assessment.class);
		when(assessment.getId()).thenReturn(ASSESMENT_ID);
		when(assessment.isSetId()).thenReturn(true);
		when(assessment.isIsRequired()).thenReturn(true);
		assessments.add(assessment);

		when(work.getAssessmentsSize()).thenReturn(1);
		when(work.getAssessments()).thenReturn(assessments);

		assessmentDTO = mock(AssessmentDTO.class);
		doReturn(assessmentDTO).when(tWorkService).makeAssessmentDTO();
	}

	@Test
	public void startUploadEventHelper_calls_uploadWork() throws WorkUploadInvalidFileTypeException, WorkUploadRowLimitExceededException, WorkUploadException, HostServiceException {
		tWorkService.startUploadEventHelper(eq(new WorkUploadRequest("some user number", "some id", true)), anyLong());
		verify(uploader).uploadWork(any(WorkUploadRequest.class));
	}

	@Test
	public void startUploadEventHelper_error_notification_sent() throws Exception {
		when(workUploadResponse.getErrorUploadsSize()).thenReturn(1);
		tWorkService.startUploadEventHelper(eq(new WorkUploadRequest("some user number", "some id", true)), anyLong());
		verify(notificationDispatcher).dispatchNotification(any(NotificationTemplate.class));
	}

	@Test
	public void startUploadEventHelper_error_notification_notSent() {
		when(workUploadResponse.getErrorUploadsSize()).thenReturn(0);
		tWorkService.startUploadEventHelper(eq(new WorkUploadRequest("some user number", "some id", true)), anyLong());
		verifyZeroInteractions(notificationDispatcher);
	}

	@Test
	public void clearSavedCustomFieldDefaultValueOnTemplate_cleared() {
		WorkCustomField actualField = new WorkCustomField();
		actualField.setName("is this test working");
		actualField.setDefaultValue("yes");
		actualField.setId(1L);

		CustomField thriftField = new CustomField();
		thriftField.setId(1L);
		thriftField.setValue("yes");

		CustomFieldGroup group = new CustomFieldGroup();
		group.setId(1L);
		group.setFields(Lists.newArrayList(thriftField));

		WorkSaveRequest request = new WorkSaveRequest();
		Work work = new Work();
		work.setCustomFieldGroups(Lists.newArrayList(group));
		request.setWork(work);

		when(customFieldDAO.findAllFieldsForCustomFieldGroup(1L)).thenReturn(Lists.newArrayList(actualField));

		tWorkService.clearCustomFieldsIfDefaultValue(request);
		assertEquals(thriftField.getValue(), null);
	}

	@Test
	public void clearSavedCustomFieldDefaultValueOnTemplate_notCleared() {
		WorkCustomField actualField = new WorkCustomField();
		actualField.setName("is this test working");
		actualField.setDefaultValue("yes");
		actualField.setId(1L);

		CustomField thriftField = new CustomField();
		thriftField.setId(1L);
		thriftField.setValue("yes - overriding default value");

		CustomFieldGroup group = new CustomFieldGroup();
		group.setId(1L);
		group.setFields(Lists.newArrayList(thriftField));

		WorkSaveRequest request = new WorkSaveRequest();
		Work work = new Work();
		work.setCustomFieldGroups(Lists.newArrayList(group));
		request.setWork(work);

		when(customFieldDAO.findAllFieldsForCustomFieldGroup(1L)).thenReturn(Lists.newArrayList(actualField));

		tWorkService.clearCustomFieldsIfDefaultValue(request);
		assertEquals(thriftField.getValue(), "yes - overriding default value");
	}

	@Test
	public void clearSavedCustomFieldDefaultValueOnTemplate_null() {
		WorkCustomField actualField = new WorkCustomField();
		actualField.setName("is this test working");
		actualField.setDefaultValue("yes");
		actualField.setId(1L);

		CustomField thriftField = new CustomField();
		thriftField.setId(1L);
		thriftField.setValue("yes - overriding default value");

		CustomFieldGroup group = new CustomFieldGroup();
		group.setId(1L);
		group.setFields(Lists.newArrayList(thriftField));

		WorkSaveRequest request = new WorkSaveRequest();
		Work work = new Work();
		work.setCustomFieldGroups(Lists.newArrayList(group));
		request.setWork(work);

		when(customFieldDAO.findAllFieldsForCustomFieldGroup(1L)).thenReturn(null);

		tWorkService.clearCustomFieldsIfDefaultValue(request);
		assertEquals(thriftField.getValue(), "yes - overriding default value");
	}

	@Test
	public void clearSavedCustomFieldDefaultValueOnTemplate_emptyList() {
		WorkCustomField actualField = new WorkCustomField();
		actualField.setName("is this test working");
		actualField.setDefaultValue("yes");
		actualField.setId(1L);

		CustomField thriftField = new CustomField();
		thriftField.setId(1L);
		thriftField.setValue("yes - overriding default value");

		CustomFieldGroup group = new CustomFieldGroup();
		group.setId(1L);
		group.setFields(Lists.newArrayList(thriftField));

		WorkSaveRequest request = new WorkSaveRequest();
		Work work = new Work();
		work.setCustomFieldGroups(Lists.newArrayList(group));
		request.setWork(work);

		when(customFieldDAO.findAllFieldsForCustomFieldGroup(1L)).thenReturn(Lists.<WorkCustomField>newArrayList());

		tWorkService.clearCustomFieldsIfDefaultValue(request);
		assertEquals(thriftField.getValue(), "yes - overriding default value");
	}

	@Test
	public void saveAssessments_assessmentsNotSet_earlyReturn() {
		when(work.isSetAssessments()).thenReturn(false);

		tWorkService.saveAssessments(workSaveRequest, WORK_ID);

		verify(tWorkService, never()).makeAssessmentDTO();
	}

	@Test
	public void saveAssessments_assessmentIDNotSet_doNotSetAssessmentsForWork() {
		when(assessment.isSetId()).thenReturn(false);

		tWorkService.saveAssessments(workSaveRequest, WORK_ID);

		verify(assessmentService, never()).setAssessmentsForWork(anyListOf(AssessmentDTO.class), anyLong());
	}

	@Test
	public void saveAssessments_assessmentIDNotSet_doNotAuditAndReindexWork() {
		when(assessment.isSetId()).thenReturn(false);

		tWorkService.saveAssessments(workSaveRequest, WORK_ID);

		verify(workAuditService, never()).auditAndReindexWork(any(WorkActionRequest.class));
	}

	@Test
	public void saveAssessments_assessmentIDSet_copyPropertiesToDTO() {
		tWorkService.saveAssessments(workSaveRequest, WORK_ID);

		verify(assessmentDTO).setId(assessment.getId());
		verify(assessmentDTO).setRequired(assessment.isIsRequired());
	}

	@Test
	public void saveAssessments_assessmentIDSet_setAssessmentsForWork() {
		tWorkService.saveAssessments(workSaveRequest, WORK_ID);

		verify(assessmentService).setAssessmentsForWork(anyListOf(AssessmentDTO.class), eq(WORK_ID));
	}

	@Test
	public void saveAssessments_assessmentIDSet_auditAndReindexWork() {
		WorkActionRequest workActionRequest = mock(WorkActionRequest.class);
		when(workActionRequestFactory.create(WORK_ID, WorkAuditType.ADD_ASSESSMENT)).thenReturn(workActionRequest);

		tWorkService.saveAssessments(workSaveRequest, WORK_ID);

		verify(workAuditService).auditAndReindexWork(workActionRequest);
	}

	@Test
	public void saveGroups_withFirstToAcceptGroups_callsWorkService() {
		when(routingStrategy.isAssignToFirstToAccept()).thenReturn(true);

		tWorkService.saveGroups(workSaveRequest, WORK_ID);

		verify(workService).addFirstToAcceptGroupsForWork(groupIds, WORK_ID);
		verify(workService, never()).addGroupsForWork(anyCollection(), anyLong());
	}

	@Test
	public void saveGroups_withNeedToApplyGroups_callsWorkService() {
		when(routingStrategy.isAssignToFirstToAccept()).thenReturn(false);

		tWorkService.saveGroups(workSaveRequest, WORK_ID);

		verify(workService).addGroupsForWork(groupIds, WORK_ID);
		verify(workService, never()).addFirstToAcceptGroupsForWork(anyCollection(), anyLong());
	}
}
