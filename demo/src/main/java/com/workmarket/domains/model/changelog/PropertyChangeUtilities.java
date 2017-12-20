package com.workmarket.domains.model.changelog;

import com.workmarket.data.annotation.TrackChanges;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;

public class PropertyChangeUtilities {
	public static <T extends Enum<T> & PropertyChangeType> T getPropertyChangeType(Object bean, String propertyName, Class<T> klazz) throws Exception {
		PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(bean, propertyName);

		if (propertyDescriptor == null)
			return null;
		if (propertyDescriptor.getWriteMethod() == null)
			return null;
		if (!propertyDescriptor.getWriteMethod().isAnnotationPresent(TrackChanges.class))
			return null;

		TrackChanges annotation = propertyDescriptor.getWriteMethod().getAnnotation(TrackChanges.class);
		return Enum.valueOf(klazz, annotation.type());
	}
}
