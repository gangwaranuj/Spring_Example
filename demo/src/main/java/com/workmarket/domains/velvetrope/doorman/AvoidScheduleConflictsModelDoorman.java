package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.AvoidScheduleConflictsModelRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.AVOID_SCHED_CONFLICT)
public class AvoidScheduleConflictsModelDoorman implements Doorman<AvoidScheduleConflictsModelRope> {
	@Override
	public void welcome(Guest guest, AvoidScheduleConflictsModelRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}
