package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.HideBackgroundChecksRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.HIDE_BG_CHECKS)
public class HideBackgroundChecksDoorman implements Doorman<HideBackgroundChecksRope> {
	@Override
	public void welcome(Guest guest, HideBackgroundChecksRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}
