package com.workmarket.dao.specialty;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.domains.model.specialty.SpecialtyPagination;
import com.workmarket.domains.model.specialty.UserSpecialtyAssociation;
import com.workmarket.domains.model.specialty.UserSpecialtyAssociationPagination;

import java.util.List;

public interface UserSpecialtyAssociationDAO extends DAOInterface<UserSpecialtyAssociation>{

	SpecialtyPagination findAllSpecialtiesByUser(Long userId, SpecialtyPagination pagination);

	UserSpecialtyAssociationPagination findAllAssociationsByUser(Long userId, UserSpecialtyAssociationPagination pagination );

	void addSpecialtyToUser(Specialty specialty, User user);

	void removeSpecialtyFromUser(Specialty specialty, User user);

	UserSpecialtyAssociation findAssociationsBySpecialtyAndUser(Long specialtyId, Long userId);

	List<UserSpecialtyAssociation> findAssociationsByUser(Long userId);

	SpecialtyPagination findAllActiveSpecialtiesByUser(Long userId, SpecialtyPagination pagination);

	void mergeSpecialties(Long fromSpecialtyId, Long toSpecialtyId);
}
