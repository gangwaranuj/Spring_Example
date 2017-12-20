package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;

import java.util.HashSet;
import java.util.Set;

import static com.natpryce.makeiteasy.Property.newProperty;

public class RoutingMaker {
	public static final Property<RoutingDTO, Set<Long>> groupIds = newProperty();
	public static final Property<RoutingDTO, Set<String>> resourceNumbers = newProperty();
	public static final Property<RoutingDTO, Boolean> assignToFirstToAccept = newProperty();
	public static final Property<RoutingDTO, Boolean> shownInFeed = newProperty();

	public static final Instantiator<RoutingDTO> RoutingDTO = new Instantiator<RoutingDTO>() {
		@Override
		public RoutingDTO instantiate(PropertyLookup<RoutingDTO> lookup) {
			return new RoutingDTO.Builder()
				.setGroupIds(lookup.valueOf(groupIds, new HashSet<Long>()))
				.setResourceNumbers(lookup.valueOf(resourceNumbers, new HashSet<String>()))
				.setAssignToFirstToAccept(lookup.valueOf(assignToFirstToAccept, false))
				.setShownInFeed(lookup.valueOf(shownInFeed, false))
				.build();
		}
	};
}
