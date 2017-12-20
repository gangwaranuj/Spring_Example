package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.service.exception.HostServiceException;

import java.io.IOException;

public interface CsvErrorsService {
	String generate(String uuid, boolean includeValid) throws IOException, HostServiceException;
}
