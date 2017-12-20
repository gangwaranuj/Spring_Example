package com.workmarket.utility;

import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

public class BeanUtilities {

	private static final Log logger = LogFactory.getLog(BeanUtilities.class);

	static {
		ConvertUtils.register(new Converter() {

			@Override
			public Object convert(Class type, Object value) {
				try {
					if (value instanceof String)
						return Double.valueOf((String) value);
					if (value instanceof BigDecimal)
						return ((BigDecimal) value).doubleValue();
					return value;
				} catch (NumberFormatException x) {
					return null;
				}
			}
		}, Double.class);

		ConvertUtils.register(new Converter() {

			@Override
			public Object convert(Class type, Object value) {
				try {
					if (value instanceof Boolean)
						return value;

					if (value instanceof Integer) {
						Integer i = (Integer) value;

						if (i == 1)
							return Boolean.TRUE;

						if (i == 0)
							return Boolean.FALSE;
					}

					if (value instanceof String) {
						String s = (String) value;
						if ("1".equals(s)) {
							return Boolean.TRUE;
						}
						if ("0".equals(s)) {
							return Boolean.FALSE;
						}
						if ("".equals(s)) {
							return Boolean.FALSE;
						}
						return Boolean.valueOf(s);
					}
					return value;
				} catch (NumberFormatException x) {
					return null;
				}
			}
		}, Boolean.class);

		ConvertUtils.register(new Converter() {
			@Override
			public Object convert(Class type, Object value) {
				try {
					if (value == null)
						return null;
					if (value instanceof String)
						return value;
					if (value instanceof Calendar)
						return DateUtilities.getISO8601((Calendar) value);
					return value.toString();
				} catch (Exception e) {
					return null;
				}
			}
		}, String.class);

		ConvertUtils.register(new Converter() {

			@Override
			public Object convert(Class type, Object value) {
				try {
					if (value == null)
						return null;
					if (value instanceof String)
						return new BigDecimal((String) value);
					if (value instanceof BigDecimal)
						return ((BigDecimal) value).add(BigDecimal.valueOf(0));
					if (value instanceof Double)
						return BigDecimal.valueOf((Double) value);
					return null;
				} catch (NumberFormatException x) {
					return null;
				}
			}
		}, BigDecimal.class);

		ConvertUtils.register(new Converter() {

			@Override
			public Object convert(Class type, Object value) {
				if (value instanceof String) {
					try {
						return DateUtilities.parseCalendar((String) value);
					} catch (Exception x) {
						return null;
					}
				}
				return value;
			}
		}, Calendar.class);

		ConvertUtils.register(new Converter() {

			@Override
			public Object convert(Class type, Object value) {
				if (value instanceof BigDecimal)
					return ((BigDecimal) value).intValue();
				if (value instanceof String)
					return Integer.valueOf((String) value);
				return value;
			}
		}, Integer.class);
	}

	public static void copyProperties(Object dest, Object orig) {
		if (dest == null || orig == null) {
			return;
		}
		ConvertUtils.register(new LongConverter(null), Long.class);

		try {
			BeanUtils.copyProperties(dest, orig);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error(e);
		}
	}

	/**
	 * This is the same as copyProperties but sets any zero values in orig to zero in dest (instead of null)
	 *
	 * @param dest
	 * @param orig
	 */
	public static void copyPropertiesZeroDefault(Object dest, Object orig) {
		if (dest == null || orig == null) {
			return;
		}
		ConvertUtils.register(new IntegerConverter(0), Integer.class);
		ConvertUtils.register(new LongConverter(0), Long.class);
		ConvertUtils.register(new DoubleConverter(0), Double.class);
		ConvertUtils.register(new BigDecimalConverter(BigDecimal.ZERO), BigDecimal.class);
		try {
			BeanUtils.copyProperties(dest, orig);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error(e);
		}
	}

	/**
	 * TODO seems there's an issue copying boolean properties with this method. investigate.
	 */
	public static void copyProperties(Object dest, Object orig, String[] ignore) {
		ConvertUtils.register(new LongConverter(null), Long.class);
		// this is interesting - spring reverses apache
		org.springframework.beans.BeanUtils.copyProperties(orig, dest, ignore);
	}

	public static <T, K> T newBean(Class<? extends T> clazz, K DTO) {
		try {
			T o = clazz.newInstance();
			BeanUtilities.copyProperties(o, DTO);
			return o;
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e);
		}

		return null;
	}

	public static Long getId(Object o) {
		try {
			try {
				if (o == null)
					return null;
				return Long.valueOf(BeanUtils.getProperty(o, "id"));
			} catch (NumberFormatException e) {
				return null;
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.error(e);
		}

		return null;
	}

	public static Map<String, Object> getPropertyValueMap(Object o) {
		PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(o.getClass());
		Map<String, Object> map = Maps.newHashMap();
		for (PropertyDescriptor descriptor : propertyDescriptors) {
			try {
				if (descriptor.getWriteMethod() != null) {
					Object v = PropertyUtils.getProperty(o, descriptor.getName());
					if (Hibernate.isInitialized(v)) {
						map.put(descriptor.getName(), v);
					} else {
						map.put(descriptor.getName(), null);
					}
				}
			} catch (LazyInitializationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				logger.error(e);
			}
		}

		return map;
	}

	public static Map<String, String> newCollectionPropertyToMap(Collection c, String keyProperty, String valueProperty)
			throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		Assert.notNull(c);
		Map<String, String> map = Maps.newHashMap();

		for (Object o : c) {
			map.put(PropertyUtils.getProperty(o, keyProperty).toString(),
					PropertyUtils.getProperty(o, valueProperty).toString());
		}

		return map;
	}

	public static <T> T updateProperties(T t, Map<String, String> properties) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		for (String property : properties.keySet()) {
			PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(t, property);
			Assert.notNull(descriptor, "Unable to find " + property + " property");

			Object value = ConvertUtils.convert(StringUtils.isBlank(properties.get(property)) ? null : properties.get(property), descriptor.getPropertyType());
			PropertyUtils.setProperty(t, property, value);
		}

		return t;
	}

	public static boolean hasAnyNullProperties(Object o) throws IllegalAccessException {
		for (Field f : o.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			if (f.get(o) == null) {
				return true;
			}
		}

		return false;
	}
}
