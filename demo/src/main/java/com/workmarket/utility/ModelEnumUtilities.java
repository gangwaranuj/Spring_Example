package com.workmarket.utility;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 2/24/12
 * Time: 11:31 AM
 */
public class ModelEnumUtilities {

	private static final Log logger = LogFactory.getLog(ModelEnumUtilities.class);

	// these names correspond to the prefix used in the properties file
	public static final Map<String, Object> workStatusTypes = Maps.newHashMap();
	public static final Map<String, Object> workSubStatusTypes = Maps.newHashMap();
	public static final Map<String, Object> workResourceStatusTypes = Maps.newHashMap();
	public static final Map<String, Object> resourceNoteTypes = Maps.newHashMap();
	public static final Map<String, Object> resourceNoteActionTypes = Maps.newHashMap();
	public static final Map<String, Object> declineWorkActionTypes = Maps.newHashMap();
	public static final Map<String, Object> pricingStrategyTypes = Maps.newHashMap();
	public static final Map<String, Object> workRequestInfo = Maps.newHashMap();
	public static final Map<String, Object> laneTypes = Maps.newHashMap();
	public static final Map<String, Object> assessmentTypes = Maps.newHashMap();
	public static final Map<String, Object> assessmentNotificationTypes = Maps.newHashMap();
	public static final Map<String, Object> assessmentItemTypes = Maps.newHashMap();
	public static final Map<String, Object> approvalStatusTypes = Maps.newHashMap();

	static {
		try {
			Properties props = PropertiesLoaderUtils.loadAllProperties("model.enum.properties");

			for (Field field : ModelEnumUtilities.class.getDeclaredFields()) {
				String keyPrefix = field.getName() + ".";

				if (field.getType().equals(Map.class)) {
					Method put = field.getType().getDeclaredMethod("put", Object.class, Object.class);

					for (String prop : props.stringPropertyNames()) {
						String key = StringUtils.substringAfter(prop, keyPrefix);
						if (!"".equals(key))
							put.invoke(field.get(null), key, props.getProperty(prop));
					}
				}
			}
		} catch (IOException e) {
			logger.error("Failed to load enum values", e);
		} catch (InvocationTargetException e) {
			logger.error("Failed to load enum values", e);
		} catch (NoSuchMethodException e) {
			logger.error("Failed to load enum values", e);
		} catch (IllegalAccessException e) {
			logger.error("Failed to load enum values", e);
		}
	}

	public static void main(String[] args) {

	}
}
