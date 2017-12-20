package com.workmarket.service.business.event.work;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WorkUpdateSearchIndexEventTest {
	WorkUpdateSearchIndexEvent event1;
	WorkUpdateSearchIndexEvent event2;

	@Before
	public void setup() {
		event1 = new WorkUpdateSearchIndexEvent();
		event2 = new WorkUpdateSearchIndexEvent();
	}

	@Test
	public void shouldEqualsTrueIfNullWorkIds() {
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeTrueIfNullWorkIds() {
		assertTrue(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkIdsEmptyAndNull() {
		event1.setWorkIds(new ArrayList<Long>());
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkIdsEmptyAndNull() {
		event1.setWorkIds(new ArrayList<Long>());
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsTrueIfWorkIdsEmptyAndEmpty() {
		event1.setWorkIds(new ArrayList<Long>());
		event2.setWorkIds(new ArrayList<Long>());
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeTrueIfWorkIdsEmptyAndEmpty() {
		event1.setWorkIds(new ArrayList<Long>());
		event2.setWorkIds(new ArrayList<Long>());
		assertTrue(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkIdsWithSingleValueAndNull() {
		event1.setWorkIds(Lists.newArrayList(1L));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkIdsWithSingleValueAndNull() {
		event1.setWorkIds(Lists.newArrayList(1L));
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkIdsWithSingleValueAndEmpty() {
		event1.setWorkIds(Lists.newArrayList(1L));
		event2.setWorkIds(new ArrayList<Long>());
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkIdsWithSingleValueAndEmpty() {
		event1.setWorkIds(Lists.newArrayList(1L));
		event2.setWorkIds(new ArrayList<Long>());
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsTrueIfWorkIdsWithEqualSingleValue() {
		event1.setWorkIds(Lists.newArrayList(1L));
		event2.setWorkIds(Lists.newArrayList(1L));
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeTrueIfWorkIdsWithEqualSingleValue() {
		event1.setWorkIds(Lists.newArrayList(1L));
		event2.setWorkIds(Lists.newArrayList(1L));
		assertTrue(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkIdsWithDifferentSingleValue() {
		event1.setWorkIds(Lists.newArrayList(1L));
		event2.setWorkIds(Lists.newArrayList(2L));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkIdsWithDifferentSingleValue() {
		event1.setWorkIds(Lists.newArrayList(1L));
		event2.setWorkIds(Lists.newArrayList(2L));
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkIdsWithTwoValuesAndNull() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkIdsWithTwoValuesAndNull() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkIdsWithTwoValuesAndEmpty() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		event2.setWorkIds(new ArrayList<Long>());
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkIdsWithTwoValuesAndEmpty() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		event2.setWorkIds(new ArrayList<Long>());
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkIdsWithTwoValuesAndSingleValue() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		event2.setWorkIds(Lists.newArrayList(1L));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkIdsWithTwoValuesAndSingleValue() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		event2.setWorkIds(Lists.newArrayList(1L));
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsTrueIfWorkIdsWithSameTwoValuesInOrder() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		event2.setWorkIds(Lists.newArrayList(1L, 2L));
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeTrueIfWorkIdsWithSameTwoValuesInOrder() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		event2.setWorkIds(Lists.newArrayList(1L, 2L));
		assertTrue(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsTrueIfWorkIdsWithSameTwoValuesOutOfOrder() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		event2.setWorkIds(Lists.newArrayList(2L, 1L));
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeTrueIfWorkIdsWithSameTwoValuesOutOfOrder() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		event2.setWorkIds(Lists.newArrayList(2L, 1L));
		assertTrue(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkIdsWithDifferentTwoValues() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		event2.setWorkIds(Lists.newArrayList(2L, 3L));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkIdsWithDifferentTwoValues() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		event2.setWorkIds(Lists.newArrayList(2L, 3L));
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsTrueIfWorkIdsSize2AndSize3() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		event2.setWorkIds(Lists.newArrayList(1L, 2L, 3L));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkIdsSize2AndSize3() {
		event1.setWorkIds(Lists.newArrayList(1L, 2L));
		event2.setWorkIds(Lists.newArrayList(1L, 2L, 3L));
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkNumbersEmptyAndNull() {
		event1.setWorkNumbers(new ArrayList<String>());
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkNumbersEmptyAndNull() {
		event1.setWorkNumbers(new ArrayList<String>());
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsTrueIfWorkNumbersEmptyAndEmpty() {
		event1.setWorkNumbers(new ArrayList<String>());
		event2.setWorkNumbers(new ArrayList<String>());
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeTrueIfWorkNumbersEmptyAndEmpty() {
		event1.setWorkNumbers(new ArrayList<String>());
		event2.setWorkNumbers(new ArrayList<String>());
		assertTrue(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkNumbersWithSingleValueAndNull() {
		event1.setWorkNumbers(Lists.newArrayList("1"));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHascodeFalseIfWorkNumbersWithSingleValueAndNull() {
		event1.setWorkNumbers(Lists.newArrayList("1"));
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkNumbersWithSingleValueAndEmpty() {
		event1.setWorkNumbers(Lists.newArrayList("1"));
		event2.setWorkNumbers(new ArrayList<String>());
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkNumbersWithSingleValueAndEmpty() {
		event1.setWorkNumbers(Lists.newArrayList("1"));
		event2.setWorkNumbers(new ArrayList<String>());
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsTrueIfWorkNumbersWithEqualSingleValue() {
		event1.setWorkNumbers(Lists.newArrayList("1"));
		event2.setWorkNumbers(Lists.newArrayList("1"));
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeTrueIfWorkNumbersWithEqualSingleValue() {
		event1.setWorkNumbers(Lists.newArrayList("1"));
		event2.setWorkNumbers(Lists.newArrayList("1"));
		assertTrue(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkNumbersWithDifferentSingleValue() {
		event1.setWorkNumbers(Lists.newArrayList("1"));
		event2.setWorkNumbers(Lists.newArrayList("2"));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkNumbersWithDifferentSingleValue() {
		event1.setWorkNumbers(Lists.newArrayList("1"));
		event2.setWorkNumbers(Lists.newArrayList("2"));
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkNumbersWithTwoValuesAndNull() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkNumbersWithTwoValuesAndNull() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkNumbersWithTwoValuesAndEmpty() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		event2.setWorkNumbers(new ArrayList<String>());
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkNumbersWithTwoValuesAndEmpty() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		event2.setWorkNumbers(new ArrayList<String>());
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkNumbersWithTwoValuesAndSingleValue() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		event2.setWorkNumbers(Lists.newArrayList("1"));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkNumbersWithTwoValuesAndSingleValue() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		event2.setWorkNumbers(Lists.newArrayList("1"));
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsTrueIfWorkNumbersWithSameTwoValuesInOrder() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		event2.setWorkNumbers(Lists.newArrayList("1", "2"));
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeTrueIfWorkNumbersWithSameTwoValuesInOrder() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		event2.setWorkNumbers(Lists.newArrayList("1", "2"));
		assertTrue(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsTrueIfWorkNumbersWithSameTwoValuesOutOfOrder() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		event2.setWorkNumbers(Lists.newArrayList("2", "1"));
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeTrueIfWorkNumbersWithSameTwoValuesOutOfOrder() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		event2.setWorkNumbers(Lists.newArrayList("2", "1"));
		assertTrue(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkNumbersWithDifferentTwoValues() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		event2.setWorkNumbers(Lists.newArrayList("2", "3"));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkNumbersWithDifferentTwoValues() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		event2.setWorkNumbers(Lists.newArrayList("2", "3"));
		assertFalse(event1.hashCode() == event2.hashCode());
	}

	@Test
	public void shouldEqualsFalseIfWorkNumbersSize2AndSize3() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		event1.setWorkNumbers(Lists.newArrayList("1", "2", "3"));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
	}

	@Test
	public void shouldEqualsHashCodeFalseIfWorkNumbersSize2AndSize3() {
		event1.setWorkNumbers(Lists.newArrayList("1", "2"));
		event1.setWorkNumbers(Lists.newArrayList("1", "2", "3"));
		assertFalse(event1.hashCode() == event2.hashCode());
	}
}