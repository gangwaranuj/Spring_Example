package com.workmarket.web.converters;

import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.web.forms.addressbook.LocationForm;
import com.workmarket.web.forms.work.PartForm;
import com.workmarket.web.forms.work.PartGroupForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartGroupFormToPartGroupDTOConverterTest {

	@Mock private LocationFormToLocationDTOConverter locationFormToLocationDTOConverter;
	@InjectMocks PartGroupFormToPartGroupDTOConverter partGroupFormToPartGroupDTOConverter;

	PartGroupForm partGroupForm;
	PartForm partForm;
	List<PartForm> partForms;

	Long id = 1L;
	boolean isSuppliedByWorker = false, isReturnRequired = true;
	ShippingDestinationType distributionMethodType = ShippingDestinationType.PICKUP;
	String uuid = "FOOO";

	@Before
	public void setup() {
		partGroupForm = mock(PartGroupForm.class);
		partForm = mock(PartForm.class);
		partForms = Lists.newArrayList(partForm);

		when(partGroupForm.getId()).thenReturn(id);
		when(partGroupForm.getUuid()).thenReturn(uuid);
		when(partGroupForm.getShippingDestinationType()).thenReturn(distributionMethodType);
		when(partGroupForm.isSuppliedByWorker()).thenReturn(isSuppliedByWorker);
		when(partGroupForm.isReturnRequired()).thenReturn(isReturnRequired);
		when(partGroupForm.getParts()).thenReturn(partForms);
	}


	@Test
	public void convert_nullPartGroupForm_returnNull() {
		assertNull(partGroupFormToPartGroupDTOConverter.convert(null));
	}

	@Test
	public void convert_withNonNullPartGroupForm_setAllPropertiesOnPartGroupDTO() {
		PartGroupDTO partGroupDTO = partGroupFormToPartGroupDTOConverter.convert(partGroupForm);

		assertEquals(partGroupDTO.getId(), partGroupForm.getId());
		assertEquals(partGroupDTO.getShippingDestinationType(), partGroupForm.getShippingDestinationType());
		assertEquals(partGroupDTO.isSuppliedByWorker(), partGroupForm.isSuppliedByWorker());
		assertEquals(partGroupDTO.isReturnRequired(), partGroupForm.isReturnRequired());
		assertEquals(partGroupDTO.getParts().size(), 1);
		assertEquals(partGroupDTO.getUuid(), uuid);
		verify(locationFormToLocationDTOConverter, times(2)).convert(any(LocationForm.class));
	}

	@Test
	public void convert_withNonPickupDistributionType_dontAccessShipToLocationOnForm() {
		when(partGroupForm.getShippingDestinationType()).thenReturn(ShippingDestinationType.ONSITE);

		partGroupFormToPartGroupDTOConverter.convert(partGroupForm);

		verify(partGroupForm, never()).getShipToLocation();
	}

	@Test
	public void convert_whenReturnIsNotRequired_dontAccessReturnToLocationOnForm() {
		when(partGroupForm.isReturnRequired()).thenReturn(false);

		partGroupFormToPartGroupDTOConverter.convert(partGroupForm);

		verify(partGroupForm, never()).getReturnToLocation();
	}
}
