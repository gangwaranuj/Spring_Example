package com.workmarket.service.business.wrapper;

import com.workmarket.common.service.wrapper.response.MessageResponse;
import com.workmarket.common.service.wrapper.response.Response;
import com.workmarket.service.business.status.DownloadProfilePhotosStatus;

public class DownloadProfilePhotosResponse extends MessageResponse {

	public DownloadProfilePhotosResponse() {
		super();
		this.status = DownloadProfilePhotosStatus.NONE;
	}

	public DownloadProfilePhotosResponse(DownloadProfilePhotosStatus status) {
		super(status);
	}

	public DownloadProfilePhotosResponse(DownloadProfilePhotosStatus status, String message) {
		super(status, message);
	}

	public static DownloadProfilePhotosResponse success() {
		return new DownloadProfilePhotosResponse(DownloadProfilePhotosStatus.SUCCESS);
	}

	public static DownloadProfilePhotosResponse fail() {
		return new DownloadProfilePhotosResponse(DownloadProfilePhotosStatus.FAILURE);
	}
}
