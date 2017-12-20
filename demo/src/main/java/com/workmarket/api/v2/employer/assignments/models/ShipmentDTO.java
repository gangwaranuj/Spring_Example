package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.service.external.TrackingStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "Shipment")
@JsonDeserialize(builder = ShipmentDTO.Builder.class)
public class ShipmentDTO {
	private final String uuid;
	private final String name;
	private final String trackingNumber;
	private final TrackingStatus trackingStatus;
	private final ShippingProvider shippingProvider;
	private final BigDecimal value;
	private final boolean isReturn;

	private ShipmentDTO(Builder builder) {
		this.uuid = builder.uuid;
		this.name = builder.name;
		this.trackingNumber = builder.trackingNumber;
		this.trackingStatus = builder.trackingStatus;
		this.shippingProvider = builder.shippingProvider;
		this.value = builder.value;
		this.isReturn = builder.isReturn;
	}

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@ApiModelProperty(name = "trackingNumber")
	@JsonProperty("trackingNumber")
	public String getTrackingNumber() {
		return trackingNumber;
	}

	@ApiModelProperty(name = "trackingStatus")
	@JsonProperty("trackingStatus")
	public TrackingStatus getTrackingStatus() {
		return trackingStatus;
	}

	@ApiModelProperty(name = "shippingProvider")
	@JsonProperty("shippingProvider")
	public ShippingProvider getShippingProvider() {
		return shippingProvider;
	}

	@ApiModelProperty(name = "value")
	@JsonProperty("value")
	public BigDecimal getValue() {
		return value;
	}

	@ApiModelProperty(name = "isReturn")
	@JsonProperty("isReturn")
	public boolean isReturn() {
		return isReturn;
	}

	public static class Builder implements AbstractBuilder<ShipmentDTO> {
		private String uuid;
		private String name;
		private String trackingNumber;
		private ShippingProvider shippingProvider;
		private TrackingStatus trackingStatus;
		private BigDecimal value;
		private boolean isReturn;

		public Builder() {}

		public Builder(ShipmentDTO shipmentDTO) {
			this.uuid = shipmentDTO.uuid;
			this.name = shipmentDTO.name;
			this.trackingNumber = shipmentDTO.trackingNumber;
			this.shippingProvider = shipmentDTO.shippingProvider;
			this.trackingStatus = shipmentDTO.trackingStatus;
			this.value = shipmentDTO.value;
			this.isReturn = shipmentDTO.isReturn;
		}

		@JsonProperty("uuid") public Builder setUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("name") public Builder setName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("trackingNumber") public Builder setTrackingNumber(String trackingNumber) {
			this.trackingNumber = trackingNumber;
			return this;
		}

		@JsonProperty("trackingStatus") public Builder setTrackingStatus(TrackingStatus trackingStatus) {
			this.trackingStatus = trackingStatus;
			return this;
		}

		@JsonProperty("shippingProvider") public Builder setShippingProvider(ShippingProvider shippingProvider) {
			this.shippingProvider = shippingProvider;
			return this;
		}

		@JsonProperty("value") public Builder setValue(BigDecimal value) {
			this.value = value;
			return this;
		}

		@JsonProperty("isReturn") public Builder setReturn(boolean isReturn) {
			this.isReturn = isReturn;
			return this;
		}

		public ShipmentDTO build() {
			return new ShipmentDTO(this);
		}
	}
}

