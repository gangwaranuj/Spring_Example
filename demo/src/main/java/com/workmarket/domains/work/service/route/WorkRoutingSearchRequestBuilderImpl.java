package com.workmarket.domains.work.service.route;

import com.google.api.client.util.Lists;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.domains.work.model.route.GroupRoutingStrategy;
import com.workmarket.search.core.model.GeoPoint;
import com.workmarket.search.request.LocationFilter;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;
import com.workmarket.search.worker.query.model.FindWorkerCriteriaBuilder;
import com.workmarket.search.worker.query.model.GroupMembershipCriteriaBuilder;
import com.workmarket.search.worker.query.model.LocationCriteria;
import com.workmarket.search.worker.query.model.LocationCriteriaBuilder;
import com.workmarket.search.worker.query.model.SearchType;
import com.workmarket.search.worker.query.model.WorkCriteria;
import com.workmarket.search.worker.query.model.WorkCriteriaBuilder;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class WorkRoutingSearchRequestBuilderImpl implements WorkRoutingSearchRequestBuilder {

	private static final Log logger = LogFactory.getLog(WorkRoutingSearchRequestBuilderImpl.class);

	@Override
	public AssignmentResourceSearchRequest build(AbstractRoutingStrategy routingStrategy) {
		Assert.notNull(routingStrategy);
		Work work = routingStrategy.getWork();
		Assert.notNull(work);

		PeopleSearchRequest searchRequest = new PeopleSearchRequest()
				.setUserId(routingStrategy.getCreatorId())
				.buildFilterFromRoutingStrategy(routingStrategy);

		buildAddressSearchFilter(routingStrategy, searchRequest);

		return new AssignmentResourceSearchRequest()
				.setRequest(searchRequest)
				.setWorkNumber(work.getWorkNumber())
				.setBoostIndustryId(work.getIndustry().getId())
				.setDescription(work.getDescription())
				.setSkills(work.getDesiredSkills());
	}

	@Override
	public PeopleSearchRequest buildAddressSearchFilter(AbstractRoutingStrategy routingStrategy, PeopleSearchRequest searchRequest) {
		Assert.notNull(routingStrategy);
		Assert.notNull(searchRequest);

		Work work = routingStrategy.getWork();
		Assert.notNull(work);
		Address address = null;

		//Address first
		if (work.getIsOnsiteAddress() && work.getAddress() != null) {
			address = work.getAddress();
			// assume the default group send radius, if provided radius is valid though, we'll switch to that
			int searchRadius = Constants.MAX_TRAVEL_DISTANCE;

			// normalize radius value
			if (searchRequest.getLocationFilter() != null &&
					NumberUtilities.isWithinRange(searchRequest.getLocationFilter().getMaxMileFromResourceToLocation(), 1, Constants.MAX_GROUP_SEND_RADIUS.intValue())) {
				searchRadius = searchRequest.getLocationFilter().getMaxMileFromResourceToLocation();
			}
			searchRequest.setLocationFilter(new LocationFilter()
				.setWillingToTravelTo(work.getAddress().getPostalCode())
				.setMaxMileFromResourceToLocation(searchRadius));

		} else if (work.getIsOnsiteAddress() && work.getCompany().getAddress() != null) {
			address = work.getCompany().getAddress();
		}

		if (address != null) {
			searchRequest.setCountryFilter(Sets.newHashSet(address.getCountry().getId()));
		}

		return searchRequest;
	}

	@Override
	public FindWorkerCriteria buildFindWorkerCriteriaForGroupRouting(AbstractRoutingStrategy routingStrategy) {
		Assert.notNull(routingStrategy);
		Assert.isTrue(GroupRoutingStrategy.GROUP_ROUTING_STRATEGY.equals(routingStrategy.getType()));
		Assert.notNull(((GroupRoutingStrategy) routingStrategy).getUserGroups());
		final Set<Long> groupIds = ((GroupRoutingStrategy) routingStrategy).getUserGroups();
		final List<String> groups = Lists.newArrayList();
		for (final Long groupId : groupIds) {
			groups.add(groupId.toString());
		}

		final Work work = routingStrategy.getWork();
		Assert.notNull(work);

		// set basic criteria
		final FindWorkerCriteriaBuilder criteriaBuilder = new FindWorkerCriteriaBuilder();
		criteriaBuilder.setRequestingUserId(routingStrategy.getCreatorId().toString());
		criteriaBuilder.setRequestingCompanyId(work.getCompany().getId().toString());
		criteriaBuilder.setSearchType(SearchType.PEOPLE_SEARCH_GROUP_MEMBER);

		// set group member filter
		// old request use companyGroupIds, which is a combination of member and override-member
		final GroupMembershipCriteriaBuilder groupCriteriaBuilder = new GroupMembershipCriteriaBuilder();
		groupCriteriaBuilder.addTalentPoolMemberships(groups);
		groupCriteriaBuilder.addTalentPoolMembershipOverrides(groups);
		criteriaBuilder.setGroupMembershipCriteria(groupCriteriaBuilder.build().get());

		Optional<LocationCriteria> locationCriteria = buildLocationCriteria(work);
		if (locationCriteria.isPresent()) {
			criteriaBuilder.setLocationCriteria(locationCriteria.get());
		}

		final WorkCriteria workCriteria = new WorkCriteriaBuilder()
			.setWorkNumber(work.getWorkNumber())
			.setTitle(work.getTitle())
			.setSkills(work.getDesiredSkills())
			.build()
			.get();

		criteriaBuilder.setWorkCriteria(workCriteria);

		return criteriaBuilder.build().get();
	}

	@VisibleForTesting
	Optional<LocationCriteria> buildLocationCriteria(final Work work) {
		LocationCriteriaBuilder locationBuilder = new LocationCriteriaBuilder();
		Address address = null;

		if (work.getIsOnsiteAddress()) {
			if (work.getAddress() != null) {
				address = work.getAddress();
			} else if (work.getCompany().getAddress() != null) {
				address = work.getCompany().getAddress();
			}
		}
		if (address != null) {
			if (address.getLatitude() != null && address.getLongitude() != null) {
				GeoPoint geoPoint = new GeoPoint(address.getLatitude().doubleValue(), address.getLongitude().doubleValue());
				locationBuilder.setGeoPoint(geoPoint);
				BigDecimal miles = new BigDecimal(Constants.MAX_TRAVEL_DISTANCE);
				BigDecimal kilometers = miles.multiply(new BigDecimal(1.609344));
				locationBuilder.setRadiusKilometers(kilometers.longValue());
			} else if (address.getState() != null) {
				locationBuilder.setState(address.getState().getShortName());
			} else if (address.getCountry() != null) {
				locationBuilder.addCountry(address.getCountry().getId());
			}
		}
		return locationBuilder.build();
	}
}
