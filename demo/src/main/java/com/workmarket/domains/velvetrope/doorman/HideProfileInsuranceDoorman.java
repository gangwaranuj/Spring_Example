package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.HideProfileInsuranceRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.HIDE_PROF_INSURANCE)
public class HideProfileInsuranceDoorman implements Doorman<HideProfileInsuranceRope> {
	@Override
	public void welcome(Guest guest, HideProfileInsuranceRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}
