
package com.workmarket.service.summary;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.block.AbstractBlockedAssociation;
import com.workmarket.domains.model.cache.MoneyAggregateSummary;
import com.workmarket.domains.model.cache.PaymentCenterAggregateSummary;
import com.workmarket.domains.model.cache.PeopleAggregateSummary;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.summary.TimeDimension;
import com.workmarket.domains.model.summary.company.CompanySummary;
import com.workmarket.domains.model.summary.group.UserGroupHistorySummary;
import com.workmarket.domains.model.summary.user.BlockedUserHistorySummary;
import com.workmarket.domains.model.summary.user.UserHistorySummary;
import com.workmarket.domains.model.summary.user.UserRatingChangeLogSummary;
import com.workmarket.domains.model.summary.user.UserRatingHistorySummary;
import com.workmarket.domains.model.summary.user.UserSummary;
import com.workmarket.domains.model.summary.work.WorkHistorySummary;
import com.workmarket.domains.model.summary.work.WorkResourceHistorySummary;
import com.workmarket.domains.model.summary.work.WorkStatusTransition;
import com.workmarket.domains.model.summary.work.WorkStatusTransitionHistorySummary;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.CompanySummaryDTO;
import com.workmarket.service.business.dto.WorkAggregatesDTO;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public interface SummaryService {

	PeopleAggregateSummary findPeopleAggregateSummaryByCompany(Long companyId);

	MoneyAggregateSummary findMoneyAggregateSummaryByCompany(Long companyId) ;

	BigDecimal getTotalUpcomingDueIn24Hours(Long userId);

	PaymentCenterAggregateSummary getPaymentCenterAggregateSummaryForBuyer(Long userId);

	PaymentCenterAggregateSummary getPaymentCenterAggregateSummaryForSeller(Long userId);

	PaymentCenterAggregateSummary getPaymentCenterAggregateSummaryForSeller(Long userId, boolean calculateFastFundableAmount);

	WorkAggregatesDTO countWorkByCompany(Long companyId);

	Integer countAssessmentsByCompany(Long companyId);

    Long findTimeDimensionId(Calendar calendar);

	TimeDimension findTimeDimension(Calendar calendar);

	//Work history
    WorkHistorySummary saveWorkHistorySummary(AbstractWork work);
	WorkHistorySummary saveWorkHistorySummary(AbstractWork work, WorkResource workResource);
    WorkHistorySummary saveWorkHistorySummary(AbstractWork work, WorkResource workResource, String workStatusTypeCode);
	WorkHistorySummary saveWorkHistorySummary(Long workId, String workStatusTypeCode, WorkResource workResource, Calendar date);

	//User history
	UserHistorySummary saveUserHistorySummary(User user);

	//Work resource history
	WorkResourceHistorySummary saveWorkResourceHistorySummary(WorkResource workResource);
	void saveWorkResourceHistorySummary(Set<WorkResource> workResources);

	//User group history
	UserGroupHistorySummary saveUserGroupAssociationHistorySummary(UserUserGroupAssociation userUserGroupAssociation);

	//Blocked user history
	<T extends AbstractBlockedAssociation> BlockedUserHistorySummary saveBlockedUserHistorySummary(T blockedAssociation);

	//Ratings history
	UserRatingHistorySummary saveUserRatingHistorySummary(Rating rating);

	//Rating change log history
	UserRatingChangeLogSummary saveUserRatingChangeLogSummary(Rating rating, Work work);

	List<WorkStatusTransition> findAllTransitionsByWork(Long workId);

	Integer countWorkStatusTransitions(String workStatusTypeCode, Calendar start, Calendar end);

	CompanySummary updateCompanySummary(long companyId);
	CompanySummary updateCompanySummary(CompanySummaryDTO companySummaryDTO);
	CompanySummary findCompanySummary(long companyId);

	/**
	 * Update group summary information for all groups that need updating.
	 *
	 * @return Ids of UserGroups that were updated
	 */
	List<Long> updateUserGroupSummary();

	WorkStatusTransitionHistorySummary saveWorkStatusTransitionHistorySummary(Long workId, WorkStatusType fromWorkStatusTypeCode, WorkStatusType toWorkStatusTypeCode, int timeInSeconds, Calendar date);
	WorkStatusTransitionHistorySummary saveWorkStatusTransitionHistorySummary(Work work, WorkStatusType fromWorkStatusTypeCode, WorkStatusType toWorkStatusTypeCode, int timeInSeconds);

	List<UserSummary> findAllUsersWithLastAssignedDateBetweenDates(Calendar fromDate, Calendar throughDate);

	int countGccBankAccountsSinceRelease();
}
