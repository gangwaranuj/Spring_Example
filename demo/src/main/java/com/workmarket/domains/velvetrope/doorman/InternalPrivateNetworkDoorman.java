package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.InternalPrivateNetworkRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.INTERNAL_NETWORK)
public class InternalPrivateNetworkDoorman implements Doorman<InternalPrivateNetworkRope> {
	@Override
	public void welcome(Guest guest, InternalPrivateNetworkRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}
