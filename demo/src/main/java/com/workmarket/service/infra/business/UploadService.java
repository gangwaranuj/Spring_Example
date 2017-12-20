package com.workmarket.service.infra.business;

import com.workmarket.domains.model.asset.Upload;
import com.workmarket.media.MediaSuccessionResponse;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.web.models.MessageBundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface UploadService {
	
	String getAuthorizedUriByUuid(String uuid) throws HostServiceException;
	String getAuthorizedDownloadUriByUuid(String uuid) throws HostServiceException;

	/**
	 * Uploads a file via an input stream
	 *
	 * @param stream
	 * @param filename
	 * @param mime
	 * @param byteSize
	 * @return an upload object that contains UUID that is unique for every upload
	 * @throws Exception
	 */
	Upload storeUpload(InputStream stream, String filename, String mime, long byteSize) throws IOException, HostServiceException;

	MediaSuccessionResponse storeExperimentUpload(InputStream stream, String filename, String mime, long byteSize) throws IOException, HostServiceException;

	/**
	 * Uploads a file from the file system
	 * 
	 * @param sourcePath
	 * @param filename
	 * @param mime
	 * @return an upload object that contains UUID that is unique for every upload
	 * @throws Exception
	 */
	Upload storeUpload(String sourcePath, String filename, String mime) throws IOException, HostServiceException;

	/**
	 * Finds an upload based on UUID
	 * 
	 * @param uuid
	 * @return an upload object with file metadata
	 */
	Upload findUploadByUUID(String uuid);

	/**
	 * Utility to method to generalize the handling of qq uploads from both IE and non-IE browsers.
	 */
	Map<String,Object> doFileUpload(String fileName, String contentType, long contentLength, InputStream inputStream) throws IOException, HostServiceException;

	MessageBundle validateContentMetadata(String contentType, long contentLength);
}
