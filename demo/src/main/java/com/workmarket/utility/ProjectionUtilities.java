package com.workmarket.utility;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static com.workmarket.utility.StringUtilities.split;
import static org.apache.commons.beanutils.PropertyUtils.getProperty;

public class ProjectionUtilities {

	private ProjectionUtilities() {}

	public static <T> Map[] projectAsArray(String[] projection, List<T> results) throws Exception {
		Assert.notNull(projection);
		Assert.noNullElements(projection);
		Assert.notNull(results);

		Map<String, String> emptyMap = Maps.newHashMap();
		return projectAsArray(projection, emptyMap, results);
	}

	public static <T> Map[] projectAsArray(
		String[] projection,
		Map<String, String> fieldNames,
		List<T> results
	) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Assert.notNull(projection);
		Assert.noNullElements(projection);
		Assert.notNull(results);

		List<Map> list = Lists.newArrayList();

		for (Object result : results) {
			Map<String, String> map = Maps.newHashMap();

			for (String column : projection) {

				String field = fieldNames.get(column);
				if (field == null) {
					field = column;
				}

				try {
					if (field.contains(".")) {
						addMappedNestedPropertyIfPresent(result, column, field, map);
					} else {
						addMappedPropertyIfPresent(result, column, field, map);
					}

				} catch (NestedNullException x) {

				}
			}

			list.add(map);
		}

		return list.toArray(new Map[results.size()]);
	}

	private static void addMappedPropertyIfPresent(Object o, String column, String field, Map<String, String> map)
		throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(o, field);
		if (descriptor != null) {
			map.put(column, ConverterUtilities.toString(getProperty(o, field)));
		}
	}

	private static void addMappedNestedPropertyIfPresent(Object parent, String column, String nestedPath, Map<String, String> map)
		throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

		String[] nestedLevels = split(nestedPath, ".");
		Object prop = null;
		for (String level : nestedLevels) {
			prop = getProperty(parent, level);
			if (prop == null) {
				break;
			} else {
				parent = prop;
			}
		}

		if (prop != null) {
			map.put(column, ConverterUtilities.toString(prop));
		}
	}
}
