package com.workmarket.service.business;

import com.workmarket.redis.RedisAdapter;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyLong;

@RunWith(MockitoJUnitRunner.class)
public class UserDashboardFilterServiceImplTest {
	private static final Long SOME_USER_ID = 99999L;
	private static final String SOME_KEY = "some key";

	@Mock RedisAdapter redisAdapter;
	@InjectMocks UserDashboardFilterServiceImpl service = spy(new UserDashboardFilterServiceImpl());

	private ExtendedUserDetails user;
	private Map<String, String> map;

	@Before
	public void setUp() throws Exception {
		map = new HashMap<String, String>() {{
			put("this", "this");
			put("that", "that");
		}};

		user = mock(ExtendedUserDetails.class);
		when(user.getId()).thenReturn(SOME_USER_ID);
		doReturn(SOME_KEY).when(service).getKey(SOME_USER_ID);
	}

	@Test
	public void get_WithAUser_CallsGetWithSomeKey() throws Exception {
		when(redisAdapter.getMap(SOME_KEY)).thenReturn(map);

		service.get(SOME_USER_ID);

		verify(redisAdapter).getMap(SOME_KEY);
	}

	@Test
	public void get_WithAUser_GetsAMap() throws Exception {
		when(redisAdapter.getMap(SOME_KEY)).thenReturn(map);
		Map<String, String> actual = service.get(SOME_USER_ID);

		assertEquals(map, actual);
	}

	@Test
	public void set_WithUserAndMap() throws Exception {
		service.set(SOME_USER_ID, map);
		verify(redisAdapter).set(eq(SOME_KEY), eq(map), anyLong());
	}
}
