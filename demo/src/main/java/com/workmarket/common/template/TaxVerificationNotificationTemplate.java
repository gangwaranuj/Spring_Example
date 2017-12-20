package com.workmarket.common.template;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

/**
 * Created by nick on 12/2/12 1:43 PM
 */
public class TaxVerificationNotificationTemplate extends NotificationTemplate {

	protected AbstractTaxEntity entity;

	public TaxVerificationNotificationTemplate(User toUser, AbstractTaxEntity entity) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL,
				toUser.getId(),
				NotificationType.newNotificationType(NotificationType.TAX_VERIFICATION),
				ReplyToType.TRANSACTIONAL);
		this.entity = entity;
	}

	public AbstractTaxEntity getEntity() {
		return entity;
	}
}
