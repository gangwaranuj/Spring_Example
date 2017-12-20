package com.workmarket.common.template;

import java.util.Calendar;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import com.workmarket.utility.DateUtilities;

public class AssetBundleAvailableNotificationTemplate extends NotificationTemplate {
	private static final long serialVersionUID = 1L;

	private String downloadUri;
	private Calendar expiration;

	public AssetBundleAvailableNotificationTemplate(Long toId, String downloadUri, Calendar expiration) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.ASSET_BUNDLE_AVAILABLE), ReplyToType.TRANSACTIONAL);
		this.downloadUri = downloadUri;
		this.expiration = expiration;
	}

	public String getDownloadUri() {
		return downloadUri;
	}
	public String getExpiration() {
		return DateUtilities.formatDateForEmail(expiration);
	}
}
