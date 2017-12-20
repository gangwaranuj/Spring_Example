package com.workmarket.utility;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionUtilities {

	private static final Predicate<Object> NULL_PREDICATE =  new Predicate<Object>() {
		@Override
		public boolean apply(Object input) {
			return input != null;
		}
	};

	public static <T> Collection<T> nullSafeCollection(Collection<T> c) {
		if (c == null)
			return Lists.newLinkedList();
		return c;
	}

	public static <T> List<T> head(Collection<T> c, int length) {
		Assert.notNull(c);
		// Assert.isTrue(length > 0);

		Iterator<T> it = c.iterator();
		int i = 0;
		List<T> head = Lists.newArrayList();
		while (it.hasNext() && i++ < length)
			head.add(it.next());

		return head;
	}

	public static <K, T> Map<K, T> newEntityIdMap(List<T> list, String idProperty) {
		Assert.notNull(list);

		if (list.size() == 0)
			return Maps.newHashMap();

		Map<K, T> map = Maps.newHashMap();

		for (T entity : list) {
			K key;
			try {
				key = (K) PropertyUtils.getProperty(entity, idProperty);
			} catch (Exception e) {
				continue;
			}

			map.put(key, entity);
		}

		return map;
	}

	public static String join(Collection c, String property, String separator) {
		return join(newListPropertyProjection(c, property), separator);
	}

	public static String join(Collection c, String separator) {
		return StringUtils.join(c, separator);
	}

	public static <T> String joinHuman(Collection<T> c, String separator, String glueWord) {
		if (CollectionUtils.isEmpty(c)) return null;
		List l = Lists.newArrayList(c);
		if (l.size() == 1) return l.get(0).toString();
		return String.format("%s %s %s", join(l.subList(0, c.size() - 1), separator), glueWord, l.get(c.size() - 1));
	}

	public static <T> String joinHuman(Collection<T> c, String property, String separator, String glueWord) {
		return joinHuman(newListPropertyProjection(c, property), separator, glueWord);
	}

	public static <T> T findFirst(Collection<T> c, String property, Object value) {
		Assert.notNull(c);
		Assert.notNull(property);
		Assert.notNull(value);

		for (T o : c) {
			try {
				Object v = PropertyUtils.getProperty(o, property);
				if (value.equals(v))
					return o;
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String[] newArrayPropertyProjection(List list, String property) {
		Assert.notNull(list);
		Assert.notNull(property);

		String[] a = new String[list.size()];
		int i = 0;
		for (Object o : list) {
			try {
				Object v = PropertyUtils.getProperty(o, property);
				if (v != null)
					a[i++] = v.toString();
				else
					a[i++] = null;
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return a;
	}


	/**
	 * <pre>
	 * Creates a new property projection array of {@code Class<T> clazz} elements.
	 * <b>Note:</b> Make sure the property exists and that it can be casted to {@code Class<T> clazz}
	 * </pre>
	 *
	 * @param collection
	 *            A list of objects that declare a the property you wish to project
	 * @param clazz
	 *            The class of the property you wish to project
	 * @param property
	 *            The property you wish to project
	 * @return A {@code T[]} array containing the selected property projection
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] newGenericArrayPropertyProjection(Collection<?> collection, Class<T> clazz, String property) {
		Assert.notNull(collection);
		Assert.notNull(property);
		Assert.notNull(clazz);

		T[] a = (T[]) Array.newInstance(clazz, collection.size());
		int i = 0;
		for (Object o : collection) {
			try {
				a[i++] = (T) PropertyUtils.getProperty(o, property);
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return a;
	}

	public static List newListPropertyProjection(List list, String property) {
		Assert.notNull(property);

		if (CollectionUtils.isEmpty(list)) return Collections.emptyList();

		List a = new ArrayList(list.size());
		for (Object o : list) {
			try {
				Object v = PropertyUtils.getProperty(o, property);
				if (v != null)
					a.add(v);
				else
					a.add(null);
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return a;
	}

	public static List newListPropertyProjection(Collection list, String property) {
		Assert.notNull(property);

		if (CollectionUtils.isEmpty(list)) return Collections.emptyList();

		List a = new ArrayList(list.size());
		int i = 0;
		for (Object o : list) {
			try {
				Object v = PropertyUtils.getProperty(o, property);
				if (v != null)
					a.add(v);
				else
					a.add(null);
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return a;
	}

	/**
	 * Creates a list of "size" with every value set to "value"
	 * @param value
	 * @param size
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> newFilledList(T value, int size) {
		Assert.isTrue(size > 0);

		List<T> result = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			result.add(value);
		}
		return result;
	}

	public static Set newSetPropertyProjection(Collection list, String property) {
		Assert.notNull(property);

		if (CollectionUtils.isEmpty(list)) return Collections.emptySet();

		Set a = new HashSet(list.size());
		int i = 0;
		for (Object o : list) {
			try {
				Object v = PropertyUtils.getProperty(o, property);
				if (v != null)
					a.add(v);
				else
					a.add(null);
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return a;
	}

	public static Map<String, String> newStringMap(String... objects) {
		Assert.notNull(objects);

		Map<String, String> map = Maps.newLinkedHashMap();

		for (int i = 0; i < objects.length / 2; i++) {
			map.put(objects[2 * i], objects[2 * i + 1]);
		}

		return map;
	}

	public static <K, V> Map<K, V> newTypedObjectMap(Object... objects) {
		Assert.notNull(objects);

		Map<K, V> map = Maps.newLinkedHashMap();

		for (int i = 0; i < objects.length / 2; i++) {
			map.put((K) objects[2 * i], (V) objects[2 * i + 1]);
		}

		return map;
	}

	public static Map<String, Object> newObjectMap(Object... objects) {
		Assert.notNull(objects);

		Map<String, Object> map = Maps.newLinkedHashMap();

		for (int i = 0; i < objects.length / 2; i++) {
			map.put(objects[2 * i].toString(), objects[2 * i + 1]);
		}

		return map;
	}

	public static void addToObjectMap(Map<String, Object> map, Object... objects) {
		Assert.notNull(objects);

		for (int i = 0; i < objects.length / 2; i++) {
			map.put(objects[2 * i].toString(), objects[2 * i + 1]);
		}
	}

	public static <T> boolean contains(Collection<T> list, T o) {
		return CollectionUtils.isNotEmpty(list) && list.contains(o);
	}

	@SafeVarargs public static <T> Boolean contains(Collection<T> list, T... objects) {
		return containsAny(list, objects);
	}

	public static boolean containsObject(Collection<Object> list, Object o) {
		return CollectionUtils.isNotEmpty(list) && list.contains(o);
	}

	@SafeVarargs public static <T> Boolean containsAny(Collection<T> list, T... objects) {
		if (CollectionUtils.isEmpty(list))
			return false;
		for (T o : objects) {
			if (list.contains(o))
				return true;
		}
		return false;
	}

	@SafeVarargs public static <T> Boolean containsAny(T needle, T... haystack) {
		return containsAny(Lists.newArrayList(haystack), needle);
	}

	public static <T> Boolean containsAny(Collection<T> col1, Collection<T> col2) {
		if(CollectionUtils.isEmpty(col1) || CollectionUtils.isEmpty(col2)) return false;
		for(T o : col1) {
			if(col2.contains(o)) return true;
		}
		return false;
	}

	@SafeVarargs public static <T> Boolean containsAll(Collection<T> list, T... objects) {
		if (CollectionUtils.isEmpty(list)) {
			return false;
		}
		for (T o : objects) {
			if (!list.contains(o)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isEmpty(@SuppressWarnings("rawtypes") Collection coll) {
		return CollectionUtils.isEmpty(coll);
	}

	public static int collectionSize(@SuppressWarnings("rawtypes") Collection coll) {
		return CollectionUtils.isEmpty(coll) ? 0 : coll.size();
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> extractKeyValues(@SuppressWarnings("rawtypes") Collection list, String key, String value) {
		if (CollectionUtils.isEmpty(list)) return Collections.emptyMap();

		Map<K, V> a = Maps.newLinkedHashMap();
		for (Object o : list) {
			K ko;
			try {
				ko = (K) PropertyUtils.getProperty(o, key);
			} catch (Exception e) {
				continue;
			}

			V vo;
			try {
				vo = (V) PropertyUtils.getProperty(o, value);
			} catch (Exception e) {
				vo = null;
			}

			a.put(ko, vo);
		}
		return a;
	}

	public static List<Map<String,Object>> extractKeyValues(@SuppressWarnings("rawtypes") Collection list, String keyName, String keyProperty, String valueName, String valueProperty) {
		if (CollectionUtils.isEmpty(list)) return Collections.emptyList();

		List<Map<String,Object>> rows = Lists.newArrayList();
		for (Object o : list) {
			Map<String,Object> a = Maps.newLinkedHashMap();
			try {
				a.put(keyName, PropertyUtils.getProperty(o, keyProperty));
			} catch (Exception e) {
				continue;
			}

			try {
				a.put(valueName, PropertyUtils.getProperty(o, valueProperty));
			} catch (Exception e) {
				a.put(valueName, null);
			}

			rows.add(a);
		}
		return rows;
	}

	public static <T> List<Map<String,Object>> extractPropertiesList(Collection<T> list, String... keys) {
		if (CollectionUtils.isEmpty(list)) return Collections.emptyList();

		List<Map<String, Object>> response = Lists.newArrayList();
		for (T o : list) {
			Map<String, Object> row = Maps.newHashMap();
			for (String key : keys) {
				try {
					row.put(key, PropertyUtils.getProperty(o, key));
				} catch (Exception e) {
					row.put(key, null);
				}
			}
			response.add(row);
		}
		return response;
	}

	/**
	 * This converts a request.getParameterMap() to something easier to deal with. Avoids having to cast or type check.
	 *
	 * @param paramMap
	 * @return
	 */
	public static Map<String, List<String>> getTypedParameterMap(Map paramMap) {
		Map<String, List<String>> result = new HashMap<>();
		Set keys = paramMap.keySet();
		for (Object key : keys) {
			if (key instanceof String && paramMap.get(key) instanceof String[])
				result.put((String) key, Arrays.asList((String[]) paramMap.get(key)));
		}
		return result;
	}

	/**
	 * Same as above but converts single-element arrays to Strings, thus requiring casting to access elements.
	 *
	 * @param paramMap
	 * @return
	 */
	public static Map<String, Object> getAndFlattenTypedParameterMap(Map paramMap) {
		Map<String, Object> result = new HashMap<>();
		Set keys = paramMap.keySet();
		for (Object key : keys) {
			Object vals = paramMap.get(key);
			if (key instanceof String && vals instanceof String[]) {
				String[] strVals = ((String[]) vals);
				switch (strVals.length) {
					case 0:
						result.put((String) key, "");
						break;
					case 1:
						result.put((String) key, strVals[0]);
						break;
					default:
						result.put((String) key, Arrays.asList(strVals));
				}
			}
		}
		return result;
	}

	@SafeVarargs public static <T> T[] newArray(T... items) {
		return items;
	}

	public static <T> T last(Collection<T> list) {
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return (T)CollectionUtils.get(list, list.size() - 1);
	}

	public static <T> T first(Collection<T> list) {
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return (T)CollectionUtils.get(list, 0);
	}

	/**
	 * Sort a map by the values.
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValues(final Map<K, V> mapToSort) {
		Assert.notNull(mapToSort);

		List<Map.Entry<K, V>> entries = new ArrayList<>(mapToSort.size());
		entries.addAll(mapToSort.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(
					final Map.Entry<K, V> entry1,
					final Map.Entry<K, V> entry2) {
				return entry1.getValue().compareTo(entry2.getValue());
			}
		});

		Map<K, V> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : entries)
			sortedMap.put(entry.getKey(), entry.getValue());

		return sortedMap;
	}

	public static<T> List<T> filterNull(List<T> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			return Lists.newArrayList(Iterables.filter(list, NULL_PREDICATE));
		}
		return Collections.EMPTY_LIST;
	}

	 public static <T> List<T> randomizeAndTruncate(List<T> list, int numberOfItems) {
		if (isEmpty(list) || numberOfItems <= 0) {
			return Lists.newArrayList();
		}

		List<T> result = Lists.newArrayList(list);
		Collections.shuffle(result);
		int endIndex = Math.min(numberOfItems, result.size());
		return result.subList(0, endIndex);
	}

	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		if (c == null) {
			return Collections.EMPTY_LIST;
		}
		List<T> list = Lists.newArrayList(c);
		Collections.sort(list);
		return list;
	}
}
