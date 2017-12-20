package com.workmarket.service.business.upload.parser;

import com.workmarket.thrift.work.exception.WorkRowParseError;

import java.util.List;
import java.util.Map;

/**
 * User: jasonpendrey
 * Date: 7/9/13
 * Time: 3:42 PM
 */
public interface ParsingStrategyNoAddress {

	public boolean parseLocation(Map<String, String> types, WorkUploaderBuildResponse response, List<WorkRowParseError> errors, Long clientId);

}
