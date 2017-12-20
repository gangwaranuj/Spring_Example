package com.workmarket.domains.work.dao;

import com.google.common.base.Optional;
import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface WorkNegotiationDAO extends PaginatableDAOInterface<AbstractWorkNegotiation> {

	<T extends AbstractWorkNegotiation> T findById(long id);

	WorkNegotiationPagination findByWork(long workId, WorkNegotiationPagination pagination);
	WorkNegotiationPagination findByUserForWork(long userId, long workId, WorkNegotiationPagination pagination);
	WorkNegotiationPagination findByCompanyForWork(final long companyId, final long workId, WorkNegotiationPagination pagination);
	WorkNegotiation findLatestByUserForWork(Long userId, Long workId);
	WorkNegotiation findLatestApprovedByUserForWork(Long userId, Long workId);
	WorkNegotiation findLatestApprovedByCompanyForWork(Long companyId, Long workId);
	WorkNegotiation findLatestApprovedForWork(Long workId);

	Optional<WorkExpenseNegotiation> findLatestActiveExpenseNegotiationByUserForWork(Long userId, Long workId);
	Optional<WorkExpenseNegotiation> findLatestActiveExpenseNegotiationByCompanyForWork(Long companyId, Long workId);
	Optional<WorkExpenseNegotiation> findLatestActiveExpenseNegotiationForWork(Long workId);
	List<WorkExpenseNegotiation> findAllActiveExpenseNegotiationsByUserAndWork(Long userId, Long workId);
	List<WorkExpenseNegotiation> findAllActiveExpenseNegotiationsByCompanyAndWork(Long companyId, Long workId);
	Optional<WorkExpenseNegotiation> findLatestApprovedExpenseIncreaseForWork(Long workId);

	Optional<WorkBudgetNegotiation> findLatestActiveBudgetNegotiationForWork(Long workId);
	Optional<WorkBudgetNegotiation> findLatestActiveBudgetNegotiationByUserForWork(Long userId, Long workId);
	Optional<WorkBudgetNegotiation> findLatestActiveBudgetNegotiationByCompanyForWork(Long companyId, Long workId);
	List<WorkBudgetNegotiation> findAllActiveBudgetNegotiationsByUserAndWork(Long userId, Long workId);
	List<WorkBudgetNegotiation> findAllActiveBudgetNegotiationsByCompanyAndWork(Long companyId, Long workId);

	Optional<WorkBonusNegotiation> findLatestActiveBonusNegotiationByUserForWork(Long userId, Long workId);
	Optional<WorkBonusNegotiation> findLatestActiveBonusNegotiationByCompanyForWork(Long companyId, Long workId);
	Optional<WorkBonusNegotiation> findLatestActiveBonusNegotiationForWork(Long workId);
	List<WorkBonusNegotiation> findAllActiveBonusNegotiationsByUserAndWork(Long userId, Long workId);
	List<WorkBonusNegotiation> findAllActiveBonusNegotiationsByCompanyAndWork(Long companyId, Long workId);
	Optional<WorkBonusNegotiation> findLatestApprovedBonusForWork(Long workId);

	WorkRescheduleNegotiation findLatestApprovedRescheduleRequestForWork(Long workId);
	WorkRescheduleNegotiation findLatestRescheduleRequestByUserForWork(Long userId, Long workId);
	WorkRescheduleNegotiation findLatestActiveRescheduleRequestByUserForWork(Long userId, Long workId);
	WorkRescheduleNegotiation findLatestActiveRescheduleRequestByCompanyForWork(boolean isResource, Long companyId, Long workId);

	Collection<WorkNegotiation> findAllByWork(Long id);

	/**
	 * These will be sorted in descending order by amount
	 */
	List<WorkExpenseNegotiation> findPreCompletionExpenseIncreasesForWork(Long workId);
	List<WorkBonusNegotiation> findPreCompletionBonusesForWork(Long workId);

	Map<String, BigDecimal> findTotalAdditionalExpensesPaidToCompany(Long companyId, DateRange dateRange);

	BigDecimal findTotalAdditionalExpensesPaidToCompanyByBuyer(Long resourceCompanyId, Long buyerCompanyId, DateRange dateRange, List<String> accountServiceType);

	List<Long> findAllApplicantsPendingApproval(long workId);
}