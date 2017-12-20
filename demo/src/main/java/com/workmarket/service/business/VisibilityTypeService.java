package com.workmarket.service.business;

import com.workmarket.domains.model.VisibilityType;

import java.util.List;
import java.util.Map;

public interface VisibilityTypeService {
	List<VisibilityType> getVisibilitySettings();
	Map<String, VisibilityType> getVisibilitySettingsAsMap();
	Map<String, String> getVisibilityDescriptionsAsMap();
}
