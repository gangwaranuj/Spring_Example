package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.OfflinePayAllRope;
import com.workmarket.domains.velvetrope.rope.OfflinePaymentRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.OFFLINE_PAY_ALL)
public class OfflinePayAllDoorman implements Doorman<OfflinePayAllRope> {
	@Override
	public void welcome(Guest guest, OfflinePayAllRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}