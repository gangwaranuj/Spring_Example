package com.workmarket.domains.work.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.exception.account.InvalidPricingException;
import com.workmarket.thrift.work.WorkSaveRequest;

import java.util.List;

public interface WorkValidationService {

	List<ConstraintViolation> validateSaveWorkResource(Long workId, Long userId);

	List<ConstraintViolation> validateComplete(Work work, CompleteWorkDTO dto, Boolean isOnBehalfOf) throws InvalidPricingException;

	List<ConstraintViolation> validateDelegate(Long workId, Long delegateUserId);

	List<ConstraintViolation> validateClosed(Work work);

	List<ConstraintViolation> validateStopPayment(Work work);

	List<ConstraintViolation> validateVoid(Long workId);

	List<ConstraintViolation> validateCancel(Work work);

	List<ConstraintViolation> validatePaid(Work work);

	List<ConstraintViolation> validateExceptionAbandonedWork(Long userId, Long workId, String message);

	List<ConstraintViolation> validatePaymentPending(Long workId);

	List <ConstraintViolation> validateUnassign(WorkStatusType workStatusType, UnassignDTO dto);

	List<String> validateApproveWorkNegotiation(Long negotiationId, Long onBehalfOfUserId);

	List<String> validateDeclineWorkNegotiation(Long negotiationId, Long onBehalfOfUserId);

	List<String> validateCancelWorkNegotiation(Long negotiationId);

	List<ConstraintViolation> validateRepriceWork(Long workId, PricingStrategy newPricing, List<WorkResource> workResources, boolean allowLowerCostOnActive);

	boolean validateAssignmentCountry(Work work, User user);

	boolean validateAssignmentCountry(Work work, PeopleSearchResult user);

	boolean isWorkResourceValidForDispatch(long userId, long workCompanyId);

	boolean isWorkResourceValidForWork(Long userId, Long userCompanyId, Long workCompanyId);

	List<ConstraintViolation> validateDeliverableRequirements(boolean isOnBehalfOf, String workNumber);

	List<ConstraintViolation> validateDeliverableRequirements(boolean isOnBehalfOf, Work work);

	List<com.workmarket.thrift.core.ConstraintViolation> validateWorkUniqueId(WorkSaveRequest request, WorkStatusType statusType);

	ConstraintViolation validateNotInvoiced(Long workId);
}
