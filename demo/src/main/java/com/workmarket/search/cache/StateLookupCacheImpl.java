package com.workmarket.search.cache;

import static org.apache.commons.lang.StringUtils.isAlpha;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.trimToEmpty;

import java.util.Map;

import com.workmarket.service.business.dto.StateDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

@Service
public class StateLookupCacheImpl implements StateLookupCache {

	@Autowired private InvariantDataService invariantDataService;
	private final Map<String, String> stateLookupMap = Maps.newHashMap();

	@Override
	public boolean isStateQuery(String keywords) {
		if (isEmpty(keywords)) {
			return false;
		}
		if (!isAlpha(keywords)) {
			return false;
		}
		keywords = trimToEmpty(keywords.toUpperCase());
		return stateLookupMap.containsKey(keywords);
	}

	@Override
	public String getStateCode(String keywords) {
		if (keywords == null) {
			return null;
		}
		return stateLookupMap.get(trimToEmpty(keywords.toUpperCase()));
	}

	public void populateMap() {
		for (StateDTO state : invariantDataService.getStateDTOs()) {
			String id = trimToEmpty(state.getShortName().toUpperCase());
			stateLookupMap.put(id, id);
			String stateName = trimToEmpty(state.getName().toUpperCase());
			stateLookupMap.put(stateName, id);
		}
	}
}
