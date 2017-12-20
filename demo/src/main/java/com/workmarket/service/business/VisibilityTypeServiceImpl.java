package com.workmarket.service.business;

import com.google.common.collect.Maps;
import com.workmarket.dao.VisibilityTypeDAO;
import com.workmarket.domains.model.VisibilityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VisibilityTypeServiceImpl implements VisibilityTypeService {

	@Autowired private VisibilityTypeDAO visibilityTypeDAO;

	@Override
	public List<VisibilityType> getVisibilitySettings() {
		return visibilityTypeDAO.getVisibilityTypes();
	}

	@Override
	public Map<String, VisibilityType> getVisibilitySettingsAsMap() {
		Map<String, VisibilityType> result = Maps.newHashMap();
		for (VisibilityType type : getVisibilitySettings()) {
			result.put(type.getCode(), type);
		}
		return result;
	}

	@Override
	public Map<String, String> getVisibilityDescriptionsAsMap() {
		Map<String, String> result = Maps.newHashMap();
		for (VisibilityType type : getVisibilitySettings()) {
			result.put(type.getCode(), type.getDescription());
		}
		return result;
	}
}
