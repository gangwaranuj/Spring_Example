package com.workmarket.service.infra.voice;

import com.workmarket.domains.model.voice.VoiceCall;
import com.workmarket.service.exception.IllegalWorkAccessException;

public interface VoiceCommand {
	public void execute(VoiceCall call, String msg) throws IllegalWorkAccessException;
}