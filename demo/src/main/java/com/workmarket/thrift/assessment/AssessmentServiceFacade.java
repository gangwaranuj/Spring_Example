package com.workmarket.thrift.assessment;

import java.util.List;

public interface AssessmentServiceFacade {

	AssessmentResponse findAssessment(AssessmentRequest request) throws AssessmentRequestException;

	AssessmentResponse findAssessmentForGrading(AssessmentGradingRequest request) throws AssessmentRequestException;

	AssessmentResponse saveOrUpdateAssessment(AssessmentSaveRequest request) throws com.workmarket.thrift.core.ValidationException;

	AssessmentResponse copyAssessment(AssessmentCopyRequest request) throws com.workmarket.thrift.core.ValidationException;

	void updateAssessmentStatus(AssessmentStatusUpdateRequest request) throws com.workmarket.thrift.core.ValidationException;

	Item addOrUpdateItem(ItemSaveRequest request) throws com.workmarket.thrift.core.ValidationException;

	void removeItem(ItemRemoveRequest request) throws AssessmentRequestException;

	void reorderItems(ItemReorderRequest request) throws AssessmentRequestException;

	Attempt startAttempt(AttemptStartRequest request) throws AssessmentAttemptLimitExceededException;

	List<Response> submitResponses(AttemptResponseRequest request) throws AssessmentAttemptTimedOutException, com.workmarket.thrift.core.ValidationException;

	List<Response> submitMultipleItemResponses(AttemptMultipleItemResponsesRequest request) throws AssessmentAttemptTimedOutException, com.workmarket.thrift.core.ValidationException;

	Attempt completeAttempt(AttemptCompleteRequest request) throws AssessmentAttemptTimedOutException;

	void gradeResponses(GradeResponsesRequest request) throws AssessmentRequestException;

	Attempt gradeAttempt(GradeAttemptRequest request) throws AssessmentRequestException, AssessmentAttemptItemsNotGradedException;
}