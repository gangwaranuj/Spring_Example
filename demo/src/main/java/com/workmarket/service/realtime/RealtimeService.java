package com.workmarket.service.realtime;

import com.workmarket.thrift.services.realtime.RealtimeCSRStatusRequest;
import com.workmarket.thrift.services.realtime.RealtimeStatusException;
import com.workmarket.thrift.services.realtime.RealtimeStatusPage;
import com.workmarket.thrift.services.realtime.RealtimeStatusRequest;
import com.workmarket.thrift.services.realtime.TotalAssignmentCount;

public interface RealtimeService {

	RealtimeStatusPage getRealtime(RealtimeStatusRequest request)
			throws RealtimeStatusException;

	RealtimeStatusPage getRealtimeCSR(RealtimeCSRStatusRequest request)
			throws RealtimeStatusException;

	TotalAssignmentCount calculateTotalOpenAssignments(String timeZone);

	TotalAssignmentCount calculateTotalOpenAssignments(long companyId, String timeZone);

}
