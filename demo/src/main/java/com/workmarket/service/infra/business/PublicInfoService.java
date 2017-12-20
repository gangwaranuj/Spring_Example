package com.workmarket.service.infra.business;

import com.google.common.collect.Multimap;
import com.workmarket.dto.PublicPageProfileDTO;

import java.util.List;

/**
 * Created by rahul on 12/9/13
 */
public interface PublicInfoService {

	public Multimap<String,PublicPageProfileDTO> getPublicProfiles(List<String> industries, int numberOfDisplayedProfiles);
}
