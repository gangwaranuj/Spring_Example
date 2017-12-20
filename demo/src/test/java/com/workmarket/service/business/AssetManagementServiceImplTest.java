package com.workmarket.service.business;

import com.workmarket.dao.UserDAO;
import com.workmarket.dao.asset.UserAssetAssociationDAO;
import com.workmarket.dao.asset.WorkAssetAssociationDAO;
import com.workmarket.dao.upload.UploadDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.AssetCdnUri;
import com.workmarket.domains.model.asset.AssetRemoteUri;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.type.UserAssetAssociationType;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.dto.UploadDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.file.RemoteFile;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.utility.CollectionUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URL;
import java.util.Calendar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssetManagementServiceImplTest {

	@Mock UserDAO userDAO;
	@Mock UserAssetAssociationDAO userAssetAssociationDAO;
	@Mock WorkAssetAssociationDAO workAssetAssociationDAO;
	@Mock WorkService workService;
	@Mock AssetService assetService;
	@Mock UploadDAO uploadDAO;
	@Mock RemoteFileAdapter remoteFileAdapter;

	@InjectMocks AssetManagementServiceImpl service;

	Long
		userId = 99999L,
		otherUserId = 88888L,
		workId = 42L,
		assetId = 1L,
		uploadId = 1L;
	String
		uuid = "UUID-TEST-UUID-TEST",
		remoteUri = "http://remote-uri.com/",
		cdnUri = "http://cdn-uri.com/",
		description = "description",
		workAssetAssociationType = WorkAssetAssociationType.ATTACHMENT,
		urlString = "http://test.com/\"",
		mimeType = "mimeType",
		fileName = "file.name";
	User user;
	Company company;
	Asset asset, backgroundImage;
	Work work;
	UserAssetAssociation existingBackgroundImage, newBackgroundImage;
	WorkAssetAssociation workAssetAssociation;
	UserAssetAssociationType type;

	AssetCdnUri assetCdnUri = new AssetCdnUri();
	AssetRemoteUri assetRemoteUri = new AssetRemoteUri();

	UploadDTO uploadDTO = new UploadDTO();
	Upload upload = new Upload();
	RemoteFile remote = new RemoteFile();
	Calendar calendar;

	@Before
	public void setup() {
		service = spy(service);

		backgroundImage = mock(Asset.class);
		user = mock(User.class);
		company = mock(Company.class);
		existingBackgroundImage = mock(UserAssetAssociation.class);
		newBackgroundImage = mock(UserAssetAssociation.class);
		workAssetAssociation = mock(WorkAssetAssociation.class);
		calendar = mock(Calendar.class);
		work = mock(Work.class, RETURNS_DEEP_STUBS);
		asset = mock(Asset.class);
		assetCdnUri.setCdnUriPrefix(cdnUri);
		assetRemoteUri.setAssetRemoteUri(remoteUri);

		when(userDAO.get(anyLong())).thenReturn(user);
		when(userDAO.getUser(anyLong())).thenReturn(user);
		when(userAssetAssociationDAO.findBackgroundImage(userId)).thenReturn(existingBackgroundImage);
		when(userAssetAssociationDAO.findBackgroundImage(otherUserId)).thenReturn(null);
		when(workAssetAssociationDAO.findWorkAssetAssociation(workId, assetId)).thenReturn(workAssetAssociation);
		when(user.getId()).thenReturn(userId);
		when(user.getCompany()).thenReturn(company);
		when(work.getId()).thenReturn(workId);
		when(work.getCompany()).thenReturn(company);
		when(asset.getName()).thenReturn(fileName);
		when(workAssetAssociation.getAsset()).thenReturn(asset);
		when(workAssetAssociation.getId()).thenReturn(assetId);
		when(assetService.getDefaultAssetCdnUri()).thenReturn(assetCdnUri);
		when(assetService.getDefaultAssetRemoteUri()).thenReturn(assetRemoteUri);

		doReturn(newBackgroundImage).when(service).makeUserAssetAssociation(any(Asset.class), any(User.class));
		doReturn(type).when(service).makeUserAssetAssociationType(UserAssetAssociationType.BACKGROUND_IMAGE);
	}

	@Test
	public void changeUserBackgroundImage_WithUserIdAndImage_GetsAUser() throws Exception {
		service.changeUserBackgroundImage(userId, backgroundImage);
		verify(userDAO).get(userId);
	}

	@Test
	public void changeUserBackgroundImage_WithNullUserIdAndImage_GetsNoUser() throws Exception {
		when(userDAO.get((Long) null)).thenReturn(null);
		try {
			service.changeUserBackgroundImage(null, backgroundImage);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void changeUserBackgroundImage_WithUserIdAndImage_FindsABackgroundImage() throws Exception {
		service.changeUserBackgroundImage(userId, backgroundImage);
		verify(userAssetAssociationDAO).findBackgroundImage(userId);
	}

	@Test
	public void changeUserBackgroundImage_WithExistingBackgroundImage_DeletesABackgroundImage() throws Exception {
		service.changeUserBackgroundImage(userId, backgroundImage);
		verify(existingBackgroundImage).setDeleted(true);
	}

	@Test
	public void changeUserBackgroundImage_WithExistingBackgroundImage_SavesTheDeletedBackgroundImage() throws Exception {
		service.changeUserBackgroundImage(userId, backgroundImage);
		verify(userAssetAssociationDAO).saveOrUpdate(existingBackgroundImage);
	}

	@Test
	public void changeUserBackgroundImage_WithoutExistingBackgroundImage_DoesntDeleteABackgroundImage() throws Exception {
		service.changeUserBackgroundImage(otherUserId, backgroundImage);
		verify(existingBackgroundImage, never()).setDeleted(true);
	}

	@Test
	public void changeUserBackgroundImage_WithoutExistingBackgroundImage_DoesntSaveTheDeletedBackgroundImage() throws Exception {
		service.changeUserBackgroundImage(otherUserId, backgroundImage);
		verify(userAssetAssociationDAO, never()).saveOrUpdate(existingBackgroundImage);
	}

	@Test
	public void changeUserBackgroundImage_SavesANewBackgroundImage() throws Exception {
		service.changeUserBackgroundImage(otherUserId, backgroundImage);
		verify(userAssetAssociationDAO).saveOrUpdate(newBackgroundImage);
	}

	private void storeAssetWithUploadDTO_setup() {
		uploadDTO.setUploadId(uploadId);
		uploadDTO.setUploadUuid(uuid);
		uploadDTO.setAssociationType(workAssetAssociationType);
		uploadDTO.setDescription(description);

		upload.setUUID(uuid);

		remote.setRemoteUri(remoteUri);
		remote.setCdnUri(cdnUri);
	}

	@Test
	public void storeAssetWithUploadDTO_Normal_Execution_Private_Association() throws Exception {
		storeAssetWithUploadDTO_setup();

		when(uploadDAO.findUploadByIdOrUUID(uploadId, uuid)).thenReturn(upload);
		when(remoteFileAdapter.move(RemoteFileType.TMP, RemoteFileType.PRIVATE, uuid)).thenReturn(remote);

		Asset serviceResponse = service.storeAsset(uploadDTO);

		verify(assetService).saveOrUpdate(any(Asset.class));

		assertNotNull(serviceResponse);
		assertFalse(serviceResponse.getAvailability().hasGuestAvailability());
	}

	@Test
	public void storeAssetWithUploadDTO_Normal_Execution_Public_Association() throws Exception {
		workAssetAssociationType = UserAssetAssociationType.AVATAR;
		storeAssetWithUploadDTO_setup();

		when(uploadDAO.findUploadByIdOrUUID(uploadId, uuid)).thenReturn(upload);
		when(remoteFileAdapter.move(RemoteFileType.TMP, RemoteFileType.PUBLIC, uuid)).thenReturn(remote);

		Asset serviceResponse = service.storeAsset(uploadDTO);

		verify(assetService).saveOrUpdate(any(Asset.class));

		assertNotNull(serviceResponse);
		assertTrue(serviceResponse.getAvailability().hasGuestAvailability());
	}

	@Test
	public void storeAssetWithUploadDTO_Returns_Correct_Cdn_And_Description() throws Exception {
		workAssetAssociationType = UserAssetAssociationType.AVATAR;
		storeAssetWithUploadDTO_setup();

		when(uploadDAO.findUploadByIdOrUUID(uploadId, uuid)).thenReturn(upload);
		when(remoteFileAdapter.move(RemoteFileType.TMP, RemoteFileType.PUBLIC, uuid)).thenReturn(remote);

		Asset serviceResponse = service.storeAsset(uploadDTO);

		verify(assetService).saveOrUpdate(any(Asset.class));

		assertEquals(serviceResponse.getCdnUri(), "http://cdn-uri.com/UU/ID/TE/ST/UU/UUID-TEST-UUID-TEST");
		assertEquals(serviceResponse.getDescription(), description);
	}

	@Test
	public void getAuthorizedDownloadUriByUuidForTempUpload_normalExecution() throws Exception {
		upload.setUUID(uuid);
		upload.setMimeType(mimeType);
		upload.setFilename(fileName);
		URL returnUri = new URL(urlString);

		when(uploadDAO.findUploadByUUID(uuid)).thenReturn(upload);
		when(remoteFileAdapter.getAuthorizedURL(RemoteFileType.TMP, uuid, null, CollectionUtilities.newStringMap(
			"response-content-type", upload.getMimeType(),
			"response-content-disposition", String.format("%s; filename=%s", "attachment", upload.getFilename())
		))).thenReturn(returnUri);
		String result = service.getAuthorizedDownloadUriByUuidForTempUpload(uuid);
		assertNotNull(result);
		assertEquals(urlString, result);
	}

	@Test
	public void getAuthorizedDownloadUriByUuidForTempUpload_findByUUIDReturnsNull() throws Exception {
		when(uploadDAO.findUploadByUUID(uuid)).thenReturn(null);
		assertEquals(service.getAuthorizedDownloadUriByUuidForTempUpload(uuid), null);
	}

	@Test
	public void getAuthorizedDownloadUriByUuidForTempUpload_NullUrlResponse() throws Exception {
		upload.setUUID(uuid);
		upload.setMimeType(mimeType);
		upload.setFilename(fileName);

		when(uploadDAO.findUploadByUUID(uuid)).thenReturn(upload);
		when(remoteFileAdapter.getAuthorizedURL(RemoteFileType.TMP, uuid, null, CollectionUtilities.newStringMap(
			"response-content-type", upload.getMimeType(),
			"response-content-disposition", String.format("%s; filename=%s", "attachment", upload.getFilename())
		))).thenReturn(null);
		String result = service.getAuthorizedDownloadUriByUuidForTempUpload(uuid);
		assertEquals(null, result);
	}

	@Test
	public void getAuthorizedDownloadUriByUuidForTempUpload_getAuthorizedURLThrowsException() throws Exception {
		upload.setUUID(uuid);
		upload.setMimeType(mimeType);
		upload.setFilename(fileName);

		when(uploadDAO.findUploadByUUID(uuid)).thenReturn(upload);
		when(remoteFileAdapter.getAuthorizedURL(RemoteFileType.TMP, uuid, null, CollectionUtilities.newStringMap(
			"response-content-type", upload.getMimeType(),
			"response-content-disposition", String.format("%s; filename=%s", "attachment", upload.getFilename())
		))).thenThrow(new HostServiceException());
		try {
			service.getAuthorizedDownloadUriByUuidForTempUpload(uuid);
			assertFalse(true);
		} catch (HostServiceException e) {
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void findAssetAssociationsByWorkAndAsset_NullWorkId_IllegalArgumentException() {
		service.findAssetAssociationsByWorkAndAsset(null, assetId);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findAssetAssociationsByWorkAndAsset_NullAssetId_IllegalArgumentException() {
		service.findAssetAssociationsByWorkAndAsset(workId, null);
	}

	@Test
	public void findAssetAssociationsByWorkAndAsset_ValidWorkIdAndAssetId_DAOCallExecuted() {
		when(service.findAssetAssociationsByWorkAndAsset(workId, assetId)).thenReturn(workAssetAssociation);

		service.findAssetAssociationsByWorkAndAsset(workId, assetId);

		verify(workAssetAssociationDAO).findWorkAssetAssociation(workId, assetId);
	}

}
