package com.workmarket.service.business;

import com.workmarket.domains.model.IndustryPagination;
import com.workmarket.domains.model.ProfileModificationPagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.asset.type.UserAssetAssociationType;
import com.workmarket.domains.model.certification.Certification;
import com.workmarket.domains.model.certification.CertificationPagination;
import com.workmarket.domains.model.certification.CertificationVendor;
import com.workmarket.domains.model.certification.CertificationVendorPagination;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.certification.UserCertificationAssociationPagination;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.CertificationDTO;
import com.workmarket.service.business.dto.CertificationVendorDTO;
import com.workmarket.service.business.dto.UserCertificationDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CertificationServiceIT extends BaseServiceIT {


	@Autowired private CertificationService service;
	@Autowired private ProfileService profileService;
	@Autowired private InvariantDataService invariantDataService;

	private Certification newCertification;

	@Before
	public void before() throws Exception {

		CertificationDTO certificationDTO = new CertificationDTO();
		certificationDTO.setName("certification" + RandomUtilities.nextLong());
		certificationDTO.setCertificationVendorId(CERTIFICATION_VENDOR_ID);

		newCertification = service.saveOrUpdateCertification(certificationDTO);
		assertNotNull(newCertification);
		assertNotNull(newCertification.getId());
		assertEquals(CERTIFICATION_VENDOR_ID, newCertification.getCertificationVendor().getId());

		service.addCertificationToUser(newCertification.getId(), ANONYMOUS_USER_ID, CERTIFICATION_NUMBER);
	}

	@Test
	public void test_findAllCertificationTypes() throws Exception {
		IndustryPagination certificationTypePagination = new IndustryPagination();
		certificationTypePagination.setResultsLimit(25);
		certificationTypePagination.setStartRow(0);

		certificationTypePagination = invariantDataService.findAllIndustries(certificationTypePagination);

		assertTrue(certificationTypePagination.getResults().size() > 0);
	}

	@Test
	public void test_findAllCertificationVendors() throws Exception {
		CertificationVendorPagination certificationVendorPagination = new CertificationVendorPagination();
		certificationVendorPagination.setResultsLimit(25);
		certificationVendorPagination.setStartRow(0);

		certificationVendorPagination = service.findAllCertificationVendors(certificationVendorPagination);

		assertTrue(certificationVendorPagination.getResults().size() > 0);
	}


	@Test
	public void test_findCertificationVendorByTypeId() {
		CertificationVendorPagination certificationVendorPagination = new CertificationVendorPagination();
		certificationVendorPagination.setResultsLimit(25);
		certificationVendorPagination.setStartRow(0);

		certificationVendorPagination = service
				.findCertificationVendorByTypeId(certificationVendorPagination, CERTIFICATION_TYPE_ID);

		assertTrue(certificationVendorPagination.getResults().size() > 0);
	}

	@Test
	public void test_findCertificationById() {
		Certification certification = service
				.findCertificationById(CERTIFICATION_ID);

		assertNotNull(certification);
	}

	@Test
	public void test_findAllCertifications() {
		CertificationPagination certificationPagination = new CertificationPagination();
		certificationPagination.setResultsLimit(25);
		certificationPagination.setStartRow(0);

		certificationPagination = service
				.findAllCertifications(certificationPagination);

		assertTrue(certificationPagination.getResults().size() > 0);
	}

	@Test
	public void test_findAllCertificationByVendor() {
		CertificationPagination certificationPagination = new CertificationPagination();
		certificationPagination.setResultsLimit(25);
		certificationPagination.setStartRow(0);


		certificationPagination = service
				.findAllCertificationByVendor(CERTIFICATION_VENDOR_ID, certificationPagination);

		assertTrue(certificationPagination.getResults().size() > 0);
	}


	@Test
	public void test_saveOrUpdateCertification() throws Exception {
		CertificationDTO certificationDTO = new CertificationDTO();
		certificationDTO.setName("certification" + RandomUtilities.nextLong());
		certificationDTO.setCertificationVendorId(CERTIFICATION_VENDOR_ID);

		Certification certification = service.saveOrUpdateCertification(certificationDTO);
		assertNotNull(certification);
		assertNotNull(certification.getId());
		assertEquals(CERTIFICATION_VENDOR_ID, certification.getCertificationVendor().getId());

		certificationDTO.setCertificationId(CERTIFICATION_ID);
		certificationDTO.setName(UPDATE_CERTIFICATION_NAME);

		certification = service.saveOrUpdateCertification(certificationDTO);
		assertEquals(certification.getName(), UPDATE_CERTIFICATION_NAME);
	}

	@Test
	public void test_findCertificationByName() {
		Certification certification = service
				.findCertificationByName("Enterprise LAN Expert");

		assertNotNull(certification);
	}

	@Test
	public void test_addCertificationToUser() throws Exception {
		UserCertificationAssociation certificationAssociation = service
				.findAssociationByCertificationIdAndUserId(newCertification.getId(), ANONYMOUS_USER_ID);

		assertNotNull(certificationAssociation);
		assertFalse(certificationAssociation.getDeleted());


		ProfileModificationPagination profilePagination = new ProfileModificationPagination();
		profilePagination.setReturnAllRows();


		ProfileModificationPagination userProfileModificationList = profileService.findAllProfileModificationsByUserId(ANONYMOUS_USER_ID, profilePagination);

		assertNotNull(userProfileModificationList);
	}

	@Test
	public void test_saveOrUpdateUserCertification() throws Exception {
		User contractor = newContractor();
		UserCertificationAssociation certificationAssociation = service
				.findAssociationByCertificationIdAndUserId(newCertification.getId(), contractor.getId());

		Assert.assertNull(certificationAssociation);

		UserCertificationDTO userCertDTO = new UserCertificationDTO();
		userCertDTO.setCertificationNumber(CERTIFICATION_NUMBER);
		userCertDTO.setIssueDate(Calendar.getInstance());
		userCertDTO.setExpirationDate(Calendar.getInstance());

		service.saveOrUpdateUserCertification(newCertification.getId(), contractor.getId(), userCertDTO);

		certificationAssociation = service
				.findAssociationByCertificationIdAndUserId(newCertification.getId(), contractor.getId());

		assertNotNull(certificationAssociation);
		assertFalse(certificationAssociation.getDeleted());


		ProfileModificationPagination profilePagination = new ProfileModificationPagination();
		profilePagination.setReturnAllRows();


		ProfileModificationPagination userProfileModificationList = profileService.findAllProfileModificationsByUserId(contractor.getId(), profilePagination);

		assertNotNull(userProfileModificationList);
	}

	@Test
	public void test_removeCertificationFromUser() throws Exception {

		User contractor = newContractor();
		UserCertificationAssociation certificationAssociation = service
				.findAssociationByCertificationIdAndUserId(newCertification.getId(), contractor.getId());

		Assert.assertNull(certificationAssociation);

		service.addCertificationToUser(newCertification.getId(), contractor.getId(), CERTIFICATION_NUMBER);

		certificationAssociation = service
				.findAssociationByCertificationIdAndUserId(newCertification.getId(), contractor.getId());


		assertNotNull(certificationAssociation);
		assertFalse(certificationAssociation.getDeleted());

		service
				.removeCertificationFromUser(newCertification.getId(), contractor.getId());

		certificationAssociation = service
				.findAssociationByCertificationIdAndUserId(newCertification.getId(), contractor.getId());

		assertNotNull(certificationAssociation);
		assertTrue(certificationAssociation.getDeleted());
	}

	@Test
	public void deleteCertificationAndAddSame_ReturnOnlyOne() throws Exception {
		User contractor = newContractor();

		service.addCertificationToUser(newCertification.getId(), contractor.getId(), CERTIFICATION_NUMBER);
		service.removeCertificationFromUser(newCertification.getId(), contractor.getId());
		service.addCertificationToUser(newCertification.getId(), contractor.getId(), CERTIFICATION_NUMBER);

		assertTrue(service
				.findAllAssociationsByUserId(contractor.getId(), new UserCertificationAssociationPagination(true)).getRowCount().equals(1));
	}

	@Test
	public void deleteCertificationWithAssetAndAddSame_ReturnOnlyOne() throws Exception {
		User contractor = newContractor();

		service.addCertificationToUser(newCertification.getId(), contractor.getId(), CERTIFICATION_NUMBER);

		String uniqueId = RandomUtilities.generateAlphaNumericString(10);
		AssetDTO asset = new AssetDTO();
		asset.setSourceFilePath(STORAGE_TEST_FILE + uniqueId);
		asset.setName(STORAGE_TEST_FILE + uniqueId);
		asset.setAssociationType(UserAssetAssociationType.NONE);
		asset.setMimeType("application/pdf");

		initializeTestFile(uniqueId);

		assetManagementService.storeAssetForUserCertification(
				asset,
				service.findAssociationByCertificationIdAndUserId(newCertification.getId(), contractor.getId()).getId());

		service.removeCertificationFromUser(newCertification.getId(), contractor.getId());

		initializeTestFile(uniqueId);
		service.addCertificationToUser(newCertification.getId(), contractor.getId(), CERTIFICATION_NUMBER);
		assetManagementService.storeAssetForUserCertification(
				asset,
				service.findAssociationByCertificationIdAndUserId(newCertification.getId(), contractor.getId()).getId());

		assertEquals(1L, (long) service
				.findAllAssociationsByUserId(contractor.getId(), new UserCertificationAssociationPagination(true)).getResults().size());
		deleteTestFile(uniqueId);
	}


	@Test
	@Transactional
	public void test_findCertificationVendorByNameAndIndustryId() throws Exception {
		CertificationVendor vendor = service.findCertificationVendorByNameAndIndustryId("Avaya", 1000L);
		assertNotNull(vendor);
		assertEquals(vendor.getName(), "Avaya");
	}

	@Test
	@Transactional
	public void test_addCertificationVendor() throws Exception {

		CertificationVendorDTO dto = new CertificationVendorDTO();
		dto.setName("AVaYa");

		CertificationVendor vendor = service.saveOrUpdateCertificationVendor(dto, INDUSTRY_ID_1000);
		assertNotNull(vendor);
		assertEquals(vendor.getName(), "Avaya");

		dto.setName("My new Vendor");
		vendor = service.saveOrUpdateCertificationVendor(dto, 1000L);
		assertNotNull(vendor);
		assertEquals(vendor.getName(), "My new Vendor");
		assertTrue(vendor.getVerificationStatus().equals(VerificationStatus.PENDING));

		CertificationVendorPagination pagination = new CertificationVendorPagination();
		pagination.setReturnAllRows();
		pagination.setFilters(new HashMap<String, String>());

		pagination.getFilters().put(CertificationVendorPagination.FILTER_KEYS.VERIFICATION_STATUS.toString(), VerificationStatus.PENDING.toString());
		pagination = service.findAllCertificationVendors(pagination);

		assertTrue(pagination.getResults().size() >= 1);

		service.verifyCertificationVendor(vendor.getId());

		vendor = service.findCertificationVendorById(vendor.getId());
		assertNotNull(vendor);
		assertEquals(vendor.getName(), "My new Vendor");
		assertTrue(vendor.getVerificationStatus().equals(VerificationStatus.VERIFIED));

		service.rejectCertificationVendor(vendor.getId());
		vendor = service.findCertificationVendorById(vendor.getId());
		assertTrue(vendor.getVerificationStatus().equals(VerificationStatus.FAILED));
	}

	@Test
	public void test_updateCertificationVendorStatus() throws Exception {

		CertificationVendorDTO dto = new CertificationVendorDTO();
		dto.setName("New Certification vendor");

		CertificationVendor vendor = service.saveOrUpdateCertificationVendor(dto, 1000L);
		assertNotNull(vendor);
		service.updateCertificationVendorStatus(vendor.getId(), VerificationStatus.ON_HOLD);
		vendor = service.findCertificationVendorById(vendor.getId());
		assertTrue(vendor.getVerificationStatus().isOnHold());
	}

	@Test
	public void test_findAllCertificationFacetsForTypeAheadFilter() throws Exception {
		List<Map<String, String>> results = service.findAllCertificationFacetsForTypeAheadFilter(newCertification.getName());
		assertFalse(results.isEmpty());
		Map<String, String> result = results.get(0);
		assertTrue(result.get("id").equals(newCertification.getId().toString()));
	}
}
