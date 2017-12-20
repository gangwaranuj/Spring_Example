package com.workmarket.service.business;

import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.certification.*;
import com.workmarket.service.business.dto.CertificationDTO;
import com.workmarket.service.business.dto.CertificationVendorDTO;
import com.workmarket.service.business.dto.UserCertificationDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CertificationService {

	/**
	 * Returns All Certification Vendors.
	 *
	 * @param pagination
	 * @return
	 */
	CertificationVendorPagination findAllCertificationVendors(CertificationVendorPagination pagination);

	/**
	 * Returns only verified Certification Vendors by Type (Industry).
	 *
	 * @param pagination
	 * @param certificationTypeId
	 * @return
	 */
	CertificationVendorPagination findCertificationVendorByTypeId(CertificationVendorPagination pagination, Long certificationTypeId);

	CertificationVendor findCertificationVendorById(Long vendorId);

	CertificationVendor findCertificationVendorByNameAndIndustryId(String name, Long industryId);

	CertificationVendor saveOrUpdateCertificationVendor(CertificationVendorDTO certificationVendorDTO, Long industryId) throws Exception;

	//Certifications
	Certification findCertificationById(Long certificationId);

	Certification saveOrUpdateCertification(CertificationDTO certificationDTO) throws Exception;

	Certification findCertificationByName(String name);

	/**
	 * Returns all Certifications.
	 *
	 * @param certificationPagination
	 * @return
	 */
	CertificationPagination findAllCertifications(CertificationPagination certificationPagination);

	/**
	 * Returns only verified Certification by Vendor.
	 *
	 * @param certificationVendorId
	 * @param certificationPagination
	 * @return
	 */
	CertificationPagination findAllCertificationByVendor(Long certificationVendorId, CertificationPagination certificationPagination);

	//User Certifications

	UserCertificationAssociation findAssociationByCertificationIdAndUserId(Long certificationId, Long userId);

	UserCertificationAssociation findActiveVerifiedAssociationByCertificationIdAndUserId(Long certificationId, Long userId);

	UserCertificationAssociation findActiveAssociationByCertificationIdAndUserId(Long certificationId, Long userId);

	UserCertificationAssociation addCertificationToUser(Long certificationId, Long userId, String certificationNumber) throws Exception;

	UserCertificationAssociation saveOrUpdateUserCertification(Long certificationId, Long userId, UserCertificationDTO userCertificationDTO) throws Exception;

	void removeCertificationFromUser(Long certificationId, Long userId) throws Exception;

	UserCertificationAssociationPagination findAllVerifiedCertificationsByUserId(Long userId, UserCertificationAssociationPagination pagination);

	UserCertificationAssociationPagination findAllPendingCertificationsByUserId(Long userId, UserCertificationAssociationPagination pagination);

	UserCertificationAssociationPagination findAllUserCertifications(UserCertificationAssociationPagination pagination);

	UserCertificationAssociationPagination findAllAssociationsByUserId(Long userId, UserCertificationAssociationPagination pagination);

	UserCertificationAssociationPagination findAllVerifiedCertificationsByUserIds(Set<Long> userIds, UserCertificationAssociationPagination pagination);

		//Approval methods
	void updateUserCertificationAssociationStatus(Long certificationId, Long userId, VerificationStatus status) throws Exception;

	void verifyCertification(Long certificationId);
	void rejectCertification(Long certificationId);
	void updateCertificationStatus(Long certificationId, VerificationStatus status);

	void verifyCertificationVendor(Long certificationVendorId);
	void rejectCertificationVendor(Long certificationVendorId);
	void updateCertificationVendorStatus(Long certificationVendorId, VerificationStatus status);

	List<Certification> findAllCertificationsByUserIdInList(long userId, List<Long> ids);

	UserCertificationAssociationPagination findAllAssociationsByUserIdInList(Long userId, List<Long> certificationIds, UserCertificationAssociationPagination pagination);

	void updateUserCertificationAssociationBuyerNotified(Long userCertificationAssociationId);

	List<Map<String, String>> findAllCertificationFacetsForTypeAheadFilter(String typeAheadFilter);
}
