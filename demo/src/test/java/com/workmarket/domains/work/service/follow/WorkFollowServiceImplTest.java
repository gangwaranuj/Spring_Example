package com.workmarket.domains.work.service.follow;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.dao.follow.WorkFollowDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.dto.WorkFollowDTO;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkFollowServiceImplTest {

	@Mock WorkFollowDAO workFollowDAO;
	@Mock UserDAO userDAO;
	@Mock EventRouter eventRouter;
	@Mock WorkService workService;
	@Mock WorkFollowCache workFollowCache;
	@Mock BaseWorkDAO baseWorkDAO;
	@InjectMocks WorkFollowServiceImpl workFollowService = spy(new WorkFollowServiceImpl());

	private static Long
		WORK_ID = 1L,
		USER_ID = 2l,
		BUYER_ID = 3L,
		companyId = 4L,
		company2Id = 5L,
		WORK_FOLLOW_ID = 6L;
	private String WORK_NUMBER = "12345";
	private Work work;
	private User user, buyer;
	private WorkFollow workFollow;
	private WorkFollow newWorkFollow;
	private List<WorkFollowDTO> workFollowDTOs = Lists.newArrayList();
	private Company company, company2;
	private Optional<List<WorkFollowDTO>> workFollowDTOOptional;
	private ArgumentCaptor<List> listArgument;
	private List<Long> followerIds;

	@Before
	public void setup() {
		company = mock(Company.class);
		when(company.getId()).thenReturn(companyId);

		company2 = mock(Company.class);
		when(company2.getId()).thenReturn(company2Id);

		user = mock(User.class);
		when(user.getId()).thenReturn(USER_ID);
		when(user.getCompany()).thenReturn(company);

		buyer = mock(User.class);
		when(buyer.getId()).thenReturn(BUYER_ID);
		when(buyer.getCompany()).thenReturn(company);

		work = mock(Work.class);
		when(work.getBuyer()).thenReturn(buyer);
		when(work.getId()).thenReturn(WORK_ID);
		when(work.getCompany()).thenReturn(company);

		workFollow = mock(WorkFollow.class);

		workFollowDTOOptional = mock(Optional.class);
		when(workService.findWorkId(WORK_NUMBER)).thenReturn(WORK_ID);
		when(workFollowCache.get(WORK_ID)).thenReturn(workFollowDTOOptional);
		when(workFollowDTOOptional.isPresent()).thenReturn(true);

		when(workFollow.getId()).thenReturn(WORK_FOLLOW_ID);
		when(workFollow.getWork()).thenReturn(work);
		when(workFollow.getUser()).thenReturn(user);
		when(workFollowDAO.getWorkFollow(WORK_ID, user.getId())).thenReturn(Optional.fromNullable(workFollow));

		listArgument = ArgumentCaptor.forClass(List.class);

		followerIds = Lists.newArrayList(USER_ID);
		when(baseWorkDAO.findByWorkNumber(WORK_NUMBER)).thenReturn(work);
		when(baseWorkDAO.get(WORK_ID)).thenReturn(work);

		when(userDAO.findUserById(USER_ID)).thenReturn(user);
		when(userDAO.get(USER_ID)).thenReturn(user);
		when(userDAO.get(BUYER_ID)).thenReturn(buyer);

		newWorkFollow = mock(WorkFollow.class);
		doReturn(newWorkFollow).when(workFollowService).makeWorkFollow(work, user);
	}

	@Test
	public void toggleFollowWork_givenInvalidUser_toggleFail() {
		when(baseWorkDAO.get(WORK_ID)).thenReturn(null);
		when(userDAO.get(USER_ID)).thenReturn(null);

		assertFalse(workFollowService.toggleFollowWork(WORK_NUMBER, USER_ID));
	}

	@Test
	public void toggleFollowWork_givenNonCompanyUser_toggleFail() {
		when(user.getCompany()).thenReturn(company2);

		assertFalse(workFollowService.toggleFollowWork(WORK_NUMBER, USER_ID));
	}

	@Test
	public void toggleFollowWork_givenWorkOwner_toggleFail() {
		assertFalse(workFollowService.toggleFollowWork(WORK_NUMBER, BUYER_ID));
	}

	@Test
	public void toggleFollowWork_givenCompanyUser_toggleSuccess() {
		assertTrue(workFollowService.toggleFollowWork(WORK_NUMBER, user.getId()));
	}

	@Test
	public void isFollowingWork_callDAO() {
		when(workFollow.getDeleted()).thenReturn(false);

		workFollowService.isFollowingWork(WORK_ID, USER_ID);

		verify(workFollowDAO).isFollowingWork(WORK_ID, USER_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getWorkFollowers_emptyWorkNumber_throwException() {
		workFollowService.getWorkFollowDTOs("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void getWorkFollowers_blankWorkNumber_throwException() {
		workFollowService.getWorkFollowDTOs("  \n");
	}

	@Test(expected = IllegalArgumentException.class)
	public void getWorkFollowers_nullWorkNumber_throwException() {
		workFollowService.getWorkFollowDTOs((String) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getWorkFollowers_invalidWorkNumber_earlyReturn() {
		when(workService.findWorkId(WORK_NUMBER)).thenReturn(null);

		workFollowService.getWorkFollowDTOs((String) null);

		verify(workFollowCache, never()).get(WORK_ID);
	}

	@Test
	public void getWorkFollowers_withValidWorkNumber_cacheChecked() {
		workFollowService.getWorkFollowDTOs(WORK_NUMBER);

		verify(workFollowCache).get(WORK_ID);
	}

	@Test
	public void getWorkFollowers_cacheHit_returnCachedValue() {
		workFollowService.getWorkFollowDTOs(WORK_NUMBER);

		verify(workFollowDTOOptional).get();
	}

	@Test
	public void getWorkFollowers_cacheMiss_hitDB() {
		when(workFollowDTOOptional.isPresent()).thenReturn(false);

		workFollowService.getWorkFollowDTOs(WORK_NUMBER);

		verify(workFollowDAO).getWorkFollowDTOs(WORK_ID);
	}

	@Test
	public void getWorkFollowers_cacheMiss_convertToDTO_setCache() {
		when(workFollowDTOOptional.isPresent()).thenReturn(false);

		workFollowService.getWorkFollowDTOs(WORK_NUMBER);

		verify(workFollowCache).set(eq(WORK_ID), listArgument.capture());

		List<WorkFollowDTO> followDTOs = (List<WorkFollowDTO>) listArgument.getValue();
		assertEquals(followDTOs, workFollowDTOs);
	}

	@Test
	public void getWorkFollowers_cacheMiss_noFollowersInDB_returnEmptyCollection() {
		when(workFollowDTOOptional.isPresent()).thenReturn(false);
		when(workFollowDAO.getFollowers(WORK_NUMBER)).thenReturn(Collections.<WorkFollow>emptyList());

		assertEquals(0, workFollowService.getWorkFollowDTOs(WORK_NUMBER).size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveFollowers_nullWorkNumber_throwException() {
		workFollowService.saveFollowers((String)null, followerIds, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveFollowers_emptyWorkNumber_throwException() {
		workFollowService.saveFollowers("", followerIds, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveFollowers_blankWorkNumber_throwException() {
		workFollowService.saveFollowers("  \n", followerIds, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveFollowers_nullFollowers_throwException() {
		workFollowService.saveFollowers(WORK_NUMBER, null, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveFollowers_workDoesNotExist_throwException() {
		when(baseWorkDAO.findByWorkNumber(WORK_NUMBER)).thenReturn(null);

		workFollowService.saveFollowers(WORK_NUMBER, followerIds, false);
	}

	@Test
	public void saveFollowers_userDoesNotExist_earlyReturn() {
		when(userDAO.findUserById(USER_ID)).thenReturn(null);

		workFollowService.saveFollowers(WORK_NUMBER, followerIds, false);

		verify(workFollowDAO, never()).getWorkFollow(WORK_ID, USER_ID);
	}

	@Test
	public void saveFollowers_userIsNotInSameCompany_earlyReturn() {
		when(user.getCompany()).thenReturn(company2);

		workFollowService.saveFollowers(WORK_NUMBER, followerIds, false);

		verify(workFollowDAO, never()).getWorkFollow(WORK_ID, USER_ID);
	}

	@Test
	public void saveFollowers_existingFollowerRecord_updateRecord() {
		workFollowService.saveFollowers(WORK_NUMBER, followerIds, false);

		verify(workFollow).setDeleted(false);
		verify(workFollowDAO).saveOrUpdate(workFollow);
	}

	@Test
	public void saveFollowers_newFollowerRecord_createNewRecord() {
		when(workFollowDAO.getWorkFollow(WORK_ID, user.getId())).thenReturn(Optional.<WorkFollow>absent());

		workFollowService.saveFollowers(WORK_NUMBER, followerIds, false);

		verify(workFollowService).makeWorkFollow(work, user);
		verify(newWorkFollow).setDeleted(false);
		verify(workFollowDAO).saveOrUpdate(newWorkFollow);
	}

	@Test
	public void saveFollowers_evictFromCache() {
		workFollowService.saveFollowers(WORK_NUMBER, followerIds, false);

		verify(workFollowCache).evict(work.getId());
	}

	@Test
	public void saveFollowers_indexWork() {
		workFollowService.saveFollowers(WORK_NUMBER, followerIds, false);

		verify(eventRouter).sendEvent(eq(new WorkUpdateSearchIndexEvent(work.getId())));
	}

}
