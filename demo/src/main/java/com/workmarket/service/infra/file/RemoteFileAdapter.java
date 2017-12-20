package com.workmarket.service.infra.file;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import com.workmarket.service.exception.HostServiceException;

public interface RemoteFileAdapter {
	RemoteFile put(InputStream inputStream, long streamLength, RemoteFileType type, String mimeType, String fileName) throws HostServiceException;
	RemoteFile put(InputStream inputStream, long streamLength, RemoteFileType type, String fileName) throws HostServiceException;
	RemoteFile put(File file, RemoteFileType type) throws HostServiceException;
	RemoteFile put(File file, RemoteFileType type, String fileName) throws HostServiceException;
	RemoteFile put(String filePath, RemoteFileType type) throws HostServiceException;
	RemoteFile put(String filePath, RemoteFileType type, String fileName) throws HostServiceException;
	RemoteFile getUris(String fileName,RemoteFileType fileType);

	InputStream getFileStream(RemoteFileType type, String fileName) throws HostServiceException;
	File getFile(RemoteFileType type, String fileName)	throws HostServiceException;
	
	RemoteFile move(RemoteFileType sourceRemoteFileType, RemoteFileType destinationRemoteFileType, String fileName) throws HostServiceException;
	
	void delete(RemoteFileType type, String fileName) throws HostServiceException;
	
	URL getAuthorizedURL(RemoteFileType type, String fileName, Date expiration, Map<String,String> requestParameters) throws HostServiceException;
	URL getAuthorizedURL(RemoteFileType type, String fileName, Date expiration) throws HostServiceException;
	URL getAuthorizedURL(RemoteFileType type, String fileName) throws HostServiceException;
}