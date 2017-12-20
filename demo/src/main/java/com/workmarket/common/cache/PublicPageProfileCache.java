package com.workmarket.common.cache;

import com.google.common.collect.Multimap;
import com.workmarket.dto.PublicPageProfileDTO;

import java.util.List;

/**
 * Created by rahul on 12/9/13
 */
public interface PublicPageProfileCache {

	public List<PublicPageProfileDTO> putNewPublicPageProfiles(String industry, List<PublicPageProfileDTO> profiles);

	public Multimap<String, PublicPageProfileDTO> getPublicPageProfiles(List<String> industries, int numberOfProfiles);
}
