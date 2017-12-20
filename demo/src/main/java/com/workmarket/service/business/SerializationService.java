package com.workmarket.service.business;

import java.util.List;

public interface SerializationService<T> {
	String toJson(T object);
	String toJson(List<?> collection);
	T fromJson(String json);
	T mergeJson(T object, String json);
}
