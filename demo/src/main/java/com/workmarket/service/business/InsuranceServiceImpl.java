package com.workmarket.service.business;

import com.workmarket.dao.UserDAO;
import com.workmarket.dao.insurance.InsuranceDAO;
import com.workmarket.dao.insurance.UserInsuranceAssociationDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.insurance.Insurance;
import com.workmarket.domains.model.insurance.InsurancePagination;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.insurance.UserInsuranceAssociationPagination;
import com.workmarket.service.business.dto.InsuranceDTO;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InsuranceServiceImpl implements InsuranceService {

	@Autowired private InsuranceDAO insuranceDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private UserInsuranceAssociationDAO userInsuranceAssociationDAO;
	@Autowired private UserIndexer userIndexer;
	@Autowired private UserGroupValidationService userGroupValidationService;

	private static final Log logger = LogFactory.getLog(InsuranceServiceImpl.class);

	@Override
	public Insurance findInsurance(Long insuranceId) {
		Assert.notNull(insuranceId);
		return insuranceDAO.get(insuranceId);
	}

	@Override
	public List<Insurance> findVerifiedInsuranceByIndustry(Long industryId) {
		return findVerifiedInsuranceByIndustry(industryId, new InsurancePagination(true)).getResults();
	}

	@Override
	public InsurancePagination findVerifiedInsuranceByIndustry(Long industryId, InsurancePagination pagination) {
		Assert.notNull(industryId);
		pagination.getFilters().put(InsurancePagination.FILTER_KEYS.VERIFICATION_STATUS.toString(), VerificationStatus.VERIFIED.toString());
		return insuranceDAO.findByIndustry(industryId, pagination);
	}

	public UserInsuranceAssociation addInsuranceToUser(Long userId, InsuranceDTO dto) {
		Assert.notNull(userId);
		Assert.notNull(dto);
		Assert.notNull(dto.getInsuranceId());

		User user = userDAO.get(userId);
		Insurance insurance = findInsurance(dto.getInsuranceId());

		Assert.notNull(insurance);

		UserInsuranceAssociation association = new UserInsuranceAssociation(user, insurance);
		association.setVerificationStatus(VerificationStatus.PENDING);
		BeanUtils.copyProperties(dto, association);
		userInsuranceAssociationDAO.saveOrUpdate(association);

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.INSURANCE_ADDED, insurance.getId());
		userGroupValidationService.revalidateAllAssociationsByUserAsync(userId, params);

		userIndexer.reindexById(userId);
		return association;
	}

	@Override
	public UserInsuranceAssociation updateUserInsuranceAssociation(Long associationId, InsuranceDTO dto) {
		Assert.notNull(associationId);
		Assert.notNull(dto);
		Assert.notNull(dto.getInsuranceId());

		UserInsuranceAssociation association = userInsuranceAssociationDAO.findById(associationId);
		Insurance insurance = findInsurance(dto.getInsuranceId());

		Assert.notNull(association);
		association.setInsurance(insurance);
		association.setVerificationStatus(VerificationStatus.PENDING);
		BeanUtils.copyProperties(dto, association);

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.INSURANCE_ADDED, insurance.getId());
		userGroupValidationService.revalidateAllAssociationsByUserAsync(association.getUser().getId(), params);

		userIndexer.reindexById(association.getUser().getId());
		return association;
	}

	@Override
	public UserInsuranceAssociation findActiveVerifiedAssociationByInsuranceIdAndUserId(Long insuranceId, Long userId) {
		return userInsuranceAssociationDAO.findBy(
			"insurance.id", insuranceId,
			"user.id", userId,
			"verificationStatus", VerificationStatus.VERIFIED,
			"deleted", false
		);
	}

	public void removeInsuranceFromUser(Long associationId) {
		UserInsuranceAssociation association = findUserInsuranceAssociation(associationId);
		Assert.notNull(association);
		association.setDeleted(true);
		userIndexer.reindexById(association.getUser().getId());
	}

	@Override
	public void updateUserInsuranceVerificationStatus(Long associationId, VerificationStatus status) {
		UserInsuranceAssociation association = findUserInsuranceAssociation(associationId);
		Assert.notNull(association);
		association.setVerificationStatus(status);
		association.setLastActivityOn(DateUtilities.getCalendarNow());
		userIndexer.reindexById(association.getUser().getId());
	}

	@Override
	public void verifyInsurance(Long insuranceId) {
		updateInsuranceVerificationStatus(insuranceId, VerificationStatus.VERIFIED);
	}

	@Override
	public void rejectInsurance(Long insuranceId) {
		updateInsuranceVerificationStatus(insuranceId, VerificationStatus.FAILED);
	}

	public void updateInsuranceVerificationStatus(Long insuranceId, VerificationStatus status) {
		Insurance insurance = findInsurance(insuranceId);
		Assert.notNull(insurance);
		insurance.setVerificationStatus(status);
		insurance.setLastActivityOn(DateUtilities.getCalendarNow());
	}

	public UserInsuranceAssociation findUserInsuranceAssociation(Long associationId) {
		Assert.notNull(associationId);
		return userInsuranceAssociationDAO.findById(associationId);
	}

	public UserInsuranceAssociationPagination findVerifiedInsuranceAssociationsByUser(Long userId, UserInsuranceAssociationPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		return userInsuranceAssociationDAO.findVerifiedByUser(userId, pagination);
	}

	public UserInsuranceAssociationPagination findUnverifiedInsuranceAssociationsByUser(Long userId, UserInsuranceAssociationPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		return userInsuranceAssociationDAO.findUnverifiedByUser(userId, pagination);
	}

	@Override
	public UserInsuranceAssociationPagination findAllUserInsuranceAssociations(UserInsuranceAssociationPagination pagination) {
		Assert.notNull(pagination);
		return userInsuranceAssociationDAO.findAllUserInsuranceAssociations(pagination);
	}

	@Override
	public UserInsuranceAssociationPagination findAllUserInsuranceAssociationsByUserId(Long userId, UserInsuranceAssociationPagination pagination) {
		Assert.notNull(pagination);
		return userInsuranceAssociationDAO.findByUser(userId, pagination);
	}

	@Override
	public UserInsuranceAssociationPagination findAllAssociationsByUserIdInList(Long userId, List<Long> insuranceIds, UserInsuranceAssociationPagination pagination) {
		return userInsuranceAssociationDAO.findAllAssociationsByUserIdInList(userId, insuranceIds, pagination);
	}

	@Override
	public UserInsuranceAssociationPagination findByUserAndInsurance(Long userId, Long insuranceId, UserInsuranceAssociationPagination pagination) {
		return userInsuranceAssociationDAO.findByUserAndInsurance(userId, insuranceId, pagination);
	}

	public InsurancePagination findAllInsurances(InsurancePagination pagination) {
		Assert.notNull(pagination);
		return insuranceDAO.findAllInsurances(pagination);
	}

	@Override
	public List<Insurance> findAllInsuranceByUserIdInList(long userId, List<Long> ids) {
		return userInsuranceAssociationDAO.findAllInsuranceByUserIdInList(userId, ids);
	}

	@Override
	public void updateUserInsuranceAssociationBuyerNotified(Long userInsuranceAssociationId) {
		Assert.notNull(userInsuranceAssociationId);

		UserInsuranceAssociation userInsuranceAssociation = userInsuranceAssociationDAO.findBy("id", userInsuranceAssociationId);
		Assert.notNull(userInsuranceAssociation);

		userInsuranceAssociation.setBuyerNotifiedOnExpiry(userInsuranceAssociation.getBuyerNotifiedOnExpiry() + 1);
	}
}
