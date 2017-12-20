package com.workmarket.service.business;

import com.workmarket.dao.UserDAO;
import com.workmarket.dao.certification.CertificationDAO;
import com.workmarket.dao.certification.CertificationVendorDAO;
import com.workmarket.dao.certification.UserCertificationAssociationDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.certification.*;
import com.workmarket.service.business.dto.CertificationDTO;
import com.workmarket.service.business.dto.CertificationVendorDTO;
import com.workmarket.service.business.dto.UserCertificationDTO;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class CertificationServiceImpl implements CertificationService {

	@Autowired private CertificationVendorDAO certificationVendorDAO;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private CertificationDAO certificationDAO;
	@Autowired private UserCertificationAssociationDAO userCertificationAssociationDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private UserGroupValidationService userGroupValidationService;
	@Autowired private UserIndexer userIndexer;

	@Override
	public CertificationVendorPagination findAllCertificationVendors(CertificationVendorPagination pagination) {
		return certificationVendorDAO.findAllCertificationVendors(pagination);
	}

	@Override
	public CertificationVendorPagination findCertificationVendorByTypeId(CertificationVendorPagination pagination, Long certificationTypeId) {
		return certificationVendorDAO.findCertificationVendorByTypeId(pagination, certificationTypeId);
	}

	@Override
	public CertificationVendor findCertificationVendorById(Long vendorId) {
		return certificationVendorDAO.findCertificationVendorById(vendorId);
	}

	@Override
	public CertificationVendor findCertificationVendorByNameAndIndustryId(String name, Long industryId) {
		Assert.notNull(industryId);
		Assert.hasText(name);
		return certificationVendorDAO.findCertificationVendorByNameAndIndustryId(name, industryId);
	}

	@Override
	public CertificationVendor saveOrUpdateCertificationVendor(CertificationVendorDTO certificationVendorDTO, Long industryId) throws Exception {
		Assert.notNull(certificationVendorDTO);
		Assert.notNull(certificationVendorDTO.getName());

		CertificationVendor vendor = null;
		Industry industry = invariantDataService.findIndustry(industryId);
		Assert.notNull(industry);

		vendor = findCertificationVendorByNameAndIndustryId(certificationVendorDTO.getName(), industryId);
		if (vendor != null) {
			vendor.setDeleted(false);
			return vendor;
		}

		if (certificationVendorDTO.getCertificationVendorId() == null) {
			vendor = BeanUtilities.newBean(CertificationVendor.class, certificationVendorDTO);

		} else {
			vendor = findCertificationVendorById(certificationVendorDTO.getCertificationVendorId());
			BeanUtilities.copyProperties(vendor, certificationVendorDTO);
		}

		vendor.setCertificationType(industry);
		certificationVendorDAO.saveOrUpdate(vendor);

		return vendor;

	}

	@Override
	public Certification findCertificationById(Long certificationId) {
		return certificationDAO.findCertificationById(certificationId);
	}

	@Override
	public CertificationPagination findAllCertifications(CertificationPagination certificationPagination) {
		return certificationDAO.findAllCertifications(certificationPagination);
	}

	@Override
	public CertificationPagination findAllCertificationByVendor(Long certificationVendorId, CertificationPagination certificationPagination) {
		if (certificationPagination.getFilters() == null)
			certificationPagination.setFilters(new HashMap<String, String>());

		certificationPagination.getFilters().put(CertificationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString(), VerificationStatus.VERIFIED.toString());
		certificationPagination.getFilters().put(CertificationPagination.FILTER_KEYS.VENDOR_ID.toString(), certificationVendorId.toString());

		return certificationDAO.findAllCertifications(certificationPagination);
	}

	@Override
	public UserCertificationAssociation findAssociationByCertificationIdAndUserId(Long certificationId, Long userId) {
		return userCertificationAssociationDAO.findAssociationByCertificationIdAndUserId(certificationId, userId);
	}

	@Override
	public UserCertificationAssociation findActiveAssociationByCertificationIdAndUserId(Long certificationId, Long userId) {
		return userCertificationAssociationDAO.findBy(
			"certification.id", certificationId,
			"user.id", userId,
			"deleted", false
		);
	}

	@Override
	public UserCertificationAssociation findActiveVerifiedAssociationByCertificationIdAndUserId(Long certificationId, Long userId) {
		return userCertificationAssociationDAO.findBy(
			"certification.id", certificationId,
			"user.id", userId,
			"verificationStatus", VerificationStatus.VERIFIED,
			"deleted", false
		);
	}

	@Override
	public Certification saveOrUpdateCertification(CertificationDTO certificationDTO) throws Exception {
		Assert.notNull(certificationDTO);
		Assert.notNull(certificationDTO.getName());
		Assert.notNull(certificationDTO.getCertificationVendorId());

		Certification certification = null;
		// Check if it already exists, maybe some other user already enter this certification name
		/*
		 * Use case: User A adds the new certification and it's in PENDING status. User B wants to add the same certification but since he can't see it in the dropdown, he tries to add it again.
		 */
		certification = certificationDAO.findCertificationByNameAndVendorId(certificationDTO.getName(),
				certificationDTO.getCertificationVendorId());

		if (certification != null) {
			certification.setDeleted(false);
			return certification;
		}

		if (certificationDTO.getCertificationId() == null) {

			certification = BeanUtilities.newBean(Certification.class, certificationDTO);

			if (certificationDTO.getCertificationVendorId() != null) {
				certification.setCertificationVendor(certificationVendorDAO.findCertificationVendorById(certificationDTO.getCertificationVendorId()));
			}

		} else {
			certification = certificationDAO.findCertificationById(certificationDTO.getCertificationId());
			Assert.notNull(certification);
			BeanUtilities.copyProperties(certification, certificationDTO);

			if (!BeanUtilities.getId(certification.getCertificationVendor()).equals(certificationDTO.getCertificationVendorId()))
				certification.setCertificationVendor(certificationVendorDAO
						.findCertificationVendorById(certificationDTO.getCertificationVendorId()));
		}

		certificationDAO.saveOrUpdate(certification);
		return certification;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserCertificationAssociation addCertificationToUser(Long certificationId, Long userId, String certificationNumber) throws Exception {
		Assert.notNull(certificationId);
		Assert.notNull(userId);

		UserCertificationAssociation userCertificationAssociation = userCertificationAssociationDAO
				.findAssociationByCertificationIdAndUserId(certificationId, userId);

		if (userCertificationAssociation == null) {

			Certification certification = certificationDAO.findCertificationById(certificationId);
			User user = userDAO.get(userId);

			Assert.notNull(certification);
			Assert.notNull(user);

			userCertificationAssociation = new UserCertificationAssociation(user, certification, certificationNumber);

			userCertificationAssociationDAO.saveOrUpdate(userCertificationAssociation);

		} else {
			userCertificationAssociation.setCertificationNumber(certificationNumber);
			userCertificationAssociation.setVerificationStatus(VerificationStatus.PENDING);
			userCertificationAssociation.setDeleted(false);
		}

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.CERTIFICATION, certificationId);

		userGroupValidationService.revalidateAllAssociationsByUserAsync(userId, params);
		userIndexer.reindexById(userId);

		return userCertificationAssociation;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserCertificationAssociation saveOrUpdateUserCertification(Long certificationId, Long userId, UserCertificationDTO userCertificationDTO) throws Exception {
		Assert.notNull(certificationId);
		Assert.notNull(userId);

		UserCertificationAssociation userCertificationAssociation = userCertificationAssociationDAO
				.findAssociationByCertificationIdAndUserId(certificationId, userId);

		if (userCertificationAssociation == null) {

			Certification certification = certificationDAO.findCertificationById(certificationId);
			User user = userDAO.get(userId);

			Assert.notNull(certification);
			Assert.notNull(user);

			userCertificationAssociation = new UserCertificationAssociation(user, certification);
		}

		userCertificationAssociation.setIssueDate(userCertificationDTO.getIssueDate());
		userCertificationAssociation.setExpirationDate(userCertificationDTO.getExpirationDate());
		userCertificationAssociation.setCertificationNumber(userCertificationDTO.getCertificationNumber());
		userCertificationAssociation.setVerificationStatus(VerificationStatus.PENDING);

		userCertificationAssociation.setDeleted(false);
		userCertificationAssociationDAO.saveOrUpdate(userCertificationAssociation);

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.CERTIFICATION, certificationId);

		userGroupValidationService.revalidateAllAssociationsByUserAsync(userId, params);
		userIndexer.reindexById(userId);

		return userCertificationAssociation;
	}

	@Override
	public void removeCertificationFromUser(Long certificationId, Long userId) throws Exception {
		Assert.notNull(certificationId);
		Assert.notNull(userId);

		UserCertificationAssociation userCertificationAssociation = userCertificationAssociationDAO
				.findAssociationByCertificationIdAndUserId(certificationId, userId);

		Assert.notNull(userCertificationAssociation);

		userCertificationAssociation.setDeleted(true);

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.CERTIFICATION, certificationId);

		userGroupValidationService.revalidateAllAssociationsByUserAsync(userId, params);
		userIndexer.reindexById(userId);
	}

	@Override
	public Certification findCertificationByName(String name) {
		return certificationDAO.findCertificationByName(name);
	}

	@Override
	public UserCertificationAssociationPagination findAllVerifiedCertificationsByUserId(Long userId, UserCertificationAssociationPagination pagination) {
		if (pagination.getFilters() == null)
			pagination.setFilters(new HashMap<String, String>());

		pagination.getFilters().put(UserCertificationAssociationPagination.FILTER_KEYS.USER_ID.toString(), userId.toString());
		pagination.getFilters().put(UserCertificationAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString(), VerificationStatus.VERIFIED.toString());

		return userCertificationAssociationDAO.findAllUserCertifications(pagination);

	}

	@Override
	public UserCertificationAssociationPagination findAllVerifiedCertificationsByUserIds(Set<Long> userIds, UserCertificationAssociationPagination pagination) {
		if (pagination.getFilters() == null)
			pagination.setFilters(new HashMap<String, String>());

		pagination.getFilters().put(UserCertificationAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString(), VerificationStatus.VERIFIED.toString());

		return userCertificationAssociationDAO.findAllUserCertificationsByUserIds(userIds, pagination);
	}


	@Override
	public UserCertificationAssociationPagination findAllPendingCertificationsByUserId(Long userId, UserCertificationAssociationPagination pagination) {
		if (pagination.getFilters() == null)
			pagination.setFilters(new HashMap<String, String>());

		pagination.getFilters().put(UserCertificationAssociationPagination.FILTER_KEYS.USER_ID.toString(), userId.toString());
		pagination.getFilters().put(UserCertificationAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString(), VerificationStatus.UNVERIFIED.toString());

		return userCertificationAssociationDAO.findAllUserCertifications(pagination);
	}

	@Override
	public UserCertificationAssociationPagination findAllUserCertifications(UserCertificationAssociationPagination pagination) {
		return userCertificationAssociationDAO.findAllUserCertifications(pagination);
	}

	@Override
	public UserCertificationAssociationPagination findAllAssociationsByUserId(Long userId, UserCertificationAssociationPagination pagination) {
		if (pagination.getFilters() == null)
			pagination.setFilters(new HashMap<String, String>());

		pagination.getFilters().put(UserCertificationAssociationPagination.FILTER_KEYS.USER_ID.toString(), userId.toString());

		return userCertificationAssociationDAO.findAllUserCertifications(pagination);
	}

	@Override
	public UserCertificationAssociationPagination findAllAssociationsByUserIdInList(Long userId, List<Long> certificationIds, UserCertificationAssociationPagination pagination) {
		return userCertificationAssociationDAO.findAllAssociationsByUserIdInList(userId, certificationIds, pagination);
	}

	@Override
	public void verifyCertification(Long certificationId) {
		updateCertificationStatus(certificationId, VerificationStatus.VERIFIED);
	}

	@Override
	public void rejectCertification(Long certificationId) {
		updateCertificationStatus(certificationId, VerificationStatus.FAILED);
	}

	@Override
	public void verifyCertificationVendor(Long certificationVendorId) {
		updateCertificationVendorStatus(certificationVendorId, VerificationStatus.VERIFIED);
	}

	@Override
	public void rejectCertificationVendor(Long certificationVendorId) {
		updateCertificationVendorStatus(certificationVendorId, VerificationStatus.FAILED);
	}

	@Override
	public void updateUserCertificationAssociationStatus(Long certificationId, Long userId, VerificationStatus status) throws Exception {
		Assert.notNull(certificationId);
		Assert.notNull(userId);

		UserCertificationAssociation userCertificationAssociation = userCertificationAssociationDAO
			.findAssociationByCertificationIdAndUserId(certificationId, userId);

		Assert.notNull(userCertificationAssociation);

		userCertificationAssociation.setVerificationStatus(status);
		userCertificationAssociation.setLastActivityOn(DateUtilities.getCalendarNow());

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.CERTIFICATION, certificationId);

		userGroupValidationService.revalidateAllAssociationsByUserAsync(userId, params);
		userIndexer.reindexById(userId);
	}

	@Override
	public void updateUserCertificationAssociationBuyerNotified(Long userCertificationAssociationId) {
		Assert.notNull(userCertificationAssociationId);

		UserCertificationAssociation userCertificationAssociation = userCertificationAssociationDAO.findBy("id", userCertificationAssociationId);
		Assert.notNull(userCertificationAssociation);

		userCertificationAssociation.setBuyerNotifiedOnExpiry(userCertificationAssociation.getBuyerNotifiedOnExpiry() + 1);
	}

	@Override
	public void updateCertificationStatus(Long certificationId, VerificationStatus status) {
		Assert.notNull(certificationId);

		Certification certification = findCertificationById(certificationId);
		Assert.notNull(certification);
		certification.setVerificationStatus(status);
		certification.setLastActivityOn(DateUtilities.getCalendarNow());
	}

	@Override
	public void updateCertificationVendorStatus(Long certificationVendorId, VerificationStatus status) {
		Assert.notNull(certificationVendorId);

		CertificationVendor certificationVendor = findCertificationVendorById(certificationVendorId);
		Assert.notNull(certificationVendor);
		certificationVendor.setVerificationStatus(status);
		certificationVendor.setLastActivityOn(DateUtilities.getCalendarNow());
	}

	@Override
	public List<Certification> findAllCertificationsByUserIdInList(long userId, List<Long> ids) {
		return userCertificationAssociationDAO.findAllCertificationsByUserIdInList(userId, ids);
	}

	@Override
	public List<Map<String, String>> findAllCertificationFacetsForTypeAheadFilter(String typeAheadFilter) {
		Assert.notNull(typeAheadFilter);
		return userCertificationAssociationDAO.findAllCertificationFacetsByTypeAheadFilter(typeAheadFilter);
	}
}
