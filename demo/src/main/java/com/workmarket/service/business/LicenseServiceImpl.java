package com.workmarket.service.business;

import com.workmarket.dao.UserDAO;
import com.workmarket.dao.license.LicenseDAO;
import com.workmarket.dao.license.UserLicenseAssociationDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.license.LicensePagination;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociationPagination;
import com.workmarket.service.business.dto.LicenseDTO;
import com.workmarket.service.business.dto.UserLicenseDTO;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LicenseServiceImpl implements LicenseService {

	@Autowired private LicenseDAO licenseDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private UserLicenseAssociationDAO userLicenseAssociationDAO;
	@Autowired private UserIndexer userIndexer;
	@Autowired private UserGroupValidationService userGroupValidationService;

	private static final Log logger = LogFactory.getLog(LicenseServiceImpl.class);

	@Override
	public License saveOrUpdateLicense(LicenseDTO licenseDTO) throws Exception {
		Assert.notNull(licenseDTO);
		Assert.notNull(licenseDTO.getName());
		Assert.notNull(licenseDTO.getDeleted());
		Assert.notNull(licenseDTO.getDeleted());

		License license;
		if (licenseDTO.getLicenseId() == null) {
			license = BeanUtilities.newBean(License.class, licenseDTO);
		} else {
			license = licenseDAO.get(licenseDTO.getLicenseId());
			BeanUtilities.copyProperties(license, licenseDTO);
		}

		licenseDAO.saveOrUpdate(license);
		return license;
	}

	@Override
	public License findLicenseById(Long licenseId) {
		return licenseDAO.findLicenseById(licenseId);
	}

	@Override
	public License findLicenseByName(String name) {
		return licenseDAO.findLicenseByName(name);
	}

	@Override
	public UserLicenseAssociation addLicenseToUser(Long licenseId, Long userId, String licenseNumber) throws Exception {
		Assert.notNull(licenseId);
		Assert.notNull(userId);

		UserLicenseAssociation userLicenseAssociation = userLicenseAssociationDAO
				.findAssociationByLicenseIdAndUserId(licenseId, userId);

		if (userLicenseAssociation == null) {
			User user = userDAO.get(userId);
			License license = findLicenseById(licenseId);

			Assert.notNull(license);
			Assert.notNull(user);

			userLicenseAssociation = new UserLicenseAssociation(user, license, licenseNumber);

			userLicenseAssociationDAO.saveOrUpdate(userLicenseAssociation);
		} else {
			userLicenseAssociation.setLicenseNumber(licenseNumber);
			userLicenseAssociation.setVerificationStatus(VerificationStatus.PENDING);
			userLicenseAssociation.setDeleted(false);
		}

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.LICENSE, licenseId);

		userGroupValidationService.revalidateAllAssociationsByUserAsync(userId, params);
		userIndexer.reindexById(userId);

		return userLicenseAssociation;
	}

	@Override
	public UserLicenseAssociation saveOrUpdateUserLicense(Long licenseId, Long userId, UserLicenseDTO userLicenseDTO) throws Exception {
		Assert.notNull(licenseId);
		Assert.notNull(userId);

		UserLicenseAssociation userLicenseAssociation = userLicenseAssociationDAO
				.findAssociationByLicenseIdAndUserId(licenseId, userId);

		if (userLicenseAssociation == null) {
			User user = userDAO.get(userId);
			License license = findLicenseById(licenseId);

			Assert.notNull(license);
			Assert.notNull(user);

			userLicenseAssociation = new UserLicenseAssociation(user, license);

		}

		// BeanUtilities.copyProperties(userLicenseAssociation, userLicenseDTO);
		userLicenseAssociation.setIssueDate(userLicenseDTO.getIssueDate());
		userLicenseAssociation.setExpirationDate(userLicenseDTO.getExpirationDate());
		userLicenseAssociation.setLicenseNumber(userLicenseDTO.getLicenseNumber());
		userLicenseAssociation.setVerificationStatus(VerificationStatus.PENDING);
		userLicenseAssociation.setDeleted(false);

		userLicenseAssociationDAO.saveOrUpdate(userLicenseAssociation);

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.LICENSE, licenseId);

		userGroupValidationService.revalidateAllAssociationsByUserAsync(userId, params);
		userIndexer.reindexById(userId);
		return userLicenseAssociation;
	}

	@Override
	public void removeLicenseFromUser(Long licenseId, Long userId) throws Exception {
		Assert.notNull(licenseId);
		Assert.notNull(userId);

		UserLicenseAssociation userLicenseAssociation = userLicenseAssociationDAO
				.findAssociationByLicenseIdAndUserId(licenseId, userId);

		Assert.notNull(userLicenseAssociation);

		userLicenseAssociation.setDeleted(true);
		userLicenseAssociation.setAssets(null);

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.LICENSE, licenseId);

		userGroupValidationService.revalidateAllAssociationsByUserAsync(userId, params);
		userIndexer.reindexById(userId);
	}

	@Override
	public LicensePagination findAllLicenses(LicensePagination licensePagination) {
		return licenseDAO.findAll(licensePagination);
	}

	@Override
	public UserLicenseAssociationPagination findAllUserLicenseAssociations(UserLicenseAssociationPagination licensePagination) {
		return userLicenseAssociationDAO.findAllUserLicenseAssociation(licensePagination);
	}

	@Override
	public LicensePagination findAllLicensesByStateId(String stateId, LicensePagination pagination) {
		return licenseDAO.findAllLicensesByStateId(stateId, pagination);
	}

	@Override
	public UserLicenseAssociationPagination findAllAssociationsByUserId(Long userId, UserLicenseAssociationPagination licensePagination) {
		return userLicenseAssociationDAO.findAllAssociationsByUserId(userId, licensePagination);
	}

	@Override
	public UserLicenseAssociationPagination findAllVerifiedAssociationsByUserIds(Set<Long> userIds, UserLicenseAssociationPagination licensePagination) {
		if (licensePagination.getFilters() == null)
			licensePagination.setFilters(new HashMap<String, String>());

		licensePagination.getFilters().put(UserLicenseAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString(), VerificationStatus.VERIFIED.toString());

		return userLicenseAssociationDAO.findAllAssociationsByUserIds(userIds, licensePagination);
	}

	@Override
	public UserLicenseAssociationPagination findAllAssociationsByUserIdInList(Long userId, List<Long> licenseIds, UserLicenseAssociationPagination licensePagination) {
		return userLicenseAssociationDAO.findAllAssociationsByUserIdInList(userId, licenseIds, licensePagination);
	}

	@Override
	public UserLicenseAssociation findAssociationByLicenseIdAndUserId(Long licenseId, Long userId) {
		return userLicenseAssociationDAO.findAssociationByLicenseIdAndUserId(licenseId, userId);
	}

	@Override
	public UserLicenseAssociation findActiveVerifiedAssociationByLicenseIdAndUserId(Long licenseId, Long userId) {
		// In this case "active", means NOT deleted. --Jim
		return userLicenseAssociationDAO.findBy(
			"user.id", userId,
			"license.id", licenseId,
			"verificationStatus", VerificationStatus.VERIFIED,
			"deleted", false
		);
	}


	@Override
	public void verifyLicense(Long licenseId) {
		updateLicenseVerificationStatus(licenseId, VerificationStatus.VERIFIED);
	}

	@Override
	public void rejectLicense(Long licenseId) {
		updateLicenseVerificationStatus(licenseId, VerificationStatus.FAILED);
	}

	@Override
	public void updateUserLicenseAssociationStatus(Long licenseId, Long userId, VerificationStatus status) throws Exception {
		Assert.notNull(licenseId);
		Assert.notNull(userId);

		UserLicenseAssociation userLicenseAssociation = userLicenseAssociationDAO
				.findAssociationByLicenseIdAndUserId(licenseId, userId);

		Assert.notNull(userLicenseAssociation);

		userLicenseAssociation.setVerificationStatus(status);
		userLicenseAssociation.setLastActivityOn(DateUtilities.getCalendarNow());

		if (status.isFailed()) {
			Map<String, Object> params = new HashMap<>();
			params.put(ProfileModificationType.LICENSE, licenseId);

			userGroupValidationService.revalidateAllAssociationsByUserAsync(userId, params);
		}
		userIndexer.reindexById(userId);
	}

	@Override
	public void updateLicenseVerificationStatus(Long licenseId, VerificationStatus status) {
		Assert.notNull(licenseId);
		License license = findLicenseById(licenseId);
		Assert.notNull(license);
		license.setVerificationStatus(status);
		license.setLastActivityOn(DateUtilities.getCalendarNow());
	}

	@Override
	public List<License> findAllLicensesByUserIdInList(long userId, List<Long> ids) {
		return userLicenseAssociationDAO.findAllLicensesByUserIdInList(userId, ids);
	}

	@Override
	public void updateUserLicenseAssociationBuyerNotified(Long userLicenseAssociationId) {
		Assert.notNull(userLicenseAssociationId);

		UserLicenseAssociation userLicenseAssociation = userLicenseAssociationDAO.findBy("id", userLicenseAssociationId);
		Assert.notNull(userLicenseAssociation);

		userLicenseAssociation.setBuyerNotifiedOnExpiry(userLicenseAssociation.getBuyerNotifiedOnExpiry() + 1);
	}
}
