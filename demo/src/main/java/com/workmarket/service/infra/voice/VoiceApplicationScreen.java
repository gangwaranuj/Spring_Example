package com.workmarket.service.infra.voice;

import java.util.Map;

import com.google.common.collect.Maps;

public class VoiceApplicationScreen {
	private String name;
	private Map<String,VoiceCommand> commands = Maps.newLinkedHashMap();
	
	public VoiceApplicationScreen(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public VoiceApplicationScreen addCommand(String match, VoiceCommand command) {
		commands.put(match, command);
		return this;
	}
	
	/**
	 * Find command for user's input: regex-key implementation
	 * If there are available commands for a screen, iterate through the set until we get a match.
	 * First to match gets returned. Allows for a possible catchall.
	 * @param input
	 * @return
	 */
	public VoiceCommand findCommand(String input) {
		for (String key : commands.keySet())
			if (input.matches(key))
				return commands.get(key);
		return null;
	}
}
