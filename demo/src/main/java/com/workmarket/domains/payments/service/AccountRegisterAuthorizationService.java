package com.workmarket.domains.payments.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.work.model.Work;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.exception.account.AccountRegisterConcurrentException;

import java.math.BigDecimal;
import java.util.Set;

public interface AccountRegisterAuthorizationService {

	/**
	 * Calls the correct account register service, no exceptions are thrown
	 * @param workId
	 * @return WorkAuthorizationResponse
	 */
	WorkAuthorizationResponse authorizeWork(long workId);

	WorkAuthorizationResponse authorizeWork(Work work);

	void deauthorizeWork(Long workId) throws AccountRegisterConcurrentException;

	void deauthorizeWork(Work work) throws AccountRegisterConcurrentException;

	WorkAuthorizationResponse verifyFundsForAuthorization(User user, Work work, BigDecimal workTotalCost);

	WorkAuthorizationResponse verifyFundsForAuthorization(User user, BigDecimal workTotalCost, boolean paymentTerms);

	/**
	 * If a work bundle has been already authorized, this method will record the authorization for a bundle's child work
	 *
	 * @param workId
	 * @return WorkAuthorizationResponse
	 */
	WorkAuthorizationResponse registerWorkInBundleAuthorization(long workId);

	/**
	 * Returns the work authorization response from the account register.
	 *
	 * @param contractors
	 * @param work
	 * @param responseSummary
	 * @return WorkAuthorizationResponse
	 */
	WorkAuthorizationResponse authorizeContractors(Set<PeopleSearchResult> contractors, Work work, WorkRoutingResponseSummary responseSummary);

	void acceptWork(WorkResource workResource);

	void acceptWork(Long workId);

	void repriceWork(Work work);

	BigDecimal findRemainingAuthorizedAmountByWorkBundle(long workBundleId);

	WorkCostDTO calculateCostOnSentWork(Work work);

	void authorizeOnCompleteWork(WorkResource workResource);

	void authorizeOnCompleteWork(Long workId, double overridePrice);

	void voidWork(Work work);
}
