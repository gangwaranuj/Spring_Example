package com.workmarket.service.infra.communication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

public class TwilioVoiceAdapterImpl implements VoiceAdapter {

	private static final Log logger = LogFactory.getLog(TwilioVoiceAdapterImpl.class);

    @Value("${voice.adapter.twilio.apiVersion}")
    private String APIVERSION;

    @Value("${voice.adapter.twilio.accountSID}")
    private String ACCOUNT_SID;

    @Value("${voice.adapter.twilio.authToken}")
    private String AUTH_TOKEN;

	@Value("${voice.adapter.twilio.callback.url}")
    private String CALLBACK_URL;

    @Value("${WM_FROM_PHONE_NUMBER}")
    private String WM_FROM_PHONE_NUMBER;

	public String getCallbackURI() {
		return CALLBACK_URL;
	}
}