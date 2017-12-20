package com.workmarket.api.v1;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v1.model.ApiListLabelDTO;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import java.util.LinkedList;
import java.util.List;

@Api(tags = "Labels")
@Controller("apiLabelsController")
@RequestMapping(value = {"/v1/employer/assignments/labels", "/api/v1/assignments/labels"})
public class LabelsController extends ApiBaseController {
	private static final Logger logger = LoggerFactory.getLogger(LabelsController.class);

	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private TWorkFacadeService tWorkFacadeService;

	/**
	 * Retrieve a list of available labels.
	 * @return "response" : An array of objects representing labels. Each label contains an id,
	 * a code such as parts_back_ordered and a human readable name such as Parts on back order.
	 */
	@ApiOperation(value = "List labels")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/list", method= RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<List<ApiListLabelDTO>> listAssignmentLablesAction() {
		List<ApiListLabelDTO> apiLabelDTOs = new LinkedList<>();

		WorkSubStatusTypeFilter filter = new WorkSubStatusTypeFilter();
		filter.setShowSystemSubStatus(true);
		filter.setShowCustomSubStatus(true);
		filter.setShowDeactivated(true);
		filter.setClientVisible(true);
		filter.setResourceVisible(true);

		List<WorkSubStatusType> subStatuses = workSubStatusService.findAllSubStatuses(
									authenticationService.getCurrentUser().getCompany().getId(), filter);

		for (WorkSubStatusType substatus : subStatuses) {
			apiLabelDTOs.add(new ApiListLabelDTO.Builder()
				.withId(substatus.getId())
				.withName(substatus.getDescription())
				.build()
			);
		}

		ApiV1Response<List<ApiListLabelDTO>> apiResponse = new ApiV1Response<>();
		apiResponse.setResponse(apiLabelDTOs);
		return apiResponse;
	}

	/**
	 * Add a label to an assignment
	 * @param workNumber is work number
	 * @param subStatusId is sub status ID
	 * @return successful true or false
	 */
	@ApiOperation(value = "Add labels to assignment")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/add", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> addPostAction(
		@RequestParam(value="id", required=false) String workNumber,
		@RequestParam(value="substatus_id", required=false) Long subStatusId,
		@RequestParam(value="note", required=false) String note) {

		MessageBundle bundle = messageHelper.newBundle();
		Long userId = authenticationService.getCurrentUser().getId();

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
			WorkSubStatusType subStatus = workSubStatusService.findWorkSubStatus(subStatusId);

			boolean authorized = (workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE)
					&& WorkSubStatusType.INCOMPLETE_WORK.equals(subStatus.getCode())
			) || workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN);

			if (!authorized) {
				throw new HttpException401("Not authorized to add substatuses to this assignment.");
			}

			workSubStatusService.addSubStatus(workResponse.getWork().getId(), subStatus.getId(), StringUtils.defaultString(note));
		}
		catch (Exception ex) {
			logger.error("error adding a label for userId={}, workNumber={} abd subStatusId={}",
					new Object[] {userId, workNumber, subStatusId}, ex);
			messageHelper.addError(bundle, "api.v1.assignments.substatuses.add.error");
		}

		if (bundle.hasErrors()) {
			return ApiV1Response.of(false, bundle.getErrors());
		}

		return ApiV1Response.of(true);
	}

	/**
	 * Add a label to an assignment
	 * @param workNumber is work number
	 * @param subStatusId is sub status ID
	 * @return successful true or false
	 */
	@ApiOperation(value = "Add labels to assignment", hidden = true)
	@RequestMapping(value="/add", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> addGetAction(
		@RequestParam(value="id", required=false) String workNumber,
		@RequestParam(value="substatus_id", required=false) Long subStatusId,
		@RequestParam(value="note", required=false) String note) {

		return addGetAction(workNumber, subStatusId, note);
	}

	/**
	 * Remove a label from an assignment
	 * @param workNumber is work number
	 * @param subStatusId is substatus ID
	 * @return successful true or false
	 */
	@ApiOperation(value = "Remove label from assignment")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/remove", method = RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> removePostAction(
		@RequestParam(value="id", required=false) String workNumber,
		@RequestParam(value="substatus_id", required=false) Long subStatusId) {

		MessageBundle bundle = messageHelper.newBundle();
		Long userId = authenticationService.getCurrentUser().getId();

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
			WorkSubStatusType subStatus = workSubStatusService.findWorkSubStatus(subStatusId);

			boolean authorized = (workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE)
					&& WorkSubStatusType.INCOMPLETE_WORK.equals(subStatus.getCode())
			) || workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN);

			if (!authorized) {
				throw new HttpException401("Not authorized to remove substatuses from this assignment.");
			}

			workSubStatusService.resolveSubStatus(userId, workResponse.getWork().getId(), subStatus.getId(), null);
		}
		catch (WorkActionException ex) {
			logger.warn("error removing labels for requested substatus_id={} and workNumber={}",
					new Object[] {subStatusId, workNumber}, ex);
			messageHelper.addError(bundle, "api.v1.assignments.substatuses.remove.error");
		}
		catch (Exception ex) {
			logger.warn("error removing labels for requested substatus_id={} and workNumber={}",
					new Object[] {subStatusId, workNumber}, ex);
			messageHelper.addError(bundle, "api.v1.assignments.substatuses.remove.error");
		}

		if (bundle.hasErrors()) {
			return ApiV1Response.of(false, bundle.getErrors());
		}

		return ApiV1Response.of(true);
	}

	/**
	 * Remove a label from an assignment
	 * @param workNumber is work number
	 * @param subStatusId is substatus ID
	 * @return successful true or false
	 */
	@ApiOperation(value = "Remove label from assignment", hidden = true)
	@RequestMapping(value="/remove", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiV1ResponseStatus> removeGetAction(
		@RequestParam(value="id", required=false) String workNumber,
		@RequestParam(value="substatus_id", required=false) Long subStatusId) {

		return removePostAction(workNumber, subStatusId);
	}
}
