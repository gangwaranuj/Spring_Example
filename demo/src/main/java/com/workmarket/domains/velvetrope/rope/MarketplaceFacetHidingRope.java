package com.workmarket.domains.velvetrope.rope;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.search.response.FacetResult;
import com.workmarket.search.response.user.PeopleFacetResultType;
import com.workmarket.velvetrope.Rope;

import java.util.List;
import java.util.Map;

public class MarketplaceFacetHidingRope implements Rope {
	private final Map<Enum<PeopleFacetResultType>, List<FacetResult>> facets;
	private static final ImmutableSet<String> NON_MARKETPLACE_FACET_NAMES = ImmutableSet.of(
		LaneType.LANE_1.getDescription(),
		LaneType.LANE_2.getDescription()
	);

	public MarketplaceFacetHidingRope(Map<Enum<PeopleFacetResultType>, List<FacetResult>> facets) {
		this.facets = facets;
	}

	@Override
	public void enter() {
		List<FacetResult> removableFacets = Lists.newArrayList();
		for (FacetResult facet : facets.get(PeopleFacetResultType.LANE)) {
			if (!NON_MARKETPLACE_FACET_NAMES.contains(facet.getFacetName())) {
				removableFacets.add(facet);
			}
		}

		facets.get(PeopleFacetResultType.LANE).removeAll(removableFacets);
	}
}
