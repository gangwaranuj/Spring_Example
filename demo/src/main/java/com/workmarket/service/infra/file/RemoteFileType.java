package com.workmarket.service.infra.file;

import org.springframework.util.Assert;

public enum RemoteFileType {
	

	TMP("workmarket-tmp-"),
	PUBLIC("workmarket-public-"),
	PRIVATE("workmarket-private-");

	/*
	 * Instance variables
	 */
	private final String name;


	private RemoteFileType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static RemoteFileType getInput(String name)throws Exception{
		Assert.notNull(name, "name object can't be null");

		RemoteFileType filteringTypes[] = RemoteFileType.values();
		for(int i = 0; i < filteringTypes.length; i++){
			if(filteringTypes[i].getName().equals(name))
				return filteringTypes[i];
		}

		throw new Exception("The RemoteFileType '" + name + "' doesn't return an appropriate RemoteFileType enum.");
	}

}


