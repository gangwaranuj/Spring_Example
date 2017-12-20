package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.MarketplaceFacetHidingRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.MARKETPLACE, bypass = true)
// All Users should go through the rope by default.
// Those who have the MARKETPLACE Feature will skip this (bypass ^^^).
public class MarketplaceFacetHidingDoorman implements Doorman<MarketplaceFacetHidingRope> {
	@Override
	public void welcome(Guest guest, MarketplaceFacetHidingRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}
