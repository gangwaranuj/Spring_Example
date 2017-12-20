package com.workmarket.service.realtime;

import com.workmarket.thrift.services.realtime.RealtimeCSRStatusRequest;
import com.workmarket.thrift.services.realtime.RealtimeStatusException;
import com.workmarket.thrift.services.realtime.RealtimeStatusPage;
import com.workmarket.thrift.services.realtime.RealtimeStatusRequest;
import com.workmarket.thrift.services.realtime.TotalAssignmentCount;
import com.workmarket.thrift.services.realtime.TotalAssignmentCountRequest;
import com.workmarket.thrift.services.realtime.WorkingOnItRequest;
import com.workmarket.thrift.work.WorkActionResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class TRealtimeServiceImpl implements TRealtimeService {

	private static final Log logger = LogFactory.getLog(TRealtimeServiceImpl.class);

	/** to work in the master db - all read/write functions **/
	private final RealtimeTransactedService realtimeTransactedService;

	/** to work in the slave - all read only functions **/
	private final RealtimeService realtimeService;
	
	@Autowired
	public TRealtimeServiceImpl(RealtimeTransactedService realtimeTransactedService, RealtimeService realtimeService) {
		this.realtimeService = checkNotNull(realtimeService);
		this.realtimeTransactedService = checkNotNull(realtimeTransactedService);
	}
	
	@Override
	public RealtimeStatusPage getRealtime(RealtimeStatusRequest request)
			throws RealtimeStatusException {
		logger.info(request);
		return realtimeService.getRealtime(request);
	}

	@Override
	public RealtimeStatusPage getRealtimeCSR(RealtimeCSRStatusRequest request)
			throws RealtimeStatusException {
		logger.info(request);
		return realtimeService.getRealtimeCSR(request);
	}

	@Override
	public WorkActionResponse markWorkingOnIt(WorkingOnItRequest request)
			throws RealtimeStatusException {
		logger.info(request);
		return realtimeTransactedService.markWorkingOnIt(request);
	}

	@Override
	public TotalAssignmentCount calculateAssignmentCounts(TotalAssignmentCountRequest request) {
		logger.info(request);
		return realtimeService.calculateTotalOpenAssignments(request.getCompanyId(), request.getTimeZone());
	}

	@Override
	public TotalAssignmentCount calculateAssignmentCountsCSR(TotalAssignmentCountRequest request) {
		return realtimeService.calculateTotalOpenAssignments(request.getTimeZone());
	}
}
