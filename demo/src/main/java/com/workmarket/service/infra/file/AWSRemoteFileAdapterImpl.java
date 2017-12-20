package com.workmarket.service.infra.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.annotation.PostConstruct;

import com.amazonaws.services.s3.internal.RepeatableInputStream;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.Transfer.TransferState;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerConfiguration;
import com.amazonaws.services.s3.transfer.Upload;
import com.workmarket.service.exception.file.AmazonS3ServiceException;
import com.workmarket.utility.FileUtilities;

@Service
public class AWSRemoteFileAdapterImpl implements RemoteFileAdapter, InitializingBean {

	private static final String DEV_ENVIRONMENT = "dev";

	@Resource(name="aWSConfigData") private AWSConfigData aWSConfigData;
	private TransferManager transferManager;

	private static final Log logger = LogFactory.getLog(AWSRemoteFileAdapterImpl.class);

	@PostConstruct
	public void init() {
		BasicAWSCredentials awsCredentials = aWSConfigData.getBasicAWSCredentials();
		if (awsCredentials.getAWSAccessKeyId() != null && !awsCredentials.getAWSAccessKeyId().isEmpty()) {
			logger.info("Using S3 Transfers with provided credentials for access key " + awsCredentials.getAWSAccessKeyId());
			transferManager = new TransferManager(awsCredentials);
		} else {
			logger.info("Using S3 Transfers without credentials - expecting instance profile.");
			transferManager = new TransferManager();
		}
	}

	@Override
	public RemoteFile put(InputStream inputStream, long streamLength, RemoteFileType remoteFileType, String mimeType, String fileName) throws AmazonS3ServiceException {
		Assert.notNull(inputStream, "inputStream can't be null");
		Assert.notNull(fileName, "fileName can't be null");
		Assert.isTrue(streamLength > 0, "Must have a streamLength");

		String hierarchicalFileName = FileUtilities.createRemoteFileandDirectoryStructor(fileName);
		
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentLength(streamLength);
		meta.setContentType(mimeType);

		InputStream stream = inputStream;

		if (!(inputStream instanceof RepeatableInputStream)) {
			stream = new RepeatableInputStream(inputStream, aWSConfigData.getBufferSize());
		}

		PutObjectRequest request = new PutObjectRequest(getaWSConfigData().getS3Bucket(remoteFileType), hierarchicalFileName, stream, meta);

		return put(request, remoteFileType, hierarchicalFileName);
	}
	
	@Override
	public RemoteFile put(InputStream inputStream, long streamLength, RemoteFileType remoteFileType, String fileName) throws AmazonS3ServiceException {
		return put(inputStream, streamLength, remoteFileType, null, fileName); 
	}

	@Override
	public RemoteFile put(File file, RemoteFileType type) throws AmazonS3ServiceException {
		return put(file, type, file.getName());
	}
	
	@Override
	public RemoteFile put(File file, RemoteFileType type, String fileName) throws AmazonS3ServiceException {
		Assert.notNull(file);
		Assert.notNull(type);
		Assert.notNull(fileName);

		String hierarchicalFileName = FileUtilities.createRemoteFileandDirectoryStructor(fileName);
		
		PutObjectRequest request = new PutObjectRequest(getaWSConfigData().getS3Bucket(type), hierarchicalFileName, file);
		
		return put(request, type, hierarchicalFileName);
	}
	
	@Override
	public RemoteFile put(String fileName, RemoteFileType type) throws AmazonS3ServiceException {
		return put(fileName, type, fileName);
	}
	
	@Override
	public RemoteFile put(String filePath, RemoteFileType type, String fileName) throws AmazonS3ServiceException {
		return put(new File(filePath), type, fileName);
	}

	@Override
	public RemoteFile getUris(String fileName,RemoteFileType fileType){
		RemoteFile remoteFile = new RemoteFile();
		remoteFile.setRemoteUri(getPrivateEnvironmentUrl(fileType, fileName));
		if (RemoteFileType.PUBLIC.equals(fileType)) {
			remoteFile.setCdnUri(getCdnEnvironmentUrl(fileName));
		}
		return remoteFile;
	}
	
