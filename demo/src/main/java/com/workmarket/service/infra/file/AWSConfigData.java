/**
 * 
 */
package com.workmarket.service.infra.file;

import java.io.Serializable;

import com.amazonaws.auth.BasicAWSCredentials;

/**
 * @since 9/1/2011
 *
 */
public class AWSConfigData implements Serializable {

	/**
	 * Instance Variables 
	 */
	private BasicAWSCredentials basicAWSCredentials;
	private String localAssetFileDirectory = "/tmp/aws-s2-multipart/";
	private String remoteFileEnvironment = "dev";
	private String defaultUrlPrefix = "https://s3.amazonaws.com/";
	private String cdnDevProdUrlPrefix = "https://dip3s7axeozs8.cloudfront.net/";
	private String cdnProdPublicUrlPrefix = "https://dmt5t3ugyo1yn.cloudfront.net/";
	private Long defaultAuthorizedUrlExpiration = (long) (1000 * 45); //45 sec
	private Long triggerMultipart = 10485760L; //10 meg
	private int bufferSize = 131072*2; // We are doubling the default buffer defined by the Amazon client libs
	private static final long serialVersionUID = 5179681638333211955L;
	private String s3BucketPrivate = "workmarket-private-dev";
	private String s3BucketPublic = "workmarket-public-dev";
	private String s3BucketTmp = "workmarket-tmp-dev";

	public int getBufferSize() { return bufferSize; }

	/**
	 * @return the basicAWSCredentials
	 */
	public BasicAWSCredentials getBasicAWSCredentials() {
		return basicAWSCredentials;
	}
	/**
	 * @param basicAWSCredentials the basicAWSCredentials to set
	 */
	public void setBasicAWSCredentials(BasicAWSCredentials basicAWSCredentials) {
		this.basicAWSCredentials = basicAWSCredentials;
	}
	
	/**
	 * @return the localAssetFileDirectory
	 */
	public String getLocalAssetFileDirectory() {
		return localAssetFileDirectory;
	}
	/**
	 * @param localAssetFileDirectory the localAssetFileDirectory to set
	 */
	public void setLocalAssetFileDirectory(String localAssetFileDirectory) {
		this.localAssetFileDirectory = localAssetFileDirectory;
	}
	/**
	 * @return the remoteFileEnvironment
	 */
	public String getRemoteFileEnvironment() {
		return remoteFileEnvironment;
	}
	/**
	 * @param remoteFileEnvironment the remoteFileEnvironment to set
	 */
	public void setRemoteFileEnvironment(String remoteFileEnvironment) {
		this.remoteFileEnvironment = remoteFileEnvironment;
	}
	/**
	 * @return the defaultUrlPrefix
	 */
	public String getDefaultUrlPrefix() {
		return defaultUrlPrefix;
	}
	/**
	 * @param defaultUrlPrefix the defaultUrlPrefix to set
	 */
	public void setDefaultUrlPrefix(String defaultUrlPrefix) {
		this.defaultUrlPrefix = defaultUrlPrefix;
	}
	/**
	 * @return the cdnDevProdUrlPrefix
	 */
	public String getCdnDevProdUrlPrefix() {
		return cdnDevProdUrlPrefix;
	}
	/**
	 * @param cdnDevProdUrlPrefix the cdnDevProdUrlPrefix to set
	 */
	public void setCdnDevProdUrlPrefix(String cdnDevProdUrlPrefix) {
		this.cdnDevProdUrlPrefix = cdnDevProdUrlPrefix;
	}
	/**
	 * @return the cdnProdPublicUrlPrefix
	 */
	public String getCdnProdPublicUrlPrefix() {
		return cdnProdPublicUrlPrefix;
	}
	/**
	 * @param cdnProdPublicUrlPrefix the cdnProdPublicUrlPrefix to set
	 */
	public void setCdnProdPublicUrlPrefix(String cdnProdPublicUrlPrefix) {
		this.cdnProdPublicUrlPrefix = cdnProdPublicUrlPrefix;
	}
	/**
	 * @return the defaultAuthorizedUrlExpiration
	 */
	public Long getDefaultAuthorizedUrlExpiration() {
		return defaultAuthorizedUrlExpiration;
	}
	/**
	 * @param defaultAuthorizedUrlExpiration the defaultAuthorizedUrlExpiration to set
	 */
	public void setDefaultAuthorizedUrlExpiration(Long defaultAuthorizedUrlExpiration) {
		this.defaultAuthorizedUrlExpiration = defaultAuthorizedUrlExpiration;
	}
	/**
	 * @return the triggerMultipart
	 */
	public Long getTriggerMultipart() {
		return triggerMultipart;
	}
	/**
	 * @param triggerMultipart the triggerMultipart to set
	 */
	public void setTriggerMultipart(Long triggerMultipart) {
		this.triggerMultipart = triggerMultipart;
	}

	public String getS3BucketPrivate() {
		return s3BucketPrivate;
	}

	public void setS3BucketPrivate(String s3BucketPrivate) {
		this.s3BucketPrivate = s3BucketPrivate;
	}

	public String getS3BucketPublic() {
		return s3BucketPublic;
	}

	public void setS3BucketPublic(String s3BucketPublic) {
		this.s3BucketPublic = s3BucketPublic;
	}

	public String getS3BucketTmp() {
		return s3BucketTmp;
	}

	public void setS3BucketTmp(String s3BucketTmp) {
		this.s3BucketTmp = s3BucketTmp;
	}

	public String getS3Bucket(RemoteFileType remoteFileType) {
		switch (remoteFileType) {
			case PRIVATE:
				return s3BucketPrivate;
			case PUBLIC:
				return s3BucketPublic;
			case TMP:
				return s3BucketTmp;
			default:
				return "";
		}
	}
}
