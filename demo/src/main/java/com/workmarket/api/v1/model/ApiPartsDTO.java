package com.workmarket.api.v1.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Parts")
@JsonDeserialize(builder = ApiPartsDTO.Builder.class)
public class ApiPartsDTO {

	private final Boolean suppliedByResource;
	private final Boolean returnRequired;
	private final String distributionMethod;
	private final ApiPartLocationDTO pickupLocation;
	private final Double pickupPartValue;
	private final String pickupShippingProvider;
	private final String pickupTrackingNumber;
	private final ApiPartLocationDTO returnLocation;
	private final Double returnPartValue;
	private final String returnShippingProvider;
	private final String returnTrackingNumber;

	private ApiPartsDTO(Builder builder) {
		suppliedByResource = builder.suppliedByResource;
		returnRequired = builder.returnRequired;
		distributionMethod = builder.distributionMethod;
		pickupLocation = builder.pickupLocation;
		pickupPartValue = builder.pickupPartValue;
		pickupShippingProvider = builder.pickupShippingProvider;
		pickupTrackingNumber = builder.pickupTrackingNumber;
		returnLocation = builder.returnLocation;
		returnPartValue = builder.returnPartValue;
		returnShippingProvider = builder.returnShippingProvider;
		returnTrackingNumber = builder.returnTrackingNumber;
	}

	@ApiModelProperty(name = "supplied_by_resource")
	@JsonProperty("supplied_by_resource")
	public Boolean getSuppliedByResource() {
		return suppliedByResource;
	}

	@ApiModelProperty(name = "return_required")
	@JsonProperty("return_required")
	public Boolean getReturnRequired() {
		return returnRequired;
	}

	@ApiModelProperty(name = "distribution_method")
	@JsonProperty("distribution_method")
	public String getDistributionMethod() {
		return distributionMethod;
	}

	@ApiModelProperty(name = "pickup_location")
	@JsonProperty("pickup_location")
	public ApiPartLocationDTO getPickupLocation() {
		return pickupLocation;
	}

	@ApiModelProperty(name = "pickup_part_value")
	@JsonProperty("pickup_part_value")
	public Double getPickupPartValue() {
		return pickupPartValue;
	}

	@ApiModelProperty(name = "pickup_shipping_provider")
	@JsonProperty("pickup_shipping_provider")
	public String getPickupShippingProvider() {
		return pickupShippingProvider;
	}

	@ApiModelProperty(name = "pickup_tracking_number")
	@JsonProperty("pickup_tracking_number")
	public String getPickupTrackingNumber() {
		return pickupTrackingNumber;
	}

	@ApiModelProperty(name = "return_location")
	@JsonProperty("return_location")
	public ApiPartLocationDTO getReturnLocation() {
		return returnLocation;
	}

	@ApiModelProperty(name = "return_part_value")
	@JsonProperty("return_part_value")
	public Double getReturnPartValue() {
		return returnPartValue;
	}

	@ApiModelProperty(name = "return_shipping_provider")
	@JsonProperty("return_shipping_provider")
	public String getReturnShippingProvider() {
		return returnShippingProvider;
	}

	@ApiModelProperty(name = "return_tracking_number")
	@JsonProperty("return_tracking_number")
	public String getReturnTrackingNumber() {
		return returnTrackingNumber;
	}

	public static final class Builder {
		private Boolean suppliedByResource;
		private Boolean returnRequired;
		private String distributionMethod;
		private ApiPartLocationDTO pickupLocation;
		private Double pickupPartValue;
		private String pickupShippingProvider;
		private String pickupTrackingNumber;
		private ApiPartLocationDTO returnLocation;
		private Double returnPartValue;
		private String returnShippingProvider;
		private String returnTrackingNumber;

		public Builder() {
		}

		public Builder(ApiPartsDTO copy) {
			this.suppliedByResource = copy.suppliedByResource;
			this.returnRequired = copy.returnRequired;
			this.distributionMethod = copy.distributionMethod;
			this.pickupLocation = copy.pickupLocation;
			this.pickupPartValue = copy.pickupPartValue;
			this.pickupShippingProvider = copy.pickupShippingProvider;
			this.pickupTrackingNumber = copy.pickupTrackingNumber;
			this.returnLocation = copy.returnLocation;
			this.returnPartValue = copy.returnPartValue;
			this.returnShippingProvider = copy.returnShippingProvider;
			this.returnTrackingNumber = copy.returnTrackingNumber;
		}

		@JsonProperty("supplied_by_resource")
		public Builder withSuppliedByResource(Boolean suppliedByResource) {
			this.suppliedByResource = suppliedByResource;
			return this;
		}

		@JsonProperty("return_required")
		public Builder withReturnRequired(Boolean returnRequired) {
			this.returnRequired = returnRequired;
			return this;
		}

		@JsonProperty("distribution_method")
		public Builder withDistributionMethod(String distributionMethod) {
			this.distributionMethod = distributionMethod;
			return this;
		}

		@JsonProperty("pickup_location")
		public Builder withPickupLocation(ApiPartLocationDTO pickupLocation) {
			this.pickupLocation = pickupLocation;
			return this;
		}

		@JsonProperty("pickup_part_value")
		public Builder withPickupPartValue(Double pickupPartValue) {
			this.pickupPartValue = pickupPartValue;
			return this;
		}

		@JsonProperty("pickup_shipping_provider")
		public Builder withPickupShippingProvider(String pickupShippingProvider) {
			this.pickupShippingProvider = pickupShippingProvider;
			return this;
		}

		@JsonProperty("pickup_tracking_number")
		public Builder withPickupTrackingNumber(String pickupTrackingNumber) {
			this.pickupTrackingNumber = pickupTrackingNumber;
			return this;
		}

		@JsonProperty("return_location")
		public Builder withReturnLocation(ApiPartLocationDTO returnLocation) {
			this.returnLocation = returnLocation;
			return this;
		}

		@JsonProperty("return_part_value")
		public Builder withReturnPartValue(Double returnPartValue) {
			this.returnPartValue = returnPartValue;
			return this;
		}

		@JsonProperty("return_shipping_provider")
		public Builder withReturnShippingProvider(String returnShippingProvider) {
			this.returnShippingProvider = returnShippingProvider;
			return this;
		}

		@JsonProperty("return_tracking_number")
		public Builder withReturnTrackingNumber(String returnTrackingNumber) {
			this.returnTrackingNumber = returnTrackingNumber;
			return this;
		}

		public ApiPartsDTO build() {
			return new ApiPartsDTO(this);
		}
	}
}