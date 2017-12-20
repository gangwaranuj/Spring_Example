package com.workmarket.dao.voice;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.voice.VoiceCall;

public interface VoiceCallDAO extends DAOInterface<VoiceCall> {
	public VoiceCall getByCallId(String callId) ;
}