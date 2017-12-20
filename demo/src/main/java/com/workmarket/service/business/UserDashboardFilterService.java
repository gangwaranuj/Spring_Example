package com.workmarket.service.business;

import java.util.Map;

public interface UserDashboardFilterService {

	Map<String, String> get(Long userId);

	void set(Long userId, Map<String, String> map);
}
