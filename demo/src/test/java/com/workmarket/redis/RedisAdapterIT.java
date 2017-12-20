package com.workmarket.redis;

import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class RedisAdapterIT extends BaseServiceIT {

	@Autowired RedisAdapter redisAdapter;

	@Before
	public void setup() throws Exception {
		redisAdapter.delete("some key");
	}

	@Test
	public void getList_withKey() {
		redisAdapter.addToList("some key", "this");
		redisAdapter.addToList("some key", "that");
		assertEquals(redisAdapter.getList("some key").size(), 2);
	}

	@Test
	public void getList_withWrongKey() {
		redisAdapter.addToList("some key", "this");
		assertEquals(redisAdapter.getList("wrong key").size(), 0);
	}

	@Test
	public void addAllToList_addsAllElements() {
		List<String> list = new ArrayList<>();
		list.add("this");
		list.add("that");
		list.add("and the other thing");
		redisAdapter.addAllToList("some key", list);
		assertEquals(redisAdapter.getList("some key").size(), 3);
	}

	@Test
	public void addAllToListWithExpiry_addsAllElements() {
		List<String> list = new ArrayList<>();
		list.add("this");
		list.add("that");
		list.add("and the other thing");
		redisAdapter.addAllToList("some key", list, 10l);
		assertEquals(redisAdapter.getList("some key").size(), 3);
	}
}
