package com.workmarket.service.business;

import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.domains.model.specialty.SpecialtyPagination;
import com.workmarket.domains.model.specialty.UserSpecialtyAssociation;
import com.workmarket.domains.model.specialty.UserSpecialtyAssociationPagination;
import com.workmarket.service.business.dto.SpecialtyDTO;

import java.util.List;

public interface SpecialtyService {
	Specialty findSpecialtyById(Long specialtyId);
	Specialty findSpecialtyByNameAndIndustryId(String name, Long industryId);
	Specialty saveOrUpdateSpecialty(SpecialtyDTO specialtyDTO);
	Specialty saveOrUpdateSpecialty(Specialty specialty);

	void declineSpecialty(Long specialtyId);
	void approveSpecialty(Long specialtyId);

	void mergeSpecialties(Long fromSpecialtyId, Long toSpecialtyId);

	SpecialtyPagination findAllSpecialties(SpecialtyPagination pagination);
	SpecialtyPagination findAllSpecialtiesByIndustry(Integer industryId, SpecialtyPagination pagination);
	SpecialtyPagination findAllSpecialtiesByUser(Long userId, SpecialtyPagination pagination);
	SpecialtyPagination findAllActiveSpecialtiesByUser(Long userId, SpecialtyPagination pagination);

	void setSpecialtiesOfUser(Integer[] specialtyIds, Long userId) throws Exception;
	void setSpecialtiesOfUser(List<Integer> specialtyIds, Long userId) throws Exception;
	void addSpecialtyToUser(Integer specialtyId, Long userId) throws Exception;
	void removeSpecialtyFromUser(Integer specialtyId, Long userId) throws Exception;
	void removeSpecialtiesFromUser(Long userId);

	void setSpecialtyLevelsForUser(Integer[] specialtyIds, Integer[] skillLevels, Long userId);

	UserSpecialtyAssociationPagination findAllAssociationsByUser(Long userId, UserSpecialtyAssociationPagination userSpecialtyAssociationPagination);
	UserSpecialtyAssociation findAssociationsBySpecialtyAndUser(Integer specialtyId, Long userId);

	int getSpecialityPopularityThreshold();

	List<Specialty> findSpecialtiesByIds(Long[] specialtyIds);
}
