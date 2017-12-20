package com.workmarket.service.business;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.User;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserDashboardFilterServiceImplIT extends BaseServiceIT {
	@Autowired RedisAdapter redis;
	@Autowired UserDashboardFilterService service;
	private User user;

	@Before
	public void setup() throws Exception {
		user = newContractor();
		redis.delete(RedisFilters.dashboardFilterKeyFor(user.getId()));
	}

	@Test
	public void get_WhenFilterMapDoesntExist_GetsAnEmptyMap() throws Exception {
		Map<String, String> blarg = service.get(user.getId());
		assertThat(blarg, equalTo(Collections.<String, String>emptyMap()));
	}

	@Test
	public void get_WhenFilterMapExists_GetsTheMap() throws Exception {
		Map<String, String> expected = Maps.newHashMap(ImmutableMap.of("this", "this", "that", "that"));
		service.set(user.getId(), expected);
		Map<String, String> actual = service.get(user.getId());
		assertThat(actual, equalTo(expected));
	}
}