	private RemoteFile put(PutObjectRequest request, RemoteFileType fileType, String fileName) throws AmazonS3ServiceException {
		RemoteFile remoteFile = new RemoteFile();
		remoteFile.setRemoteUri(getPrivateEnvironmentUrl(fileType, fileName));
		
		if (RemoteFileType.PUBLIC.equals(fileType)) {
			request = request.withCannedAcl(CannedAccessControlList.PublicRead);
			remoteFile.setCdnUri(getCdnEnvironmentUrl(fileName));
		} else {
			request = request.withCannedAcl(CannedAccessControlList.Private);
		}
		
		try {
			Upload upload = transferManager.upload(request);
			UploadResult uploadResult = upload.waitForUploadResult();
			remoteFile.setETag(uploadResult.getETag());

			// Note that the TransferManager class does NOT throw an exception in certain instances of failed transfer.
			// We've seen this happen in the event of invalid S3 authentication keys.
			// So though the transfer is "done", it may have a status of "Failed" or "Cancelled" (or something else).
			// Naturally, we really only care for things to have "Completed" so check for that and error on everything else.
			if (!upload.getState().equals(TransferState.Completed)) {
				throw new AmazonS3ServiceException("Transfer did NOT complete. State: " + upload.getState());
			}
		} catch (AmazonClientException | InterruptedException e) {
			throw new AmazonS3ServiceException(e);
		}
		return remoteFile;
	}

	private String getCdnEnvironmentUrl(String fileName){
		if (getaWSConfigData().getRemoteFileEnvironment().equals(DEV_ENVIRONMENT)) {
			return getaWSConfigData().getCdnDevProdUrlPrefix() + fileName;
		} else {
			return getaWSConfigData().getCdnProdPublicUrlPrefix() + fileName;
		}
	}

	private String getPrivateEnvironmentUrl(RemoteFileType remoteFileType, String fileName){
		return getaWSConfigData().getDefaultUrlPrefix() + getaWSConfigData().getS3Bucket(remoteFileType) + "/" + fileName;
	}

	@Override
	public InputStream getFileStream(RemoteFileType remoteFileType, String fileName) throws AmazonS3ServiceException {
		Assert.notNull(fileName, "fileName can't be null");
		Assert.notNull(remoteFileType, "remoteFileType can't be null");
		String newFileName = FileUtilities.createRemoteFileandDirectoryStructor(fileName);
		
		GetObjectRequest getObjectRequest = new GetObjectRequest(getaWSConfigData().getS3Bucket(remoteFileType), newFileName);
		try {
			S3Object s3Object = getAmazonS3().getObject(getObjectRequest);
			if (s3Object == null) return null;
			return s3Object.getObjectContent();
		} catch (AmazonClientException e) {
			throw new AmazonS3ServiceException(e);
		}
	}

	@Override
	public File getFile(RemoteFileType remoteFileType, String fileName) throws AmazonS3ServiceException {
		Assert.notNull(fileName, "fileName can't be null");
		Assert.notNull(remoteFileType, "remoteFileType can't be null");

		InputStream inputStream = getFileStream(remoteFileType, fileName);
		if (inputStream == null) return null;
		
		try {
			return createFile(fileName, inputStream, false);
		} catch (IOException e) {
			throw new AmazonS3ServiceException(e);
		}
	}

	@Override
	public RemoteFile move(RemoteFileType sourceRemoteFileType, RemoteFileType destinationRemoteFileType, String fileName) throws AmazonS3ServiceException {

		Assert.notNull(fileName, "fileName can't be null");
		Assert.notNull(destinationRemoteFileType, "destinationRemoteFileType can't be null");
		Assert.notNull(sourceRemoteFileType, "sourceRemoteFileType can't be null");

		String sourceBucketName = getaWSConfigData().getS3Bucket(sourceRemoteFileType);
		String newFileName = FileUtilities.createRemoteFileandDirectoryStructor(fileName);
		String destinationBucketName = getaWSConfigData().getS3Bucket(destinationRemoteFileType);

		CopyObjectRequest copyObjectRequest =
			new CopyObjectRequest(sourceBucketName, newFileName, destinationBucketName, newFileName);

		RemoteFile remoteFile = new RemoteFile();
		remoteFile.setRemoteUri(getPrivateEnvironmentUrl(destinationRemoteFileType, newFileName));

		switch (destinationRemoteFileType) {
			case PUBLIC:
				copyObjectRequest = copyObjectRequest.withCannedAccessControlList(CannedAccessControlList.PublicRead);
				remoteFile.setCdnUri(getCdnEnvironmentUrl(newFileName));
				break;
			default: // defaults to private
				copyObjectRequest = copyObjectRequest.withCannedAccessControlList(CannedAccessControlList.Private);
		}

		try {
			AmazonS3 amazonS3 = getAmazonS3();
			amazonS3.copyObject(copyObjectRequest);
			DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(sourceBucketName, newFileName);
			amazonS3.deleteObject(deleteObjectRequest);
		} catch (AmazonClientException e) {
			throw new AmazonS3ServiceException(e);
		}

		return remoteFile;
	}
	
