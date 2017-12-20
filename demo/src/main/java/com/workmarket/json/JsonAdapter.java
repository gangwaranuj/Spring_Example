package com.workmarket.json;

import java.lang.reflect.Type;

public interface JsonAdapter {
	String toJson(Object object);
	String toJson(Object object, String... ignore);
	<E> E fromJson(String json, Class<E> clazz);
	<E> E fromJson(String json, Type typeOfT);
}