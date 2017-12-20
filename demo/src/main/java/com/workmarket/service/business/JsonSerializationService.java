package com.workmarket.service.business;

import java.lang.reflect.Type;

public interface JsonSerializationService {
	String toJson(Object object, String... ignore);

	String toJson(Object object);

	/**
	 *
	 * @param object
	 * @return Json without converting fields to underscore naming convention
	 */
	String toJsonIdentity(Object object);
	<E> E fromJson(String json, Class<E> clazz);
	<E> E fromJson(String json, Type typeOfT);
}