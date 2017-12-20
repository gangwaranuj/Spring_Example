package com.workmarket.web.models;

public interface PaginatableHttpResponse<T,M> {
	void addRow(T data);
	void addRow(T data, M meta);
}
