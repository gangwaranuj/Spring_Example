package com.workmarket.dao.certification;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.certification.Certification;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.certification.UserCertificationAssociationPagination;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface UserCertificationAssociationDAO extends DAOInterface<UserCertificationAssociation> {

	UserCertificationAssociation findAssociationById(Long id);

	UserCertificationAssociation findAssociationByCertificationIdAndUserId(Long certificationId, Long userId);

	UserCertificationAssociationPagination findAllUserCertifications(UserCertificationAssociationPagination pagination);

	UserCertificationAssociationPagination findAllUserCertificationsByUserIds(Set<Long> userIds, UserCertificationAssociationPagination pagination);

	List<Certification> findAllCertificationsByUserIdInList(long userId, List<Long> ids);

	List<Map<String, String>> findAllCertificationFacetsByTypeAheadFilter(String typeAheadFilter);

	UserCertificationAssociationPagination findAllAssociationsByUserIdInList(Long userId, List<Long> certificationIds, UserCertificationAssociationPagination pagination);
}





