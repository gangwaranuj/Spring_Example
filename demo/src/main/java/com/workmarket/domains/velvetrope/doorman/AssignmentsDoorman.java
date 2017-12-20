package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.AssignmentsSurveyDeletionRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.ASSIGNMENTS)
public class AssignmentsDoorman implements Doorman<AssignmentsSurveyDeletionRope> {
	@Override
	public void welcome(Guest guest, AssignmentsSurveyDeletionRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}
