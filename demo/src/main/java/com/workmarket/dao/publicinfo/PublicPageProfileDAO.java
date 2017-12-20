package com.workmarket.dao.publicinfo;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.publicinfo.PublicPageProfile;
import com.workmarket.dto.PublicPageProfileDTO;

import java.util.List;

/**
 * Created by rahul on 12/15/13
 */
public interface PublicPageProfileDAO extends DAOInterface<PublicPageProfile> {

	public List<PublicPageProfile> getAllPublicPageProfilesByIndustry(String industry);
	public List<PublicPageProfileDTO> getAllPublicPageProfilesDataByIndustry(String industry);
}
