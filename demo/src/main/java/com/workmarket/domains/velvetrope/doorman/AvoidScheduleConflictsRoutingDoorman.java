package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.AvoidScheduleConflictsRoutingRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.AVOID_SCHED_CONFLICT)
public class AvoidScheduleConflictsRoutingDoorman implements Doorman<AvoidScheduleConflictsRoutingRope> {
	@Override
	public void welcome(Guest guest, AvoidScheduleConflictsRoutingRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}
