package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.HideWorkFeedRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.HIDE_WORKFEED)
public class HideWorkFeedDoorman implements Doorman<HideWorkFeedRope> {
	@Override
	public void welcome(Guest guest, HideWorkFeedRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}