	@Override
	public URL getAuthorizedURL(RemoteFileType type, String fileName, Date expiration, Map<String,String> requestParameters) throws AmazonS3ServiceException {
		Assert.notNull(fileName, "fileName can't be null");
		Assert.notNull(type, "remoteFileType can't be null");
		Assert.notNull(requestParameters);
		String newFileName = FileUtilities.createRemoteFileandDirectoryStructor(fileName);
		if (expiration == null) {
			expiration = new Date(System.currentTimeMillis() + getaWSConfigData().getDefaultAuthorizedUrlExpiration());
		}

		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(getaWSConfigData().getS3Bucket(type), newFileName);
		request.setExpiration(expiration);
		for (String key : requestParameters.keySet())
			request.addRequestParameter(key, requestParameters.get(key));
		
		try {
			return getAmazonS3().generatePresignedUrl(request);
		} catch (AmazonClientException e) {
			throw new AmazonS3ServiceException(e);
		}
	}

	@Override
	public URL getAuthorizedURL(RemoteFileType type, String fileName, Date expiration) throws AmazonS3ServiceException {
		return getAuthorizedURL(type, fileName, expiration, Collections.<String,String>emptyMap());
	}

	@Override
	public URL getAuthorizedURL(RemoteFileType type, String fileName) throws AmazonS3ServiceException {
		return getAuthorizedURL(type, fileName, null);
	}

	@Override
	public void delete(RemoteFileType remoteFileType, String fileName) throws AmazonS3ServiceException {
		Assert.notNull(fileName, "fileName can't be null");
		Assert.notNull(remoteFileType, "remoteFileType can't be null");
		String newFileName = FileUtilities.createRemoteFileandDirectoryStructor(fileName);
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(getaWSConfigData().getS3Bucket(remoteFileType), newFileName);
		try {
			getAmazonS3().deleteObject(deleteObjectRequest);
		} catch (AmazonClientException e) {
			throw new AmazonS3ServiceException(e);
		}
	}
	
	/**
	 * @return the amazonS3
	 */
	public AmazonS3 getAmazonS3() {
		return transferManager.getAmazonS3Client();
	}

	/**
	 * @param fileName
	 * @param inputStream
	 * @param randomName
	 * @return
	 * @throws IOException
	 */
	private File createFile(String fileName, InputStream inputStream, Boolean randomName) throws IOException {
		if (fileName != null && inputStream != null) {
			File dir = new File(getaWSConfigData().getLocalAssetFileDirectory());
			if (!dir.exists())
				dir.mkdirs();

			File file;
			if (randomName)
				file = new File(getaWSConfigData().getLocalAssetFileDirectory() + fileName + UUID.randomUUID().toString());
			else
				file = new File(getaWSConfigData().getLocalAssetFileDirectory() + fileName);

			FileOutputStream os = new FileOutputStream(file);
			byte buffer[] = new byte[1024];
			int length;

			while ((length = inputStream.read(buffer)) > 0)
				os.write(buffer, 0, length);

			os.close();
			inputStream.close();
			return file;
		}
		return null;
	}

	/**
	 * @return the aWSConfigData
	 */
	public AWSConfigData getaWSConfigData() {
		return aWSConfigData;
	}

	/**
	 * @param aWSConfigData the aWSConfigData to set
	 */
	public void setaWSConfigData(AWSConfigData aWSConfigData) {
		this.aWSConfigData = aWSConfigData;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		TransferManagerConfiguration config = new TransferManagerConfiguration();
//		config.setMinimumUploadPartSize(2 * 1024 * 1024);
//		config.setMultipartUploadThreshold(50 * 1024 * 1024);
		transferManager.setConfiguration(config);
	}
	
}
