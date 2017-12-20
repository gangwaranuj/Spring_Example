package com.workmarket.service.search;

import java.util.Map;

public interface SearchPreferencesService {

	Map<String, String> get(Long userId);

	void set(Long userId, Map<String, String> map);
}
