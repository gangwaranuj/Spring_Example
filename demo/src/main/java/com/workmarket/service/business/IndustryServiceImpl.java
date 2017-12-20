package com.workmarket.service.business;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.workmarket.dao.industry.IndustryDAO;
import com.workmarket.dao.industry.ProfileIndustryAssociationDAO;
import com.workmarket.dao.profile.ProfileDAO;
import com.workmarket.domains.model.AbstractEntityUtilities;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.ProfileIndustryAssociation;
import com.workmarket.redis.RedisConfig;
import com.workmarket.service.business.dto.IndustryDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class IndustryServiceImpl implements IndustryService {

	@Autowired private ProfileIndustryAssociationDAO profileIndustryAssociationDAO;
	@Autowired private ProfileDAO profileDAO;
	@Autowired private IndustryDAO industryDAO;

	public static final String INDUSTRIES = RedisConfig.INDUSTRIES;
	public static final String INDUSTRIES_FOR_PROFILE = RedisConfig.INDUSTRIES_FOR_PROFILE;

	@Override
	public Set<Industry> getAllIndustriesForProfile(Long id) {
		Assert.notNull(id);

		return profileIndustryAssociationDAO.findIndustriesForProfile(id, true);
	}

	@Override
	public Set<IndustryDTO> getAllIndustryDTOsForProfile(Long id) {
		Assert.notNull(id);

		Set<Industry> industries = getAllIndustriesForProfile(id);
		Set<IndustryDTO> industryDTOs = Sets.newHashSetWithExpectedSize(industries.size());
		for (Industry industry : industries) {
			industryDTOs.add(IndustryDTO.newDTO(industry));
		}

		return industryDTOs;
	}

	@Override
	public Set<Industry> getIndustriesForProfile(Long id) {
		Assert.notNull(id);

		return profileIndustryAssociationDAO.findIndustriesForProfile(id, false);
	}

	@Override
	@Cacheable(
		value = INDUSTRIES_FOR_PROFILE,
		key = "#root.target.INDUSTRIES_FOR_PROFILE + #id"
	)
	public Set<IndustryDTO> getIndustryDTOsForProfile(Long id) {
		Assert.notNull(id);

		Set<Industry> industries = getIndustriesForProfile(id);
		Set<IndustryDTO> industryDTOs = Sets.newHashSetWithExpectedSize(industries.size());
		for (Industry industry : industries) {
			if(industry.getId().equals(Industry.GENERAL.getId())){
				industry.setOtherName(profileIndustryAssociationDAO.findOtherNameByProfileId(id));
			}
			industryDTOs.add(IndustryDTO.newDTO(industry));
		}

		return industryDTOs;
	}

	@Override
	public List<Long> getIndustryIdsForProfile(Long id) {
		Assert.notNull(id);

		Set<IndustryDTO> industryList = getIndustryDTOsForProfile(id);
		List<Long> industryIds = Lists.newArrayListWithCapacity(industryList.size());
		for (IndustryDTO industry : industryList) {
			industryIds.add(industry.getId());
		}

		return industryIds;
	}

	@Override
	public Industry getDefaultIndustryForProfile(Long id) {
		Assert.notNull(id);

		return MoreObjects.firstNonNull(profileIndustryAssociationDAO.findDefaultIndustryForProfile(id), Industry.NONE);
	}

	@Override
	public Map<Long, Long> getDefaultIndustriesForUsers(Collection<Long> userIds) {
		Assert.notNull(userIds);
		return profileIndustryAssociationDAO.findDefaultIndustriesForUsers(userIds);
	}

	@Override
	public List<Industry> getAllIndustries() {
		return industryDAO.findAllIndustries();
	}

	@Override
	@Cacheable(value = INDUSTRIES, key = "#root.target.INDUSTRIES")
	public List<IndustryDTO> getAllIndustryDTOs() {
		List<Industry> industries = getAllIndustries();
		List<IndustryDTO> industryDTOs = Lists.newArrayListWithCapacity(industries.size());
		for (Industry industry : industries) {
			industryDTOs.add(IndustryDTO.newDTO(industry));
		}
		return industryDTOs;
	}

	@Override
	public Industry getIndustryById(Long id) {
		Assert.notNull(id);

		return industryDAO.get(id);
	}

	@Override
	public boolean doesProfileHaveIndustry(Long profileId, Long industryId) {
		Assert.notNull(profileId);
		Assert.notNull(industryId);

		return profileIndustryAssociationDAO.doesProfileHaveIndustry(profileId, industryId);
	}

	@Override
	@CacheEvict(
		value = INDUSTRIES_FOR_PROFILE,
		key = "#root.target.INDUSTRIES_FOR_PROFILE + #id"
	)
	public void setIndustriesForProfile(Long id, Set<Industry> industries) {
		Assert.notNull(id);
		Assert.notNull(industries);

		Map<Long, Industry> industryIdsList = AbstractEntityUtilities.newEntityIdMap(Lists.newArrayList(industries));
		Profile profile = profileDAO.get(id);
		Set<ProfileIndustryAssociation> associations = getAllIndustryProfileAssociationsByProfile(id);

		for (ProfileIndustryAssociation association : associations) {
			Long industryId = association.getIndustry().getId();

			if (!industryIdsList.keySet().contains(industryId)) {
				if (!association.getDeleted()) {
					association.setDeleted(true);
				}
			} else {
				if (association.getDeleted()) {
					association.setDeleted(false);
				}
				industryIdsList.remove(industryId);
			}
		}

		for (Industry industry : industryIdsList.values()) {
			makeProfileIndustryAssociation(industry, profile);
		}
	}

	@Override
	public ProfileIndustryAssociation findProfileIndustryAssociationByProfileIdAndIndustryId(Long profileId, Long industryId) {
		Assert.notNull(profileId);
		Assert.notNull(industryId);

		return profileIndustryAssociationDAO.findByProfileIdAndIndustryId(profileId, industryId);
	}

	public ProfileIndustryAssociation makeProfileIndustryAssociation(Industry industry, Profile profile) {
		ProfileIndustryAssociation profileIndustryAssociation = new ProfileIndustryAssociation(industry, profile);
		profileIndustryAssociationDAO.saveOrUpdate(profileIndustryAssociation);

		return profileIndustryAssociation;
	}

	private Set<ProfileIndustryAssociation> getAllIndustryProfileAssociationsByProfile(Long id) {
		Assert.notNull(id);

		return profileIndustryAssociationDAO.findAllIndustryProfileAssociationsByProfile(id);
	}
}
