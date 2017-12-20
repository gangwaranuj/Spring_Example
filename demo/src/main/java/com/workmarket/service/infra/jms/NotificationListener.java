package com.workmarket.service.infra.jms;

import javax.jms.Message;

public interface NotificationListener {

	void onMessage(Message message);

}