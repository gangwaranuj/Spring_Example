package com.workmarket.utility;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(BlockJUnit4ClassRunner.class)
public class CollectionUtilitiesTest {

	@Test
	public void testHead() throws Exception {
		List<String> list = Lists.newArrayList(Arrays.asList(new String[]{"one", "two", "three", "four"}));

		List<String> head = CollectionUtilities.head(list, 1);
		Assert.assertEquals(1, head.size());
		Assert.assertEquals("one", head.get(0));

		head = CollectionUtilities.head(list, 2);
		assertEquals(2, head.size());
	}

	@Test
	public void testFilledList() {
		List<Integer> nullList = CollectionUtilities.newFilledList(null, 5);

		assertEquals(Collections2.filter(nullList, new Predicate<Integer>() {
			@Override public boolean apply(@Nullable Integer integer) {
				return integer == null;
			}
		}).size(), 5);
		assertEquals(Collections2.filter(nullList, new Predicate<Integer>() {
			@Override public boolean apply(@Nullable Integer integer) {
				return integer != null && integer.equals(7);
			}
		}).size(), 0);
	}
	
	@Test
	public void testC1ContainsAnyOfC2() {
		List<Integer> c1 = Lists.newArrayList(1,2,3,4,5,6,7,8,9,10);
		List<Integer> c2 = Lists.newArrayList(3, 4, 5, 11);
		List<Integer> c3 = Lists.newArrayList(12, 100, -1);

		assertTrue(CollectionUtilities.containsAny(c2, c1));
		assertFalse(CollectionUtilities.containsAny(c3, c1));
		assertFalse(CollectionUtilities.containsAny(c3, Collections.emptyList()));
		assertFalse(CollectionUtilities.containsAny(Collections.emptyList(), Collections.emptyList()));
	}

	@Test
	public void filterNull_withNullList_success() {

		assertTrue(CollectionUtilities.filterNull(null).isEmpty());
	}

	@Test
	public void filterNull_success() {
		List<BigDecimal> list = Lists.newArrayListWithCapacity(4);
		list.add(BigDecimal.valueOf(100));
		list.add(BigDecimal.valueOf(78));
		list.add(null);
		list.add(BigDecimal.valueOf(4));

		assertEquals(CollectionUtilities.filterNull(list).size(), 3);
	}

	@Test
	public void randomizeAndTruncate_NullList_EmptyList() {
		List<String> listOfStrings = null;
		int NUMBER_OF_ITEMS = 2;
		List<String> result;

		result = CollectionUtilities.randomizeAndTruncate(listOfStrings, NUMBER_OF_ITEMS);
		assertTrue(CollectionUtilities.isEmpty(result));
	}

	@Test
	public void randomizeAndTruncate_NegativeTruncateSize_EmptyList() {
		List<String> listOfStrings = Lists.newArrayList("Item1", "Item2");
		int NUMBER_OF_ITEMS = -1;
		List<String> result;

		result = CollectionUtilities.randomizeAndTruncate(listOfStrings, NUMBER_OF_ITEMS);
		assertTrue(CollectionUtilities.isEmpty(result));
	}

	@Test
	public void randomizeAndTruncate_OriginalListWithMoreElementsThanTruncateSize_ListOfTruncateSize() {
		List<String> listOfStrings = Lists.newArrayList("Item1", "Item2");
		int NUMBER_OF_ITEMS = 1;
		List<String> result;

		result = CollectionUtilities.randomizeAndTruncate(listOfStrings, NUMBER_OF_ITEMS);
		assertEquals(result.size(), NUMBER_OF_ITEMS);
	}

	@Test
	public void randomizeAndTruncate_OriginalListWithNotEnoughElements_AllElementsInOrignalList() {
		List<String> listOfStrings = Lists.newArrayList("Item1");
		int NUMBER_OF_ITEMS = 2;
		List<String> result;

		result = CollectionUtilities.randomizeAndTruncate(listOfStrings, NUMBER_OF_ITEMS);
		assertEquals(result.size(), listOfStrings.size());
	}
}
