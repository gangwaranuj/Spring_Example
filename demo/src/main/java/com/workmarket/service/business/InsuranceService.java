package com.workmarket.service.business;

import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.insurance.Insurance;
import com.workmarket.domains.model.insurance.InsurancePagination;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.insurance.UserInsuranceAssociationPagination;
import com.workmarket.service.business.dto.InsuranceDTO;

import java.util.List;

public interface InsuranceService {
	Insurance findInsurance(Long insuranceId);

	List<Insurance> findVerifiedInsuranceByIndustry(Long industryId);
	InsurancePagination findVerifiedInsuranceByIndustry(Long industryId, InsurancePagination pagination);

	UserInsuranceAssociation addInsuranceToUser(Long userId, InsuranceDTO dto);
	UserInsuranceAssociation updateUserInsuranceAssociation(Long associationId, InsuranceDTO dto);
	UserInsuranceAssociation findActiveVerifiedAssociationByInsuranceIdAndUserId(Long insuranceId, Long userId);

	void removeInsuranceFromUser(Long associationId);
	void updateUserInsuranceVerificationStatus(Long associationId, VerificationStatus status);

	void verifyInsurance(Long insuranceId);
	void rejectInsurance(Long insuranceId);
	void updateInsuranceVerificationStatus(Long insuranceId, VerificationStatus status);

	UserInsuranceAssociation findUserInsuranceAssociation(Long associationId);
	UserInsuranceAssociationPagination findVerifiedInsuranceAssociationsByUser(Long userId, UserInsuranceAssociationPagination pagination);
	UserInsuranceAssociationPagination findUnverifiedInsuranceAssociationsByUser(Long userId, UserInsuranceAssociationPagination pagination);

	UserInsuranceAssociationPagination findAllUserInsuranceAssociations(UserInsuranceAssociationPagination pagination);
	UserInsuranceAssociationPagination findAllUserInsuranceAssociationsByUserId(Long userId, UserInsuranceAssociationPagination pagination);

	InsurancePagination findAllInsurances(InsurancePagination pagination);

	List<Insurance> findAllInsuranceByUserIdInList(long userId, List<Long> ids);

	UserInsuranceAssociationPagination findAllAssociationsByUserIdInList(Long userId, List<Long> insuranceIds, UserInsuranceAssociationPagination pagination);

	void updateUserInsuranceAssociationBuyerNotified(Long userInsuranceAssociationId);

	UserInsuranceAssociationPagination findByUserAndInsurance(Long userId, Long insuranceId, UserInsuranceAssociationPagination pagination);
}
