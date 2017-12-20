package com.workmarket.domains.work.service.state;

import com.workmarket.domains.model.*;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.*;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.service.business.wrapper.CloseWorkResponse;
import com.workmarket.service.exception.account.AccountRegisterConcurrentException;
import com.workmarket.service.exception.account.InsufficientFundsException;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface WorkStatusService {
	WorkStatusType findWorkStatusTypeByCode(String code);

	List<WorkStatusType> findAllStatuses();

	void transitionToCreated(Long workId, Long userId, User onBehalfOfUser, Long companyId);

	void flatWorkTransitionToCreated(Long workId, Long userId, Long companyId);

	void transitionToSend(Work work, WorkActionRequest auditRequest);

	List<ConstraintViolation> transitionToComplete(WorkActionRequest auditRequest, CompleteWorkDTO completeWorkDTO);

	void transitionToIncomplete(Work work);

	CloseWorkResponse transitionToClosed(WorkActionRequest auditRequest, CloseWorkDTO closeWorkDTO);

	void onPostTransitionToClosed(long workId, CloseWorkDTO closeWorkDTO);

	List<ConstraintViolation> transitionToStopPayment(WorkActionRequest workRequest, StopPaymentDTO stopPaymentDTO);

	void transitionToAccepted(WorkActionRequest auditRequest, WorkResource workResource);

	/**
	 * Bulk payment of assignments
	 *
	 * @param assignmentIds
	 * @return
	 */
	Map<String, List<ConstraintViolation>> transitionPaymentPendingToPaid(List<Long> assignmentIds) throws InsufficientFundsException;

	/**
	 * There are different ways to pay an assignment, single payment, via an invoice bundle, statement, bulk payment.
	 * Assignments can be auto paid, pay within a delay process, have payment terms or being prefund.
	 * This method will handle all the post-paid operations that need to be performed on the assignment or related objects.
	 *
	 * @param work
	 * @param workResourceId
	 * @param paymentDate
	 * @param actor
	 */
	void onPostPayAssignment(Work work, Long workResourceId, Calendar paymentDate, User actor);

	void onPostPayAssignment(Work work, Long workResourceId, Calendar paymentDate, User actor, WorkMilestones workMilestones);

	/**
	 * Fulfills the assignment payment when done via paying an statement or invoice bundle
	 *
	 * @param workId
	 * @param invoicePaymentTrxId
	 * @param userActorId
	 * @return
	 */
	List<ConstraintViolation> transitionToFulfilledAndPaidFromInvoiceBulkPayment(long workId, long invoicePaymentTrxId, long userActorId);

	List<ConstraintViolation> transitionToVoid(WorkActionRequest auditRequest) throws AccountRegisterConcurrentException;

	List<ConstraintViolation> transitionToCancel(WorkActionRequest auditRequest, CancelWorkDTO cancelWorkDTO);

	void transitionToDeclined(WorkActionRequest auditRequest);

	void transitionFromAbandonedToOpenWork(Long workerUserId, WorkActionRequest workRequest);

	List<ConstraintViolation> transitionToExceptionAbandonedWork(Long contractorId, WorkActionRequest auditRequest, String message) throws AccountRegisterConcurrentException;

	List<ConstraintViolation> transitionActiveToSent(WorkActionRequest auditRequest, Boolean buyerInitiated);

	List<ConstraintViolation> transitionDeclinedToSent(WorkActionRequest auditRequest);

}
