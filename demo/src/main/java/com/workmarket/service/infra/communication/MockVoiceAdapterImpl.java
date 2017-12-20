package com.workmarket.service.infra.communication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.workmarket.utility.RandomUtilities;

public class MockVoiceAdapterImpl implements VoiceAdapter {
	
	private static final Log logger = LogFactory.getLog(MockVoiceAdapterImpl.class);

	public String call(String to, String msg) throws Exception {
		if (logger.isDebugEnabled())
			logger.debug(String.format("Sending voice message: %s => %s", to, msg));
		return RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17);
	}

	public String getCallbackURI() {
		return null;
	}
}