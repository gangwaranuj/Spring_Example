package com.workmarket.common.template;

import com.workmarket.domains.model.screening.Screening;

public class BackgroundCheckFailedNotificationTemplate extends ScreeningStatusNotificationTemplate {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5204139224878424646L;

	public BackgroundCheckFailedNotificationTemplate(Long toId, Screening screening) {
		super(toId, screening);
	}
}