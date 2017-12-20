package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "ShipmentGroup")
@JsonDeserialize(builder = ShipmentGroupDTO.Builder.class)
public class ShipmentGroupDTO {
	private final String uuid;
	private final boolean returnShipment;
	private final boolean suppliedByWorker;
	private final ShippingDestinationType shippingDestinationType;
	private final LocationDTO shipToAddress;
	private final LocationDTO returnAddress;
	private final List<ShipmentDTO> shipments = Lists.newArrayList();

	private ShipmentGroupDTO(Builder builder) {
		this.uuid = builder.uuid;
		this.returnShipment = builder.returnShipment;
		this.suppliedByWorker = builder.suppliedByWorker;
		this.shippingDestinationType = builder.shippingDestinationType;

		if (builder.shipToAddress != null) {
			this.shipToAddress = builder.shipToAddress.build();
		} else {
			this.shipToAddress = null;
		}

		if (builder.returnAddress != null) {
			this.returnAddress = builder.returnAddress.build();
		} else {
			this.returnAddress = null;
		}

		for (ShipmentDTO.Builder shipment : builder.shipments) {
			this.shipments.add(shipment.build());
		}
	}

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "returnShipment")
	@JsonProperty("returnShipment")
	public boolean isReturnShipment() {
		return returnShipment;
	}

	@ApiModelProperty(name = "suppliedByWorker")
	@JsonProperty("suppliedByWorker")
	public boolean isSuppliedByWorker() {
		return suppliedByWorker;
	}

	@ApiModelProperty(name = "shippingDestinationType")
	@JsonProperty("shippingDestinationType")
	public ShippingDestinationType getShippingDestinationType() {
		return shippingDestinationType;
	}

	@ApiModelProperty(name = "shipToAddress")
	@JsonProperty("shipToAddress")
	public LocationDTO getShipToAddress() {
		return shipToAddress;
	}

	@ApiModelProperty(name = "returnAddress")
	@JsonProperty("returnAddress")
	public LocationDTO getReturnAddress() {
		return returnAddress;
	}

	@ApiModelProperty(name = "shipments")
	@JsonProperty("shipments")
	public List<ShipmentDTO> getShipments() {
		return shipments;
	}

	public static class Builder implements AbstractBuilder<ShipmentGroupDTO> {
		private String uuid;
		private boolean returnShipment = false;
		private boolean suppliedByWorker = false;
		private ShippingDestinationType shippingDestinationType = ShippingDestinationType.WORKER;
		private LocationDTO.Builder shipToAddress;
		private LocationDTO.Builder returnAddress;
		private List<ShipmentDTO.Builder> shipments = Lists.newArrayList();

		public Builder() {}

		public Builder(ShipmentGroupDTO shipmentGroupDTO) {
			this.uuid = shipmentGroupDTO.uuid;
			this.returnShipment = shipmentGroupDTO.returnShipment;
			this.suppliedByWorker = shipmentGroupDTO.suppliedByWorker;
			this.shippingDestinationType = shipmentGroupDTO.shippingDestinationType;

			if (shipmentGroupDTO.shipToAddress != null) {
				this.shipToAddress = new LocationDTO.Builder(shipmentGroupDTO.shipToAddress);
			}

			if (shipmentGroupDTO.returnAddress != null) {
				this.returnAddress = new LocationDTO.Builder(shipmentGroupDTO.returnAddress);
			}

			for (ShipmentDTO shipment : shipmentGroupDTO.shipments) {
				shipments.add(new ShipmentDTO.Builder(shipment));
			}
		}

		@JsonProperty("uuid") public Builder setUuid(final String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("returnShipment") public Builder setReturnShipment(final boolean returnShipment) {
			this.returnShipment = returnShipment;
			return this;
		}

		@JsonProperty("suppliedByWorker") public Builder setSuppliedByWorker(final boolean suppliedByWorker) {
			this.suppliedByWorker = suppliedByWorker;
			return this;
		}

		@JsonProperty("shippingDestinationType") public Builder setShippingDestinationType(final ShippingDestinationType shippingDestinationType) {
			this.shippingDestinationType = shippingDestinationType;
			return this;
		}

		@JsonProperty("shipToAddress") public Builder setShipToAddress(final LocationDTO.Builder shipToAddress) {
			this.shipToAddress = shipToAddress;
			return this;
		}

		@JsonProperty("returnAddress") public Builder setReturnAddress(final LocationDTO.Builder returnAddress) {
			this.returnAddress = returnAddress;
			return this;
		}

		@JsonProperty("shipments") public Builder setShipments(List<ShipmentDTO.Builder> shipments) {
			this.shipments = shipments;
			return this;
		}

		public Builder addShipment(ShipmentDTO.Builder shipment) {
			this.shipments.add(shipment);
			return this;
		}

		public Builder clearShipments() {
			this.shipments.clear();
			return this;
		}

		public Builder reset() {
			return this.setUuid(null)
				.setReturnShipment(false)
				.setSuppliedByWorker(false)
				.setShippingDestinationType(ShippingDestinationType.WORKER)
				.setShipToAddress(null)
				.setReturnAddress(null)
				.clearShipments();
		}

		@Override
		public ShipmentGroupDTO build() {
			return new ShipmentGroupDTO(this);
		}
	}
}
