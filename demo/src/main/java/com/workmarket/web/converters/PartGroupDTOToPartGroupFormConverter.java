package com.workmarket.web.converters;

import com.google.common.collect.Lists;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.web.forms.work.PartForm;
import com.workmarket.web.forms.work.PartGroupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PartGroupDTOToPartGroupFormConverter implements Converter<PartGroupDTO, PartGroupForm> {

	@Autowired LocationDTOToLocationFormConverter locationDTOToLocationFormConverter;

	@Override
	public PartGroupForm convert(PartGroupDTO partGroupDTO) {
		if (partGroupDTO == null) {
			return null;
		}

		PartGroupForm partGroupForm = new PartGroupForm();

		partGroupForm.setId(partGroupDTO.getId());
		partGroupForm.setUuid(partGroupDTO.getUuid());
		partGroupForm.setShippingDestinationType(partGroupDTO.getShippingDestinationType());
		partGroupForm.setSuppliedByWorker(partGroupDTO.isSuppliedByWorker());
		partGroupForm.setReturnRequired(partGroupDTO.isReturnRequired());

		partGroupForm.setShipToLocation(locationDTOToLocationFormConverter.convert(partGroupDTO.getShipToLocation()));
		partGroupForm.setReturnToLocation(locationDTOToLocationFormConverter.convert(partGroupDTO.getReturnToLocation()));

		List<PartForm> partForms = Lists.newArrayListWithExpectedSize(partGroupDTO.getParts().size());
		for (PartDTO dto : partGroupDTO.getParts()) {
			partForms.add(dto.asForm());
		}
		partGroupForm.setParts(partForms);

		return partGroupForm;
	}
}
