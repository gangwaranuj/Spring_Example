package com.workmarket.service.search;

import java.util.Map;

public interface SearchFilterService {
	public Map<String, String> get(Long userId);
	void set(Long userId, Map<String, String> map);
}
