package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Sets;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Routing")
@JsonDeserialize(builder = RoutingDTO.Builder.class)
public class RoutingDTO implements RoutingCandidates {
	private final Set<Long> groupIds;
	private final Set<String> resourceNumbers;
	private final Set<String> vendorCompanyNumbers;
	private final boolean assignToFirstToAccept;
	private final boolean shownInFeed;
	private final boolean smartRoute;
	private final RoutingCandidatesDTO firstToAcceptCandidates;
	private final RoutingCandidatesDTO needToApplyCandidates;

	private RoutingDTO(Builder builder) {
		this.groupIds = builder.groupIds;
		this.resourceNumbers = builder.resourceNumbers;
		this.vendorCompanyNumbers = builder.vendorCompanyNumbers;
		this.assignToFirstToAccept = builder.assignToFirstToAccept;
		this.shownInFeed = builder.shownInFeed;
		this.smartRoute = builder.smartRoute;
		this.firstToAcceptCandidates = builder.firstToAcceptCandidates.build();
		this.needToApplyCandidates = builder.needToApplyCandidates.build();
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

	@ApiModelProperty(name = "assignToFirstToAccept")
	@JsonProperty("assignToFirstToAccept")
	public boolean isAssignToFirstToAccept() {
		return assignToFirstToAccept;
	}

	@ApiModelProperty(name = "shownInFeed")
	@JsonProperty("shownInFeed")
	public boolean isShownInFeed() {
		return shownInFeed;
	}

	@ApiModelProperty(name = "smartRoute")
	@JsonProperty("smartRoute")
	public boolean isSmartRoute() {
		return smartRoute;
	}

	@ApiModelProperty(name = "firstToAcceptCandidates")
	@JsonProperty("firstToAcceptCandidates")
	public RoutingCandidatesDTO getFirstToAcceptCandidates() {
		return firstToAcceptCandidates;
	}

	@ApiModelProperty(name = "needToApplyCandidates")
	@JsonProperty("needToApplyCandidates")
	public RoutingCandidatesDTO getNeedToApplyCandidates() {
		return needToApplyCandidates;
	}

	public static class Builder implements AbstractBuilder<RoutingDTO> {
		private Set<Long> groupIds = Sets.newHashSet();
		private Set<String> resourceNumbers = Sets.newHashSet();
		private Set<String> vendorCompanyNumbers = Sets.newHashSet();
		private boolean assignToFirstToAccept = false;
		private boolean shownInFeed = false;
		private boolean smartRoute = false;
		private RoutingCandidatesDTO.Builder firstToAcceptCandidates = new RoutingCandidatesDTO.Builder();
		private RoutingCandidatesDTO.Builder needToApplyCandidates = new RoutingCandidatesDTO.Builder();

		public Builder() {}

		public Builder(RoutingDTO routingDTO) {
			this.groupIds = routingDTO.groupIds;
			this.resourceNumbers = routingDTO.resourceNumbers;
			this.vendorCompanyNumbers = routingDTO.vendorCompanyNumbers;
			this.assignToFirstToAccept = routingDTO.assignToFirstToAccept;
			this.shownInFeed = routingDTO.shownInFeed;
			this.smartRoute = routingDTO.smartRoute;
			this.firstToAcceptCandidates = new RoutingCandidatesDTO.Builder(routingDTO.firstToAcceptCandidates);
			this.needToApplyCandidates = new RoutingCandidatesDTO.Builder(routingDTO.needToApplyCandidates);
		}

		@JsonProperty("groupIds") public Builder setGroupIds(Set<Long> groupIds) {
			this.groupIds = groupIds;
			return this;
		}

		public Builder addGroupId(Long groupId) {
			this.groupIds.add(groupId);
			return this;
		}

		@JsonProperty("resourceNumbers") public Builder setResourceNumbers(Set<String> resourceNumbers) {
			this.resourceNumbers = resourceNumbers;
			return this;
		}

		public Builder addResourceNumber(String userNumber) {
			this.resourceNumbers.add(userNumber);
			return this;
		}

		@JsonProperty("vendorCompanyNumbers") public Builder setVendorCompanyNumbers(Set<String> vendorCompanyNumbers) {
			this.vendorCompanyNumbers = vendorCompanyNumbers;
			return this;
		}

		public Builder addVendorCompanyNumber(String vendorCompanyNumber) {
			this.vendorCompanyNumbers.add(vendorCompanyNumber);
			return this;
		}

		@JsonProperty("assignToFirstToAccept") public Builder setAssignToFirstToAccept(boolean assignToFirstToAccept) {
			this.assignToFirstToAccept = assignToFirstToAccept;
			return this;
		}

		@JsonProperty("shownInFeed") public Builder setShownInFeed(boolean shownInFeed) {
			this.shownInFeed = shownInFeed;
			return this;
		}

		@JsonProperty("smartRoute") public Builder setSmartRoute(boolean smartRoute) {
			this.smartRoute = smartRoute;
			return this;
		}

		@JsonProperty("firstToAcceptCandidates") public Builder setFirstToAcceptCandidates(RoutingCandidatesDTO.Builder firstToAcceptCandidates) {
			this.firstToAcceptCandidates = firstToAcceptCandidates;
			return this;
		}

		@JsonProperty("needToApplyCandidates") public Builder setNeedToApplyCandidates(RoutingCandidatesDTO.Builder needToApplyCandidates) {
			this.needToApplyCandidates = needToApplyCandidates;
			return this;
		}

		public RoutingDTO build() {
			return new RoutingDTO(this);
		}
	}
}
