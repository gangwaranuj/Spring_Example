package com.workmarket.api.v2.employer.uploads.services;

import com.google.api.client.util.Maps;
import com.google.common.collect.ImmutableMap;
import com.workmarket.api.v2.employer.uploads.models.CellDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.api.ApiBaseError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static com.workmarket.domains.work.service.upload.WorkUploadColumn.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CsvValidationServiceTest {
	@InjectMocks private CsvValidationServiceImpl csvValidationService;

	@Test
	public void testValidation_emptyMap() {
		Map<String, CellDTO> rowData = Maps.newHashMap();
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_OwnerUserNumber_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			OWNER_USER_NUMBER.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(OWNER_USER_NUMBER.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_OwnerUserNumber_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			OWNER_USER_NUMBER.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(OWNER_USER_NUMBER.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_OwnerEmail_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			OWNER_EMAIL.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(OWNER_EMAIL.getUploadColumnDescription())
				.setValue("valid@workmarket.com")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_OwnerEmail_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			OWNER_EMAIL.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(OWNER_EMAIL.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_SupportContactUserNumber_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			SUPPORT_CONTACT_USER_NUMBER.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(SUPPORT_CONTACT_USER_NUMBER.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_SupportContactUserNumber_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			SUPPORT_CONTACT_USER_NUMBER.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(SUPPORT_CONTACT_USER_NUMBER.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_SupportContactEmail_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			SUPPORT_CONTACT_EMAIL.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(SUPPORT_CONTACT_EMAIL.getUploadColumnDescription())
				.setValue("valid@workmarket.com")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_SupportContactEmail_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			SUPPORT_CONTACT_EMAIL.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(SUPPORT_CONTACT_EMAIL.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_IndustryId_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			INDUSTRY_ID.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(INDUSTRY_ID.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_IndustryId_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			INDUSTRY_ID.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(INDUSTRY_ID.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_LocationNumber_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			LOCATION_NUMBER.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(LOCATION_NUMBER.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_LocationNumber_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			LOCATION_NUMBER.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(LOCATION_NUMBER.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_TemplateNumber_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			TEMPLATE_NUMBER.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(TEMPLATE_NUMBER.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_TemplateNumber_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			TEMPLATE_NUMBER.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(TEMPLATE_NUMBER.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_Resources_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			USER_NUMBER.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(USER_NUMBER.getUploadColumnDescription())
				.setValue("1, 2")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_Resources_invalidNumber() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			USER_NUMBER.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(USER_NUMBER.getUploadColumnDescription())
				.setValue("1, invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_Resources_invalidExceedsLimit() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i <= Constants.UPLOAD_SEND_RESOURCES_LIMIT; i++) {
			str.append("i").append(",");
		}
		str.deleteCharAt(str.length() - 1);
		Map<String, CellDTO> rowData = ImmutableMap.of(
			USER_NUMBER.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(USER_NUMBER.getUploadColumnDescription())
				.setValue(str.toString())
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_MaxUnits_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			MAX_NUMBER_OF_UNITS.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(MAX_NUMBER_OF_UNITS.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_MaxUnits_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			MAX_NUMBER_OF_UNITS.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(MAX_NUMBER_OF_UNITS.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_MaxHoursInitialPrice_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_MaxHoursInitialPrice_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_MaxHoursAdditionalPrice_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_MaxHoursAdditionalPrice_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_FlatPriceClientFee_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			FLAT_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(FLAT_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_FlatPriceClientFee_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			FLAT_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(FLAT_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_FlatPriceResourceFee_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			FLAT_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(FLAT_PRICE_RESOURCE_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_FlatPriceResourceFee_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			FLAT_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(FLAT_PRICE_RESOURCE_FEE.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_PerHourPriceClientFee_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_PerHourPriceClientFee_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_PerHourPriceResourceFee_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_PerHourPriceResourceFee_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_InitialPerHourPriceClientFee_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			INITIAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(INITIAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_InitialPerHourPriceClientFee_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			INITIAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(INITIAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_InitialPerHourPriceResourceFee_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			INITIAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(INITIAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_InitialPerHourPriceResourceFee_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			INITIAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(INITIAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_AdditionalPerHourPriceClientFee_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_AdditionalPerHourPriceClientFee_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_AdditionalPerHourPriceResourceFee_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_AdditionalPerHourPriceResourceFee_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_PerUnitPriceClientFee_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			PER_UNIT_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_UNIT_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_PerUnitPriceClientFee_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			PER_UNIT_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_UNIT_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_PerUnitPriceResourceFee_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_PerUnitPriceResourceFee_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_FlatAndPerUnitPricing_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
			,
			FLAT_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(FLAT_PRICE_CLIENT_FEE.getUploadColumnDescription())
					.setValue("1")
					.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_PerUnitAndPerHourPricing_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
			,
			PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(OWNER_USER_NUMBER.getUploadColumnDescription())
					.setValue("1")
					.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_FlatAndPerHourPricing_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			FLAT_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(OWNER_USER_NUMBER.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
			,
			PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnDescription())
					.setValue("1")
					.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_FlatClientAndResource_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			FLAT_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(FLAT_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
			,
			FLAT_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(FLAT_PRICE_RESOURCE_FEE.getUploadColumnDescription())
					.setValue("1")
					.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_PerHourClientAndResource_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
			,
			PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnDescription())
					.setValue("1")
					.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_PerUnitClientAndResource_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			PER_UNIT_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_UNIT_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
			,
			PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnDescription())
					.setValue("1")
					.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_BlendedClientAndResource_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			INITIAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(INITIAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
			,
			INITIAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(INITIAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnDescription())
					.setValue("1")
					.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_BundleId_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			EXISTING_BUNDLE_ID.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(EXISTING_BUNDLE_ID.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_BundleId_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			EXISTING_BUNDLE_ID.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(EXISTING_BUNDLE_ID.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_NewBundleNameNoDescription_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			NEW_BUNDLE_NAME.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(NEW_BUNDLE_NAME.getUploadColumnDescription())
				.setValue("bundle1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_NewBundleDescriptionNoName_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			NEW_BUNDLE_DESCRIPTION.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(NEW_BUNDLE_DESCRIPTION.getUploadColumnDescription())
				.setValue("bundle1")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_BundleIdAndNewBundleDescription_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			EXISTING_BUNDLE_ID.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(EXISTING_BUNDLE_ID.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
			,
			NEW_BUNDLE_DESCRIPTION.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(NEW_BUNDLE_DESCRIPTION.getUploadColumnDescription())
					.setValue("bundle1")
					.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_BundleIdAndNewBundleName_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			EXISTING_BUNDLE_ID.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(EXISTING_BUNDLE_ID.getUploadColumnDescription())
				.setValue("1")
				.setIndex(0)
				.build()
			,
			NEW_BUNDLE_NAME.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(NEW_BUNDLE_NAME.getUploadColumnDescription())
					.setValue("bundle1")
					.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_StartDateTime_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			START_DATE_TIME.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(START_DATE_TIME.getUploadColumnDescription())
				.setValue("1/1/2016 1:00 AM")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_StartDateTime_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			START_DATE_TIME.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(START_DATE_TIME.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_EndDateTime_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			END_DATE_TIME.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(END_DATE_TIME.getUploadColumnDescription())
				.setValue("1/1/2016 1:00 AM")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_EndDateTime_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			END_DATE_TIME.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(END_DATE_TIME.getUploadColumnDescription())
				.setValue("invalid")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_StartDateAndTime_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			START_DATE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(START_DATE.getUploadColumnDescription())
				.setValue("1/1/2016")
				.setIndex(0)
				.build()
			,
			START_TIME.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(START_TIME.getUploadColumnDescription())
					.setValue("1:00 AM")
					.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_StartDateMissingStartTime_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			START_DATE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(START_DATE.getUploadColumnDescription())
				.setValue("1/1/2016")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_StartTimeMissignStartDate_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			START_TIME.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(START_TIME.getUploadColumnDescription())
				.setValue("1:00 AM")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_EndDateAndTime_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			END_DATE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(END_DATE.getUploadColumnDescription())
				.setValue("1/1/2016")
				.setIndex(0)
				.build()
			,
			END_TIME.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(OWNER_USER_NUMBER.getUploadColumnDescription())
					.setValue("1:00 AM")
					.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(0, validationErrors.size());
	}

	@Test
	public void testValidation_EndDateMissingEndTime_invalid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			END_DATE.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(END_DATE.getUploadColumnDescription())
				.setValue("1/1/2016")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}

	@Test
	public void testValidation_EndTimeMissignEndDate_valid() {
		Map<String, CellDTO> rowData = ImmutableMap.of(
			END_TIME.getUploadColumnName(),
			new CellDTO.Builder()
				.setHeader(END_TIME.getUploadColumnDescription())
				.setValue("1:00 AM")
				.setIndex(0)
				.build()
		);
		List<ApiBaseError> validationErrors = csvValidationService.validate(rowData, 0);
		assertEquals(1, validationErrors.size());
	}
}
