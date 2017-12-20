package com.workmarket.service.business.upload.parser;

import com.google.common.collect.Lists;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.thrift.LocationTimeZoneHelper;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("locationParserInternationalImpl")
public class LocationParserInternationalImpl implements LocationParser {

	@Autowired @Qualifier("locationTimeZoneHelperInternationalImpl") private LocationTimeZoneHelper locationTimeZoneHelper;
	@Autowired LocationParsingStrategyHasAddressFactory locationParsingStrategyHasAddressFactory;
	@Autowired LocationParsingStrategyNoAddressFactory locationParsingStrategyNoAddressFactory;

	@Override
	public void build(WorkUploaderBuildResponse response, WorkUploaderBuildData buildData) {
		boolean parsed;
		Map<String, String> types = buildData.getTypes();
		int lineNum = buildData.getLineNumber();

		if (WorkUploadColumn.containsAny(types, WorkUploadColumn.LOCATION_OFFSITE)) {
			boolean val = WorkUploadColumn.parseBoolean(types, WorkUploadColumn.LOCATION_OFFSITE);
			response.getWork().setOffsiteLocation(val);
			if (val) { return; }
		}

		if (WorkUploadColumn.containsAll(types, WorkUploadColumn.LOCATION_ADDRESS_1, WorkUploadColumn.LOCATION_CITY )) {

			if (response.getWork().isSetClientCompany() && response.getWork().getClientCompany().isSetId()) {
				long clientId = response.getWork().getClientCompany().getId();

				ParsingStrategyHasAddress parsingStrategyHasAddress = locationParsingStrategyHasAddressFactory.createParser(types, WorkUploadColumn.LOCATION_NUMBER, WorkUploadColumn.LOCATION_NAME);
				if (parsingStrategyHasAddress != null){
					parsingStrategyHasAddress.parseLocation(types, response, clientId, lineNum);
					return;
				}

			}
			locationTimeZoneHelper.setLocationAndTimeZone(response, types, lineNum);
		} else {
			Long clientId = null;
			if (response.getWork().isSetClientCompany() && response.getWork().getClientCompany().isSetId()) {
				clientId = response.getWork().getClientCompany().getId();
			}

			List<WorkRowParseError> errors = Lists.newArrayList();
			ParsingStrategyNoAddress parsingStrategyNoAddress = locationParsingStrategyNoAddressFactory.createParser(types, WorkUploadColumn.LOCATION_NUMBER, WorkUploadColumn.LOCATION_NAME);

			if (WorkUploadColumn.isNotEmpty(types, WorkUploadColumn.LOCATION_NUMBER)) {
				parsed = parsingStrategyNoAddress.parseLocation(types, response, errors, clientId);
				if (parsed) { return; }
			}

			if (WorkUploadColumn.isNotEmpty(types, WorkUploadColumn.LOCATION_NAME)) {
				parsingStrategyNoAddress.parseLocation(types, response, errors, clientId);
			}

		}
	}
}
