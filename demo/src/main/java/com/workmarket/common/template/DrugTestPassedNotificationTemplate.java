package com.workmarket.common.template;

import com.workmarket.domains.model.screening.Screening;

public class DrugTestPassedNotificationTemplate extends ScreeningStatusNotificationTemplate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 161963306384518517L;

	public DrugTestPassedNotificationTemplate(Long toId, Screening screening) {
		super(toId, screening);
	}
}