package com.workmarket.domains.model;

public enum MimeType {

	TEXT_PLAIN("text/plain"),
	TEXT_CSV("text/csv"),
	TEXT_CSV_ALTERNATIVE("text/x-comma-separated-values"),
	TEXT_HTML("text/html"),
	IMAGE_PNG("image/png"),
	IMAGE_JPEG("image/jpeg"),
	MS_WORD("application/msword"),
	PDF("application/pdf"),
	ZIP("application/zip"),
	JSON("application/json"),
	ICS("text/calendar");

	private String mimeType;

	MimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMimeType() {
		return mimeType;
	}
}
