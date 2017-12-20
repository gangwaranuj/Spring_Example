package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.ProfileCustomFieldRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.PROFILE_CUSTOM_FIELD)
public class ProfileCustomFieldDoorman implements Doorman<ProfileCustomFieldRope> {
	@Override
	public void welcome(Guest guest, ProfileCustomFieldRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}