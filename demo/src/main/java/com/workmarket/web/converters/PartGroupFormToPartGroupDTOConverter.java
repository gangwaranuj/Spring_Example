package com.workmarket.web.converters;

import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.web.forms.work.PartForm;
import com.workmarket.web.forms.work.PartGroupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PartGroupFormToPartGroupDTOConverter implements Converter<PartGroupForm, PartGroupDTO> {

	@Autowired private LocationFormToLocationDTOConverter locationFormToLocationDTOConverter;

	@Override
	public PartGroupDTO convert(PartGroupForm partGroupForm) {
		if (partGroupForm == null) {
			return null;
		}

		PartGroupDTO partGroupDTO = new PartGroupDTO();

		partGroupDTO.setId(partGroupForm.getId());
		partGroupDTO.setUuid(partGroupForm.getUuid());
		partGroupDTO.setShippingDestinationType(partGroupForm.getShippingDestinationType());
		partGroupDTO.setSuppliedByWorker(partGroupForm.isSuppliedByWorker());
		partGroupDTO.setReturnRequired(partGroupForm.isReturnRequired());

		if (ShippingDestinationType.PICKUP.equals(partGroupForm.getShippingDestinationType())) {
			partGroupDTO.setShipToLocation(
				locationFormToLocationDTOConverter.convert(partGroupForm.getShipToLocation())
			);
		}
		if (partGroupForm.isReturnRequired()) {
			partGroupDTO.setReturnToLocation(
				locationFormToLocationDTOConverter.convert(partGroupForm.getReturnToLocation())
			);
		}

		List<PartForm> parts = partGroupForm.getParts();
		List<PartDTO> partDTOs = Lists.newArrayListWithExpectedSize(parts.size());
		for (PartForm partForm : parts) {
			partDTOs.add(partForm.asDTO());
		}
		partGroupDTO.setParts(partDTOs);

		return partGroupDTO;
	}
}
