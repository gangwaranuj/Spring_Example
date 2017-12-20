package com.workmarket.redis;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedisAdapterImplTest {

	@Mock RedisTemplate redisTemplate;
	@InjectMocks RedisAdapterImpl redisAdapter = spy(new RedisAdapterImpl(new RedisTemplate()));

	private static final long EXPIRY = 100L;
	private static final String 
		KEY1 = "some key", KEY2 = "ferg";

	private Map<String, String> map;
	private List<String> list, itemsToRemove;
	private Set<String> itemsToAdd;
	private String itemOne, itemTwo;
	HashOperations opsForHash;
	ValueOperations opsForValue;
	ListOperations opsForList;
	SetOperations opsForSet;

	@Before
	public void setUp() throws Exception {
		map = new HashMap<String, String>() {{
			put("this", "this");
			put("that", "that");
		}};

		list = Lists.newArrayList("this", "that");

		itemOne = "1";
		itemTwo = "2";
		itemsToRemove = Lists.newArrayList(itemOne, itemTwo);
		itemsToAdd = Sets.newSet(itemOne, itemTwo);

		opsForValue = mock(ValueOperations.class);
		when(opsForValue.setIfAbsent(KEY1, "value")).thenReturn(true);
		when(redisTemplate.opsForValue()).thenReturn(opsForValue);

		opsForHash = mock(HashOperations.class);
			when(opsForHash.entries(any(String.class))).thenReturn(map);
			when(redisTemplate.opsForHash()).thenReturn(opsForHash);

		opsForList = mock(ListOperations.class);
			when(opsForList.range(any(String.class), anyInt(), anyInt())).thenReturn(list);
			when(redisTemplate.opsForList()).thenReturn(opsForList);

		opsForSet = mock(SetOperations.class);
			when(opsForSet.add(anyString(), anyString())).thenReturn(1L);
			when(opsForSet.remove(anyString(), anyString())).thenReturn(1L);
			when(opsForSet.members(anyString())).thenReturn(itemsToAdd);

			when(redisTemplate.opsForSet()).thenReturn(opsForSet);
	}

	@Test
	public void getMap_usesRedisTemplateHashOperations() {
		redisAdapter.getMap(KEY1);
		verify(redisTemplate).opsForHash();
	}

	@Test
	public void getList_usesRedisTemplateListOperations() {
		redisAdapter.getList(KEY1);
		verify(redisTemplate).opsForList();
	}

	@Test
	public void getMap_withAKey_getsEntries() {
		redisAdapter.getMap(KEY1);
		verify(opsForHash).entries(KEY1);
	}

	@Test
	public void getList_withAKey_getsEntries() {
		redisAdapter.getList(KEY1);
		verify(opsForList).range(KEY1, 0, -1);
	}

	@Test
	public void getMap_withAKey_returnsTheExpectedMap() {
		Map<String, String> actual = redisAdapter.getMap(KEY1);
		assertEquals(map, actual);
	}

	@Test
	public void getList_withAKey_returnsTheExpectedList() {
		List<String> actual = redisAdapter.getList(KEY1);
		assertEquals(list, actual);
	}

	@Test
	public void getMap_whenExceptionIsThrown_returnsANewEmptyMap() {
		when(opsForHash.entries(KEY1))
			.thenThrow(new DataAccessResourceFailureException("Some message"));

		Map<String, String> actual = redisAdapter.getMap(KEY1);

		assertNotSame(map, actual);
		assertThat(actual, equalTo(Collections.<String, String>emptyMap()));
	}

	@Test
	public void getList_whenExceptionIsThrown_returnsANewEmptyList() {
		when(opsForList.range(KEY1, 0, -1))
				.thenThrow(new DataAccessResourceFailureException("Some message"));

		List<String> actual = redisAdapter.getList(KEY1);

		assertThat(actual, equalTo(Collections.<String>emptyList()));
	}

	@Test
	public void getSet_whenExceptionIsThrown_returnsANewEmptySet() {
		when(opsForSet.members(KEY1))
			.thenThrow(new DataAccessResourceFailureException("Some message"));

		Set<String> actual = redisAdapter.getSet(KEY1);

		assertThat(actual, equalTo(Collections.<String>emptySet()));
	}

	@Test
	public void getSet_withKey_usesRedisTemplateSetOperations() {
		redisAdapter.getSet(KEY1);

		verify(opsForSet).members(KEY1);
	}

	@Test
	public void addSingleItemToSet_whenExceptionIsThrown_returnsZero() {
		when(opsForSet.add(KEY1, "1"))
			.thenThrow(new DataAccessResourceFailureException("Some message"));

		Long numberOfItemsAddedToSet = redisAdapter.addToSet(KEY1, "1");

		assertEquals(numberOfItemsAddedToSet, new Long(0L));
	}

	@Test
	public void addToSet_withKeyAndValue_usesRedisTemplateSetOperations() {
		redisAdapter.addToSet(KEY1, "1");

		verify(opsForSet).add(KEY1, "1");
	}

	@Test
	public void addToSet_withKeyAndOneValue_returnsOne() {
		Long numberOfValuesAdded = redisAdapter.addToSet(KEY1, "1");

		assertEquals(numberOfValuesAdded, new Long(1));
	}

	@Test
	public void addMultipleItemsToSet_whenExceptionIsThrown_returnsZero() {
		when(opsForSet.add(eq(KEY1), anyString()))
			.thenThrow(new DataAccessResourceFailureException("Some message"));

		Long numberOfItemsAddedToSet = redisAdapter.addToSet(KEY1, itemsToAdd);

		assertEquals(numberOfItemsAddedToSet, new Long(0L));
	}

	@Test
	public void addMultipleItemsToSet_withExpiry_callAddToSet() {
		redisAdapter.addToSet(KEY1, itemsToAdd, EXPIRY);

		verify(redisAdapter).addToSet(KEY1, itemsToAdd);
	}

	@Test
	public void addMultipleItemsToSet_withExpiry_setExpiry() {
		redisAdapter.addToSet(KEY1, itemsToAdd, EXPIRY);

		verify(redisTemplate).expire(KEY1, EXPIRY, TimeUnit.SECONDS);
	}

	@Test
	public void addMultipleItemsToSet_withExpiry_returnsTwo() {
		assertEquals(new Long(2), redisAdapter.addToSet(KEY1, itemsToAdd, EXPIRY));
	}

	@Test
	public void addToSet_withKeyAndValues_usesRedisTemplateSetOperations() {
		redisAdapter.addToSet(KEY1, itemsToAdd);

		verify(opsForSet).add(KEY1, itemOne);
		verify(opsForSet).add(KEY1, itemTwo);
	}

	@Test
	public void addToSet_withKeyAndTwoValues_returnsTwo() {
		Long numberOfValuesAdded = redisAdapter.addToSet(KEY1, itemsToAdd);

		assertEquals(numberOfValuesAdded, new Long(2));
	}

	@Test
	public void removeFromSet_whenExceptionIsThrown_returnsZero() {
		when(opsForSet.remove(eq(KEY1), anyString()))
			.thenThrow(new DataAccessResourceFailureException("Some message"));

		Long numberOfItemsAddedToSet = redisAdapter.removeFromSet(KEY1, itemsToRemove);

		assertEquals(numberOfItemsAddedToSet, new Long(0L));
	}

	@Test
	public void removeSet_withKeyAndValues_usesRedisTemplateSetOperations() {
		redisAdapter.removeFromSet(KEY1, itemsToRemove);

		verify(opsForSet).remove(KEY1, itemOne);
		verify(opsForSet).remove(KEY1, itemTwo);
	}

	@Test
	public void setAll_withKeyAndExpiry_setsExpiry() throws Exception {
		redisAdapter.set(KEY1, "some property", "some value", 1l);

		verify(redisTemplate).expireAt(eq(KEY1), any(Date.class));
	}

	@Test
	public void set_withAKeyAndMap_deletesTheExistingHash() {
		redisAdapter.set(KEY1, map);
		verify(redisTemplate).delete(KEY1);
	}

	@Test
	public void rightPopAndLeftPush_withAKeyAndKey2_rightPopAndLeftPushUsed() {
		redisAdapter.rightPopAndLeftPush(KEY1, "some item");
		verify(opsForList).rightPopAndLeftPush(eq(KEY1), eq("some item"));
	}

	@Test
	public void addToList_withAKeyAndList_usesRedisTemplateListOperations() {
		redisAdapter.addToList(KEY1, "some item");
		verify(redisTemplate).opsForList();
	}

	@Test
	public void set_withAKeyAndMap_usesRedisTemplateHashOperations() {
		redisAdapter.set(KEY1, map);
		verify(redisTemplate).opsForHash();
	}

	@Test
	public void set_withAKeyAndMap_putsAll() throws Exception {
		redisAdapter.set(KEY1, map);
		verify(opsForHash).putAll(KEY1, map);
	}

	@Test
	public void set_whenExceptionIsThrown() {
		doThrow(new DataAccessResourceFailureException("Some message"))
			.when(redisTemplate).delete(KEY1);

		redisAdapter.set(KEY1, map);

		verify(opsForHash, never()).putAll(KEY1, map);
	}

	@Test
	public void setIfAbsent_withKeyAndValue_useSetIfAbsentOp() {
		redisAdapter.setIfAbsent(KEY1, "value");
		verify(opsForValue).setIfAbsent(KEY1, "value");
	}

	@Test
	public void delete_withKey_useDeleteOp() {
		redisAdapter.delete(KEY1);
		verify(redisTemplate).delete(KEY1);
	}

	@Test
	public void addAllToList_usesLeftPush() {
		redisAdapter.addAllToList(KEY1, list);
		verify(opsForList).leftPush(KEY1, list.get(0));
	}

	@Test
	public void addAllToListWithExpiry_usesLeftPush() {
		redisAdapter.addAllToList(KEY1, list, EXPIRY);
		verify(opsForList).leftPush(KEY1, list.get(0));
	}

	@Test
	public void addAllToListWithExpiry_expirySet() {
		redisAdapter.addAllToList(KEY1, list, EXPIRY);
		verify(redisTemplate).expireAt(eq(KEY1), any(Date.class));
	}

	@Test
	public void increment_shouldIncrement() {
		redisAdapter.increment("key", 1);
		verify(opsForValue).increment("key", 1);
	}

	@Test
	public void increment_shouldIncrementKeyByAmountAndExpiryInTransaction() {
		redisAdapter.increment("key", 1, 2);
		verify(redisTemplate).execute(any(RedisCallback.class));
	}
}
