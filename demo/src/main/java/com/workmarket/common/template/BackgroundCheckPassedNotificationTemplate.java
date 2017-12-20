package com.workmarket.common.template;

import com.workmarket.domains.model.screening.Screening;

public class BackgroundCheckPassedNotificationTemplate extends ScreeningStatusNotificationTemplate {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1218799061505064949L;

	public BackgroundCheckPassedNotificationTemplate(Long toId, Screening screening) {
		super(toId, screening);
	}
}