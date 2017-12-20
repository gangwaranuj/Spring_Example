package com.workmarket.dao.profile;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.ProfileLanguage;

import java.util.List;

public interface ProfileLanguageDAO extends DAOInterface<ProfileLanguage> {

	List<ProfileLanguage> findAllProfileLanguageByProfileId(long profileId);

}