package com.workmarket.common.template.voice;


public interface VoiceTemplateFactory {
	
	VoiceTemplate buildVoiceTemplate(String toNumber, String msg);
}
