package com.workmarket.web.validators;

import com.google.common.collect.Lists;
import com.workmarket.BaseUnitTest;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartGroupValidatorTest extends BaseUnitTest {

	@Mock AddressWorkValidator addressWorkValidator;
	@Mock PartValidator partValidator;
	@Mock MessageBundleHelper messageHelper;
	@InjectMocks PartGroupValidator validator;

	PartGroupDTO partGroup;
	Errors errors;
	LocationDTO shipToLocation, returnLocation;
	List<PartDTO> parts = Lists.newArrayList(new PartDTO());

	@Before
	public void setup() {
		partGroup = mock(PartGroupDTO.class);
		errors = mock(Errors.class);
		shipToLocation = mock(LocationDTO.class);
		returnLocation = mock(LocationDTO.class);
		when(partGroup.isSuppliedByWorker()).thenReturn(false);
		when(partGroup.isSetShippingDestinationType()).thenReturn(true);
		when(partGroup.hasShipToLocation()).thenReturn(true);
		when(partGroup.getShippingDestinationType()).thenReturn(ShippingDestinationType.PICKUP);
		when(partGroup.getShipToLocation()).thenReturn(shipToLocation);
		when(partGroup.isReturnRequired()).thenReturn(true);
		when(partGroup.hasReturnToLocation()).thenReturn(true);
		when(partGroup.getReturnToLocation()).thenReturn(returnLocation);
	}

	@Test
	public void validate_shipToLocationSet_validateLocation() {
		validator.validate(partGroup, errors);

		verify(addressWorkValidator).validate(eq(shipToLocation), any(BindingResult.class));
	}

	@Test
	public void validate_returnLocationSet_validateLocation() {
		validator.validate(partGroup, errors);

		verify(addressWorkValidator).validate(eq(returnLocation), any(BindingResult.class));
	}

	@Test
	public void validate_pickupDistributionSet_withNoShipToLocationSet_rejectValue() {
		when(partGroup.hasShipToLocation()).thenReturn(false);

		validator.validate(partGroup, errors);

		verify(errors).rejectValue(eq("shipToLocation"), eq("NotNull"), anyString());
	}

	@Test
	public void validate_partsNotSuppliedByWorker_withNoDistributionMethodSet_rejectValue() {
		when(partGroup.isSetShippingDestinationType()).thenReturn(false);

		validator.validate(partGroup, errors);

		verify(errors).rejectValue(eq("shippingDestinationType"), eq("NotNull"), anyString());
	}

	@Test
	public void validate_noReturnRequired_dontValidateReturnLocation() {
		when(partGroup.isReturnRequired()).thenReturn(false);

		validator.validate(partGroup, errors);

		verify(addressWorkValidator, never()).validate(returnLocation, errors);
	}

	@Test
	public void validate_noReturnLocationSet_rejectValue() {
		when(partGroup.hasReturnToLocation()).thenReturn(false);

		validator.validate(partGroup, errors);

		verify(errors).rejectValue(eq("returnToLocation"), eq("NotNull"), anyString());
	}

	@Test
	public void validate_noPartsSet_dontValidateParts() {
		when(partGroup.getParts()).thenReturn(Lists.<PartDTO>newArrayList());

		validator.validate(partGroup, errors);

		verify(partValidator, never()).validate(any(PartDTO.class), any(Errors.class));
	}

	@Test
	public void validate_partsSet_validateParts() {
		when(partGroup.getParts()).thenReturn(parts);

		validator.validate(partGroup, errors);

		verify(partValidator).validate(eq(parts.get(0)), any(BindingResult.class));
	}
}
