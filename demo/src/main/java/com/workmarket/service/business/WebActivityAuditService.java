package com.workmarket.service.business;

import java.util.Calendar;

public interface WebActivityAuditService {

	void saveLoginInfo(Long userId, Calendar date, Long companyId, String inetAddress, boolean succesful);
}
