package com.workmarket.api.v2.employer.assignments.services;

public interface UseCase<T, K> {
	T execute() throws Exception;
	K andReturn() throws Exception;
}
