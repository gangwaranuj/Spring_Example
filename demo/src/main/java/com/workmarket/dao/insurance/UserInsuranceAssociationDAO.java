package com.workmarket.dao.insurance;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.insurance.Insurance;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.insurance.UserInsuranceAssociationPagination;

import java.util.List;

public interface UserInsuranceAssociationDAO extends PaginatableDAOInterface<UserInsuranceAssociation> {
	
	UserInsuranceAssociation findById(Long id);
	
	UserInsuranceAssociationPagination findByUser(Long userId, UserInsuranceAssociationPagination pagination);
	UserInsuranceAssociationPagination findVerifiedByUser(final Long userId, UserInsuranceAssociationPagination pagination);
	UserInsuranceAssociationPagination findUnverifiedByUser(final Long userId, UserInsuranceAssociationPagination pagination);

	UserInsuranceAssociationPagination findByUserAndInsurance(Long userId, Long insuranceId, UserInsuranceAssociationPagination pagination);
	UserInsuranceAssociationPagination findAllUserInsuranceAssociations(UserInsuranceAssociationPagination pagination);
	
	List<Insurance> findAllInsuranceByUserIdInList(long userId, List<Long> ids);

	@SuppressWarnings("unchecked") UserInsuranceAssociationPagination findAllAssociationsByUserIdInList(Long userId, List<Long> insuranceIds, UserInsuranceAssociationPagination pagination);
}