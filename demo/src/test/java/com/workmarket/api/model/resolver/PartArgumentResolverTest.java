package com.workmarket.api.model.resolver;

import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.api.v1.AssignmentsCreateController;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PartArgumentResolverTest {

	private PartArgumentResolver resolver;
	final private String
		suppliedByResource = "0",
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
		returnRequired = "1",
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

	@Before
	public void setup() {
		resolver = new PartArgumentResolver();
	}

	@Test
	public void populateParts_copyOverAllProperties() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("parts[supplied_by_resource]", suppliedByResource);
		request.addParameter("parts[distribution_method]", distributionMethod);
		request.addParameter("parts[pickup_location_name]", pickupLocationName);
		request.addParameter("parts[pickup_location_address1]", pickupLocationAddress1);
		request.addParameter("parts[pickup_location_address2]", pickupLocationAddress2);
		request.addParameter("parts[pickup_location_city]", pickupLocationCity);
		request.addParameter("parts[pickup_location_state]", pickupLocationState);
		request.addParameter("parts[pickup_location_zip]", pickupLocationZip);
		request.addParameter("parts[pickup_location_country]", pickupLocationCountry);
		request.addParameter("parts[pickup_location_type]", pickupLocationType);
		request.addParameter("parts[pickup_tracking_number]", pickupTrackingNumber);
		request.addParameter("parts[pickup_shipping_provider]", pickupShippingProvider);
		request.addParameter("parts[pickup_part_value]", pickupPartValue);
		request.addParameter("parts[return_required]", returnRequired);
		request.addParameter("parts[return_location_name]", returnLocationName);
		request.addParameter("parts[return_location_address1]", returnLocationAddress1);
		request.addParameter("parts[return_location_address2]", returnLocationAddress2);
		request.addParameter("parts[return_location_city]", returnLocationCity);
		request.addParameter("parts[return_location_state]", returnLocationState);
		request.addParameter("parts[return_location_zip]", returnLocationZip);
		request.addParameter("parts[return_location_country]", returnLocationCountry);
		request.addParameter("parts[return_location_type]", returnLocationType);
		request.addParameter("parts[return_tracking_number]", returnTrackingNumber);
		request.addParameter("parts[return_shipping_provider]", returnShippingProvider);
		request.addParameter("parts[return_part_value]", returnPartValue);

		Map<String, String> parts = resolver.populateParts(request);
		assertEquals(parts.get("supplied_by_resource"), suppliedByResource);
		assertEquals(parts.get("distribution_method"), distributionMethod);
		assertEquals(parts.get("pickup_location_name"), pickupLocationName);
		assertEquals(parts.get("pickup_location_address1"), pickupLocationAddress1);
		assertEquals(parts.get("pickup_location_address2"), pickupLocationAddress2);
		assertEquals(parts.get("pickup_location_city"), pickupLocationCity);
		assertEquals(parts.get("pickup_location_state"), pickupLocationState);
		assertEquals(parts.get("pickup_location_zip"), pickupLocationZip);
		assertEquals(parts.get("pickup_location_country"), pickupLocationCountry);
		assertEquals(parts.get("pickup_location_type"), pickupLocationType);
		assertEquals(parts.get("pickup_tracking_number"), pickupTrackingNumber);
		assertEquals(parts.get("pickup_shipping_provider"), pickupShippingProvider);
		assertEquals(parts.get("pickup_part_value"), pickupPartValue);
		assertEquals(parts.get("return_required"), returnRequired);
		assertEquals(parts.get("return_location_name"), returnLocationName);
		assertEquals(parts.get("return_location_address1"), returnLocationAddress1);
		assertEquals(parts.get("return_location_address2"), returnLocationAddress2);
		assertEquals(parts.get("return_location_city"), returnLocationCity);
		assertEquals(parts.get("return_location_state"), returnLocationState);
		assertEquals(parts.get("return_location_zip"), returnLocationZip);
		assertEquals(parts.get("return_location_country"), returnLocationCountry);
		assertEquals(parts.get("return_location_type"), returnLocationType);
		assertEquals(parts.get("return_tracking_number"), returnTrackingNumber);
		assertEquals(parts.get("return_shipping_provider"), returnShippingProvider);
		assertEquals(parts.get("return_part_value"), returnPartValue);
	}

	@Test
	public void evaluateArgument_copyOverAllProperties() {
		Map<String, String> parts = new HashMap<>();
		parts.put("supplied_by_resource", suppliedByResource);
		parts.put("distribution_method", distributionMethod);
		parts.put("pickup_location_name", pickupLocationName);
		parts.put("pickup_location_address1", pickupLocationAddress1);
		parts.put("pickup_location_address2", pickupLocationAddress2);
		parts.put("pickup_location_city", pickupLocationCity);
		parts.put("pickup_location_state", pickupLocationState);
		parts.put("pickup_location_zip", pickupLocationZip);
		parts.put("pickup_location_country", pickupLocationCountry);
		parts.put("pickup_location_type", pickupLocationType);
		parts.put("pickup_tracking_number", pickupTrackingNumber);
		parts.put("pickup_shipping_provider", pickupShippingProvider);
		parts.put("pickup_part_value", pickupPartValue);
		parts.put("return_required", returnRequired);
		parts.put("return_location_name", returnLocationName);
		parts.put("return_location_address1", returnLocationAddress1);
		parts.put("return_location_address2", returnLocationAddress2);
		parts.put("return_location_city", returnLocationCity);
		parts.put("return_location_state", returnLocationState);
		parts.put("return_location_zip", returnLocationZip);
		parts.put("return_location_country", returnLocationCountry);
		parts.put("return_location_type", returnLocationType);
		parts.put("return_tracking_number", returnTrackingNumber);
		parts.put("return_shipping_provider", returnShippingProvider);
		parts.put("return_part_value", returnPartValue);

		PartGroupDTO partGroupDTO = resolver.evaluateArgument(parts);
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
		for (PartDTO part : partGroupDTO.getParts()) {
			if (part.isReturn()) {
				assertEquals(part.getShippingProvider(), ShippingProvider.UPS);
				assertEquals(part.getPartValue(), new BigDecimal(returnPartValue));
				assertEquals(part.getTrackingNumber(), returnTrackingNumber);
			} else {
				assertEquals(part.getShippingProvider(), ShippingProvider.FEDEX);
				assertEquals(part.getPartValue(), new BigDecimal(pickupPartValue));
				assertEquals(part.getTrackingNumber(), pickupTrackingNumber);
			}
		}
	}

	@Test
	public void evaluateArgument_emptyLocationProperties_dontCreateLocations() throws Exception {
		Map<String, String> parts = new HashMap<>();
		parts.put("supplied_by_resource", "1");
		parts.put("distribution_method", "");
		parts.put("pickup_location_zip", "");
		parts.put("return_location_zip", null);

		PartGroupDTO partGroupDTO = resolver.evaluateArgument(parts);
		assertNull(partGroupDTO.getShipToLocation());
		assertNull(partGroupDTO.getReturnToLocation());
	}

	@Test
	public void supportsMethodParameter() throws Exception {
		Method method = BeanUtils.findDeclaredMethodWithMinimalParameters(AssignmentsCreateController.class, "create");
		MethodParameter methodParameter = new MethodParameter(method, 52);
		assertTrue(resolver.supportsParameter(methodParameter));
	}
}
