package com.workmarket.dao.summary.work;

import com.workmarket.dao.DAOInterface;
import com.workmarket.data.report.internal.BuyerSummary;
import com.workmarket.domains.model.summary.work.WorkStatusTransition;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public interface WorkStatusTransitionDAO extends DAOInterface<WorkStatusTransition> {

	WorkStatusTransition findWorkStatusTransition(Long workId, String workStatusTypeCode);

	List<WorkStatusTransition> findAllTransitionsByWork(Long workId);

	void deleteWorkStatusTransition(Long workId, String workStatusTypeCode);

	Integer countWorkStatusTransitions(String workStatusTypeCode, Calendar start, Calendar end);

	Integer countUniqueCompaniesWithWorkStatusTransitions(String workStatusTypeCode, Calendar start, Calendar end);

	List<BuyerSummary> findUniqueBuyersSummary(Calendar start, Calendar end);

	BigDecimal calculatePotentialRevenueByWorkStatusType(String workStatusTypeCode, Calendar start, Calendar end);

	BigDecimal calculateAveragePriceByWorkStatusType(String workStatusTypeCode, Calendar start, Calendar end);

	List<Object[]> findRoutedAssignmentsPerCompany(Calendar start, Calendar end);
}
   