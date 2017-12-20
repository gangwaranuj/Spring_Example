package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.MarketplaceRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.MARKETPLACE)
public class MarketplaceDoorman implements Doorman<MarketplaceRope> {
	@Override
	public void welcome(Guest guest, MarketplaceRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}
