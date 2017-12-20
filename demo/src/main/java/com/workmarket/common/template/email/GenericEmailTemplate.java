package com.workmarket.common.template.email;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;

public class GenericEmailTemplate extends EmailTemplate {

    /**
	 *
	 */
	private static final long serialVersionUID = 8504349466632649487L;
	private String message;

	public GenericEmailTemplate(Long toId) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, "General notification");
        setNotificationType(new NotificationType(NotificationType.MISC));
	}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
