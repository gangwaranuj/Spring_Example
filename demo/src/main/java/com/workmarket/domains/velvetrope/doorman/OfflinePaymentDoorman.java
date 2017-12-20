package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.OfflinePaymentRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.OFFLINE_PAY)
public class OfflinePaymentDoorman implements Doorman<OfflinePaymentRope> {
	@Override
	public void welcome(Guest guest, OfflinePaymentRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}