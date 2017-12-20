package com.workmarket.service.business;

import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.ProfileIndustryAssociation;
import com.workmarket.service.business.dto.IndustryDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IndustryService {

	Set<Industry> getAllIndustriesForProfile(Long id);

	Set<IndustryDTO> getAllIndustryDTOsForProfile(Long id);

	Set<Industry> getIndustriesForProfile(Long id);

	Set<IndustryDTO> getIndustryDTOsForProfile(Long id);

	List<Long> getIndustryIdsForProfile(Long id);

	Industry getDefaultIndustryForProfile(Long id);

	Map<Long, Long> getDefaultIndustriesForUsers(Collection<Long> userIds);

	Industry getIndustryById(Long id);

	List<IndustryDTO> getAllIndustryDTOs();

	List<Industry> getAllIndustries();

	boolean doesProfileHaveIndustry(Long profileId, Long industryId);

	void setIndustriesForProfile(Long profileId, Set<Industry> industries);

	ProfileIndustryAssociation findProfileIndustryAssociationByProfileIdAndIndustryId(Long profileId, Long industryId);

	ProfileIndustryAssociation makeProfileIndustryAssociation(Industry industry, Profile profile);
}
