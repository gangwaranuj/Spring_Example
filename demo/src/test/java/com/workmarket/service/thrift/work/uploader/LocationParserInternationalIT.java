package com.workmarket.service.thrift.work.uploader;

import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.exception.WorkRowParseErrorType;
import com.workmarket.service.business.upload.parser.LocationParser;
import com.workmarket.service.business.upload.parser.WorkUploadLocation;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildData;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import com.workmarket.utility.CollectionUtilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
public class LocationParserInternationalIT extends BaseServiceIT {

	@Autowired @Qualifier("locationParserInternationalImpl") private LocationParser locationParserInternationalImpl;

	@Test
	public void build_hasaddress1_address1Added() {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());

		Map<String, String> types = CollectionUtilities.newStringMap(
				WorkUploadColumn.LOCATION_POSTAL_CODE.getUploadColumnName(), "07013",
				WorkUploadColumn.LOCATION_CITY.getUploadColumnName(), "Clifton",
				WorkUploadColumn.LOCATION_COUNTRY.getUploadColumnName(), "USA",
				WorkUploadColumn.LOCATION_STATE.getUploadColumnName(), "NJ",
				WorkUploadColumn.LOCATION_ADDRESS_1.getUploadColumnName(), "80 main street"
		);
		WorkUploaderBuildData build = new WorkUploaderBuildData().setTypes(types);
		build.setLineNumber(1);

