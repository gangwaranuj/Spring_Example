package com.workmarket.service.realtime;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.dao.workingonit.RealtimeWorkingOnItDAO;
import com.workmarket.domains.work.model.RealtimeWorkingOnIt;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.exception.account.DuplicateWorkNumberException;
import com.workmarket.thrift.services.realtime.RealtimeStatusException;
import com.workmarket.thrift.services.realtime.WorkingOnItRequest;
import com.workmarket.thrift.services.realtime.WorkingOnItStatusType;
import com.workmarket.thrift.work.WorkActionResponse;
import com.workmarket.thrift.work.WorkActionResponseCodeType;

@Service
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class RealtimeTransactedServiceImpl implements RealtimeTransactedService {

	private static final Log logger = LogFactory.getLog(RealtimeTransactedServiceImpl.class);
	
	@Autowired
	private RealtimeWorkingOnItDAO workingOnItDAO;
	
	@Autowired
	private WorkDAO workDAO;

	@Override
	public WorkActionResponse markWorkingOnIt(WorkingOnItRequest request)
			throws RealtimeStatusException {
		logger.info(request);
		if (!request.isSetWorkAction()) {
			logger.error("No work action set. " + request);
			throw new RealtimeStatusException();
		}
		if (!request.getWorkAction().isSetWorkNumber() || !request.getWorkAction().isSetOnBehalfOfUserNumber()) {
			logger.error("No work number or on behalf of user set");
		}
		
		Work work;
		try {
			work = workDAO.findWorkByWorkNumber(request.getWorkAction().getWorkNumber());
		} catch (DuplicateWorkNumberException e1) {
			logger.error("problem getting work from the request: " + request, e1);
			throw new RealtimeStatusException(e1);
		}
		
		RealtimeWorkingOnIt workingOnIt = workingOnItDAO.findOpenedByWorkId(work.getId());
		if (workingOnIt == null && request.getStatus() == WorkingOnItStatusType.OFF) {
			logger.error("Working on it request made to toggle off but no working on it row found.  Doing nothing." + request);
			return new WorkActionResponse().setResponseCode(WorkActionResponseCodeType.INVALID_WORK_STATE).setMessage("The work is already done or no longer being worked on.");
		} else if (workingOnIt == null) {
			workingOnIt = new RealtimeWorkingOnIt();
			workingOnIt.setCreatedOn(Calendar.getInstance());
			if (request.getWorkAction().isSetMasqueradeUserNumber()) {
				workingOnIt.setMasqueradeUserNumber(request.getWorkAction().getMasqueradeUserNumber());
			}
			String onBehalfOfUserNumber = request.getWorkAction().getOnBehalfOfUserNumber();
			workingOnIt.setOnBehalfOfUserNumber(onBehalfOfUserNumber);
			workingOnIt.setWorkId(work.getId());
		} else {
			if (request.getStatus() == WorkingOnItStatusType.OFF) {
				workingOnIt.setClosedOn(Calendar.getInstance());
			} else {
				logger.error("Work already being worked on. " + request);
				return new WorkActionResponse().setMessage("Work already being worked on. " + workingOnIt.toString()).setResponseCode(WorkActionResponseCodeType.INVALID_WORK_STATE);
			}
		}
		workingOnItDAO.saveOrUpdate(workingOnIt);
		return new WorkActionResponse().setResponseCode(WorkActionResponseCodeType.SUCCESS);
	}

}
