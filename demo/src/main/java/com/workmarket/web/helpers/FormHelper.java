package com.workmarket.web.helpers;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 2/15/12
 * Time: 4:27 PM
 */
public interface FormHelper {
	public Map<String, String> getCompanyUserGroupNamesForSelect(Long userId, Long companyId, Boolean prefixWithCompanyId);
}
