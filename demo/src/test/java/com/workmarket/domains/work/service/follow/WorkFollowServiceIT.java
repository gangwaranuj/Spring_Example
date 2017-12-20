package com.workmarket.domains.work.service.follow;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.WorkFollowDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkFollowServiceIT extends BaseServiceIT {

	@Autowired WorkFollowService workFollowService;
	@Autowired RedisAdapter redisAdapter;

	@Test
	public void getWorkFollowDTOs_workDoesNotExist_returnEmptyList() {
		assertEquals(0, workFollowService.getWorkFollowDTOs(0L).size());
	}

	@Test
	public void getWorkFollowDTOs_recordExists_fetchDTO() throws Exception {
		User user = newFirstEmployee();
		Work work = newWork(user.getId());
		workFollowService.saveOrUpdateWorkFollow(new WorkFollow(work, user));
		WorkFollow workFollow = workFollowService.getWorkFollowers(work.getId()).get(0);

		List<WorkFollowDTO> followers = workFollowService.getWorkFollowDTOs(work.getId());
		WorkFollowDTO workFollowDTO = followers.get(0);

		assertEquals(workFollowDTO.getId(), workFollow.getId());
		assertEquals(workFollowDTO.getUserId(), workFollow.getUser().getId());
		assertEquals(workFollowDTO.getFollowerFirstName(), user.getFirstName());
		assertEquals(workFollowDTO.getFollowerLastName(), user.getLastName());
		assertEquals(workFollowDTO.getFollowerFullName(), user.getFullName());
	}

	@Test
	public void getWorkFollowers_testCacheEviction_removeWorkFollower() throws Exception {
		User user = newFirstEmployee();
		Work work = newWork(user.getId());
		workFollowService.saveOrUpdateWorkFollow(new WorkFollow(work, user));

		WorkFollowDTO workFollowDTO = workFollowService.getWorkFollowDTOs(work.getWorkNumber()).get(0);
		assertTrue(redisAdapter.get(RedisFilters.followersKey(work.getId())).isPresent());

		workFollowService.removeWorkFollower(work.getWorkNumber(), workFollowDTO.getId());
		assertFalse(redisAdapter.get(RedisFilters.followersKey(work.getId())).isPresent());
	}

	@Test
	public void getWorkFollowers_testCacheEviction_saveFollowers() throws Exception {
		User user = newFirstEmployee();
		Work work = newWork(user.getId());
		workFollowService.saveOrUpdateWorkFollow(new WorkFollow(work, user));

		WorkFollowDTO workFollowDTO = workFollowService.getWorkFollowDTOs(work.getWorkNumber()).get(0);
		assertTrue(redisAdapter.get(RedisFilters.followersKey(work.getId())).isPresent());

		workFollowService.saveFollowers(work.getId(), Lists.newArrayList(workFollowDTO.getUserId()), false);
		assertFalse(redisAdapter.get(RedisFilters.followersKey(work.getId())).isPresent());
	}

	@Test
	public void isFollowingWork_recordDoesNotExist_returnFalse() {
		assertFalse(workFollowService.isFollowingWork(0L, 0L));
	}

	@Test
	public void isFollowingWork_recordExists_returnFalse() throws Exception {
		User user = newFirstEmployee();
		Work work = newWork(user.getId());
		workFollowService.saveOrUpdateWorkFollow(new WorkFollow(work, user));

		assertTrue(workFollowService.isFollowingWork(work.getId(), user.getId()));
	}
}
