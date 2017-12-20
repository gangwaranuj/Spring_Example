package com.workmarket.service.infra.file;

public class RemoteFile {

	private String remoteUri;
	private String cdnUri;
	@Deprecated private String eTag;

	/*
	 * Empty constructor
	 */
	public RemoteFile(){
	}

	public RemoteFile(String remoteUri, String cdnUri){
		this.remoteUri = remoteUri;
		this.cdnUri = cdnUri;
	}

	public String getRemoteUri() {
		return remoteUri;
	}
	public void setRemoteUri(String remoteUri) {
		this.remoteUri = remoteUri;
	}
	public String getCdnUri() {
		return cdnUri;
	}
	public void setCdnUri(String cdnUri) {
		this.cdnUri = cdnUri;
	}

	public String getETag() {
		return eTag;
	}

	public void setETag(final String eTag) {
		this.eTag = eTag;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder("RemoteFile[");
		sb.append(" remoteUri:" + getRemoteUri());
		sb.append(", cdnUri:" + getCdnUri());
		sb.append("]");

		return sb.toString();
	}
}
