package com.workmarket.domains.work.service;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiationPagination;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.wrapper.WorkNegotiationResponse;
import groovy.lang.Tuple2;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface WorkNegotiationService {

	/**
	 * Find a negotiation
	 *
	 * @param negotiationId
	 * @return Negotiation object
	 */
	AbstractWorkNegotiation findById(Long negotiationId);

	// Negotiating

	/**
	 * Find negotiations for an assignment
	 *
	 * @param workId
	 * @param pagination
	 * @return Negotiation objects
	 */
	WorkNegotiationPagination findByWork(Long workId, WorkNegotiationPagination pagination);

	Collection<WorkNegotiation> findAllNegotiationsByWorkId(Long id);

	/**
	 * Find negotiations for an assignment by a user
	 *
	 * @param userId
	 * @param workId
	 * @return Negotiation object
	 */
	WorkNegotiationPagination findByUserForWork(Long userId, Long workId, WorkNegotiationPagination pagination) throws Exception;

	WorkNegotiationPagination findByCompanyForWork(Long companyId, Long workId, WorkNegotiationPagination pagination) throws Exception;

	/**
	 * Find latest negotiation Approved for an assignment by a user that is approved.
	 *
	 * @param userId
	 * @param workId
	 * @return Negotiation object
	 */
	WorkNegotiation findLatestApprovedByUserForWork(Long userId, Long workId);

	WorkNegotiation findLatestApprovedByCompanyForWork(Long companyId, Long workId);

	/**
	 * Find latest spend limit increase by user
	 *
	 * @param userId TODO
	 * @param workId
	 * @return Reschedule negotiation object
	 */
	Optional<WorkExpenseNegotiation> findLatestActiveExpenseNegotiationByUserForWork(Long userId, Long workId) throws Exception;

	/**
	 * Find latest spend limit increase by company
	 *
	 * @param companyId
	 * @param workId
	 * @return Optional WorkExpenseNegotiation
	 */
	Optional<WorkExpenseNegotiation> findLatestActiveExpenseNegotiationByCompanyForWork(Long companyId, Long workId) throws Exception;

	/**
	 * Find all price negotiations by user
	 *
	 * @param userId
	 * @param workId
	 * @return
	 * @throws Exception
	 */
	Optional<WorkBonusNegotiation> findLatestActiveBonusNegotiationByUserForWork(Long userId, Long workId);

	/**
	 * Find latest active bonus negotiation by companyId
	 *
	 * @param companyId
	 * @param workId
	 * @return Optional WorkBonusNegotiation
	 * @throws Exception
	 */
	Optional<WorkBonusNegotiation> findLatestActiveBonusNegotiationByCompanyForWork(Long companyId, Long workId);
	/**
	 * Find latest approved reschedule request for an assignment.
	 * Note: When a reschedule request from the buyer is declined by the resource
	 * 			then the negotiation will be implicitly approved as the resource
	 * 			is removed from the assignment and no longer has a say in the matter.
	 *
	 * @param workId
	 * @return Reschedule negotiation object
	 */
	WorkRescheduleNegotiation findLatestApprovedRescheduleRequestForWork(Long workId);


	/**
	 * Find latest pending reschedule request for an assignment by a user of the company.
	 *
	 * @param companyId
	 * @param workId
	 * @return Reschedule negotiation object
	 * @throws Exception
	 */
	WorkRescheduleNegotiation findLatestActiveRescheduleRequestByCompanyForWork(boolean isResource, Long companyId, Long workId);

	/**
	 * Find all SLIs for work that happened before the work moved to Complete state
	 *
	 * @param workId
	 * @return
	 */
	List<WorkExpenseNegotiation> findPreCompletionExpenseIncreasesForWork(Long workId);

	/**
	 * Find all budget increases for work that happened before the work moved to Complete state
	 *
	 * @param workId
	 * @return
	 */
	List<WorkBonusNegotiation> findPreCompletionBonusesForWork(Long workId);

	/**
	 * Cancel any pending negotiations for an assignment by a user
	 *
	 * @param userId
	 * @param workId
	 */
	void cancelPendingNegotiationsByUserForWork(Long userId, Long workId) throws Exception;

	void cancelPendingNegotiationsByCompanyForWork(Long companyId, Long workId);

	/**
	 * Cancel any pending negotiations for an assignment by a user
	 *
	 * @param userId
	 * @param workId
	 */
	void cancelAllNegotiationsByUserForWork(Long userId, Long workId);

	void cancelAllNegotiationsByCompanyForWork(Long companyId, Long workId);

	/**
	 * Cancel any pending negotiations for an assignment
	 *
	 * @param workId -
	 */
	void cancelAllNegotiationsForWork(Long workId);


	/**
	 * A work resource can ask for a spend limit increase when an assignment is in progress.
	 * This differs in a other negotiations as its only related to price.
	 *
	 * @param workId
	 * @param dto
	 * @return WorkSpendlimitNegotiation object
	 */
	WorkNegotiationResponse createExpenseIncreaseNegotiation(Long workId, WorkNegotiationDTO dto) throws Exception;


	/**
	 * A work resource can ask for a budget increase when an assignment is in progress.
	 *
	 * @param workId
	 * @param dto
	 * @return WorkSpendlimitNegotiation object
	 */
	WorkNegotiationResponse createBudgetIncreaseNegotiation(Long workId, WorkNegotiationDTO dto) throws Exception;


	/**
	 * Allow a work resource to create a price and/or schedule counteroffer
	 * for approval by the assignment's buyer.
	 *
	 * @param workId
	 * @param dto
	 * @return Negotiation object
	 */
	WorkNegotiationResponse createNegotiation(Long workId, WorkNegotiationDTO dto) throws Exception;

	/**
	 * Allow a work resource to apply to an assignment, with an optional
	 * price and/or schedule counteroffer for approval by the assignment's buyer.
	 *
	 * @param workId
	 * @param dto
	 * @return Negotiation object
	 */
	WorkNegotiationResponse createApplyNegotiation(Long workId, WorkNegotiationDTO dto) throws Exception;

	WorkNegotiationResponse createApplyNegotiation(Long workId, Long workerId, WorkNegotiationDTO dto) throws Exception;

	/**
	 * Allow an active work resource to create a reschedule request
	 * for approval by the assignment's buyer.
	 *
	 * @param workId
	 * @param dto
	 * @return Reschedule negotiation object
	 */
	WorkRescheduleNegotiation createRescheduleNegotiation(Long workId, WorkNegotiationDTO dto);

	/**
	 * As the assignment's buyer, approve a negotiation offer,
	 * effectively updating the details of the assignment (conditional on the negotiation)
	 * and assigning the resource as the active resource for the assignment.
	 *
	 * @param negotiationId
	 */
	WorkNegotiationResponse approveNegotiation(Long negotiationId) throws Exception;

	WorkNegotiationResponse approveNegotiation(Long negotiationId, Long onBehalfOfUserId) throws Exception;

	/**
	 * As the assignment's buyer, decline a negotiation offer.
	 *
	 * @param negotiationId
	 */
	WorkNegotiationResponse declineNegotiation(Long negotiationId, String declineNote, Long onBehalfOfUserId) throws Exception;

	/**
	 * As the requestor, cancel a negotiation.
	 *
	 * @param negotiationId
	 */
	WorkNegotiationResponse cancelNegotiation(Long negotiationId) throws Exception;

	/**
	 * Extend the expiration date for the negotiation by X time.
	 *
	 * @param negotiationId
	 * @param time
	 * @param unit
	 */
	void extendNegotiationExpiration(Long negotiationId, Integer time, String unit) throws Exception;

	WorkNegotiationResponse createNegotiation(Long workId, WorkNegotiationDTO dto, Long onBehalfOfId) throws Exception;

	// TODO: lots of code duplication, refactor this and the previous two methods
	WorkNegotiationResponse createBonusNegotiation(Long workId, WorkNegotiationDTO dto) throws Exception;

	/**
	 * Returns a map<K,V> of the expenses paid to a company where the K = account service type code and V = amount paid
	 *
	 * @param companyId
	 * @param dateRange
	 * @return
	 */
	Map<String, BigDecimal> findTotalAdditionalExpensesPaidToCompany(Long companyId, DateRange dateRange);

	BigDecimal findTotalAdditionalExpensesPaidToCompanyByBuyer(Long resourceCompanyId, Long buyerCompanyId, DateRange dateRange, List<String> accountServiceType);

	Tuple2<ImmutableList<String>, String> reschedule(long workId, DateRange dateRange, String notes);
	WorkNegotiation findLatestByUserForWork(Long userId, Long workId);
}