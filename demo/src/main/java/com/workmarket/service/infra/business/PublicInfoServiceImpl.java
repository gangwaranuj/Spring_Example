package com.workmarket.service.infra.business;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.workmarket.common.cache.PublicPageProfileCache;
import com.workmarket.dao.publicinfo.PublicPageProfileDAO;
import com.workmarket.dto.PublicPageProfileDTO;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicInfoServiceImpl implements PublicInfoService {

	private static final Log logger = LogFactory.getLog(PublicInfoServiceImpl.class);

	@Autowired private PublicPageProfileDAO publicPageProfileDAO;
	@Autowired private PublicPageProfileCache publicPageProfileCache;

	private List<String> findAllInvalidCaches(Multimap<String,PublicPageProfileDTO> cacheResult, List<String> allIndustries) {
		List<String> invalidCaches = Lists.newArrayList();

		for (String key : allIndustries) {
			if (!cacheResult.containsKey(key)) {
				invalidCaches.add(key);
			}
		}

		return invalidCaches;
	}

	@Override
	public Multimap<String,PublicPageProfileDTO> getPublicProfiles(List<String> industries, int numberOfDisplayedProfiles) {
		if (CollectionUtilities.isEmpty(industries) || numberOfDisplayedProfiles <= 0) {
			return ArrayListMultimap.create();
		}

		Multimap<String, PublicPageProfileDTO> results = publicPageProfileCache.getPublicPageProfiles(industries, numberOfDisplayedProfiles);
		List<String> invalidIndustryCacheKeys = findAllInvalidCaches(results, industries);

		for(String invalidIndustryCacheKey : invalidIndustryCacheKeys) {
			List<PublicPageProfileDTO> profiles = publicPageProfileDAO.getAllPublicPageProfilesDataByIndustry(invalidIndustryCacheKey);
			publicPageProfileCache.putNewPublicPageProfiles(invalidIndustryCacheKey, profiles);
			profiles = CollectionUtilities.randomizeAndTruncate(profiles, numberOfDisplayedProfiles);
			results.replaceValues(invalidIndustryCacheKey, profiles);
		}

		return results;
	}
}
