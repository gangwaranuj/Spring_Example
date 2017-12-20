package com.workmarket.web.converters;

import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartGroupDTOToPartGroupFormConverterTest {

	@Mock LocationDTOToLocationFormConverter locationDTOToLocationFormConverter;
	@InjectMocks PartGroupDTOToPartGroupFormConverter partGroupDTOToPartGroupFormConverter;

	PartGroupDTO partGroupDTO;
	PartDTO partDTO;
	List<PartDTO> partDTOs;

	Long id = 1L;
	boolean isSuppliedByWorker = true, isReturnRequired = false;
	ShippingDestinationType shippingDestinationType = ShippingDestinationType.ONSITE;
	String uuid = "FOOO";

	@Before
	public void setup() {
		partGroupDTO = mock(PartGroupDTO.class);
		partDTO = mock(PartDTO.class);
		partDTOs = Lists.newArrayList(partDTO);

		when(partGroupDTO.getId()).thenReturn(id);
		when(partGroupDTO.getUuid()).thenReturn(uuid);
		when(partGroupDTO.getShippingDestinationType()).thenReturn(shippingDestinationType);
		when(partGroupDTO.isSuppliedByWorker()).thenReturn(isSuppliedByWorker);
		when(partGroupDTO.isReturnRequired()).thenReturn(isReturnRequired);
		when(partGroupDTO.getParts()).thenReturn(partDTOs);
	}

	@Test
	public void convert_nullPartGroupDTO_returnNull() {
		assertNull(partGroupDTOToPartGroupFormConverter.convert(null));
	}

	@Test
	public void convert_withNonNullPartGroupDTO_setAllPropertiesOnPartGroupForm() {
		PartGroupForm partGroupForm = partGroupDTOToPartGroupFormConverter.convert(partGroupDTO);

		assertEquals(partGroupForm.getId(), partGroupDTO.getId());
		assertEquals(partGroupForm.getShippingDestinationType(), partGroupDTO.getShippingDestinationType());
		assertEquals(partGroupForm.isSuppliedByWorker(), partGroupDTO.isSuppliedByWorker());
		assertEquals(partGroupForm.isReturnRequired(), partGroupDTO.isReturnRequired());
		assertEquals(partGroupForm.getParts().size(), 1);
		assertEquals(partGroupForm.getUuid(), uuid);
		verify(locationDTOToLocationFormConverter, times(2)).convert(any(LocationDTO.class));
	}
}
