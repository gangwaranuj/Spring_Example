package com.workmarket.service.business;

import com.workmarket.dao.UserDAOImpl;
import com.workmarket.domains.work.dao.WorkDAOImpl;
import com.workmarket.dao.rating.RatingDAOImpl;
import com.workmarket.dao.summary.user.UserRatingHistorySummaryDAOImpl;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.reporting.RatingReportPagination;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.analytics.cache.ScorecardCacheImpl;
import com.workmarket.service.business.dto.RatingDTO;
import com.workmarket.domains.groups.service.UserGroupValidationServiceImpl;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.security.LaneContext;
import com.workmarket.service.summary.SummaryServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyLong;

@RunWith(MockitoJUnitRunner.class)
public class RatingServiceTest {

	@Mock RatingDAOImpl ratingDAO;
	@Mock UserDAOImpl userDAO;
	@Mock WorkDAOImpl workDAO;
	@Mock UserRatingHistorySummaryDAOImpl userRatingHistorySummaryDAO;
	@Mock LaneServiceImpl laneService;
	@Mock SummaryServiceImpl summaryService;
	@Mock UserNotificationService userNotificationService;
	@Mock ScorecardCacheImpl scorecardCache;
	@Mock UserGroupValidationServiceImpl userGroupValidationService;
	@Mock EventRouter eventRouter;
	@InjectMocks RatingServiceImpl ratingService = spy(new RatingServiceImpl());

	private final Integer GOOD_RATING_VALUE = 3;

	private Long companyId;
	private Work work;
	private WorkStatusType workStatusType;
	private User raterUser, ratedUser;
	private Company raterCompany;
	private RatingReportPagination reportPagination;
	private RatingDTO ratingDTO;
	private Rating rating;
	private LaneContext laneContext;
	private Map<String, Object> params;

	@Before
	public void setup() {
		companyId = 1L;

		reportPagination = mock(RatingReportPagination.class);
		ratingDTO = mock(RatingDTO.class);
		when(ratingDTO.getValue()).thenReturn(GOOD_RATING_VALUE);
		when(ratingDTO.getCommunication()).thenReturn(GOOD_RATING_VALUE);
		when(ratingDTO.getQuality()).thenReturn(GOOD_RATING_VALUE);
		when(ratingDTO.getProfessionalism()).thenReturn(GOOD_RATING_VALUE);

		raterCompany = mock(Company.class);
		raterUser = mock(User.class);
		when(raterUser.getCompany()).thenReturn(raterCompany);
		when(raterUser.getId()).thenReturn(1001L);
		when(raterCompany.getId()).thenReturn(companyId);

		ratedUser = mock(User.class);
		when(ratedUser.getId()).thenReturn(9999L);

		work = mock(Work.class);
		workStatusType = mock(WorkStatusType.class);
		when(work.getId()).thenReturn(1L);
		when(workDAO.get(anyLong())).thenReturn(work);
		when(work.getBuyer()).thenReturn(raterUser);
		when(work.getWorkStatusType()).thenReturn(workStatusType);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.PAID);

		raterCompany = mock(Company.class);
		laneContext = mock(LaneContext.class);
		rating = mock(Rating.class);
		when(rating.getId()).thenReturn(1L);
		when(rating.getRatedUser()).thenReturn(ratedUser);


		params = new HashMap<>();
		params.put(ProfileModificationType.RATING, GOOD_RATING_VALUE);

		when(userDAO.get(raterUser.getId())).thenReturn(raterUser);
		when(userDAO.get(ratedUser.getId())).thenReturn(ratedUser);
		when(ratingDAO.get(rating.getId())).thenReturn(rating);
		when(laneService.getLaneContextForUserAndCompany(eq(ratedUser.getId()), anyLong())).thenReturn(laneContext);

		when(laneContext.getLaneType()).thenReturn(LaneType.LANE_3);
	}

	@Test
	public void createRating_ratingCreated() {
		Rating result = ratingService.createRatingForWork(raterUser.getId(), ratedUser.getId(), work.getId(), ratingDTO);
		verify(summaryService).saveUserRatingHistorySummary(any(Rating.class));
		verify(userNotificationService).onRatingCreated(any(Rating.class));
		verify(scorecardCache).evictAllResourceScoreCardsForUser(ratedUser.getId());
		verify(userGroupValidationService).revalidateAllAssociationsByUserAsync(ratedUser.getId(), params);
		assertNotNull(result);
	}

	@Test
	public void flagRatingForReview_ratingFlagged() {
		boolean flagForReview = true;
		Rating result = ratingService.flagRatingForReview(rating.getId(), flagForReview);
		verify(rating).setFlaggedForReview(flagForReview);
		assertNotNull(result);
	}

	@Test
	public void deleteRating_ratingDeleted() {
		Rating result = ratingService.deleteRating(rating.getId());
		verify(rating).setDeleted(true);
		verify(userRatingHistorySummaryDAO).deleteRatingHistorySummaryByRatingId(rating.getId());
		assertNotNull(result);
	}

	@Test
	public void findRatingsByCompany_returnPagination() {
		ratingService.findRatingsByCompany(companyId, reportPagination);
		verify(ratingDAO).buildRatingReportForCompany(companyId, reportPagination);
	}

	@Test
	public void findLatestRatingForUserForWork_returnRating() {
		ratingService.findLatestRatingForUserForWork(ratedUser.getId(), 1L);
		verify(ratingDAO).findLatestForUserForWork(ratedUser.getId(), 1L);
	}

}
