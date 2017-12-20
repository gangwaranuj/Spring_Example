package com.workmarket.service.business.integration.mbo;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.MboProfile;

import java.util.Set;

public interface MboProfileDAO extends DAOInterface<MboProfile> {

	MboProfile findMboProfile(Long userId);

	MboProfile findMboProfileByGUID(String objectGUID);

	Set<Long> filterMboResourcesFromList(Set<Long> userIds);
}
