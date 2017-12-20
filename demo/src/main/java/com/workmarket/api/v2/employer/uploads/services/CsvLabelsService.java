package com.workmarket.api.v2.employer.uploads.services;

public interface CsvLabelsService {
	void label(String uuid);
	void label(String uuid, long index, Long labelId);
}
