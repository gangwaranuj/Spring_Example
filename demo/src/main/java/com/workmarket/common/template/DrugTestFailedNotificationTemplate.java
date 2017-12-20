package com.workmarket.common.template;

import com.workmarket.domains.model.screening.Screening;

public class DrugTestFailedNotificationTemplate extends ScreeningStatusNotificationTemplate {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1952047028210956570L;

	public DrugTestFailedNotificationTemplate(Long toId, Screening screening) {
		super(toId, screening);
	}
}