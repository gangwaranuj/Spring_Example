package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.ESignatureRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.ESIGNATURE)
public class ESignatureDoorman implements Doorman<ESignatureRope> {
	@Override
	public void welcome(Guest guest, ESignatureRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}