		locationParserInternationalImpl.build(response, build);
		for (WorkUploadLocation location : response.getNewLocations().keySet()) {
			assertNotNull(location.getLocation().getAddress());
			assertEquals("80 main street", location.getLocation().getAddress().getAddressLine1());
		}
	}

	@Test
	public void build_hasCity_cityAdded() {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());

		Map<String, String> types = CollectionUtilities.newStringMap(
				WorkUploadColumn.LOCATION_POSTAL_CODE.getUploadColumnName(), "07505",
				WorkUploadColumn.LOCATION_CITY.getUploadColumnName(), "Paterson",
				WorkUploadColumn.LOCATION_COUNTRY.getUploadColumnName(), "USA",
				WorkUploadColumn.LOCATION_STATE.getUploadColumnName(), "NJ",
				WorkUploadColumn.LOCATION_ADDRESS_1.getUploadColumnName(), "80 main street"
		);
		WorkUploaderBuildData build = new WorkUploaderBuildData().setTypes(types);
		build.setLineNumber(1);

		locationParserInternationalImpl.build(response, build);
		for (WorkUploadLocation location : response.getNewLocations().keySet()) {
			assertNotNull(location.getLocation().getAddress());
			assertEquals("Paterson", location.getLocation().getAddress().getCity());
		}
	}

	@Test
	public void build_hasPostalCodeCountryStateCity_postalCodeAdded() {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());

		Map<String, String> types = CollectionUtilities.newStringMap(
				WorkUploadColumn.LOCATION_POSTAL_CODE.getUploadColumnName(), "07505",
				WorkUploadColumn.LOCATION_CITY.getUploadColumnName(), "Paterson",
				WorkUploadColumn.LOCATION_COUNTRY.getUploadColumnName(), "USA",
				WorkUploadColumn.LOCATION_STATE.getUploadColumnName(), "NJ",
				WorkUploadColumn.LOCATION_ADDRESS_1.getUploadColumnName(), "80 main street"
		);
		WorkUploaderBuildData build = new WorkUploaderBuildData().setTypes(types);
		build.setLineNumber(1);

		locationParserInternationalImpl.build(response, build);
		for (WorkUploadLocation location : response.getNewLocations().keySet()) {
			assertNotNull(location.getLocation().getAddress());
			assertEquals("07505", location.getLocation().getAddress().getZip());
		}
	}

	@Test
	public void build_hasPostalCodeCountryStateCity_StateAdded() {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());

		Map<String, String> types = CollectionUtilities.newStringMap(
				WorkUploadColumn.LOCATION_POSTAL_CODE.getUploadColumnName(), "07505",
				WorkUploadColumn.LOCATION_CITY.getUploadColumnName(), "Paterson",
				WorkUploadColumn.LOCATION_COUNTRY.getUploadColumnName(), "USA",
				WorkUploadColumn.LOCATION_STATE.getUploadColumnName(), "NJ",
				WorkUploadColumn.LOCATION_ADDRESS_1.getUploadColumnName(), "80 main street"
		);
		WorkUploaderBuildData build = new WorkUploaderBuildData().setTypes(types);
		build.setLineNumber(1);

		locationParserInternationalImpl.build(response, build);
		for (WorkUploadLocation location : response.getNewLocations().keySet()) {
			assertNotNull(location.getLocation().getAddress());
			assertEquals("NJ", location.getLocation().getAddress().getState());
		}
	}

	@Test
	public void build_hasCountry_countryAdded() {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());

		Map<String, String> types = CollectionUtilities.newStringMap(
				WorkUploadColumn.LOCATION_POSTAL_CODE.getUploadColumnName(), "07505",
				WorkUploadColumn.LOCATION_CITY.getUploadColumnName(), "Paterson",
				WorkUploadColumn.LOCATION_COUNTRY.getUploadColumnName(), "USA",
				WorkUploadColumn.LOCATION_STATE.getUploadColumnName(), "NJ",
				WorkUploadColumn.LOCATION_ADDRESS_1.getUploadColumnName(), "80 main street"
		);
		WorkUploaderBuildData build = new WorkUploaderBuildData().setTypes(types);
		build.setLineNumber(1);

		locationParserInternationalImpl.build(response, build);
		for (WorkUploadLocation location : response.getNewLocations().keySet()) {
			assertNotNull(location.getLocation().getAddress());
			assertEquals("USA", location.getLocation().getAddress().getCountry());
		}
	}

	@Test
	public void build_setLocationAndTimeZoneInternational_hasvalidAddress_TimeZoneCorrect() {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());

		Map<String, String> types = CollectionUtilities.newStringMap(
				WorkUploadColumn.LOCATION_POSTAL_CODE.getUploadColumnName(), "07505",
				WorkUploadColumn.LOCATION_CITY.getUploadColumnName(), "Paterson",
				WorkUploadColumn.LOCATION_COUNTRY.getUploadColumnName(), "USA",
				WorkUploadColumn.LOCATION_STATE.getUploadColumnName(), "NJ",
				WorkUploadColumn.LOCATION_ADDRESS_1.getUploadColumnName(), "80 main street"
		);
		WorkUploaderBuildData build = new WorkUploaderBuildData().setTypes(types);
		build.setLineNumber(1);

		locationParserInternationalImpl.build(response, build);
		for (WorkUploadLocation location : response.getNewLocations().keySet()) {
			assertNotNull(location.getLocation().getAddress());
			assertEquals("America/New_York", response.getWork().getTimeZone());
		}
	}

	@Test
	public void build_noCountry_addsCountry() {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());

		Map<String, String> types = CollectionUtilities.newStringMap(
				WorkUploadColumn.LOCATION_POSTAL_CODE.getUploadColumnName(), "07505",
				WorkUploadColumn.LOCATION_CITY.getUploadColumnName(), "Paterson",
				WorkUploadColumn.LOCATION_COUNTRY.getUploadColumnName(), "PRE",
				WorkUploadColumn.LOCATION_STATE.getUploadColumnName(), "NJ",
				WorkUploadColumn.LOCATION_ADDRESS_1.getUploadColumnName(), "80 main street"
		);
		WorkUploaderBuildData build = new WorkUploaderBuildData().setTypes(types);
		build.setLineNumber(1);

		locationParserInternationalImpl.build(response, build);
		for (WorkUploadLocation location : response.getNewLocations().keySet()) {
			assertNotNull(location.getLocation().getAddress());
			assertTrue(response.getErrors().isEmpty());
			assertEquals("US", location.getLocation().getAddress().getCountry());
		}
	}

	@Test
	public void build_CanadianAddress_success() {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());

		Map<String, String> types = CollectionUtilities.newStringMap(
				WorkUploadColumn.LOCATION_POSTAL_CODE.getUploadColumnName(), "B0V1A0",
				WorkUploadColumn.LOCATION_CITY.getUploadColumnName(), "Digby",
				WorkUploadColumn.LOCATION_COUNTRY.getUploadColumnName(), "CA",
				WorkUploadColumn.LOCATION_STATE.getUploadColumnName(), "NS",
				WorkUploadColumn.LOCATION_ADDRESS_1.getUploadColumnName(), "70 Warwick St"
		);
		WorkUploaderBuildData build = new WorkUploaderBuildData().setTypes(types);
		build.setLineNumber(1);

		locationParserInternationalImpl.build(response, build);
		for (WorkUploadLocation location : response.getNewLocations().keySet()) {
			assertNotNull(location.getLocation().getAddress());
			assertTrue(response.getErrors().isEmpty());
		}
	}

	@Test
	public void build_badState_fixesState() {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());

		Map<String, String> types = CollectionUtilities.newStringMap(
				WorkUploadColumn.LOCATION_POSTAL_CODE.getUploadColumnName(), "07505",
				WorkUploadColumn.LOCATION_CITY.getUploadColumnName(), "Paterson",
				WorkUploadColumn.LOCATION_COUNTRY.getUploadColumnName(), "PRE",
				WorkUploadColumn.LOCATION_STATE.getUploadColumnName(), "NN",
				WorkUploadColumn.LOCATION_ADDRESS_1.getUploadColumnName(), "80 main street"
		);
		WorkUploaderBuildData build = new WorkUploaderBuildData().setTypes(types);
		build.setLineNumber(1);

		locationParserInternationalImpl.build(response, build);

		for (WorkUploadLocation location : response.getNewLocations().keySet()) {
			assertNotNull(location.getLocation().getAddress());
			assertEquals("Paterson", location.getLocation().getAddress().getCity());
		}
	}
}
