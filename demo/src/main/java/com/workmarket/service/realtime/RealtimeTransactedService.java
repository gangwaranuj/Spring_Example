package com.workmarket.service.realtime;

import com.workmarket.thrift.services.realtime.RealtimeStatusException;
import com.workmarket.thrift.services.realtime.WorkingOnItRequest;
import com.workmarket.thrift.work.WorkActionResponse;

public interface RealtimeTransactedService {

	WorkActionResponse markWorkingOnIt(WorkingOnItRequest request)
			throws RealtimeStatusException;

}
