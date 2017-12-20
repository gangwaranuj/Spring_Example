package com.workmarket.service.thrift;

import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.AcceptWorkOfferRequest;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkActionResponse;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.WorkSaveRequest;

import java.util.List;
import java.util.Set;

public interface TWorkFacadeService {

	WorkResponse saveOrUpdateWork(WorkSaveRequest request) throws ValidationException;

	WorkResponse saveOrUpdateWork(WorkSaveRequest request, Set<WorkRequestInfo> includes) throws ValidationException;

	WorkResponse saveOrUpdateWorkDraft(WorkSaveRequest request) throws ValidationException;

	WorkResponse saveOrUpdateWorkDraft(WorkSaveRequest request, Set<WorkRequestInfo> includes) throws ValidationException;

	WorkResponse saveOrUpdateWorkTemplate(WorkSaveRequest request) throws ValidationException;

	WorkResponse findWorkDetail(WorkRequest request) throws WorkActionException;

	WorkResponse findWorkDetailLight(WorkRequest request) throws WorkActionException;

	WorkResponse findWork(WorkRequest request) throws WorkActionException;

	List<WorkResponse> findWorks(List<WorkRequest> request) throws WorkActionException;

	AcceptWorkResponse acceptWork(User user, Work work);

	AcceptWorkResponse acceptWork(Long userId, Long workId);

	public List<AcceptWorkResponse> acceptWorkBundle(Long userId, Long workId);

	WorkActionResponse acceptWorkOnBehalf(AcceptWorkOfferRequest acceptWorkOfferRequest) throws WorkActionException;

	TimeTrackingResponse checkInActiveResource(TimeTrackingRequest timeTrackingRequest);

	TimeTrackingResponse checkOutActiveResource(TimeTrackingRequest timeTrackingRequest);

	WorkResponse eventUploadHelper(WorkSaveRequest saveRequest, String uploadKey, String uploadSizeKey);
}
