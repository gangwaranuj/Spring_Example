package com.workmarket.service.infra.voice;

import java.util.Map;

import org.springframework.util.Assert;

import com.google.common.collect.Maps;

public class VoiceApplication {
	private Map<String,VoiceApplicationScreen> screens = Maps.newHashMap();
	
	public VoiceApplication addScreen(VoiceApplicationScreen screen) {
		Assert.notNull(screen.getName());
		screens.put(screen.getName(), screen);
		return this;
	}
	
	public VoiceApplicationScreen getScreen(String name) {
		return screens.get(name);
	}
	
	public boolean hasScreen(String name) {
		return screens.containsKey(name);
	}
}