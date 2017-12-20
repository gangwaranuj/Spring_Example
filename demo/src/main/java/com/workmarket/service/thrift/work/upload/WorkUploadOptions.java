package com.workmarket.service.thrift.work.upload;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WorkUploadOptions {
	private static Log logger = LogFactory.getLog(WorkUploadOptions.class);
	private volatile boolean isPreview = true;

	public boolean isPreview() {
		return isPreview;
	}

	public void setPreview(boolean isPreview) {
		this.isPreview = isPreview;
		logger.debug("setPrevew(" + isPreview + ")");
	}

	@Override
	public String toString() {
		return "WorkUploadOptions [isPreview=" + isPreview + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isPreview ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorkUploadOptions other = (WorkUploadOptions) obj;
		if (isPreview != other.isPreview)
			return false;
		return true;
	}

}
