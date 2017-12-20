package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.HideDrugTestsRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.HIDE_DRUG_TESTS)
public class HideDrugTestsDoorman implements Doorman<HideDrugTestsRope> {
	@Override
	public void welcome(Guest guest, HideDrugTestsRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}
