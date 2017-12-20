package com.workmarket.service.thrift.transactional;

import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.DeclineWorkOfferRequest;
import com.workmarket.thrift.work.MultipleWorkSendRequest;
import com.workmarket.thrift.work.RescheduleRequest;
import com.workmarket.thrift.work.ResourceNoteRequest;
import com.workmarket.thrift.work.VoidWorkRequest;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkActionResponse;
import com.workmarket.thrift.work.WorkQuestionRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.thrift.work.WorkSendRequest;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;

import java.util.List;
import java.util.Set;

public interface TWorkService {

	Work saveWork(WorkSaveRequest request) throws com.workmarket.thrift.core.ValidationException;

	WorkDTO buildWorkDTO(WorkSaveRequest request, com.workmarket.domains.model.User currentUser);

	WorkResponse saveOrUpdateWorkDraft(WorkSaveRequest request) throws com.workmarket.thrift.core.ValidationException;

	WorkResponse saveOrUpdateWorkDraft(WorkSaveRequest request, Set<WorkRequestInfo> includes) throws ValidationException;

	WorkResponse saveOrUpdateWorkTemplate(WorkSaveRequest request) throws com.workmarket.thrift.core.ValidationException;

	void startUploadEventHelper(WorkUploadRequest uploadRequest, Long userId);

	void uploadWorkAsync(List<WorkSaveRequest> requests) throws com.workmarket.thrift.core.ValidationException;

	WorkActionResponse sendWork(WorkSendRequest request) throws WorkActionException;

	WorkActionResponse sendMultipleWork(MultipleWorkSendRequest request) throws WorkActionException;

	WorkActionResponse askQuestion(WorkQuestionRequest questionRequest) throws WorkActionException;

	WorkActionResponse noteWorkResource(ResourceNoteRequest request) throws WorkActionException;

	WorkActionResponse declineWorkOnBehalf(DeclineWorkOfferRequest request) throws WorkActionException;

	WorkActionResponse voidWork(VoidWorkRequest request) throws WorkActionException;

	WorkActionResponse rescheduleWork(RescheduleRequest request) throws WorkActionException, ValidationException;

	WorkActionResponse resendAllAssignments(WorkActionRequest request) throws WorkActionException;

	void saveAssets(WorkSaveRequest request, long workId);

	void saveCustomFields(WorkSaveRequest request, long workId);

	/**
	 * clearCustomFieldsIfDefaultValue happens on a template, where, if the value for a custom field in the template is the same as the default value, it doesn't save it as as response.
	 * This way, when the custom field is updated with a new value, the template is also updated.
	 * However, if the user changes the value to something other than the default, it saves that and future changes to the custom field default value are not represented in the template.
	 * @param request -
	 */
	void clearCustomFieldsIfDefaultValue(WorkSaveRequest request);

	void saveAssessments(WorkSaveRequest request, long workId);

	void saveResource(WorkSaveRequest request, long workId);

	void saveWorkResources(WorkSaveRequest request, long workId) throws Exception;

	void saveFollowers(WorkSaveRequest request, long workId);

	void saveWorkCore(WorkSaveRequest request, WorkDTO dto);

	void saveGroups(WorkSaveRequest request, long workId);

	void saveRoutingStrategy(WorkSaveRequest request, long workId);

	void saveOptions(WorkSaveRequest request, AbstractWork work);

	Set<WorkAuthorizationResponse> validateWorkForRouting(long workId);
}
