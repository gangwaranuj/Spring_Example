package com.workmarket.dao.realtime;

import com.workmarket.domains.model.realtime.RealtimeReportType;
import com.workmarket.domains.model.realtime.RealtimeServicePagination;
import com.workmarket.thrift.services.realtime.RealtimeFilter;
import com.workmarket.utility.sql.SQLBuilder;

public interface RealtimeSQLFactory {
	
	String getResourceSQL();

	SQLBuilder getOwnerDropDownSQL(RealtimeReportType reportType,
			Long companyId);
	SQLBuilder getClientDropdownSQL(RealtimeReportType reportType,
			Long companyId);
	SQLBuilder getProjectDropdownSQL(RealtimeReportType reportType,
			Long companyId);
	
	SQLBuilder getNumberOfResultsSql(Long companyId,
			RealtimeReportType reportType, RealtimeFilter filters,
			RealtimeServicePagination pagination);

	SQLBuilder createRealtimeAssignmentSQL(Long companyId,
			RealtimeReportType reportType, RealtimeFilter filters,
			RealtimeServicePagination pagination);

	SQLBuilder getMaxUnansweredQuestionsSQL(Long companyId, RealtimeReportType type);

	SQLBuilder getAssignmentTotalCountSQL(long companyId);

	SQLBuilder getAssignmentTotalCountSQL();

	SQLBuilder getAssignmentTodayTotalSQL(long companyId, String timeZone);

	SQLBuilder getAssignmentTodayTotalSQL(String timeZone);

	SQLBuilder getAssignmentSentTodayTotalSQL(String timeZone, Long companyId);
}
