package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.rating.RatingPagination;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.rating.RatingValidatingServiceImpl;
import com.workmarket.domains.work.service.WorkServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: iloveopt
 * Date: 2/26/14
 */

@RunWith(MockitoJUnitRunner.class)
public class RatingValidatingServiceImplTest {

	@InjectMocks RatingValidatingServiceImpl ratingValidatingService;
	@Mock RatingServiceImpl ratingService;
	@Mock WorkServiceImpl workService;
	@Mock UserServiceImpl userService;

	private Long workerUserId;
	private Long buyerUserId;
	private Long workId;
	private Work work;
	private WorkStatusType workStatusType;
	private WorkResource workResource;
	private User worker;
	private User buyer;
	private Company buyerCompany;
	private Company workerCompany;
	private Rating rating;
	private RatingPagination ratingPagination;
	private List<Rating> ratings;

	@Before
	public void setup() {
		workId = 1L;
		workerUserId = 1L;  // Worker
		buyerUserId = 2L;  // Buyer
		rating = mock(Rating.class);
		ratingPagination = mock(RatingPagination.class);
		ratings = Lists.newArrayList();

		work = mock(Work.class);
		when(work.getId()).thenReturn(workId);

		worker = mock(User.class);
		when(worker.getId()).thenReturn(workerUserId);

		buyer = mock(User.class);
		when(buyer.getId()).thenReturn(buyerUserId);

		workerCompany = mock(Company.class);
		when(workerCompany.getId()).thenReturn(1L);
		buyerCompany = mock(Company.class);
		when(buyerCompany.getId()).thenReturn(2L);

		workResource = mock(WorkResource.class);
		when(workResource.getUser()).thenReturn(worker);

		workStatusType = mock(WorkStatusType.class);

		when(buyer.getCompany()).thenReturn(buyerCompany);
		when(worker.getCompany()).thenReturn(workerCompany);
		when(work.getBuyer()).thenReturn(buyer);
		when(work.getWorkStatusType()).thenReturn(workStatusType);


		when(workService.findWork(anyLong())).thenReturn(work);
		when(userService.getUser(buyerUserId)).thenReturn(buyer);
		when(userService.getUser(workerUserId)).thenReturn(worker);
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);

