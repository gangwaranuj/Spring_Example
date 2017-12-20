package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.service.UserGroupBaseIT;
import com.workmarket.domains.groups.service.UserGroupRequirementSetService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.certification.Certification;
import com.workmarket.domains.model.insurance.Insurance;
import com.workmarket.domains.model.license.License;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class DocumentationPackagerServiceIT extends UserGroupBaseIT {

	@Autowired AssetManagementService assetManagementService;
	@Autowired LaneService laneService;
	@Autowired DocumentationPackagerService documentationPackagerService;
	@Autowired UserGroupRequirementSetService userGroupRequirementSetService;

	static Long USER_ID = 1L;
	User resource;
	User buyer;
	Company company;
	UserGroup userGroup;

	@Test(expected = IllegalArgumentException.class)
	public void downloadDocumentationPackage_NullGroupId() throws Exception {
		documentationPackagerService.getDocumentationPackage(USER_ID, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void downloadDocumentationPackage_Nulls() throws Exception {
		documentationPackagerService.getDocumentationPackage(null, null);
	}

	private void initializeVars() throws Exception {
		resource = newContractorIndependentlane4Ready();
		buyer = newEmployeeWithCashBalance();
		company = companyService.findCompanyById(buyer.getCompany().getId());
		resource = newContractorIndependentlane4Ready();
		userGroup = generateUserGroup(
			buyer.getCompany().getId(), REQUIRES_APPROVAL, OPEN_MEMBERSHIP, buyer.getId()
		);
	}

	@Test
	public void downloadDocumentationPackage_WithDocumentsAndLicense_ForUser() throws Exception {
		initializeVars();

		// add document requirements to the group
		// TODO - Micah - this relies on AWS services being available. Should Mock AWS services asap.
		AssetDTO dto = newAssetDTO();
		dto.setName("Blarg1");
		Asset asset = dto.toAsset();
		asset = assetManagementService.storeAsset(dto, asset, true);
		dto.setAssetId(asset.getId());

		assetManagementService.addAssetToCompany(dto, company.getId());
		userGroupRequirementSetService.addDocumentRequirement(userGroup.getId(), asset.getId(), false);

		dto = newAssetDTO();
		dto.setName("Blarg2");
		Asset asset2 = dto.toAsset();
		asset2 = assetManagementService.storeAsset(dto, asset2, true);
		dto.setAssetId(asset2.getId());

		assetManagementService.addAssetToCompany(dto, company.getId());
		userGroupRequirementSetService.addDocumentRequirement(userGroup.getId(), asset2.getId(), false);

		// add driver's license requirement
		License driversLicense = licenseService.findLicenseById(5086L);
		userGroupRequirementSetService.addLicenseRequirement(userGroup.getId(), driversLicense.getId(), false, false);

		// make resource lane3 active
		laneService.addUserToCompanyLane3(resource.getId(), company.getId());

		// upload document from the resource
		saveDocumentReference(resource, userGroup, asset, null);
		saveDocumentReference(resource, userGroup, asset2, null);

		// save license reference
		saveLicense(resource.getId(), 5086L, "123456", "Blarg3");

		// apply to group
		userGroupService.applyToGroup(userGroup.getId(), resource.getId());

		// accept application
		userGroupService.approveUser(userGroup.getId(), resource.getId());

		// get documentation package
		Optional<Asset> documentationPackage = documentationPackagerService.getDocumentationPackageForUser(USER_ID, userGroup, resource);

		assertTrue(documentationPackage.isPresent());
	}

	@Test
	public void downloadDocumentationPackage_WithInsurance_ForUser() throws Exception {
		initializeVars();

		// add insurance requirement
		Insurance insurance = insuranceService.findInsurance(1002L);
		userGroupRequirementSetService.addInsuranceRequirement(
			userGroup.getId(),
			insurance.getId(),
			new BigDecimal("0.00"),
			false,
			false);

		// save insurance reference
		saveInsurance(resource.getId(), 1002L, "Blarg4");

		// apply to group
		userGroupService.applyToGroup(userGroup.getId(), resource.getId());

		// accept application
		userGroupService.approveUser(userGroup.getId(), resource.getId());

		// get documentation package
		Optional<Asset> documentationPackage = documentationPackagerService.getDocumentationPackageForUser(USER_ID, userGroup, resource);

		assertTrue(documentationPackage.isPresent());
	}

	@Test
	public void downloadDocumentationPackage_WithCertification_ForUser() throws Exception {
		initializeVars();

		// add certification requirement
		Certification certification = certificationService.findCertificationById(1000L);
		userGroupRequirementSetService.addCertificationRequirement(
			userGroup.getId(),
			certification.getId(),
			false,
			true);

		// save certification reference
		saveCertification(resource.getId(), 1000L, "Blarg5");

		// apply to group
		userGroupService.applyToGroup(userGroup.getId(), resource.getId());

		// accept application
		userGroupService.approveUser(userGroup.getId(), resource.getId());

		// get documentation package
		Optional<Asset> documentationPackage = documentationPackagerService.getDocumentationPackageForUser(USER_ID, userGroup, resource);

		assertTrue(documentationPackage.isPresent());
	}

	@Test
	public void downloadDocumentationPackage_WithCertification_ForAllMethodVariations() throws Exception {
		initializeVars();

		// add certification requirement
		Certification certification = certificationService.findCertificationById(1000L);
		userGroupRequirementSetService.addCertificationRequirement(
			userGroup.getId(),
			certification.getId(),
			false,
			true);

		// save certification reference
		saveCertification(resource.getId(), 1000L, "Blarg5");

		// apply to group
		userGroupService.applyToGroup(userGroup.getId(), resource.getId());

		// accept application
		userGroupService.approveUser(userGroup.getId(), resource.getId());

		Optional<Asset> documentationPackage = documentationPackagerService.getDocumentationPackageForUser(USER_ID, userGroup.getId(), resource.getId());
		assertTrue(documentationPackage.isPresent());

		documentationPackage = documentationPackagerService.getDocumentationPackage(USER_ID, userGroup.getId());
		assertTrue(documentationPackage.isPresent());

		List<User> users = Lists.newArrayList();
		users.add(resource);
		documentationPackage = documentationPackagerService.getDocumentationPackage(USER_ID, userGroup, users);
		assertTrue(documentationPackage.isPresent());
	}
}
