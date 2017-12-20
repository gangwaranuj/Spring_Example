package com.workmarket.domains.work.service;

import com.google.common.collect.Lists;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.common.template.WorkDeliverableRejectedNotificationTemplate;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.asset.WorkAssetAssociationDAO;
import com.workmarket.dao.asset.WorkAssetVisibilityDAO;
import com.workmarket.domains.model.DeliverableRequirement;
import com.workmarket.domains.model.DeliverableRequirementGroup;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.work.dao.DeliverableRequirementDAO;
import com.workmarket.domains.work.dao.DeliverableRequirementGroupDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.AssetService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.notification.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeliverableServiceImplTest {

	@Mock AssetService assetService;
	@Mock DeliverableRequirementDAO deliverableRequirementDAO;
	@Mock DeliverableRequirementGroupDAO deliverableRequirementGroupDAO;
	@Mock UserDAO userDAO;
	@Mock WorkSubStatusService workSubStatusService;
	@Mock NotificationTemplateFactory notificationTemplateFactory;
	@Mock WorkAssetAssociationDAO workAssetAssociationDAO;
	@Mock LookupEntityDAO lookupEntityDAO;
	@Mock WorkAssetVisibilityDAO workAssetVisibilityDAO;
	@Mock NotificationService notificationService;
	@Mock AssetManagementService assetManagementService;
	@Mock WorkService workService;
	@Mock UserNotificationService userNotificationService;
	@Mock WebHookEventService webHookEventService;
	@Mock WorkResourceService workResourceService;
	@InjectMocks DeliverableServiceImpl deliverableService = spy(new DeliverableServiceImpl());

	int originalNumberOfFilesForRequirementOne = 1,
		newNumberOfFilesForRequirementOne = 2;
	Long
		deliverableRequirementGroupId = 0L,
		deliverableRequirementIdOne = 1L,
		deliverableRequirementIdTwo = 2L,
		userId = 99999L,
		dispatcherId = 3L,
		workId = 42L,
		assetId = 1L,
		workNumberForNonExistentAssignment = 123L,
		idForNonExistentDeliverableRequirementGroup = 123L;

	String
		deliverableRequirementOneType = "photos",
		deliverableRequirementTwoType = "sign_off",
		newDeliverableRequirementType = "other",
		rejectionReason = "rejectionReason",
		deliverableRejectedStatus = WorkSubStatusType.DELIVERABLE_REJECTED,
		fileName = "file.name",
		workNumber = "12312312";

	User user;
	Work work;
	Asset asset;
	AssetDTO assetDTO;
	InputStream inputStream;
	WorkResource workResource;
	WorkAssetAssociation workAssetAssociation;
	WorkDeliverableRejectedNotificationTemplate workerNotification, dispatcherNotification;
	WorkAssetAssociationType deliverableRequirementOneWorkAssetType, deliverableRequirementTwoWorkAssetType;
	DeliverableRequirementGroupDTO deliverableRequirementGroupDTO;
	DeliverableRequirementGroup deliverableRequirementGroup;
	DeliverableRequirementDTO deliverableRequirementDTOOne, deliverableRequirementDTOTwo, newDeliverableRequirementDTO;
	List<DeliverableRequirementDTO> deliverableRequirementDTOs;
	DeliverableRequirement deliverableRequirementOne, deliverableRequirementTwo;
	List<DeliverableRequirement> deliverableRequirements;

	ArgumentCaptor<WorkAssetAssociationType> workAssetAssociationTypeArgumentCaptor;
	ArgumentCaptor<DeliverableRequirementGroup> deliverableRequirementGroupArgumentCaptor;
	ArgumentCaptor<String> stringArgumentCaptor;

	@Before
	public void setup() throws Exception {
		user = mock(User.class);
		work = mock(Work.class, RETURNS_DEEP_STUBS);
		asset = mock(Asset.class);
		assetDTO = mock(AssetDTO.class);
		inputStream = mock(InputStream.class);
		workerNotification = mock(WorkDeliverableRejectedNotificationTemplate.class);
		dispatcherNotification = mock(WorkDeliverableRejectedNotificationTemplate.class);
		workAssetAssociation = mock(WorkAssetAssociation.class);
		deliverableRequirementOne = mock(DeliverableRequirement.class);
		deliverableRequirementTwo = mock(DeliverableRequirement.class);
		deliverableRequirements = Lists.newArrayList(deliverableRequirementOne, deliverableRequirementTwo);
		workAssetAssociationTypeArgumentCaptor = ArgumentCaptor.forClass(WorkAssetAssociationType.class);
		deliverableRequirementGroupArgumentCaptor = ArgumentCaptor.forClass(DeliverableRequirementGroup.class);
		stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
		deliverableRequirementDTOOne = mock(DeliverableRequirementDTO.class);
		deliverableRequirementDTOTwo = mock(DeliverableRequirementDTO.class);
		newDeliverableRequirementDTO = mock(DeliverableRequirementDTO.class);
		deliverableRequirementDTOs = Lists.newArrayList();
		deliverableRequirementDTOs.add(deliverableRequirementDTOOne);
		deliverableRequirementDTOs.add(deliverableRequirementDTOTwo);
		deliverableRequirementGroup = mock(DeliverableRequirementGroup.class);
		deliverableRequirementGroupDTO = mock(DeliverableRequirementGroupDTO.class);
		deliverableRequirementOneWorkAssetType = mock(WorkAssetAssociationType.class);
		deliverableRequirementTwoWorkAssetType = mock(WorkAssetAssociationType.class);
		workResource = mock(WorkResource.class);

		when(user.getId()).thenReturn(userId);
		when(work.getId()).thenReturn(workId);
		when(workResource.getWork()).thenReturn(work);
		when(workResource.getUser()).thenReturn(user);
		when(workService.findActiveWorkResource(workId)).thenReturn(workResource);
		when(asset.getName()).thenReturn(fileName);
		when(workAssetAssociation.getAsset()).thenReturn(asset);
		when(userDAO.getUser(anyLong())).thenReturn(user);
		when(deliverableRequirementGroupDTO.getId()).thenReturn(deliverableRequirementGroupId);
		when(notificationTemplateFactory.buildWorkDeliverableRejectedNotificationTemplate(
			userId, work, fileName, rejectionReason))
			.thenReturn(workerNotification);
		when(notificationTemplateFactory.buildWorkDeliverableRejectedNotificationTemplate(
			dispatcherId, work, fileName, rejectionReason)
		).thenReturn(dispatcherNotification);
		when(workAssetAssociationDAO.findWorkAssetAssociation(workId, assetId)).thenReturn(workAssetAssociation);
		when(deliverableRequirementGroup.getId()).thenReturn(deliverableRequirementGroupId);
		when(deliverableRequirementGroup.getDeliverableRequirements()).thenReturn(deliverableRequirements);
		when(deliverableRequirementDTOOne.getId()).thenReturn(deliverableRequirementIdOne);
		when(deliverableRequirementDTOOne.getType()).thenReturn(deliverableRequirementOneType);
		when(deliverableRequirementDTOOne.getNumberOfFiles()).thenReturn(newNumberOfFilesForRequirementOne);
		when(deliverableRequirementDTOTwo.getId()).thenReturn(deliverableRequirementIdTwo);
		when(deliverableRequirementDTOTwo.getType()).thenReturn(deliverableRequirementTwoType);
		when(newDeliverableRequirementDTO.getId()).thenReturn(null);
		when(newDeliverableRequirementDTO.getType()).thenReturn(newDeliverableRequirementType);
		when(deliverableRequirementOneWorkAssetType.getCode()).thenReturn(deliverableRequirementOneType);
		when(deliverableRequirementTwoWorkAssetType.getCode()).thenReturn(deliverableRequirementTwoType);
		when(deliverableRequirementOne.getType()).thenReturn(deliverableRequirementOneWorkAssetType);
		when(deliverableRequirementOne.getNumberOfFiles()).thenReturn(originalNumberOfFilesForRequirementOne);
		when(deliverableRequirementTwo.getType()).thenReturn(deliverableRequirementTwoWorkAssetType);
		when(deliverableRequirementGroupDAO.get(deliverableRequirementGroupId)).thenReturn(deliverableRequirementGroup);
		when(deliverableRequirementGroupDAO.findDeliverableGroupByWorkNumber(String.valueOf(deliverableRequirementGroupId))).thenReturn(deliverableRequirementGroup);
		when(deliverableRequirementDAO.get(deliverableRequirementIdOne)).thenReturn(deliverableRequirementOne);
		when(deliverableRequirementDAO.get(deliverableRequirementIdTwo)).thenReturn(deliverableRequirementTwo);
		when(deliverableRequirementDAO.findAllDeliverableRequirementsByGroupId(deliverableRequirementGroupId)).thenReturn(deliverableRequirements);
		when(deliverableRequirementDAO.findDeletedDeliverableRequirementByGroupIdAndType(deliverableRequirementGroupId, deliverableRequirementOneType)).thenReturn(deliverableRequirementOne);
		when(deliverableRequirementDAO.findDeletedDeliverableRequirementByGroupIdAndType(deliverableRequirementGroupId, deliverableRequirementTwoType)).thenReturn(deliverableRequirementTwo);
		when(deliverableRequirementOne.getId()).thenReturn(deliverableRequirementIdOne);
		when(assetManagementService.findAssetAssociationsByWorkAndAsset(workId, assetId)).thenReturn(workAssetAssociation);
		when(workService.findWorkByWorkNumber(workNumber)).thenReturn(work);
		when(workService.findWork(workId)).thenReturn(work);
		when(assetManagementService.storeAssetForWork(assetDTO, work.getId(), true)).thenReturn(workAssetAssociation);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), workResource.getUser().getId())).thenReturn(null);
		doReturn(originalNumberOfFilesForRequirementOne).when(deliverableService).countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementIdOne);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveOrUpdateDeliverableRequirementGroup_NullDeliverableRequirementGroupDTO_IllegalArgumentException() {
		deliverableService.saveOrUpdateDeliverableRequirementGroup(null);
	}

	@Test
	public void saveOrUpdateDeliverableRequirementGroup_NullDeliverableRequirementGroupId_newDeliverableRequirementGroupCreated() {
		when(deliverableRequirementGroupDTO.getId()).thenReturn(null);

		deliverableService.saveOrUpdateDeliverableRequirementGroup(deliverableRequirementGroupDTO);

		verify(deliverableRequirementGroupDAO, never()).get(anyLong());
		verify(deliverableRequirementGroupDAO).saveOrUpdate(deliverableRequirementGroupArgumentCaptor.capture());
		assertNull(deliverableRequirementGroupArgumentCaptor.getValue().getId());
	}

	@Test
	public void saveOrUpdateDeliverableRequirementGroup_existingDeliverableRequirementGroupId_existingDeliverableRequirementGroupLoadedAndUpdated() {
		deliverableService.saveOrUpdateDeliverableRequirementGroup(deliverableRequirementGroupDTO);

		verify(deliverableRequirementGroupDAO).get(deliverableRequirementGroupId);
		verify(deliverableRequirementGroupDAO).saveOrUpdate(deliverableRequirementGroupArgumentCaptor.capture());
		assertEquals(deliverableRequirementGroupId, deliverableRequirementGroupArgumentCaptor.getValue().getId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveOrUpdateDeliverableRequirements_NullDeliverableRequirementGroup_IllegalArgumentException() {
		deliverableService.saveOrUpdateDeliverableRequirements(deliverableRequirementDTOs, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveOrUpdateDeliverableRequirements_NullDeliverableRequirementsList_IllegalArgumentException() {
		deliverableService.saveOrUpdateDeliverableRequirements(null, deliverableRequirementGroup);
	}

	@Test
	public void saveOrUpdateDeliverableRequirements_DeliverableRequirementUpdatedAndSavedProperly() {
		deliverableService.saveOrUpdateDeliverableRequirements(deliverableRequirementDTOs, deliverableRequirementGroup);

		verify(deliverableRequirementOne).setType(workAssetAssociationTypeArgumentCaptor.capture());
		assertEquals(deliverableRequirementOneType, workAssetAssociationTypeArgumentCaptor.getValue().getCode());
		verify(deliverableRequirementOne).setDeliverableRequirementGroup(deliverableRequirementGroup);
		verify(deliverableRequirementOne).setDeleted(false);
		verify(deliverableRequirementDAO).saveOrUpdate(deliverableRequirementOne);

		verify(deliverableRequirementTwo).setType(workAssetAssociationTypeArgumentCaptor.capture());
		assertEquals(deliverableRequirementTwoType, workAssetAssociationTypeArgumentCaptor.getValue().getCode());
		verify(deliverableRequirementTwo).setDeliverableRequirementGroup(deliverableRequirementGroup);
		verify(deliverableRequirementTwo).setDeleted(false);
		verify(deliverableRequirementDAO).saveOrUpdate(deliverableRequirementTwo);
	}

	@Test
	public void saveOrUpdateDeliverableRequirements_newDeliverableAdded() {
		deliverableRequirementDTOs.add(newDeliverableRequirementDTO);

		deliverableService.saveOrUpdateDeliverableRequirements(deliverableRequirementDTOs, deliverableRequirementGroup);

		verify(deliverableRequirementDAO).findDeletedDeliverableRequirementByGroupIdAndType(deliverableRequirementGroupId, newDeliverableRequirementType);
		assertTrue(deliverableRequirementDAO.findDeletedDeliverableRequirementByGroupIdAndType(deliverableRequirementGroupId, newDeliverableRequirementType) == null);
	}

	@Test
	public void saveOrUpdateDeliverableRequirements_deliverableOneUpdatedDeliverableTwoDeleted() {
		deliverableRequirementDTOs.clear();
		deliverableRequirementDTOs.add(deliverableRequirementDTOOne);

		deliverableService.saveOrUpdateDeliverableRequirements(deliverableRequirementDTOs, deliverableRequirementGroup);

		verify(deliverableRequirementOne).setType(workAssetAssociationTypeArgumentCaptor.capture());
		assertEquals(deliverableRequirementOneType, workAssetAssociationTypeArgumentCaptor.getValue().getCode());
		verify(deliverableRequirementOne).setDeliverableRequirementGroup(deliverableRequirementGroup);
		verify(deliverableRequirementOne).setDeleted(false);
		verify(deliverableRequirementDAO).saveOrUpdate(deliverableRequirementOne);
		verify(deliverableRequirementOne, never()).setDeleted(true);

		verify(deliverableRequirementTwo, never()).setType(any(WorkAssetAssociationType.class));
		verify(deliverableRequirementTwo, never()).setDeliverableRequirementGroup(any(DeliverableRequirementGroup.class));
		verify(deliverableRequirementTwo, never()).setDeleted(false);
		verify(deliverableRequirementDAO, never()).saveOrUpdate(deliverableRequirementTwo);
		verify(deliverableRequirementTwo).setDeleted(true);
	}

	@Test
	public void saveOrUpdateDeliverableRequirements_deliverablesOneAndTwoDeleted() {
		deliverableRequirementDTOs.clear();

		deliverableService.saveOrUpdateDeliverableRequirements(deliverableRequirementDTOs, deliverableRequirementGroup);

		verify(deliverableRequirementOne, never()).setType(any(WorkAssetAssociationType.class));
		verify(deliverableRequirementOne, never()).setDeliverableRequirementGroup(any(DeliverableRequirementGroup.class));
		verify(deliverableRequirementOne, never()).setDeleted(false);
		verify(deliverableRequirementDAO, never()).saveOrUpdate(deliverableRequirementOne);
		verify(deliverableRequirementOne).setDeleted(true);

		verify(deliverableRequirementTwo, never()).setType(any(WorkAssetAssociationType.class));
		verify(deliverableRequirementTwo, never()).setDeliverableRequirementGroup(any(DeliverableRequirementGroup.class));
		verify(deliverableRequirementTwo, never()).setDeleted(false);
		verify(deliverableRequirementDAO, never()).saveOrUpdate(deliverableRequirementTwo);
		verify(deliverableRequirementTwo).setDeleted(true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void disableDeliverableDeadline_nullWorkNumber_IllegalArgumentException() {
		String workNumber = null;
		deliverableService.disableDeliverableDeadline(workNumber);
	}

	@Test(expected = IllegalArgumentException.class)
	public void disableDeliverableDeadline_blankWorkNumber_IllegalArgumentException() {
		deliverableService.disableDeliverableDeadline("");
	}

	@Test
	public void disableDeliverableDeadline_workNumberForAssignmentThatDoesNotExist_doNothing() {
		deliverableService.disableDeliverableDeadline(String.valueOf(workNumberForNonExistentAssignment));

		verify(deliverableRequirementGroupDAO, never()).saveOrUpdate(any(DeliverableRequirementGroup.class));
	}

	@Test
	public void disableDeliverableDeadline_validWorkNumber_disableDeadline() {
		deliverableService.disableDeliverableDeadline(String.valueOf(deliverableRequirementGroupId));

		verify(deliverableRequirementGroup).setDeadlineActive(false);
		verify(deliverableRequirementGroupDAO).saveOrUpdate(deliverableRequirementGroup);
	}

	@Test(expected = IllegalArgumentException.class)
	public void disableDeliverableReminder_nullWorkNumber_IllegalArgumentException() {
		deliverableService.disableDeliverableReminder(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void disableDeliverableReminder_blankWorkNumber_IllegalArgumentException() {
		deliverableService.disableDeliverableReminder(" ");
	}

	@Test
	public void disableDeliverableReminder_workNumberForAssignmentThatDoesNotExist_doNothing() {
		deliverableService.disableDeliverableReminder(String.valueOf(workNumberForNonExistentAssignment));

		verify(deliverableRequirementGroupDAO, never()).saveOrUpdate(any(DeliverableRequirementGroup.class));
	}

	@Test
	public void disableDeliverableReminder_validWorkNumber_disableReminder() {
		deliverableService.disableDeliverableReminder(String.valueOf(deliverableRequirementGroupId));

		verify(deliverableRequirementGroup).setReminderSent(true);
		verify(deliverableRequirementGroupDAO).saveOrUpdate(deliverableRequirementGroup);
	}

	@Test
	public void reactivateDeliverableDeadlineAndReminder_idForDeliverableRequirementGroupThatDoesNotExist_doNothing() {
		deliverableService.reactivateDeliverableDeadlineAndReminder(idForNonExistentDeliverableRequirementGroup);

		verify(deliverableRequirementGroup, never()).setReminderSent(anyBoolean());
		verify(deliverableRequirementGroup, never()).setDeadlineActive(anyBoolean());
		verify(deliverableRequirementGroupDAO, never()).saveOrUpdate(any(DeliverableRequirementGroup.class));
	}

	@Test
	public void reactivateDeliverableDeadlineAndReminder_validDeliverableRequirementGroupId_deadlineAndReminderActivated() {
		deliverableService.reactivateDeliverableDeadlineAndReminder(deliverableRequirementGroupId);

		verify(deliverableRequirementGroup).setReminderSent(false);
		verify(deliverableRequirementGroup).setDeadlineActive(true);
		verify(deliverableRequirementGroupDAO).saveOrUpdate(deliverableRequirementGroup);
	}

	@Test
	public void reactivateDeliverableDeadlineAndReminder_validDeliverableRequirementGroup_deadlineAndReminderActivated() {
		deliverableService.reactivateDeliverableDeadlineAndReminder(deliverableRequirementGroup);

		verify(deliverableRequirementGroup).setReminderSent(false);
		verify(deliverableRequirementGroup).setDeadlineActive(true);
		verify(deliverableRequirementGroupDAO).saveOrUpdate(deliverableRequirementGroup);
	}

	@Test(expected = IllegalArgumentException.class)
	public void countDeliverableAssetsByDeliverableRequirementID_nullId_IllegalArgumentException() {
		deliverableService.countDeliverableAssetsByDeliverableRequirementId(null);
	}

	@Test
	public void countDeliverableAssetsByDeliverableRequirementID_returnCountSuccess() {
		when(deliverableService.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementIdOne)).thenReturn(1);

		assertEquals(deliverableService.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementIdOne), 1);
	}

	@Test
	public void isDeliverableRequirementComplete_returnTrue() {
		assertTrue(deliverableService.isDeliverableRequirementComplete(deliverableRequirementOne));
	}

	@Test
	public void isDeliverableRequirementComplete_nonRejectedFilesGreaterThanRequired_returnTrue() {
		int numOfNonrejectedFiles = 2;
		assertTrue(numOfNonrejectedFiles > deliverableRequirementOne.getNumberOfFiles());
		doReturn(numOfNonrejectedFiles).when(deliverableService).countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementIdOne);
		assertTrue(deliverableService.isDeliverableRequirementComplete(deliverableRequirementOne));
	}

	@Test
	public void isDeliverableRequirementComplete_returnFalse() {
		doReturn(0).when(deliverableService).countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementIdOne);
		assertFalse(deliverableService.isDeliverableRequirementComplete(deliverableRequirementOne));
	}

	@Test
	public void isDeliverableRequirementGroupComplete_returnTrue() {
		assertTrue(deliverableService.isDeliverableRequirementGroupComplete(deliverableRequirementGroupId));
	}

	@Test
	public void isDeliverableRequirementGroupComplete_returnFalse() {
		doReturn(0).when(deliverableService).countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementIdOne);
		assertFalse(deliverableService.isDeliverableRequirementGroupComplete(deliverableRequirementGroupId));
	}

	@Test
	public void isDeliverableRequirementGroupComplete_noDeliverableRequirements_returnFalse() {
		when(deliverableRequirementGroup.getDeliverableRequirements()).thenReturn(Lists.<DeliverableRequirement>newArrayList());
		assertFalse(deliverableService.isDeliverableRequirementGroupComplete(deliverableRequirementGroupId));
	}

	@Test(expected = IllegalArgumentException.class)
	public void rejectDeliverable_blankReason_throwException() {
		deliverableService.rejectDeliverable("", userId, workId, assetId);
	}

	@Test(expected = IllegalArgumentException.class)
	public void rejectDeliverable_nullReason_throwException() {
		deliverableService.rejectDeliverable(null, userId, workId, assetId);
	}

	@Test(expected = IllegalArgumentException.class)
	public void rejectDeliverable_nullUserId_throwException() {
		deliverableService.rejectDeliverable(rejectionReason, null, workId, assetId);
	}

	@Test(expected = IllegalArgumentException.class)
	public void rejectDeliverable_nullWorkId_throwException() {
		deliverableService.rejectDeliverable(rejectionReason, userId, null, assetId);
	}

	@Test(expected = IllegalArgumentException.class)
	public void rejectDeliverable_nullAssetId_throwException() {
		deliverableService.rejectDeliverable(rejectionReason, userId, workId, null);
	}

	@Test
	public void rejectDeliverable_ValidRejection_RejectedBySet() {
		deliverableService.rejectDeliverable(rejectionReason, userId, workId, assetId);

		verify(workAssetAssociation).setRejectedBy(user);
	}

	@Test
	public void rejectDeliverable_validRejection_RejectedOnSet() {
		deliverableService.rejectDeliverable(rejectionReason, userId, workId, assetId);

		verify(workAssetAssociation, times(1)).setRejectedOn(any(Calendar.class));
	}

	@Test
	public void rejectDeliverable_validRejection_rejectedReasonSet() {
		deliverableService.rejectDeliverable(rejectionReason, userId, workId, assetId);

		verify(workAssetAssociation).setRejectionReason(rejectionReason);
	}

	@Test
	public void rejectDeliverable_validRejection_deliverableRejectedSystemLabelAdded() {
		deliverableService.rejectDeliverable(rejectionReason, userId, workId, assetId);

		verify(workSubStatusService).addSystemSubStatus(user, workId, deliverableRejectedStatus);
	}

	@Test
	public void rejectDeliverable_sendNotification_toWorker() {
		deliverableService.rejectDeliverable(rejectionReason, userId, workId, assetId);

		verify(notificationService).sendNotification(workerNotification);
	}

	@Test
	public void rejectDeliverable_sendNotification_toDispatcher() {
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), workResource.getUser().getId())).thenReturn(dispatcherId);

		deliverableService.rejectDeliverable(rejectionReason, userId, workId, assetId);

		verify(dispatcherNotification).setOnBehalfOfId(workResource.getUser().getId());
		verify(notificationService).sendNotification(dispatcherNotification);
	}

	@Test
	public void rejectDeliverable_doNotSendNotification_toDispatcher() {
		deliverableService.rejectDeliverable(rejectionReason, userId, workId, assetId);

		verify(notificationService, never()).sendNotification(dispatcherNotification);
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeAllDeliverablesFromWork_nullWorkId_throwException() {
		deliverableService.removeAllDeliverablesFromWork(null);
	}

	@Test
	public void removeAllDeliverablesFromWork_workId_deliverableFromWorkIsDeleted() {
		List<WorkAssetAssociation> workAssetAssociations = com.google.api.client.util.Lists.newArrayList();
		workAssetAssociations.add(workAssetAssociation);
		when(workAssetAssociationDAO.findAllDeliverablesByWork(workId)).thenReturn(workAssetAssociations);

		deliverableService.removeAllDeliverablesFromWork(workId);

		verify(workAssetAssociation).setDeleted(true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addDeliverable_nullWorkNumber_throwException() throws Exception {
		deliverableService.addDeliverable(null, assetDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addDeliverable_nullAssetDTO_throwException() throws Exception {
		deliverableService.addDeliverable(workNumber, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addDeliverable_withInputStream_nullWorkNumber_throwException() throws Exception {
		deliverableService.addDeliverable(null, inputStream, assetDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addDeliverable_withInputStream_nullAssetDTO_throwException() throws Exception {
		deliverableService.addDeliverable(workNumber, inputStream, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addDeliverable_withInputStream_nullInputstream_throwException() throws Exception {
		deliverableService.addDeliverable(workNumber, null, assetDTO);
	}

	@Test
	public void addDeliverable_success() throws Exception {
		deliverableService.addDeliverable(workNumber, assetDTO);

		verify(assetDTO).setDeliverable(true);
		verify(assetDTO).setAssociationType(anyString());
		verify(workService).findWorkByWorkNumber(workNumber);
		verify(assetManagementService).storeAssetForWork(assetDTO, work.getId(), true);
		verify(userNotificationService).onWorkAttachmentAdded(work, asset);
	}

	@Test
	public void addDeliverable_withInputStream_success() throws Exception {
		File file = mock(File.class);
		when(file.getAbsolutePath()).thenReturn("asdasdasdasd");
		doReturn(true).when(deliverableService).isDeliverableRequirementGroupComplete(deliverableRequirementGroupId);
		doReturn(file).when(deliverableService).copyStreamToTempFile(inputStream);

		deliverableService.addDeliverable(workNumber, inputStream, assetDTO);

		verify(assetDTO).setSourceFilePath(anyString());
		verify(deliverableService).addDeliverable(workNumber, assetDTO);
	}

	@Test
	public void addDeliverable_deliverableRequirementGroupComplete_markCompletion() throws Exception {
		when(work.getDeliverableRequirementGroup()).thenReturn(deliverableRequirementGroup);
		doReturn(true).when(deliverableService).isDeliverableRequirementGroupComplete(deliverableRequirementGroupId);

		deliverableService.addDeliverable(workNumber, assetDTO);

		verify(workSubStatusService).resolveSystemSubStatusByAction(work.getId(), WorkSubStatusType.DELIVERABLE_REJECTED);
		verify(userNotificationService).onDeliverableRequirementComplete(work);
	}

	@Test
	public void addDeliverable_noDeliverableRequirementGroup_doNotMarkCompletion() throws Exception {
		when(work.getDeliverableRequirementGroup()).thenReturn(null);

		deliverableService.addDeliverable(workNumber, assetDTO);

		verify(workSubStatusService, never()).resolveSystemSubStatusByAction(work.getId(), WorkSubStatusType.DELIVERABLE_REJECTED);
		verify(userNotificationService, never()).onDeliverableRequirementComplete(work);
	}

	@Test
	public void addDeliverable_requirementGroupNotComplete_doNotMarkCompletion() throws Exception {
		doReturn(false).when(deliverableService).isDeliverableRequirementGroupComplete(deliverableRequirementGroupId);

		deliverableService.addDeliverable(workNumber, assetDTO);

		verify(workSubStatusService, never()).resolveSystemSubStatusByAction(work.getId(), WorkSubStatusType.DELIVERABLE_REJECTED);
		verify(userNotificationService, never()).onDeliverableRequirementComplete(work);
	}
}
