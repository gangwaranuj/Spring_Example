package com.workmarket.service.infra.sms;

import com.workmarket.notification.sms.vo.SmsNotifyResponse;
import com.workmarket.service.business.dto.SMSDTO;

public interface SMSService {
	void sendSMS(SMSDTO dto) throws Exception;
	SmsNotifyResponse sendSMS(final String toNumber, final String message);
}
