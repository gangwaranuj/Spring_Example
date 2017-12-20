package com.workmarket.service.business;

import com.workmarket.dao.rating.RatingDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.rating.AverageRating;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.work.model.Work;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisConfig;
import com.workmarket.service.business.dto.RatingDTO;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class RatingServiceIT extends BaseServiceIT {

	@Autowired LaneService laneService;
	@Autowired SummaryService summaryService;
	@Autowired RatingDAO ratingDAO;
	@Autowired RatingService ratingService;
	@Autowired @Qualifier("redisCacheOnly") RedisAdapter redisAdapter;

	private User worker;
	private User buyer;
	
	@Before
	public void initUsers() throws Exception {
		worker = newContractor();
		buyer = newFirstEmployee();
		laneService.addUserToCompanyLane2(worker.getId(), buyer.getCompany().getId());
	}

	@Test
	public void createRating_success() throws Exception {
		User employee = newWMEmployee();
		User contractor = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		RatingDTO ratingDTO = new RatingDTO(Rating.SATISFIED, Rating.EXCELLENT, Rating.EXCELLENT, Rating.EXCELLENT, "Decent work!");
		ratingDTO.setReviewSharedFlag(false);

		Work work = newWork(employee.getId());
		Rating rating = ratingService.createRatingForWork(employee.getId(), contractor.getId(), work.getId(), ratingDTO);

		Assert.assertTrue(rating.getId() > 0);
		assertEquals(Rating.SATISFIED, rating.getValue());
		assertEquals("Decent work!", rating.getReview());
		Assert.assertNotNull(rating.getWork());
	}

	@Test
	public void findAverageRatingForUserByCompany_withAverageRating_thenAverageRatingIsCached() throws Exception {
		User worker = newContractor();
		User buyer = newFirstEmployee();

		laneService.addUserToCompanyLane2(worker.getId(), buyer.getCompany().getId());

		RatingDTO ratingDTO = new RatingDTO(Rating.SATISFIED, Rating.EXCELLENT, Rating.EXCELLENT, Rating.EXCELLENT, "Decent work!");
		Work work = newWork(buyer.getId());
		work.setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.PAID));
		workService.saveOrUpdateWork(work);
		ratingService.createRatingForWork(buyer.getId(), worker.getId(), work.getId(), ratingDTO);

		ratingService.refreshAverageRatingForUserByCompany(worker.getId(), buyer.getCompany().getId());
		assertTrue(redisAdapter.get(getRedisKey(worker.getId(), buyer.getCompany().getId())).isPresent());
	}

	private static String getRedisKey(Long userId, Long companyId) {
		return String.format("%s%d:%d", RedisConfig.AVERAGE_USER_RATING_FOR_COMPANY, userId, companyId);
	}

	@Test
	public void findAverageRatingForUserByCompany_ratingCalculationIsCorrect() throws Exception {
		User worker = newContractor();
		User buyer = newFirstEmployee();

		laneService.addUserToCompanyLane2(worker.getId(), buyer.getCompany().getId());

		RatingDTO ratingDTO = new RatingDTO(Rating.SATISFIED, Rating.EXCELLENT, Rating.EXCELLENT, Rating.EXCELLENT, "Decent work!");
		Work work = newWork(buyer.getId());
		Rating rating = ratingService.createRatingForWork(buyer.getId(), worker.getId(), work.getId(), ratingDTO);
		summaryService.saveUserRatingHistorySummary(rating);

		AverageRating r = ratingService.findAverageRatingForUserByCompany(worker.getId(), buyer.getCompany().getId());
		assertEquals(100, r.getAverage(), 0);
		assertEquals(1, r.getCount(), 0);
	}

	@Test
	public void findLatestForUserForWork() throws Exception {
		RatingDTO ratingDTO = new RatingDTO(Rating.SATISFIED, Rating.EXCELLENT, Rating.EXCELLENT, Rating.EXCELLENT, "Decent work!");
		Work work = newWork(buyer.getId());
		Rating rating = ratingService.createRatingForWork(buyer.getId(), worker.getId(), work.getId(), ratingDTO);
		summaryService.saveUserRatingHistorySummary(rating);
		Rating r = ratingService.findLatestRatingForUserForWork(worker.getId(), work.getId());
		assertNotNull(r);
	}

	@Test
	public void findLatestForUserForWork_nullIfDeleted() throws Exception {
		RatingDTO ratingDTO = new RatingDTO(Rating.SATISFIED, Rating.EXCELLENT, Rating.EXCELLENT, Rating.EXCELLENT, "Decent work!");
		Work work = newWork(buyer.getId());
		Rating rating = ratingService.createRatingForWork(buyer.getId(), worker.getId(), work.getId(), ratingDTO);
		summaryService.saveUserRatingHistorySummary(rating);
		ratingService.deleteRating(rating.getId());
		Rating r = ratingService.findLatestRatingForUserForWork(worker.getId(), work.getId());
		assertNull(r);
	}
}
