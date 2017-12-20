package com.workmarket.domains.work.service;

import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.asset.WorkAssetVisibilityDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VisibilityType;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.WorkAssetVisibility;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceImplTest {

	@Mock AssetManagementService assetManagementService;
	@Mock LookupEntityDAO lookupEntityDAO;
	@Mock WorkAssetVisibilityDAO workAssetVisibilityDAO;
	@Mock AuthenticationService authenticationService;
	@Mock WorkService workService;
	@Mock UserNotificationService userNotificationService;
	@Mock WebHookEventService webHookEventService;

	@InjectMocks DocumentServiceImpl documentService;

	String visibilityTypeCode = VisibilityType.PUBLIC;
	Long
		assetId = 1L,
		workId = 2L,
		buyerId = 3L,
		workerId = 4L,
		workerId2 = 5L,
		worker1CompanyId = 6L,
		worker2CompanyId = 7L;
	User buyer, worker1, worker2;
	Work work;
	Company buyerCompany;
	WorkAssetAssociation workAssetAssociation;
	WorkAssetVisibility workAssetVisibility;
	VisibilityType visibilityType;

	@Before
	public void setup() {
		buyer = mock(User.class);
		work = mock(Work.class, RETURNS_DEEP_STUBS);
		worker1 = mock(User.class);
		worker2 = mock(User.class);
		buyerCompany = mock(Company.class);
		workAssetAssociation = mock(WorkAssetAssociation.class);
		workAssetVisibility = mock(WorkAssetVisibility.class, RETURNS_DEEP_STUBS);
		visibilityType = mock(VisibilityType.class);

		when(buyer.getId()).thenReturn(buyerId);
		when(buyer.getCompany()).thenReturn(buyerCompany);
		when(worker1.getId()).thenReturn(workerId);
		when(work.getId()).thenReturn(workId);
		when(work.getCompany()).thenReturn(buyerCompany);
		when(workAssetVisibilityDAO.findByWorkAssetAssociationId(workAssetAssociation.getId())).thenReturn(workAssetVisibility);
		when(lookupEntityDAO.findByCode(VisibilityType.class, visibilityTypeCode)).thenReturn(visibilityType);
		doReturn(workAssetAssociation).when(assetManagementService).findAssetAssociationsByWorkAndAsset(workId, assetId);
	}

	@Test
	public void updateDocumentVisibility_doSave() {
		documentService.updateDocumentVisibility(workId, assetId, visibilityTypeCode);

		verify(workAssetVisibilityDAO, times(1)).saveOrUpdate(workAssetVisibility);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateDocumentVisibility_nullWorkId_throwException() {
		documentService.updateDocumentVisibility(null, assetId, visibilityTypeCode);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateDocumentVisibility_nullAssetId_throwException() {
		documentService.updateDocumentVisibility(workId, null, visibilityTypeCode);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateDocumentVisibility_nullVisibilityCode_throwException() {
		documentService.updateDocumentVisibility(workId, assetId, null);
	}

	@Test
	public void updateDocumentVisibility_errorFetchingCode_dontSave() {
		when(lookupEntityDAO.findByCode(VisibilityType.class, visibilityTypeCode)).thenReturn(null);

		documentService.updateDocumentVisibility(workId, assetId, visibilityTypeCode);

		verify(workAssetVisibilityDAO, never()).saveOrUpdate(workAssetVisibility);
	}

	@Test
	public void updateDocumentVisibility_errorFetchingAsset_dontSave() {
		doReturn(null).when(assetManagementService).findAssetAssociationsByWorkAndAsset(anyLong(), anyLong());

		documentService.updateDocumentVisibility(workId, assetId, visibilityTypeCode);

		verify(workAssetVisibilityDAO, never()).saveOrUpdate(workAssetVisibility);
	}

	@Test
	public void updateDocumentVisibility_noExistingSetting_doSave() {
		when(workAssetVisibilityDAO.findByWorkAssetAssociationId(workAssetAssociation.getId())).thenReturn(null);

		documentService.updateDocumentVisibility(workId, assetId, visibilityTypeCode);

		verify(workAssetVisibilityDAO, times(1)).saveOrUpdate(any(WorkAssetVisibility.class));
	}

	@Test
	public void isDocumentVisible_docIsPublic_returnTrue() {
		when(workAssetVisibility.getVisibilityType().isPublic()).thenReturn(true);

		assertTrue(documentService.isDocumentVisible(workAssetVisibility, work));
	}

	@Test
	public void isDocumentVisible_docIsInternalAndCurrentUserIsAdmin_returnTrue() {
		when(workAssetVisibility.getVisibilityType().isInternal()).thenReturn(true);
		when(authenticationService.getCurrentUserId()).thenReturn(buyerId);

		assertTrue(documentService.isDocumentVisible(workAssetVisibility, work));
	}

	@Test
	public void isDocumentVisible_docIsInternalAndCurrentUserIsNotAdmin_returnFalse() {
		when(workAssetVisibility.getVisibilityType().isInternal()).thenReturn(true);
		when(authenticationService.getCurrentUserId()).thenReturn(workerId);
		when(authenticationService.getCurrentUserCompanyId()).thenReturn(buyerId);

		assertFalse(documentService.isDocumentVisible(workAssetVisibility, work));
	}

	@Test
	public void isDocumentVisible_docIsForAssignedWorkerAndCurrentUserIsAssignedWorker_returnTrue() {
		when(workAssetVisibility.getVisibilityType().isAssignedWorker()).thenReturn(true);
		when(authenticationService.getCurrentUserId()).thenReturn(workerId);
		when(authenticationService.getCurrentUserCompanyId()).thenReturn(worker1CompanyId);
		when(workService.findActiveWorkerId(work.getId())).thenReturn(workerId);

		assertTrue(documentService.isDocumentVisible(workAssetVisibility, work));
	}

	@Test
	public void isDocumentVisible_docIsForAssignedWorkerAndCurrentUserIsAdmin_returnTrue() {
		when(workAssetVisibility.getVisibilityType().isAssignedWorker()).thenReturn(true);
		when(authenticationService.getCurrentUserId()).thenReturn(buyerId);

		assertTrue(documentService.isDocumentVisible(workAssetVisibility, work));
	}

	@Test
	public void isDocumentVisible_docIsForAssignedWorkerAndCurrentUserIsNotAssignedWorkerOrAdmin_returnFalse() {
		when(workAssetVisibility.getVisibilityType().isAssignedWorker()).thenReturn(true);
		when(authenticationService.getCurrentUserId()).thenReturn(workerId2);
		when(authenticationService.getCurrentUserCompanyId()).thenReturn(worker2CompanyId);
		when(workService.findActiveWorkerId(work.getId())).thenReturn(workerId);

		assertFalse(documentService.isDocumentVisible(workAssetVisibility, work));
	}

	@Test
	public void isDocumentVisible_docIsForAssignedWorkerAndAssignedWorkerIsNullAndCurrentUserIsNotAdmin_returnFalse() {
		when(workAssetVisibility.getVisibilityType().isAssignedWorker()).thenReturn(true);
		when(authenticationService.getCurrentUser()).thenReturn(worker2);
		when(authenticationService.getCurrentUserCompanyId()).thenReturn(worker2CompanyId);
		when(workService.findActiveWorkerId(work.getId())).thenReturn(null);

		assertFalse(documentService.isDocumentVisible(workAssetVisibility, work));
	}
}
