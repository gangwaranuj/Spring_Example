package com.workmarket.dao.profile;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.datetime.TimeZone;

import java.util.Map;

public interface ProfileDAO extends DAOInterface<Profile> {
	Profile findById(Long profileId);

	Long findProfileId(Long userId);

	Profile findByUser(Long userId);

	TimeZone findUserProfileTimeZone(Long userId);

	Map<String, Boolean> getProfileCompleteness(Long userId);

	Map<String, Object> getProjectionMapByUserNumber(String userNumber, String... fields);
}
