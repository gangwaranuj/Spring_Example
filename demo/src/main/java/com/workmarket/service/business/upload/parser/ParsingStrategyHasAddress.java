package com.workmarket.service.business.upload.parser;

import java.util.Map;

/**
 * User: jasonpendrey
 * Date: 7/9/13
 * Time: 3:42 PM
 */
public interface ParsingStrategyHasAddress {
	public boolean parseLocation(
			Map<String, String> types, WorkUploaderBuildResponse response, long clientId, int lineNum);

}
