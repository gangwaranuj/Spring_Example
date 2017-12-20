package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Sets;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "RoutingCandidates")
@JsonDeserialize(builder = RoutingCandidatesDTO.Builder.class)
public class RoutingCandidatesDTO implements RoutingCandidates {
	private final Set<Long> groupIds;
	private final Set<String> resourceNumbers;
	private final Set<String> vendorCompanyNumbers;

	public RoutingCandidatesDTO(Builder builder) {
		this.groupIds = builder.groupIds;
		this.resourceNumbers = builder.resourceNumbers;
		this.vendorCompanyNumbers = builder.vendorCompanyNumbers;
	}

	@ApiModelProperty(name = "groupIds")
	@JsonProperty("groupIds")
	@Override
	public Set<Long> getGroupIds() {
		return groupIds;
	}

	@ApiModelProperty(name = "resourceNumbers")
	@JsonProperty("resourceNumbers")
	@Override
	public Set<String> getResourceNumbers() {
		return resourceNumbers;
	}

	@ApiModelProperty(name = "vendorCompanyNumbers")
	@JsonProperty("vendorCompanyNumbers")
	@Override
	public Set<String> getVendorCompanyNumbers() {
		return vendorCompanyNumbers;
	}

	public static class Builder implements AbstractBuilder<RoutingCandidatesDTO> {
		private Set<Long> groupIds = Sets.newHashSet();
		private Set<String> resourceNumbers = Sets.newHashSet();
		private Set<String> vendorCompanyNumbers = Sets.newHashSet();

		public Builder() {}

		public Builder(RoutingCandidatesDTO routingTargetDTO) {
			this.groupIds = routingTargetDTO.groupIds;
			this.resourceNumbers = routingTargetDTO.resourceNumbers;
			this.vendorCompanyNumbers = routingTargetDTO.vendorCompanyNumbers;
		}

		public Builder setGroupIds(Set<Long> groupIds) {
			this.groupIds = groupIds;
			return this;
		}

		public Builder addGroupId(Long groupId) {
			this.groupIds.add(groupId);
			return this;
		}

		public Builder setResourceNumbers(Set<String> resourceNumbers) {
			this.resourceNumbers = resourceNumbers;
			return this;
		}

		public Builder addResourceNumber(String resourceNumber) {
			this.resourceNumbers.add(resourceNumber);
			return this;
		}

		public Builder setVendorCompanyNumbers(Set<String> vendorCompanyNumbers) {
			this.vendorCompanyNumbers = vendorCompanyNumbers;
			return this;
		}

		public Builder addVendorCompanyNumber(String vendorCompanyNumber) {
			this.vendorCompanyNumbers.add(vendorCompanyNumber);
			return this;
		}

		@Override
		public RoutingCandidatesDTO build() {
			return new RoutingCandidatesDTO(this);
		}
	}
}
