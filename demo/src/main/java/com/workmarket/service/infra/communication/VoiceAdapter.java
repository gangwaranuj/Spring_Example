package com.workmarket.service.infra.communication;

public interface VoiceAdapter {
	/**
	 * Returns the callback URI for the service.
	 * @return Callback URI
	 */
	String getCallbackURI();
}