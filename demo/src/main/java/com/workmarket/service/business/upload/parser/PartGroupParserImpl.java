package com.workmarket.service.business.upload.parser;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.work.model.part.PartDistributionMethodType;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.thrift.CountryAssignmentHelper;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import com.workmarket.thrift.work.exception.WorkRowParseErrorType;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class PartGroupParserImpl implements PartGroupParser {

	@Autowired private CountryAssignmentHelper countryAssignmentHelper;
	@Autowired private MessageBundleHelper messageHelper;

	List<WorkUploadColumn> PICKUP_LOCATION_COLUMNS = new ImmutableList.Builder<WorkUploadColumn>()
		.add(WorkUploadColumn.PICKUP_LOCATION_NAME)
		.add(WorkUploadColumn.PICKUP_LOCATION_NUMBER)
		.add(WorkUploadColumn.PICKUP_LOCATION_ADDRESS_1)
		.add(WorkUploadColumn.PICKUP_LOCATION_ADDRESS_2)
		.add(WorkUploadColumn.PICKUP_LOCATION_CITY)
		.add(WorkUploadColumn.PICKUP_LOCATION_STATE)
		.add(WorkUploadColumn.PICKUP_LOCATION_POSTAL_CODE)
		.add(WorkUploadColumn.PICKUP_LOCATION_COUNTRY)
		.add(WorkUploadColumn.PICKUP_LOCATION_TYPE)
		.build();

	List<WorkUploadColumn> RETURN_LOCATION_COLUMNS = new ImmutableList.Builder<WorkUploadColumn>()
		.add(WorkUploadColumn.RETURN_LOCATION_NAME)
		.add(WorkUploadColumn.RETURN_LOCATION_NUMBER)
		.add(WorkUploadColumn.RETURN_LOCATION_ADDRESS_1)
		.add(WorkUploadColumn.RETURN_LOCATION_ADDRESS_2)
		.add(WorkUploadColumn.RETURN_LOCATION_CITY)
		.add(WorkUploadColumn.RETURN_LOCATION_STATE)
		.add(WorkUploadColumn.RETURN_LOCATION_POSTAL_CODE)
		.add(WorkUploadColumn.RETURN_LOCATION_COUNTRY)
		.add(WorkUploadColumn.RETURN_LOCATION_TYPE)
		.build();

	List<WorkUploadColumn> PARTS_COLUMNS = new ImmutableList.Builder<WorkUploadColumn>()
		.add(WorkUploadColumn.SUPPLIED_BY_RESOURCE)
		.add(WorkUploadColumn.DISTRIBUTION_METHOD)
		.addAll(PICKUP_LOCATION_COLUMNS)
		.add(WorkUploadColumn.PICKUP_TRACKING_NUMBER)
		.add(WorkUploadColumn.PICKUP_PART_VALUE)
		.addAll(RETURN_LOCATION_COLUMNS)
		.add(WorkUploadColumn.RETURN_TRACKING_NUMBER)
		.add(WorkUploadColumn.RETURN_PART_VALUE)
		.add(WorkUploadColumn.RETURN_REQUIRED)
		.build();

	public void build(WorkUploaderBuildResponse response, WorkUploaderBuildData buildData) {
		final Map<String, String> types = buildData.getTypes();

		if (!response.getWork().isSetPartGroup() && !WorkUploadColumn.containsAny(types, PARTS_COLUMNS)) {
			return;
		}

		PartGroupDTO partGroup = parsePartGroup(types, response, buildData);

		partGroup.addPart(parsePart(
			types,
			response,
			WorkUploadColumn.PICKUP_TRACKING_NUMBER,
			WorkUploadColumn.PICKUP_PART_VALUE,
			WorkUploadColumn.PICKUP_SHIPPING_PROVIDER,
			false
		));

		partGroup.addPart(parsePart(
			types,
			response,
			WorkUploadColumn.RETURN_TRACKING_NUMBER,
			WorkUploadColumn.RETURN_PART_VALUE,
			WorkUploadColumn.RETURN_SHIPPING_PROVIDER,
			true
		));

		response.getWork().setPartGroup(partGroup);
	}

	private PartGroupDTO parsePartGroup(final Map<String, String> types, final WorkUploaderBuildResponse response, final WorkUploaderBuildData buildData) {
		final PartGroupDTO partGroup = new PartGroupDTO();
		if (response.getWork().isSetPartGroup()) {
			BeanUtilities.copyProperties(partGroup, response.getWork().getPartGroup());
			partGroup.setId(null);
			partGroup.setUuid(null);
		}

		boolean isInfoSet = false;
		if (WorkUploadColumn.containsAny(types, PICKUP_LOCATION_COLUMNS)) {
			partGroup.setShippingDestinationType(ShippingDestinationType.PICKUP);
			partGroup.setSuppliedByWorker(false);
			isInfoSet = true;

			String country = countryAssignmentHelper.getCountryForAssignments(
				WorkUploadColumn.PICKUP_LOCATION_COUNTRY, response, WorkUploadColumn.get(types, WorkUploadColumn.PICKUP_LOCATION_COUNTRY)
			);
			partGroup.setShipToLocation(new LocationDTO(
				null,
				WorkUploadColumn.get(types, WorkUploadColumn.PICKUP_LOCATION_NAME),
				WorkUploadColumn.get(types, WorkUploadColumn.PICKUP_LOCATION_NUMBER),
				WorkUploadColumn.get(types, WorkUploadColumn.PICKUP_LOCATION_ADDRESS_1),
				WorkUploadColumn.get(types, WorkUploadColumn.PICKUP_LOCATION_ADDRESS_2),
				WorkUploadColumn.get(types, WorkUploadColumn.PICKUP_LOCATION_CITY),
				WorkUploadColumn.get(types, WorkUploadColumn.PICKUP_LOCATION_STATE),
				WorkUploadColumn.get(types, WorkUploadColumn.PICKUP_LOCATION_POSTAL_CODE),
				WorkUploadColumn.get(types, WorkUploadColumn.PICKUP_LOCATION_TYPE),
				country
			));

		} else if (WorkUploadColumn.containsAny(types, WorkUploadColumn.DISTRIBUTION_METHOD)) {
			final String value = StringUtils.trimAllWhitespace(WorkUploadColumn.get(types, WorkUploadColumn.DISTRIBUTION_METHOD));
			try {
				ShippingDestinationType type = ShippingDestinationType.convertFromDistributionMethod(
					PartDistributionMethodType.valueOf(value.toUpperCase())
				);
				partGroup.setShippingDestinationType(type);
				partGroup.setSuppliedByWorker(false);
				isInfoSet = true;
			} catch (IllegalArgumentException e) {
				final WorkRowParseError error = new WorkRowParseError();
				error.setMessage(messageHelper.getMessage("partsAndLogistics.shippingDestinationType.invalid", value));
				error.setErrorType(WorkRowParseErrorType.INVALID_DATA);
				response.addToRowParseErrors(error);
			}

		} else if (WorkUploadColumn.containsAny(types, WorkUploadColumn.SUPPLIED_BY_RESOURCE)) {
			partGroup.setSuppliedByWorker(WorkUploadColumn.parseBoolean(types, WorkUploadColumn.SUPPLIED_BY_RESOURCE));
			isInfoSet = true;

		} else if (buildData.getWork() != null && buildData.getWork().getPartGroup() != null) {
			final PartGroupDTO templatePartGroup = buildData.getWork().getPartGroup();
			BeanUtilities.copyProperties(partGroup, templatePartGroup, new String[]{"id"});
			isInfoSet = true;
		}

		if (!isInfoSet && !partGroup.isSuppliedByWorker() ) {
			final WorkRowParseError error = new WorkRowParseError();
			error.setMessage(messageHelper.getMessage("partsAndLogistics.bulkUpload.missingInput"));
			error.setErrorType(WorkRowParseErrorType.MISSING_PARAMETER);
			error.setColumn(WorkUploadColumn.SUPPLIED_BY_RESOURCE);
			error.setData(types.get(WorkUploadColumn.get(types, WorkUploadColumn.SUPPLIED_BY_RESOURCE)));
			response.addToRowParseErrors(error);
		}

		if (WorkUploadColumn.isNotEmpty(types, WorkUploadColumn.RETURN_REQUIRED)) {
			partGroup.setReturnRequired(WorkUploadColumn.parseBoolean(types, WorkUploadColumn.RETURN_REQUIRED));
		}

		if (WorkUploadColumn.containsAny(types, RETURN_LOCATION_COLUMNS)) {
			String country = countryAssignmentHelper.getCountryForAssignments(
				WorkUploadColumn.RETURN_LOCATION_COUNTRY, response, WorkUploadColumn.get(types, WorkUploadColumn.RETURN_LOCATION_COUNTRY)
			);
			partGroup.setReturnToLocation(new LocationDTO(
				null,
				WorkUploadColumn.get(types, WorkUploadColumn.RETURN_LOCATION_NAME),
				WorkUploadColumn.get(types, WorkUploadColumn.RETURN_LOCATION_NUMBER),
				WorkUploadColumn.get(types, WorkUploadColumn.RETURN_LOCATION_ADDRESS_1),
				WorkUploadColumn.get(types, WorkUploadColumn.RETURN_LOCATION_ADDRESS_2),
				WorkUploadColumn.get(types, WorkUploadColumn.RETURN_LOCATION_CITY),
				WorkUploadColumn.get(types, WorkUploadColumn.RETURN_LOCATION_STATE),
				WorkUploadColumn.get(types, WorkUploadColumn.RETURN_LOCATION_POSTAL_CODE),
				WorkUploadColumn.get(types, WorkUploadColumn.RETURN_LOCATION_TYPE),
				country
			));

			partGroup.setReturnRequired(true);
		}

		return partGroup;
	}

	private PartDTO parsePart(
		final Map<String, String> types, final WorkUploaderBuildResponse response,
		final WorkUploadColumn TRACKING_NUMBER_COLUMN,
		final WorkUploadColumn PART_VALUE_COLUMN,
		final WorkUploadColumn PART_SHIPPING_PROVIDER_COLUMN,
		final boolean isReturn) {

		String trackingNumber = null;
		ShippingProvider shippingProvider = null;
		BigDecimal value = null;

		if (WorkUploadColumn.isNotEmpty(types, TRACKING_NUMBER_COLUMN)) {
			trackingNumber = types.get(TRACKING_NUMBER_COLUMN.getUploadColumnName());
		}

		if (WorkUploadColumn.isNotEmpty(types, PART_SHIPPING_PROVIDER_COLUMN)) {
			String shippingProviderCode = types.get(PART_SHIPPING_PROVIDER_COLUMN.getUploadColumnName());
			shippingProvider = ShippingProvider.getShippingProvider(shippingProviderCode);
		}

		if (WorkUploadColumn.isNotEmpty(types, PART_VALUE_COLUMN)) {
			try {
				value = WorkUploadColumn.parseBigDecimal(types, PART_VALUE_COLUMN);
			} catch (Exception e) {
				final WorkRowParseError error = new WorkRowParseError();
				error.setMessage(messageHelper.getMessage("partsAndLogistics.partValue.invalid", isReturn ? "return" : "pick up"));
				error.setErrorType(WorkRowParseErrorType.INVALID_DATA);
				error.setColumn(PART_VALUE_COLUMN);
				error.setData(types.get(WorkUploadColumn.get(types, PART_VALUE_COLUMN)));
				response.addToRowParseErrors(error);
			}
		}

		// TODO: Alex - remove this when we add a return part name field in bulk upload
		if (shippingProvider != null || isNotBlank(trackingNumber) || value != null) {
			return new PartDTO(shippingProvider, trackingNumber, value, isReturn, isReturn ? "Return part" : "Part");
		} else {
			return null;
		}
	}
}
