package com.workmarket.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.splitter.FeatureDomain;
import com.workmarket.splitter.WorkmarketComponent;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@WorkmarketComponent(FeatureDomain.UTILS)
public class ArrayUtilities {

	/**
	 * Convert array of Integers to array of Longs
	 * @param a
	 * @return
	 */
	public static Long[] convertToLongArrays(Integer[] a) {
		Assert.noNullElements(a);
		Long[] newA = new Long[a.length];
		for (int i = 0; i < a.length; i++)
			newA[i] = a[i].longValue();
		return newA;
	}

	/**
	 * Filter duplicates out of an array of strings, case insensitive (i.e. "c" and "C" are equal; the first one will be taken)
	 * @param a
	 * @return new array with dupes removed
	 */
	public static String[] unique(String[] a) {
		Assert.notNull(a);

		if (a.length == 0) {
			return new String[] {};
		}
		Set<String> set = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);

		Collections.addAll(set, a);

		return set.toArray(new String[set.size()]);
	}

	/**
	 * Sort a string array
	 * @param a
	 * @return
	 */
	public static String[] sort(String[] a) {
		Assert.notNull(a);

		if (a.length == 0)
			return new String[] {};

		List<String> list = Lists.newArrayList();

		Collections.addAll(list, a);

		Collections.sort(list);

		return list.toArray(new String[list.size()]);
	}

	/**
	 * Join string array into a single string with a separator
	 * @param strings
	 * @param separator
	 * @return
	 */
	public static String join(String[] strings, String separator) {
		Assert.notNull(strings);
		Assert.notNull(separator);
		if (strings.length == 0)
			return "";
		StringBuilder sb = new StringBuilder();

		sb.append(strings[0]);

		for (int i = 1; i < strings.length; i++)
			sb.append(separator).append(strings[i]);

		return sb.toString();
	}

}