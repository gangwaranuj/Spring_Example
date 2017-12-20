package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.uploads.models.CsvDTO;
import com.workmarket.service.exception.HostServiceException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CsvStorageService {
	CsvDTO save(MultipartFile file) throws IOException, HostServiceException;
	CsvDTO get(String uuid) throws HostServiceException;
	String download(String uuid) throws HostServiceException;
}
