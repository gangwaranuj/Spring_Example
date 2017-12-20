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
public class LocationParsingStrategyHasAddressFactory {

	@Autowired @Qualifier("parsingStrategyAddressNameImpl") ParsingStrategyHasAddress parsingStrategyAddressNameImpl;
	@Autowired @Qualifier("parsingStrategyAddressNumberImpl") ParsingStrategyHasAddress parsingStrategyAddressNumberImpl;

	public ParsingStrategyHasAddress createParser(Map<String, String> types, WorkUploadColumn locationNumber, WorkUploadColumn locationName) {
		if (WorkUploadColumn.isNotEmpty(types, locationNumber)) {
			return parsingStrategyAddressNumberImpl;
		} else if (WorkUploadColumn.isNotEmpty(types, locationName)) {
			return parsingStrategyAddressNameImpl;
		}
		return null;
	}
}
