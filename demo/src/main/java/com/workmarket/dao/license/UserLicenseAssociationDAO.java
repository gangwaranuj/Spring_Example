package com.workmarket.dao.license;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociationPagination;

import java.util.List;
import java.util.Set;


public interface UserLicenseAssociationDAO extends DAOInterface<UserLicenseAssociation> {

    UserLicenseAssociation findAssociationById(Long id);

    UserLicenseAssociation findAssociationByLicenseIdAndUserId(Long licenseId, Long userId);

	UserLicenseAssociationPagination findAllUserLicenseAssociation(UserLicenseAssociationPagination pagination);

    UserLicenseAssociationPagination findAllAssociationsByUserId(Long userId, UserLicenseAssociationPagination licensePagination);

    UserLicenseAssociationPagination findAllAssociationsByUserIds(Set<Long> userIds, UserLicenseAssociationPagination licensePagination);

    List<License> findAllLicensesByUserIdInList(long userId, List<Long> ids);

	@SuppressWarnings("unchecked") UserLicenseAssociationPagination findAllAssociationsByUserIdInList(Long userId, List<Long> licenseIds, UserLicenseAssociationPagination pagination);
}
