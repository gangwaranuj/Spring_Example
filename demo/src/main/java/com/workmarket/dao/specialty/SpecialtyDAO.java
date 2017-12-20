package com.workmarket.dao.specialty;

import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.domains.model.specialty.SpecialtyPagination;
import com.workmarket.dto.SuggestionDTO;

public interface SpecialtyDAO extends DAOInterface<Specialty>{

    Specialty findSpecialtyById(Long specialtyId);

    Specialty findSpecialtyByNameAndIndustryId(String name, Long industryId);

    SpecialtyPagination findAllSpecialties(SpecialtyPagination pagination);

    SpecialtyPagination findAllSpecialtiesByIndustry(Integer industryId, SpecialtyPagination pagination);

    List<SuggestionDTO> suggest(String prefix, String property);

    List<Specialty> findSpecialtiesByIds(Long[] specialtyIds);
}
