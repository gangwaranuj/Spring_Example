package com.workmarket.service.business;

import java.util.Map;

public interface UserNavService {
	Map<String, String> get(Long userId);
	void set(Long userId, Map<String, String> map);
}
