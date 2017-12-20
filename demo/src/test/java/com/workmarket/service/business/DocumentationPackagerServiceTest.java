package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.domains.groups.service.UserGroupRequirementSetService;
import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.ZipAssetBundle;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.certification.UserCertificationAssociationPagination;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReference;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReferencePagination;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.insurance.UserInsuranceAssociationPagination;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociationPagination;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.domains.model.DocumentationManager;
import com.workmarket.domains.groups.service.DocumentationManagerFactory;
import com.workmarket.domains.groups.service.DocumentationVisitorImpl;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.service.infra.notification.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class DocumentationPackagerServiceTest {
	@Mock UserGroupService userGroupService;
	@Mock UserService userService;
	@Mock AssetManagementService assetManagementService;
	@Mock NotificationService notificationService;
	@Mock NotificationTemplateFactory notificationTemplateFactory;

	@Mock DocumentationManagerFactory documentationManagerFactory;
	@Mock UserUserGroupDocumentReferenceService userUserGroupDocumentReferenceService;
	@Mock LicenseService licenseService;
	@Mock InsuranceService insuranceService;
	@Mock CertificationService certificationService;
	@Mock UserGroupRequirementSetService userGroupRequirementSetService;

	/*
		documentationVisitor is both a spy and is injected into documentationPackagerService.
		documentationVisitor also has remoteFileAdapter injected into it.

		This is because we want the actual visit methods to be called in the documentationVisitor
		but we still want the ability to mock out other autowired dependencies, like RemoteFileAdaper
	 */
	@Mock RemoteFileAdapter remoteFileAdapter;
	@InjectMocks @Spy DocumentationVisitorImpl documentationVisitor = new DocumentationVisitorImpl();

	@InjectMocks DocumentationPackagerServiceImpl documentationPackagerService;

	private static final long USER_ID = 1L;
	private static final String USER_FULL_NAME = "My Name";
	private static final String USER_NUMBER = "123456";
	private static final long USER_GROUP_ID = 2L;
	private static final String USER_GROUP_NAME = "my_user_group";
	private static final long COMPANY_ID = 3L;
	private static final String UUID = "aaaa-bbbb-cccc";
	private static final int BUFFER = 2048;

	private static final String DOCUMENTS_TYPE = "documents";
	private static final String LICENSES_TYPE = "licenses";
	private static final String INSURANCES_TYPE = "insurances";
	private static final String CERTIFICATIONS_TYPE = "certifications";

	@Spy DocumentationManager documentationManagerSpy;
	@Mock UserGroup userGroup;
	@Mock User user;
	@Mock Asset asset;

	User creator;
	List<User> users;
	List<UserUserGroupDocumentReference> userUserGroupDocumentReferences;
	List<UserLicenseAssociation> userLicenseAssociations;
	List<UserInsuranceAssociation> userInsuranceAssociations;
	List<UserCertificationAssociation> userCertificationAssociations;

	@Before
	public void setup() throws IOException, HostServiceException, AssetTransformationException {
		doNothing().when(documentationManagerSpy).start(USER_GROUP_NAME);
		ZipAssetBundle zipAssetBundle = mock(ZipAssetBundle.class);
		doReturn(zipAssetBundle).when(documentationManagerSpy).finish();
		ZipOutputStream zipOutputStream = mock(ZipOutputStream.class);
		documentationManagerSpy.setZipOutputStream(zipOutputStream);

		when(documentationManagerFactory.build()).thenReturn(documentationManagerSpy);

		Company company = mock(Company.class);
		when(company.getId()).thenReturn(COMPANY_ID);
		when(userGroup.getId()).thenReturn(USER_GROUP_ID);
		when(userGroup.getName()).thenReturn(USER_GROUP_NAME);
		when(userGroup.getCompany()).thenReturn(company);

		when(user.getId()).thenReturn(USER_ID);
		when(user.getFullName()).thenReturn(USER_FULL_NAME);
		when(user.getUserNumber()).thenReturn(USER_NUMBER);
		users = Lists.newArrayList();
		users.add(user);

		userUserGroupDocumentReferences = Lists.newArrayList();
		UserUserGroupDocumentReferencePagination userUserGroupDocumentReferencePagination = mock(UserUserGroupDocumentReferencePagination.class);
		when(userUserGroupDocumentReferenceService.findAllDocumentReferencesByUserIdAndUserGroupId(eq(USER_ID), eq(USER_GROUP_ID), any(UserUserGroupDocumentReferencePagination.class))).
				thenReturn(userUserGroupDocumentReferencePagination);
		when(userUserGroupDocumentReferencePagination.getResults()).thenReturn(userUserGroupDocumentReferences);

		UserLicenseAssociationPagination userLicenseAssociationPagination = mock(UserLicenseAssociationPagination.class);

		userLicenseAssociations = Lists.newArrayList();
		when(licenseService.findAllAssociationsByUserIdInList(eq(USER_ID), any(List.class), any(UserLicenseAssociationPagination.class))).thenReturn(userLicenseAssociationPagination);
		when(userLicenseAssociationPagination.getResults()).thenReturn(userLicenseAssociations);

		UserInsuranceAssociationPagination userInsuranceAssociationPagination = mock(UserInsuranceAssociationPagination.class);

		userInsuranceAssociations = Lists.newArrayList();
		when(insuranceService.findAllAssociationsByUserIdInList(eq(USER_ID), any(List.class), any(UserInsuranceAssociationPagination.class))).thenReturn(userInsuranceAssociationPagination);
		when(userInsuranceAssociationPagination.getResults()).thenReturn(userInsuranceAssociations);

		UserCertificationAssociationPagination userCertificationAssociationPagination = mock(UserCertificationAssociationPagination.class);

		userCertificationAssociations = Lists.newArrayList();
		when(certificationService.findAllAssociationsByUserIdInList(eq(USER_ID), any(List.class), any(UserCertificationAssociationPagination.class))).thenReturn(userCertificationAssociationPagination);
		when(userCertificationAssociationPagination.getResults()).thenReturn(userCertificationAssociations);

		AvailabilityType availabilityType = mock(AvailabilityType.class);
		when(availabilityType.hasGuestAvailability()).thenReturn(false);
		when(asset.getUUID()).thenReturn(UUID);
		when(asset.getAvailability()).thenReturn(availabilityType);
		when(asset.getCreatorId()).thenReturn(USER_ID);
		when(asset.getName()).thenReturn("first").thenReturn("second");

		InputStream inputStream = mock(InputStream.class);
		when(inputStream.read(any(byte[].class), eq(0), eq(BUFFER))).thenReturn(-1);
		when(remoteFileAdapter.getFileStream(RemoteFileType.PRIVATE, UUID)).thenReturn(inputStream);

		when(assetManagementService.storeAssetForCompany(any(AssetDTO.class), eq(COMPANY_ID))).thenReturn(asset);

		creator = mock(User.class);
		final Map<String, Object> map = new HashMap<String, Object>() {{ put("firstName", "John"); put("lastName", "Smith"); put("userNumber", "34234234"); }};
		when(creator.getId()).thenReturn(USER_ID);
		when(creator.getFullName()).thenReturn(USER_FULL_NAME);
		when(creator.getUserNumber()).thenReturn(USER_NUMBER);
		when(userService.findUserById(any(Long.class))).thenReturn(creator);
		when(userService.findAllUsersByIds(any(List.class))).thenReturn(com.google.common.collect.Lists.newArrayList(creator));
		when(userService.getProjectionMapById(any(Long.class), any(String.class), any(String.class), any(String.class))).thenReturn(map);
		when(userService.getProjectionMapByIds(any(List.class), any(String.class), any(String.class), any(String.class))).thenReturn(new HashMap<Long, Map<String,Object>>() {{ put(USER_ID, map); }});
	}

	@Test
	public void getDocumentationPackage_NoDocumentation() throws IOException, HostServiceException, AssetTransformationException {
		when(assetManagementService.storeAssetForCompany(any(AssetDTO.class), eq(COMPANY_ID))).thenReturn(asset);
		Optional<Asset> result = documentationPackagerService.getDocumentationPackage(USER_ID, userGroup, users);

		verify(documentationManagerSpy, times(1)).start(USER_GROUP_NAME);
		verify(documentationManagerSpy, never()).putNextAsset(any(String.class), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
	}

	private void setupTwoDocuments() {
		UserUserGroupDocumentReference userUserGroupDocumentReference = spy(new UserUserGroupDocumentReference());
		when(userUserGroupDocumentReference.getReferencedDocument()).thenReturn(asset);

		userUserGroupDocumentReferences.add(userUserGroupDocumentReference);
		userUserGroupDocumentReferences.add(userUserGroupDocumentReference);
	}

	private void setupTwo() {
		Set<Asset> assets = Sets.newHashSet();
		assets.add(asset);

		UserLicenseAssociation userLicenseAssociation = spy(new UserLicenseAssociation());
		when(userLicenseAssociation.getAssets()).thenReturn(assets);
		UserInsuranceAssociation userInsuranceAssociation = spy(new UserInsuranceAssociation());
		when(userInsuranceAssociation.getAssets()).thenReturn(assets);
		UserCertificationAssociation userCertificationAssociation = spy(new UserCertificationAssociation());
		when(userCertificationAssociation.getAssets()).thenReturn(assets);

		userLicenseAssociations.add(userLicenseAssociation);
		userLicenseAssociations.add(userLicenseAssociation);
		userInsuranceAssociations.add(userInsuranceAssociation);
		userInsuranceAssociations.add(userInsuranceAssociation);
		userCertificationAssociations.add(userCertificationAssociation);
		userCertificationAssociations.add(userCertificationAssociation);
	}

	@Test
	public void getDocumentationPackage_TwoDocuments() throws IOException {
		setupTwoDocuments();

		Optional<Asset> result = documentationPackagerService.getDocumentationPackage(USER_ID, userGroup, users);

		verify(documentationManagerSpy, times(1)).start(USER_GROUP_NAME);
		verify(documentationManagerSpy, times(2)).putNextAsset(eq(DOCUMENTS_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
	}

	@Test
	public void getDocumentationPackage_TwoLicenses() throws IOException {
		setupTwo();
		when(userGroupRequirementSetService.findUserGroupsRequiredLicenseIds(userGroup.getId())).thenReturn(Lists.newArrayList(1L, 2L));

		Optional<Asset> result = documentationPackagerService.getDocumentationPackage(USER_ID, userGroup, users);

		verify(documentationManagerSpy, times(1)).start(USER_GROUP_NAME);
		verify(documentationManagerSpy, times(2)).putNextAsset(eq(LICENSES_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
		verify(documentationManagerSpy, times(0)).putNextAsset(eq(INSURANCES_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
		verify(documentationManagerSpy, times(0)).putNextAsset(eq(CERTIFICATIONS_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
	}

	@Test
	public void getDocumentationPackage_TwoDocumentsAndTwoLicenses() throws IOException, HostServiceException, AssetTransformationException {
		setupTwoDocuments();
		setupTwo();
		when(userGroupRequirementSetService.findUserGroupsRequiredLicenseIds(userGroup.getId())).thenReturn(Lists.newArrayList(1L, 2L));

		Optional<Asset> result = documentationPackagerService.getDocumentationPackage(USER_ID, userGroup, users);

		verify(documentationManagerSpy, times(1)).start(USER_GROUP_NAME);
		verify(documentationManagerSpy, times(2)).putNextAsset(eq(LICENSES_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
		verify(documentationManagerSpy, times(2)).putNextAsset(eq(DOCUMENTS_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
		verify(documentationManagerSpy, times(0)).putNextAsset(eq(INSURANCES_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
		verify(documentationManagerSpy, times(0)).putNextAsset(eq(CERTIFICATIONS_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
	}

	@Test
	public void getDocumentationPackage_TwoInsurances() throws IOException, HostServiceException, AssetTransformationException {
		setupTwo();
		when(userGroupRequirementSetService.findUserGroupsRequiredInsuranceIds(userGroup.getId())).thenReturn(Lists.newArrayList(1L, 2L));

		Optional<Asset> result = documentationPackagerService.getDocumentationPackage(USER_ID, userGroup, users);

		verify(documentationManagerSpy, times(1)).start(USER_GROUP_NAME);
		verify(documentationManagerSpy, times(2)).putNextAsset(eq(INSURANCES_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
		verify(documentationManagerSpy, times(0)).putNextAsset(eq(LICENSES_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
		verify(documentationManagerSpy, times(0)).putNextAsset(eq(CERTIFICATIONS_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
	}

	@Test
	public void getDocumentationPackage_TwoCertifications() throws IOException, HostServiceException, AssetTransformationException {
		setupTwo();
		when(userGroupRequirementSetService.findUserGroupsRequiredCertificationIds(userGroup.getId())).thenReturn(Lists.newArrayList(1L, 2L));

		Optional<Asset> result = documentationPackagerService.getDocumentationPackage(USER_ID, userGroup, users);

		verify(documentationManagerSpy, times(1)).start(USER_GROUP_NAME);
		verify(documentationManagerSpy, times(2)).putNextAsset(eq(CERTIFICATIONS_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
		verify(documentationManagerSpy, times(0)).putNextAsset(eq(INSURANCES_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
		verify(documentationManagerSpy, times(0)).putNextAsset(eq(LICENSES_TYPE), any(Asset.class), any(String.class), any(String.class), any(String.class), any(InputStream.class));
	}
}
