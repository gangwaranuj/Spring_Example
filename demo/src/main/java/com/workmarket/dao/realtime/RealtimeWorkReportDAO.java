package com.workmarket.dao.realtime;

import com.workmarket.domains.model.realtime.IRealtimeStatusPage;
import com.workmarket.domains.model.realtime.RealtimeReportType;
import com.workmarket.domains.model.realtime.RealtimeServicePagination;
import com.workmarket.thrift.services.realtime.RealtimeFilter;
import com.workmarket.thrift.services.realtime.TotalAssignmentCount;

public interface RealtimeWorkReportDAO {

	IRealtimeStatusPage generateRealtimeStatusPage(Long companyId, RealtimeReportType reportType,
												   RealtimeFilter filters, RealtimeServicePagination pagination);

	TotalAssignmentCount calculateTotalOpenAssignments(long companyId, String timeZone);

	TotalAssignmentCount calculateTotalOpenAssignments(String timeZone);


}
