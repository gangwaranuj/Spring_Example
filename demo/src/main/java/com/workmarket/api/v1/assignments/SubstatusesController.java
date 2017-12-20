package com.workmarket.api.v1.assignments;

import com.workmarket.api.v1.ApiV1ResponseStatus;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.api.v1.ApiV1Response;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;

/*
@Deprecated use ApiLabelsController
 */

@Deprecated
@Controller("apiSubstatusesController")
@RequestMapping({"/v1/employer/assignments/substatuses", "/api/v1/assignments/substatuses"})
public class SubstatusesController {
	private static final Logger logger = LoggerFactory.getLogger(SubstatusesController.class);

	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private AuthenticationService authenticationService;

	/**
	 * Add substatus.
	 * @param workNumber is work assignment number
	 * @param subStatusId is sub-status id
	 * @return Ajax response
	 */
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public @ResponseBody ApiV1Response<ApiV1ResponseStatus> add(
								@RequestParam(value="id", required=false) String workNumber,
								@RequestParam(value="substatus_id", required=false) String subStatusId) {

		MessageBundle bundle = messageHelper.newBundle();
		Long userId = authenticationService.getCurrentUser().getId();

		logger.debug("adding substatus_id={} and workNumber={} for userId={}",
												new Object[] { subStatusId, workNumber, userId });

		// Load assignment.
		WorkRequest workRequest = new WorkRequest();
		workRequest.setUserId(userId);
		workRequest.setWorkNumber(workNumber);
		workRequest.setIncludes(new HashSet<WorkRequestInfo>() {{
			add(WorkRequestInfo.STATUS_INFO);
			add(WorkRequestInfo.CONTEXT_INFO);
		}});

		try {
			WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);

			boolean authorized = (workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE)
									&& WorkSubStatusType.INCOMPLETE_WORK.equals(subStatusId)
								) || workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN);

			if (!authorized) {
				throw new HttpException401("Not authorized to add substatuses to this assignment.");
			}

			logger.debug("authorized to add substasuses to workNumber={}, substatusId={} and userId={}",
					new Object[] { workNumber, subStatusId, userId });

			WorkSubStatusType subStatus = workSubStatusService.findSystemWorkSubStatus(subStatusId);
			workSubStatusService.addSubStatus(workResponse.getWork().getId(), subStatus.getId(), StringUtils.EMPTY);

			logger.debug("operation to add substatus using substatus_id={} and workNumber={} for userId={}: succeeded=true",
					new Object[] { subStatusId, workNumber, userId});

			return ApiV1Response.of(true);
		} catch (WorkActionException ex) {
			logger.error("error adding a substatus for userId={}, workNumber={} abd subStatusId={}",
					new Object[]{userId, workNumber, subStatusId}, ex);
			messageHelper.addError(bundle, "api.v1.assignments.substatuses.add.error");
		} catch (Exception ex) {
			logger.error("error adding a substatus for userId={}, qworkNumber={} abd subStatusId={}",
					new Object[]{userId, workNumber, subStatusId}, ex);
			messageHelper.addError(bundle, "api.v1.assignments.substatuses.add.error");
		}

		logger.debug("operation to add substatus using substatus_id={} and workNumber={} for userId={}: succeeded=false",
				new Object[] { subStatusId, workNumber, userId});

		return ApiV1Response.of(false, bundle.getErrors());
	}

	/**
	 * Remove substatus.
	 * @param workNumber is work assignment number
	 * @param subStatusId is sub-status id
	 * @return ApiV1Response
	 */
	@RequestMapping(value="/remove", method=RequestMethod.GET)
	public @ResponseBody ApiV1Response<ApiV1ResponseStatus> remove(
						@RequestParam("id") String workNumber,
						@RequestParam(value="substatus_id", required=false) String subStatusId) {

		MessageBundle bundle = messageHelper.newBundle();
		Long userId = authenticationService.getCurrentUser().getId();

		logger.debug("removing substatus_id={} and workNumber={} for userId={}",
				new Object[] { subStatusId, workNumber, userId });

		// Load assignment.
		WorkRequest workRequest = new WorkRequest();
		workRequest.setUserId(userId);
		workRequest.setWorkNumber(workNumber);
		workRequest.setIncludes(new HashSet<WorkRequestInfo>() {{
			add(WorkRequestInfo.STATUS_INFO);
			add(WorkRequestInfo.CONTEXT_INFO);
		}});

		try {
			WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);

			boolean authorized = (workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE)
					&& WorkSubStatusType.INCOMPLETE_WORK.equals(subStatusId)
			) || workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN);

			if (!authorized) {
				throw new HttpException401("Not authorized to remove substatuses from this assignment.");
			}

			logger.debug("authorized to remove substasuses to workNumber={}, substatusId={} and userId={}",
					new Object[] { workNumber, subStatusId, userId });

			WorkSubStatusType subStatus = workSubStatusService.findSystemWorkSubStatus(subStatusId);
			workSubStatusService.resolveSubStatus(userId, workResponse.getWork().getId(), subStatus.getId(), null);

			return ApiV1Response.of(true);
		} catch (WorkActionException ex) {
			logger.warn("error removing substatus for requested substatus_id={} and workNumber={}",
																	new Object[] {subStatusId, workNumber}, ex);
			messageHelper.addError(bundle, "api.v1.assignments.substatuses.remove.error");
		} catch (Exception ex) {
			logger.warn("error removing substatus for requested substatus_id={} and workNumber={}",
																	new Object[] {subStatusId, workNumber}, ex);
			messageHelper.addError(bundle, "api.v1.assignments.substatuses.remove.error");
		}

		logger.debug("operation to remove substatus using substatus_id={} and workNumber={} for userId={}: succeeded=false",
				new Object[] { subStatusId, workNumber, userId });

		return ApiV1Response.of(false, bundle.getErrors());
	}
}
