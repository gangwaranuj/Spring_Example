package com.workmarket.domains.velvetrope.doorman;

	import com.workmarket.domains.velvetrope.rope.FastFundsRope;
	import com.workmarket.velvetrope.Doorman;
	import com.workmarket.velvetrope.Guest;
	import com.workmarket.velvetrope.VelvetRope;
	import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.FAST_FUNDS)
public class FastFundsDoorman implements Doorman<FastFundsRope> {
	@Override
	public void welcome(Guest guest, FastFundsRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}
