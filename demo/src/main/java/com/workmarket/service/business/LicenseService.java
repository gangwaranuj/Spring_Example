package com.workmarket.service.business;

import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.license.LicensePagination;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociationPagination;
import com.workmarket.service.business.dto.LicenseDTO;
import com.workmarket.service.business.dto.UserLicenseDTO;

import java.util.List;
import java.util.Set;

public interface LicenseService {

	License saveOrUpdateLicense(LicenseDTO licenseDTO) throws Exception;

	License findLicenseById(Long licenseId);

	License findLicenseByName(String name);

	UserLicenseAssociation addLicenseToUser(Long licenseId, Long userId, String licenseNumber) throws Exception;

	UserLicenseAssociation saveOrUpdateUserLicense(Long licenseId, Long userId, UserLicenseDTO userLicenseDTO) throws Exception;

	UserLicenseAssociation findAssociationByLicenseIdAndUserId(Long licenseId, Long userId);
	UserLicenseAssociation findActiveVerifiedAssociationByLicenseIdAndUserId(Long licenseId, Long userId);

	void removeLicenseFromUser(Long licenseId, Long userId) throws Exception;

	LicensePagination findAllLicenses(LicensePagination licensePagination);

	UserLicenseAssociationPagination findAllUserLicenseAssociations(UserLicenseAssociationPagination licensePagination);

	LicensePagination findAllLicensesByStateId(String stateId, LicensePagination pagination);

	UserLicenseAssociationPagination findAllAssociationsByUserId(Long userId, UserLicenseAssociationPagination licensePagination);

	UserLicenseAssociationPagination findAllVerifiedAssociationsByUserIds(Set<Long> userId, UserLicenseAssociationPagination licensePagination);

	void updateUserLicenseAssociationStatus(Long licenseId, Long userId, VerificationStatus status) throws Exception;

	void verifyLicense(Long licenseId);
	void rejectLicense(Long licenseId);
	void updateLicenseVerificationStatus(Long licenseId, VerificationStatus status);

	List<License> findAllLicensesByUserIdInList(long userId, List<Long> ids);

	UserLicenseAssociationPagination findAllAssociationsByUserIdInList(Long userId, List<Long> licenseIds, UserLicenseAssociationPagination licensePagination);

	void updateUserLicenseAssociationBuyerNotified(Long userLicenseAssociationId);
}
