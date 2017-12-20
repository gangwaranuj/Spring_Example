package com.workmarket.service.realtime;

import com.workmarket.thrift.services.realtime.RealtimeCSRStatusRequest;
import com.workmarket.thrift.services.realtime.RealtimeStatusException;
import com.workmarket.thrift.services.realtime.RealtimeStatusPage;
import com.workmarket.thrift.services.realtime.RealtimeStatusRequest;
import com.workmarket.thrift.services.realtime.TotalAssignmentCount;
import com.workmarket.thrift.services.realtime.TotalAssignmentCountRequest;
import com.workmarket.thrift.services.realtime.WorkingOnItRequest;

public interface TRealtimeService {

	public RealtimeStatusPage getRealtime(RealtimeStatusRequest request) throws RealtimeStatusException;

    public RealtimeStatusPage getRealtimeCSR(RealtimeCSRStatusRequest request) throws RealtimeStatusException;

    public com.workmarket.thrift.work.WorkActionResponse markWorkingOnIt(WorkingOnItRequest request) throws RealtimeStatusException;

    public TotalAssignmentCount calculateAssignmentCounts(TotalAssignmentCountRequest request);

    public TotalAssignmentCount calculateAssignmentCountsCSR(TotalAssignmentCountRequest request);
  
}
