package com.workmarket.domains.work.service.part;

import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.shipment.vo.DestinationType;
import com.workmarket.shipment.vo.Shipment;
import com.workmarket.shipment.vo.ShipmentGroup;
import com.workmarket.web.converters.LocationToLocationDTOConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

@Component
public class ShipmentConverter {
	private static final Logger logger = LoggerFactory.getLogger(ShipmentConverter.class);
	public static Func1<Shipment, PartDTO> CONVERT_SHIPMENT_TO_PART_DTO = new Func1<Shipment, PartDTO>() {
		@Override
		public PartDTO call(final Shipment shipment) {
			return ShipmentConverter.convertShipmentToPartDTO(shipment);
		}
	};

	public static Func1<PartDTO, Shipment> CONVERT_PART_DTO_TO_SHIPMENT = new Func1<PartDTO, Shipment>() {
		@Override
		public Shipment call(final PartDTO part) {
			return ShipmentConverter.convertPartDTOToShipment(part);
		}
	};

	private final DirectoryService directoryService;
	private final LocationToLocationDTOConverter locationToLocationDTOConverter;

	@Autowired
	public ShipmentConverter(DirectoryService directoryService,
		LocationToLocationDTOConverter locationToLocationDTOConverter) {
		this.directoryService = directoryService;
		this.locationToLocationDTOConverter = locationToLocationDTOConverter;
	}

	public Func1<ShipmentGroup, PartGroupDTO> convertShipmentGroupPartGroupDTO() {
		return new Func1<ShipmentGroup, PartGroupDTO>() {
			@Override
			public PartGroupDTO call(final ShipmentGroup shipmentGroup) {
				final PartGroupDTO group = new PartGroupDTO();
				group.setReturnRequired(shipmentGroup.getReturnToLocationId() != null);
				group.setReturnToLocation(getLocation(shipmentGroup.getReturnToLocationId(), "ReturnTo"));
				group.setShipToLocation(getLocation(shipmentGroup.getShipToLocationId(), "ShipTo"));

				final DestinationType destinationType = shipmentGroup.getDestinationType();
				group.setShippingDestinationType((destinationType == null || destinationType == DestinationType.NONE)
					? ShippingDestinationType.NONE
					: ShippingDestinationType.valueOf(destinationType.toString()));
				group.setSuppliedByWorker(DestinationType.NONE == destinationType);
				group.setWorkId(shipmentGroup.getWorkId() == null ? null : Long.valueOf(shipmentGroup.getWorkId()));

				group.setUuid(shipmentGroup.getUuid());

				final List<PartDTO> parts = new ArrayList<>();
				if (shipmentGroup.getShipments() != null) {
					for (final Shipment shipment : shipmentGroup.getShipments()) {
						parts.add(convertShipmentToPartDTO(shipment));
					}
					group.setParts(parts);
				}
				return group;
			}
		};
	}

	private LocationDTO getLocation(final String locationId, final String which) {
		logger.debug("{} id is {}, {}", which, locationId, locationId == null ? null : Long.valueOf(locationId));
		return locationToLocationDTOConverter.convert(
			locationId == null
				? null
				: directoryService.findLocationById(Long.valueOf(locationId)));
	}

	public static PartDTO convertShipmentToPartDTO(final Shipment shipment) {
		final PartDTO result = new PartDTO();
		result.setId(42L); // FIXME?!?!?!
		result.setName(shipment.getName());
		result.setPartGroupId(42L); // FIXME!?!?!
		result.setPartGroupUuid(shipment.getShipmentGroupUuid());
		result.setPartValue(shipment.getDeclaredValue());
		result.setReturn(shipment.getIsReturn());
		result.setShippingProvider(
		    shipment.getShippingProvider() == null ? null
		        : ShippingProvider.getShippingProvider(shipment.getShippingProvider().toString()));
		result.setTrackingNumber(shipment.getTrackingNumber());
		result.setUuid(shipment.getUuid());
		return result;
	}

	public static Shipment convertPartDTOToShipment(final PartDTO partDTO) {
		return Shipment.builder()
			.setIsReturn(partDTO.isReturn())
			.setDeclaredValue(partDTO.getPartValue())
			.setName(partDTO.getName())
			.setShipmentGroupUuid(partDTO.getPartGroupUuid())
			.setShippingProvider(convertShippingProvider(partDTO))
			.setTrackingNumber(partDTO.getTrackingNumber())
			.setUuid(partDTO.getUuid())
			.build();
	}

	public static com.workmarket.shipment.vo.ShippingProvider convertShippingProvider(final PartDTO partDTO) {
		return partDTO.getShippingProvider() == null ? null
			: com.workmarket.shipment.vo.ShippingProvider.valueOf(partDTO.getShippingProvider().getCode());
	}

	public static DestinationType convertDestination(final ShippingDestinationType shippingDestinationType) {
		if (shippingDestinationType == ShippingDestinationType.NONE) {
			return null;
		}
		return DestinationType.valueOf(shippingDestinationType.toString());
	}
}
