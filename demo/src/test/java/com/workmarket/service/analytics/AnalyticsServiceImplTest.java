package com.workmarket.service.analytics;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.session.ImpressionDAO;
import com.workmarket.dao.summary.user.BlockedUserHistorySummaryDAO;
import com.workmarket.dao.summary.user.UserRatingHistorySummaryDAO;
import com.workmarket.dao.summary.work.WorkHistorySummaryDAO;
import com.workmarket.dao.summary.work.WorkStatusTransitionHistorySummaryDAO;
import com.workmarket.domains.work.dao.WorkResourceLabelDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.analytics.BuyerScoreCard;
import com.workmarket.domains.model.analytics.BuyerScoreField;
import com.workmarket.domains.model.analytics.ResourceScoreCard;
import com.workmarket.domains.model.analytics.ScoreCard;
import com.workmarket.domains.model.rating.RatingSummary;
import com.workmarket.domains.model.session.Impression;
import com.workmarket.domains.model.session.ImpressionType;
import com.workmarket.domains.model.summary.company.CompanySummary;
import com.workmarket.domains.work.model.WorkResourceAggregateFilter;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.analytics.cache.ScorecardCache;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.RatingService;
import com.workmarket.dto.CampaignStatisticsDTO;
import com.workmarket.service.business.dto.ImpressionDTO;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.summary.SummaryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AnalyticsServiceImplTest {

	@Mock ImpressionDAO impressionDAO;
	@Mock private BlockedUserHistorySummaryDAO blockedUserHistorySummaryDAO;
	@Mock private WorkHistorySummaryDAO workHistorySummaryDAO;
	@Mock private UserDAO userDAO;
	@Mock private SummaryService summaryService;
	@Mock private RatingService ratingService;
	@Mock private WorkResourceService resourceService;
	@Mock private WorkResourceLabelDAO workResourceLabelDAO;
	@Mock private WorkStatusTransitionHistorySummaryDAO workStatusTransitionHistorySummaryDAO;
	@Mock private UserRatingHistorySummaryDAO userRatingHistorySummaryDAO;
	@Mock private ScorecardCache scorecardCache;
	@Mock private BillingService billingService;
	@Mock private WorkService workService;
	@Mock private CompanyService companyService;
	@InjectMocks AnalyticsServiceImpl analyticsService;

	private User user;
	private Company company;
	private CompanySummary companySummary;
	private ImpressionDTO impressionDTO;
	private RatingSummary ratingSummary;

	@Before
	public void setUp() {
		companySummary = new CompanySummary();
		companySummary.setTotalCancelledAssignments(10);
		companySummary.setTotalPaidAssignments(100);
		companySummary.setTotalLatePaidAssignments(10);

		impressionDTO = new ImpressionDTO();
		impressionDTO.setCampaignId(1L);
		impressionDTO.setReferrer("Referrer");
		impressionDTO.setUserAgent("Firefox");
		impressionDTO.setField1Name("rcampagin");
		impressionDTO.setField1Value("2235");
		impressionDTO.setImpressionTypeId(Long.valueOf(ImpressionType.RECRUITING.ordinal()));

		user = mock(User.class);
		company = mock(Company.class);
		ratingSummary = mock(RatingSummary.class);
		when(userDAO.get(anyLong())).thenReturn(user);
		when(user.getCompany()).thenReturn(company);
		when(impressionDAO.newCampaignStatistics(anyLong(), anyLong())).thenReturn(mock(CampaignStatisticsDTO.class));
		when(summaryService.findCompanySummary(anyLong())).thenReturn(companySummary);
		when(workStatusTransitionHistorySummaryDAO.calculateAverageTransitionTimeByCompanyInSeconds(anyString(), anyString(), anyLong(), any(DateRange.class))).thenReturn(BigDecimal.valueOf(86400));
		when(userRatingHistorySummaryDAO.calculatePercentageRatingsByCompany(anyLong(), anyInt(), any(DateRange.class), anyBoolean())).thenReturn(BigDecimal.valueOf(99));
		when(workStatusTransitionHistorySummaryDAO.calculateAverageTimeToPayFromDueDateByCompanyInSeconds(anyLong(), any(DateRange.class), anyBoolean())).thenReturn(BigDecimal.valueOf(86400));
		when(impressionDAO.get(anyLong())).thenReturn(mock(Impression.class));
		when(scorecardCache.getBuyerScorecard(anyLong())).thenReturn(Optional.<BuyerScoreCard>absent());
		when(scorecardCache.getResourceScorecard(anyLong())).thenReturn(Optional.<ResourceScoreCard>absent());
		when(scorecardCache.getResourceScorecard(anyLong(), anyLong())).thenReturn(Optional.<ResourceScoreCard>absent());
		when(ratingService.findRatingSummaryForUser(anyLong())).thenReturn(ratingSummary);
		when(ratingService.findRatingSummaryForUserSinceDate(anyLong(), any(Calendar.class))).thenReturn(ratingSummary);
		when(ratingService.findRatingSummaryForUserByCompany(anyLong(), anyLong())).thenReturn(ratingSummary);
		when(ratingService.findRatingSummaryForUserByCompanySinceDate(anyLong(), anyLong(), any(Calendar.class))).thenReturn(ratingSummary);
		when(ratingSummary.getSatisfactionRate()).thenReturn(100.00);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveOrUpdateImpression_withNullDTO_Fail() {
	 	analyticsService.saveOrUpdateImpression(null);
	}

	@Test
	public void saveOrUpdateImpression_success() {
		assertNotNull(analyticsService.saveOrUpdateImpression(impressionDTO));
	}

	@Test
	public void saveOrUpdateImpression_withExistingId_success() {
		impressionDTO.setImpressionId(1L);
		assertNotNull(analyticsService.saveOrUpdateImpression(impressionDTO));
	}

	@Test(expected = IllegalArgumentException.class)
	public void newCampaignStatistics_withNullArgs_fails () {
		analyticsService.newCampaignStatistics(null, null);
	}

	@Test
	public void newCampaignStatistics_success () {
		assertNotNull(analyticsService.newCampaignStatistics(1L, 1L));
	}

	@Test(expected = IllegalArgumentException.class)
	public void countDistinctBlockingCompaniesByUser_withNullDate_Fail() {
		analyticsService.countDistinctBlockingCompaniesByUser(null);
	}

	@Test
	public void countDistinctBlockingCompaniesByUser_success() {
		assertNotNull(analyticsService.countDistinctBlockingCompaniesByUser(Calendar.getInstance()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void countDistinctBlockingCompaniesByUser_withNullDateAndNullUsers_Fail() {
		analyticsService.countDistinctBlockingCompaniesByUser(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void countDistinctBlockingCompaniesByUser_withNullDateAndNonEmptyUsers_Fail() {
		analyticsService.countDistinctBlockingCompaniesByUser(null, Lists.newArrayList(1L));
	}

	@Test
	public void countDistinctBlockingCompaniesByUser_withDateAndUsers_success() {
		assertNotNull(analyticsService.countDistinctBlockingCompaniesByUser(Calendar.getInstance(), Lists.newArrayList(1L)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void countRepeatedClientsByUser_withNullDateAndNullUsers_Fail() {
		analyticsService.countRepeatedClientsByUser(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void countRepeatedClientsByUser_withNullDateAndNonEmptyUsers_Fail() {
		analyticsService.countRepeatedClientsByUser(null, Lists.newArrayList(1L));
	}

	@Test
	public void countRepeatedClientsByUser_withDateAndUsers_success() {
		assertNotNull(analyticsService.countRepeatedClientsByUser(Calendar.getInstance(), Lists.newArrayList(1L)));
	}

	@Test
	public void getResourceScoreCard_withUserSuccess() {
		ScoreCard scoreCard = analyticsService.getResourceScoreCard(1L);
		assertNotNull(scoreCard);
	}

	@Test
	public void getResourceScoreCard_verifyRatingService() {
		ScoreCard scoreCard = analyticsService.getResourceScoreCard(1L);
		assertNotNull(scoreCard);
		verify(ratingService, times(1)).findRatingSummaryForUser(1L);
	}

	@Test
	public void getResourceScoreCard_verifyWorkResourceLabelDAO() {
		ScoreCard scoreCard = analyticsService.getResourceScoreCard(1L);
		assertNotNull(scoreCard);
		verify(workResourceLabelDAO, times(3)).countAllConfirmedWorkResourceLabelsByUserId(any(WorkResourceAggregateFilter.class), any(List.class));
	}

	@Test
	public void getResourceScoreCard_verifyResourceService() {
		ScoreCard scoreCard = analyticsService.getResourceScoreCard(1L);
		assertNotNull(scoreCard);
		verify(resourceService, times(1)).countAssignmentsByResourceUserIdAndStatus(any(List.class), any(WorkResourceAggregateFilter.class));
		verify(resourceService, times(2)).calculateOnTimePercentageForUser(Lists.newArrayList(anyLong()), any(WorkResourceAggregateFilter.class), anyMap());
		verify(resourceService, times(2)).calculateDeliverableOnTimePercentageForUser(Lists.newArrayList(anyLong()), any(WorkResourceAggregateFilter.class), anyMap());
		verify(resourceService, times(2)).countConfirmedWorkResourceLabelByUserId(anyLong(), any(WorkResourceAggregateFilter.class));
		verify(resourceService).countAllAssignmentsByResourceUserIdAndStatus(any(List.class), any(WorkResourceAggregateFilter.class));
	}

	@Test
	public void getResourceScoreCardForCompany_withUserSuccess() {
		ScoreCard scoreCard = analyticsService.getResourceScoreCardForCompany(1L, 1L);
		assertNotNull(scoreCard);
		verify(ratingService, times(2)).findRatingSummaryForUserByCompany(1L, 1L);
		verify(workResourceLabelDAO, times(3)).countAllConfirmedWorkResourceLabelsByUserId(any(WorkResourceAggregateFilter.class), any(List.class));
	}

	@Test
	public void getBuyerScoreCard_success() {
		when(billingService.countAllDueWorkByCompany(anyLong())).thenReturn(1);
		when(workService.countWorkByCompanyByStatus(anyLong(), anyList())).thenReturn(1);
		when(workService.countAllDueWorkByCompany(anyLong())).thenReturn(1);
		when(companyService.hasWorkPastDueMoreThanXDays(anyLong(), anyInt())).thenReturn(false);

		BuyerScoreCard scoreCard = (BuyerScoreCard)analyticsService.getBuyerScoreCardByUserId(1L);
		assertNotNull(scoreCard);
		assertNotNull(scoreCard.getValues());
		assertEquals(scoreCard.getValues().get(BuyerScoreField.PAID_WORK).getAll(), 100D, .001);
		assertEquals(scoreCard.getValues().get(BuyerScoreField.AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS).getNet90(), 1D, .001);
		assertEquals(scoreCard.getValues().get(BuyerScoreField.AVERAGE_TIME_TO_PAY_WORK_IN_DAYS).getNet90(), 1D, .001);
	}

	@Test(expected = Exception.class)
	public void getResourceScoreCards_ByUser_NullInput_TossException() {
		analyticsService.getResourceScoreCards(null);
	}

	@Test
	public void getResourceScoreCards_ByUser_EmptyInput_EmptyResult() {
		Map<Long, ResourceScoreCard> result = analyticsService.getResourceScoreCards(Lists.<Long>newArrayList());
		assertTrue(result.isEmpty());
	}

	@Test
	public void getResourceScoreCards_ByUser_withUserSuccess() {
		Map<Long, ResourceScoreCard> scoreCards = analyticsService.getResourceScoreCards(Lists.newArrayList(1L, 2L));
		assertEquals(2, scoreCards.size());
	}

	@Test
	public void getResourceScoreCard_ByUser_verifyRatingService() {
		analyticsService.getResourceScoreCards(Lists.newArrayList(1L, 2L));
		verify(ratingService, times(1)).findRatingSummaryForUser(1L);
		verify(ratingService, times(1)).findRatingSummaryForUser(2L);
	}

	@Test
	public void getResourceScoreCards_ByUser_verifyWorkResourceLabelDAO() {
		analyticsService.getResourceScoreCards(Lists.newArrayList(1L, 2L));
		verify(workResourceLabelDAO, times(6)).countAllConfirmedWorkResourceLabelsByUserId(any(WorkResourceAggregateFilter.class), any(List.class));
	}

	@Test
	public void getResourceScoreCards_ByUser_verifyResourceService() {
		analyticsService.getResourceScoreCards(Lists.newArrayList(1L, 2L));
		verify(resourceService, times(2)).countAssignmentsByResourceUserIdAndStatus(any(List.class), any(WorkResourceAggregateFilter.class));
		verify(resourceService, times(4)).calculateOnTimePercentageForUser(Lists.newArrayList(anyLong()), any(WorkResourceAggregateFilter.class), anyMap());
		verify(resourceService, times(4)).calculateDeliverableOnTimePercentageForUser(Lists.newArrayList(anyLong()), any(WorkResourceAggregateFilter.class), anyMap());
		verify(resourceService, times(4)).countConfirmedWorkResourceLabelByUserId(anyLong(), any(WorkResourceAggregateFilter.class));
		verify(resourceService, times(2)).countAllAssignmentsByResourceUserIdAndStatus(any(List.class), any(WorkResourceAggregateFilter.class));
	}

	@Test(expected = Exception.class)
	public void getResourceScoreCards_ByCompany_NullInput_TossException() {
		analyticsService.getResourceScoreCardsForCompany(1L, null);
	}

	@Test
	public void getResourceScoreCards_ByCompany_EmptyInput_EmptyResult() {
		Map<Long, ResourceScoreCard> result = analyticsService.getResourceScoreCardsForCompany(1L, Lists.<Long>newArrayList());
		assertTrue(result.isEmpty());
	}

	@Test
	public void getResourceScoreCards_ByCompany_withUserSuccess() {
		Map<Long, ResourceScoreCard> scoreCards = analyticsService.getResourceScoreCardsForCompany(1L, Lists.newArrayList(1L, 2L));
		assertEquals(2, scoreCards.size());
	}

	@Test
	public void getResourceScoreCard_ByCompany_verifyRatingService() {
		analyticsService.getResourceScoreCardsForCompany(3L, Lists.newArrayList(1L, 2L));
		verify(ratingService, times(2)).findRatingSummaryForUserByCompany(1L, 3L);
		verify(ratingService, times(2)).findRatingSummaryForUserByCompany(2L, 3L);
	}

	@Test
	public void getResourceScoreCards_ByCompany_verifyWorkResourceLabelDAO() {
		analyticsService.getResourceScoreCardsForCompany(1L, Lists.newArrayList(1L, 2L));
		verify(workResourceLabelDAO, times(6)).countAllConfirmedWorkResourceLabelsByUserId(any(WorkResourceAggregateFilter.class), any(List.class));
	}

	@Test
	public void getResourceScoreCards_ByCompany_verifyResourceService() {
		analyticsService.getResourceScoreCardsForCompany(1L, Lists.newArrayList(1L, 2L));
		verify(resourceService, times(2)).countAssignmentsByResourceUserIdAndStatus(any(List.class), any(WorkResourceAggregateFilter.class));
		verify(resourceService, times(4)).calculateOnTimePercentageForUser(Lists.newArrayList(anyLong()), any(WorkResourceAggregateFilter.class), anyMap());
		verify(resourceService, times(4)).calculateDeliverableOnTimePercentageForUser(Lists.newArrayList(anyLong()), any(WorkResourceAggregateFilter.class), anyMap());
		verify(resourceService, times(4)).countConfirmedWorkResourceLabelByUserId(anyLong(), any(WorkResourceAggregateFilter.class));
		verify(resourceService, times(2)).countAllAssignmentsByResourceUserIdAndStatus(any(List.class), any(WorkResourceAggregateFilter.class));
	}
}
