package com.workmarket.service.thrift.work.uploader;

import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.business.upload.parser.PartGroupParser;
import com.workmarket.thrift.work.Work;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildData;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class PartGroupParserIT extends BaseServiceIT {

	@Autowired private PartGroupParser partGroupParser;

	final private String
		suppliedByResource = "false",
		distributionMethod = "pickup",
		pickupLocationName = "Worldwide TechServices Technician",
		pickupLocationAddress1 = "Worldwide TechServices #450B",
		pickupLocationAddress2 = "4016 Flowers Road",
		pickupLocationCity = "Atlanta",
		pickupLocationState = "GA",
		pickupLocationZip = "30360",
		pickupLocationCountry = "USA",
		pickupLocationType = "1",
		pickupTrackingNumber = "753086630074494",
		pickupShippingProvider = "fedex",
		pickupPartValue = "300",
		returnRequired = "true",
		returnLocationName = "AL's pad",
		returnLocationAddress1 = "832 Classon Ave",
		returnLocationAddress2 = "3L",
		returnLocationCity = "Brooklyn",
		returnLocationState = "NY",
		returnLocationZip = "11238",
		returnLocationCountry = "USA",
		returnLocationType = "2",
		returnTrackingNumber = "1345678987653",
		returnShippingProvider = "ups",
		returnPartValue = "100";

	@Test
	public void build_copyAllProperties() throws Exception {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		Map<String, String> types = CollectionUtilities.newStringMap(
			WorkUploadColumn.SUPPLIED_BY_RESOURCE.getUploadColumnName(), suppliedByResource,
			WorkUploadColumn.DISTRIBUTION_METHOD.getUploadColumnName(), distributionMethod,
			WorkUploadColumn.PICKUP_LOCATION_NAME.getUploadColumnName(), pickupLocationName,
			WorkUploadColumn.PICKUP_LOCATION_ADDRESS_1.getUploadColumnName(), pickupLocationAddress1,
			WorkUploadColumn.PICKUP_LOCATION_ADDRESS_2.getUploadColumnName(), pickupLocationAddress2,
			WorkUploadColumn.PICKUP_LOCATION_CITY.getUploadColumnName(), pickupLocationCity,
			WorkUploadColumn.PICKUP_LOCATION_STATE.getUploadColumnName(), pickupLocationState,
			WorkUploadColumn.PICKUP_LOCATION_POSTAL_CODE.getUploadColumnName(), pickupLocationZip,
			WorkUploadColumn.PICKUP_LOCATION_COUNTRY.getUploadColumnName(), pickupLocationCountry,
			WorkUploadColumn.PICKUP_LOCATION_TYPE.getUploadColumnName(), pickupLocationType,
			WorkUploadColumn.PICKUP_TRACKING_NUMBER.getUploadColumnName(), pickupTrackingNumber,
			WorkUploadColumn.PICKUP_SHIPPING_PROVIDER.getUploadColumnName(), pickupShippingProvider,
			WorkUploadColumn.PICKUP_PART_VALUE.getUploadColumnName(), pickupPartValue,
			WorkUploadColumn.RETURN_REQUIRED.getUploadColumnName(), returnRequired,
			WorkUploadColumn.RETURN_LOCATION_NAME.getUploadColumnName(), returnLocationName,
			WorkUploadColumn.RETURN_LOCATION_ADDRESS_1.getUploadColumnName(), returnLocationAddress1,
			WorkUploadColumn.RETURN_LOCATION_ADDRESS_2.getUploadColumnName(), returnLocationAddress2,
			WorkUploadColumn.RETURN_LOCATION_CITY.getUploadColumnName(), returnLocationCity,
			WorkUploadColumn.RETURN_LOCATION_STATE.getUploadColumnName(), returnLocationState,
			WorkUploadColumn.RETURN_LOCATION_POSTAL_CODE.getUploadColumnName(), returnLocationZip,
			WorkUploadColumn.RETURN_LOCATION_COUNTRY.getUploadColumnName(), returnLocationCountry,
			WorkUploadColumn.RETURN_LOCATION_TYPE.getUploadColumnName(), returnLocationType,
			WorkUploadColumn.RETURN_TRACKING_NUMBER.getUploadColumnName(), returnTrackingNumber,
			WorkUploadColumn.RETURN_SHIPPING_PROVIDER.getUploadColumnName(), returnShippingProvider,
			WorkUploadColumn.RETURN_PART_VALUE.getUploadColumnName(), returnPartValue
		);
		partGroupParser.build(response, new WorkUploaderBuildData().setTypes(types));

		PartGroupDTO partGroupDTO = response.getWork().getPartGroup();
		assertNotNull(partGroupDTO);
		assertFalse(partGroupDTO.isSuppliedByWorker());
		assertEquals(partGroupDTO.getShippingDestinationType(), ShippingDestinationType.PICKUP);

		assertNotNull(partGroupDTO.getShipToLocation());
		assertEquals(partGroupDTO.getShipToLocation().getName(), pickupLocationName);
		assertEquals(partGroupDTO.getShipToLocation().getAddress1(), pickupLocationAddress1);
		assertEquals(partGroupDTO.getShipToLocation().getAddress2(), pickupLocationAddress2);
		assertEquals(partGroupDTO.getShipToLocation().getCity(), pickupLocationCity);
		assertEquals(partGroupDTO.getShipToLocation().getState(), pickupLocationState);
		assertEquals(partGroupDTO.getShipToLocation().getPostalCode(), pickupLocationZip);
		assertEquals(partGroupDTO.getShipToLocation().getCountry(), pickupLocationCountry);
		assertEquals(partGroupDTO.getShipToLocation().getLocationTypeId(), NumberUtils.createLong(pickupLocationType));

		assertNotNull(partGroupDTO.getReturnToLocation());
		assertEquals(partGroupDTO.getReturnToLocation().getName(), returnLocationName);
		assertEquals(partGroupDTO.getReturnToLocation().getAddress1(), returnLocationAddress1);
		assertEquals(partGroupDTO.getReturnToLocation().getAddress2(), returnLocationAddress2);
		assertEquals(partGroupDTO.getReturnToLocation().getCity(), returnLocationCity);
		assertEquals(partGroupDTO.getReturnToLocation().getState(), returnLocationState);
		assertEquals(partGroupDTO.getReturnToLocation().getPostalCode(), returnLocationZip);
		assertEquals(partGroupDTO.getReturnToLocation().getCountry(), returnLocationCountry);
		assertEquals(partGroupDTO.getReturnToLocation().getLocationTypeId(), NumberUtils.createLong(returnLocationType));

		assertEquals(partGroupDTO.getParts().size(), 2);
		for (PartDTO partDTO : partGroupDTO.getParts()) {
			if (partDTO.isReturn()) {
				assertEquals(partDTO.getShippingProvider(), ShippingProvider.UPS);
				assertEquals(partDTO.getPartValue(), new BigDecimal(returnPartValue));
				assertEquals(partDTO.getTrackingNumber(), returnTrackingNumber);
			} else {
				assertEquals(partDTO.getShippingProvider(), ShippingProvider.FEDEX);
				assertEquals(partDTO.getPartValue(), new BigDecimal(pickupPartValue));
				assertEquals(partDTO.getTrackingNumber(), pickupTrackingNumber);
			}
		}
	}

	@Test
	public void build_emptyLocationProperties_dontCreateLocation() throws Exception {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		Map<String, String> types = CollectionUtilities.newStringMap(
			WorkUploadColumn.SUPPLIED_BY_RESOURCE.getUploadColumnName(), suppliedByResource
		);
		partGroupParser.build(response, new WorkUploaderBuildData().setTypes(types));

		PartGroupDTO partGroupDTO = response.getWork().getPartGroup();
		assertNotNull(partGroupDTO);
		assertNull(partGroupDTO.getShipToLocation());
		assertNull(partGroupDTO.getReturnToLocation());
	}

	@Test
	public void build_existingPartGroup_copyPropertiesAndSetIdToNull() throws Exception {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		PartGroupDTO existingPartGroupDTO = new PartGroupDTO();
		existingPartGroupDTO.setId(3L);
		existingPartGroupDTO.setUuid("3L");
		existingPartGroupDTO.setShippingDestinationType(ShippingDestinationType.ONSITE);
		response.getWork().setPartGroup(existingPartGroupDTO);

		Map<String, String> types = CollectionUtilities.newStringMap(
			WorkUploadColumn.SUPPLIED_BY_RESOURCE.getUploadColumnName(), suppliedByResource
		);
		partGroupParser.build(response, new WorkUploaderBuildData().setTypes(types));

		PartGroupDTO partGroupDTO = response.getWork().getPartGroup();
		assertNotNull(partGroupDTO);
		assertEquals(partGroupDTO.getShippingDestinationType(), existingPartGroupDTO.getShippingDestinationType());
		assertNull(partGroupDTO.getId());
		assertNull(partGroupDTO.getUuid());
	}
}
