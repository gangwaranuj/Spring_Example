package com.workmarket.domains.velvetrope.rope;

import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.velvetrope.Rope;

import java.util.Set;

public class MarketplaceResultHidingRope implements Rope {
	private static final Set<LaneType> NON_MARKETPLACE_LANE_TYPES = ImmutableSet.of(
		LaneType.LANE_0,
		LaneType.LANE_1,
		LaneType.LANE_2
	);
	private Set<LaneType> laneFilter;
	private Set<LaneType> laneTypes;

	public MarketplaceResultHidingRope(Set<LaneType> laneFilter, Set<LaneType> laneTypes) {
		this.laneFilter = laneFilter;
		this.laneTypes = laneTypes;
	}

	@Override
	public void enter() {
		if (laneFilter != null) {
			laneTypes.addAll(laneFilter);
			laneTypes.retainAll(NON_MARKETPLACE_LANE_TYPES);
			if (laneTypes.isEmpty()) {
				laneTypes.addAll(NON_MARKETPLACE_LANE_TYPES);
			}
		} else {
			laneTypes.addAll(NON_MARKETPLACE_LANE_TYPES);
		}
	}
}
