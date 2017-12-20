package com.workmarket.thrift.assessment;

import java.io.Serializable;

/**
 * Created by arjun on 12/2/14.
 */
public class Link implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String remoteUri;
	private String availabilityTypeCode;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemoteUri() {
		return this.remoteUri;
	}

	public void setRemoteUri(String remoteUri) {
		this.remoteUri = remoteUri;
	}

	public String getAvailabilityTypeCode() {
		return this.availabilityTypeCode;
	}

	public void setAvailabilityTypeCode(String availabilityTypeCode) {
		this.availabilityTypeCode = availabilityTypeCode;
	}
}
