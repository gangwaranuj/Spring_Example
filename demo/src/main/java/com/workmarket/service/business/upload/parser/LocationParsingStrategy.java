package com.workmarket.service.business.upload.parser;

import com.workmarket.domains.work.service.upload.WorkUploadColumn;

import java.util.Map;

/**
 * User: jasonpendrey
 * Date: 7/11/13
 * Time: 2:16 PM
 */
public interface LocationParsingStrategy {
	public  ParsingStrategyHasAddress createParser(Map<String, String> types,WorkUploadColumn LOCATION_NUMBER,WorkUploadColumn LOCATION_NAME);
	}
