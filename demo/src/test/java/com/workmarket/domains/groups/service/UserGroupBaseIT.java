package com.workmarket.domains.groups.service;

import com.workmarket.dao.certification.UserCertificationAssociationDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.dto.UserGroupDTO;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.InsuranceService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserUserGroupAssociationService;
import com.workmarket.service.business.UserUserGroupDocumentReferenceService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.InsuranceDTO;
import com.workmarket.service.business.dto.LicenseDTO;
import com.workmarket.service.business.dto.UserCertificationDTO;
import com.workmarket.service.business.dto.UserLicenseDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.utility.RandomUtilities;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Calendar;

import static org.junit.Assert.assertNotNull;

public class UserGroupBaseIT extends BaseServiceIT {

	@Autowired protected UserGroupService userGroupService;
	@Autowired protected UserUserGroupAssociationService userUserGroupAssociationService;
	@Autowired protected UserUserGroupDocumentReferenceService userUserGroupDocumentReferenceService;
	@Autowired protected InsuranceService insuranceService;
	@Autowired UserCertificationAssociationDAO userCertificationAssociationDAO;

	protected Asset savedAsset;
	protected User worker, buyer;
	protected UserGroup group1, group2, group3, group4, groupWithReqs;
	protected Company company;
	protected Work work;

	protected void createGroupWithReqs() throws HostServiceException, IOException, AssetTransformationException {
		groupWithReqs = saveOrUpdateGroup(null, COMPANY_ID, REQUIRES_APPROVAL, OPEN_MEMBERSHIP, ANONYMOUS_USER_ID);

		// TODO - Micah - this relies on AWS services being available. Should Mock AWS services asap.
		AssetDTO dto = newAssetDTO();
		Asset asset = dto.toAsset();
		savedAsset = assetManagementService.storeAsset(dto, asset, true);
		dto.setAssetId(asset.getId());

		assetManagementService.addAssetToCompany(dto, COMPANY_ID);
	}

	String newUserGroupName() {
		return "userGroup " + RandomUtilities.nextLong();
	}

	protected static final boolean REQUIRES_APPROVAL = true;
	protected static final boolean DOES_NOT_REQUIRE_APPROVAL = false;
	protected static final boolean OPEN_MEMBERSHIP = true;
	protected static final boolean CLOSED_MEMBERSHIP = false;

	protected UserGroup saveOrUpdateGroup(Long userGroupId, long companyID, boolean requiresApproval, boolean isOpenMembership, long ownerId) {
		UserGroupDTO userGroupDTO = new UserGroupDTO();
		if (userGroupId != null) {
			userGroupDTO.setUserGroupId(userGroupId);
		}
		userGroupDTO.setCompanyId(companyID);
		userGroupDTO.setName(newUserGroupName());
		userGroupDTO.setDescription("description");
		userGroupDTO.setRequiresApproval(requiresApproval);
		userGroupDTO.setOpenMembership(isOpenMembership);
		userGroupDTO.setOwnerId(ownerId);
		UserGroup userGroup = userGroupService.saveOrUpdateCompanyUserGroup(userGroupDTO);
		assertNotNull(userGroup);
		return userGroup;
	}

	protected UserGroup generateUserGroup(long companyID, boolean approval, boolean membership, long ownerId) {
		return saveOrUpdateGroup(null, companyID, approval, membership, ownerId);
	}

	protected UserGroup generateUserGroup(long companyID) {
		return saveOrUpdateGroup(null, companyID, false, true, ANONYMOUS_USER_ID);
	}

	protected void saveDocumentReference(User user, UserGroup userGroup, Asset requiredAsset, String dateStr) throws Exception {
		AssetDTO dto = newAssetDTO();
		dto.setName(requiredAsset.getName());
		Asset referencedAsset = dto.toAsset();
		assetManagementService.storeAsset(dto, referencedAsset, true);
		dto.setAssetId(referencedAsset.getId());
		assetManagementService.addAssetToUser(dto, user.getId());

		userUserGroupDocumentReferenceService.saveDocumentReference(
				user.getId(), userGroup.getId(), requiredAsset.getId(), referencedAsset.getId(), dateStr
		);
	}

	protected Long saveLicenseWithExpiration(Long resourceId, Long licenseId, String licenseNumber, String name, Calendar expiration) throws Exception {
		LicenseDTO license = new LicenseDTO();
		// driver's license
		license.setLicenseId(licenseId);
		license.setLicenseNumber(licenseNumber);

		UserLicenseDTO userLicense = new UserLicenseDTO();
		userLicense.setLicenseNumber(licenseNumber);
		if (expiration != null) {
			userLicense.setExpirationDate(expiration);
		}

		UserLicenseAssociation userLicenseAssoc = licenseService.saveOrUpdateUserLicense(licenseId, resourceId, userLicense);
		licenseService.updateUserLicenseAssociationStatus(licenseId, resourceId, VerificationStatus.VERIFIED);

		AssetDTO dto = newAssetDTO();
		dto.setName(name);
		assetManagementService.storeAssetForUserLicense(dto, userLicenseAssoc.getId());

		return userLicenseAssoc.getId();
	}

	protected Long saveLicense(Long resourceId, Long licenseId, String licenseNumber, String name) throws Exception {
		return saveLicenseWithExpiration(resourceId, licenseId, licenseNumber, name, null);
	}

	protected Long saveInsurance(Long resourceId, Long insuranceId, String name) throws Exception {
		InsuranceDTO insurance = new InsuranceDTO();
		insurance.setInsuranceId(insuranceId);
		insurance.setProvider("insurance provider");
		insurance.setPolicyNumber("insurance policy number");
		insurance.setCoverage("123456");

		UserInsuranceAssociation association = insuranceService.addInsuranceToUser(resourceId, insurance);

		AssetDTO dto = newAssetDTO();
		dto.setName(name);
		assetManagementService.storeAssetForUserInsurance(dto, association.getId());

		return association.getId();
	}

	protected Long saveCertification(Long resourceId, Long certificationId, String name) throws Exception {
		UserCertificationDTO certification = new UserCertificationDTO();
		certification.setCertificationNumber("");

		return saveCertification(resourceId, certificationId, certification, name);
	}

	protected Long saveCertification(Long resourceId, Long certificationId, UserCertificationDTO certification, String name) throws Exception {
		UserCertificationAssociation userCertificationAssociation =
			certificationService.saveOrUpdateUserCertification(certificationId, resourceId, certification);
		certificationService.updateUserCertificationAssociationStatus(
			userCertificationAssociation.getCertification().getId(),
			userCertificationAssociation.getUser().getId(),
			VerificationStatus.VERIFIED
		);

		AssetDTO dto = newAssetDTO();
		dto.setName(name);
		assetManagementService.storeAssetForUserCertification(dto, userCertificationAssociation.getId());

		return userCertificationAssociation.getId();
	}

	protected Long saveInsurance(Long resourceId, InsuranceDTO insurance, String name) throws Exception {
		insurance.setProvider("ABC");
		insurance.setPolicyNumber("DEF");
		insurance.setCoverage("10000");

		UserInsuranceAssociation userInsuranceAssociation =
			insuranceService.addInsuranceToUser(resourceId, insurance);
		insuranceService.updateUserInsuranceVerificationStatus(
			userInsuranceAssociation.getId(),
			VerificationStatus.VERIFIED
		);

		AssetDTO dto = newAssetDTO();
		dto.setName(name);
		assetManagementService.storeAssetForUserInsurance(dto, userInsuranceAssociation.getId());

		return userInsuranceAssociation.getId();
	}
}
