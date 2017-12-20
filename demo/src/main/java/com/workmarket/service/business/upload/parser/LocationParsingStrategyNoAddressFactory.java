package com.workmarket.service.business.upload.parser;

import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * User: jasonpendrey
 * Date: 7/9/13
 * Time: 5:48 PM
 */
@Service
public class LocationParsingStrategyNoAddressFactory {
	@Autowired @Qualifier("parsingStrategyNoAddressNameImpl") ParsingStrategyNoAddress parsingStrategyNoAddressNameImpl;
	@Autowired @Qualifier("parsingStrategyNoAddressNumberImpl") ParsingStrategyNoAddress parsingStrategyNoAddressNumberImpl;

	public ParsingStrategyNoAddress createParser(Map<String, String> types, WorkUploadColumn LOCATION_NUMBER, WorkUploadColumn LOCATION_NAME) {
		if (WorkUploadColumn.isNotEmpty(types, LOCATION_NUMBER)) {
			return parsingStrategyNoAddressNumberImpl;
		} else if (WorkUploadColumn.isNotEmpty(types, LOCATION_NAME)) {
			return parsingStrategyNoAddressNameImpl;
		}
		return null;
	}
}
