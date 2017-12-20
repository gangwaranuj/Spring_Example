package com.workmarket.dao.industry;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.ProfileIndustryAssociation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ProfileIndustryAssociationDAO extends DeletableDAOInterface<ProfileIndustryAssociation> {
	Set<Industry> findIndustriesForProfile(Long id, boolean includeDeleted);

	Industry findDefaultIndustryForProfile(Long id);

	Map<Long, Long> findDefaultIndustriesForUsers(Collection<Long> userIds);

	boolean doesProfileHaveIndustry(Long profileId, Long industryId);

	Set<ProfileIndustryAssociation> findAllIndustryProfileAssociationsByProfile(Long id);

	ProfileIndustryAssociation findByProfileIdAndIndustryId(Long profileId, Long industryId);

	String findOtherNameByProfileId(Long profileId);

	void saveOtherNameForProfileId(Long profileId, String otherName);
}