		when(ratingService.findRatingsForUserForWork(anyLong(), anyLong())).thenReturn(ratings);
		when(ratingService.findByUserCompanyForWork(anyLong(), anyLong(), any(RatingPagination.class))).thenReturn(ratings);
		when(ratingPagination.getResults()).thenReturn(ratings);
	}

	@Test
	public void isWorkRatingEditableByUser_ratingNotExist_returnFalse() {
		assertFalse(ratingValidatingService.isWorkRatingEditableByUser(workId, buyerUserId));
	}

	@Test
	public void isWorkRatingEditableByUser_ratingExist_workDraft_returnFalse() {
		ratings.add(rating);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.DRAFT);
		assertFalse(ratingValidatingService.isWorkRatingEditableByUser(workId, buyerUserId));
	}

	@Test
	public void isWorkRatingEditableByUser_ratingExist_workSent_returnFalse() {
		ratings.add(rating);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.SENT);
		assertFalse(ratingValidatingService.isWorkRatingEditableByUser(workId, buyerUserId));
	}

	@Test
	public void isWorkRatingEditableByBuyer_ratingExist_workActive_returnTrue() {
		ratings.add(rating);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.ACTIVE);
		assertTrue(ratingValidatingService.isWorkRatingEditableByUser(workId, buyerUserId));
	}

	@Test
	public void isWorkRatingEditableByWorker_ratingExist_workActive_returnFalse() {
		ratings.add(rating);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.ACTIVE);
		assertFalse(ratingValidatingService.isWorkRatingEditableByUser(workId, workerUserId));
	}

	@Test
	public void isWorkRatingEditableByBuyer_ratingExist_workComplete_returnTrue() {
		ratings.add(rating);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.COMPLETE);
		assertTrue(ratingValidatingService.isWorkRatingEditableByUser(workId, buyerUserId));
	}

	@Test
	public void isWorkRatingEditableByWorker_ratingExist_workComplete_returnFalse() {
		ratings.add(rating);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.COMPLETE);
		assertFalse(ratingValidatingService.isWorkRatingEditableByUser(workId, workerUserId));
	}

	@Test
	public void isWorkRatingEditableByBuyer_ratingExist_workPaid_returnFalse() {
		ratings.add(rating);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.PAID);
		assertFalse(ratingValidatingService.isWorkRatingEditableByUser(workId, buyerUserId));
	}

	@Test
	public void isWorkRatingEditableByWorker_ratingExist_workPaid_returnFalse() {
		ratings.add(rating);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.PAID);
		assertFalse(ratingValidatingService.isWorkRatingEditableByUser(workId, workerUserId));
	}

	@Test
	public void isWorkRatingRatable_nullWork_returnFalse() {
		when(workService.findWork(anyLong())).thenReturn(null);
		assertFalse(ratingValidatingService.isWorkRatingRatableByUser(workId, buyerUserId));
	}

	@Test
		 public void isWorkRatingRatable_nullRater_returnFalse() {
		when(userService.getUser(anyLong())).thenReturn(null);
		assertFalse(ratingValidatingService.isWorkRatingRatableByUser(workId, buyerUserId));
	}

	@Test
	public void isWorkRatingRatable_nullWorkStatusType_returnFalse() {
		when(work.getWorkStatusType()).thenReturn(null);
		assertFalse(ratingValidatingService.isWorkRatingRatableByUser(workId, buyerUserId));
	}

	@Test
	public void isWorkRatingRatable_nullBuyer_returnFalse() {
		when(work.getBuyer()).thenReturn(null);
		assertFalse(ratingValidatingService.isWorkRatingRatableByUser(workId, workerUserId));
	}

	@Test
	public void isWorkRatingRatableByBuyer_workDraft_returnFalse() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.DRAFT);
		assertFalse(ratingValidatingService.isWorkRatingRatableByUser(workId, buyerUserId));
	}

	@Test
	public void isWorkRatingRatableByWorker_workDraft_returnFalse() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.DRAFT);
		when(workResource.getUser()).thenReturn(null);

		assertFalse(ratingValidatingService.isWorkRatingRatableByUser(workId, workerUserId));
	}

	@Test
	public void isWorkRatingRatableByBuyer_workSent_returnFalse() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.SENT);
		assertFalse(ratingValidatingService.isWorkRatingRatableByUser(workId, buyerUserId));
	}

	@Test
	public void isWorkRatingRatableByWorker_workSent_returnFalse() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.SENT);
		when(workResource.getUser()).thenReturn(null);
		assertFalse(ratingValidatingService.isWorkRatingRatableByUser(workId, workerUserId));
	}

	@Test
	public void isWorkRatingRatableByBuyer_workActive_returnTrue() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.ACTIVE);
		assertTrue(ratingValidatingService.isWorkRatingRatableByUser(workId, buyerUserId));
	}

	@Test
	public void isWorkRatingRatableByWorker_workActive_returnFalse() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.ACTIVE);
		assertFalse(ratingValidatingService.isWorkRatingRatableByUser(workId, workerUserId));
	}

	@Test
	public void isWorkRatingRatableByBuyer_workComplete_returnTrue() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.COMPLETE);
		assertTrue(ratingValidatingService.isWorkRatingRatableByUser(workId, buyerUserId));
	}

	@Test
	public void isWorkRatingRatableByWorker_workComplete_returnFalse() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.COMPLETE);
		assertFalse(ratingValidatingService.isWorkRatingRatableByUser(workId, workerUserId));
	}

	@Test
	public void isWorkRatingRatableByBuyer_workPaid_returnTrue() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.PAID);
		assertTrue(ratingValidatingService.isWorkRatingRatableByUser(workId, buyerUserId));
	}

	@Test
	public void isWorkRatingRatableByWorker_workPaid_returnTrue() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.PAID);
		assertTrue(ratingValidatingService.isWorkRatingRatableByUser(workId, workerUserId));
	}

}
