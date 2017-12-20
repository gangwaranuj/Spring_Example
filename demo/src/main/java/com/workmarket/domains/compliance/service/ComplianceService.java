package com.workmarket.domains.compliance.service;

import com.workmarket.domains.compliance.model.Compliance;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.negotiation.ScheduleNegotiation;

public interface ComplianceService {
	Compliance getComplianceFor(String resourceNumber, String workNumber);

	Compliance getComplianceFor(Long userId, Long workId);

	Compliance getComplianceFor(Long workId, DateRange dateRange);

	Compliance getComplianceFor(User user, AbstractWork work);

	Compliance getComplianceFor(User user, AbstractWork work, DateRange schedule);
}
