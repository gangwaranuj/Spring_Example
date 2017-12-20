package com.workmarket.domains.velvetrope.doorman;

import com.workmarket.domains.velvetrope.rope.EmployeeWorkerRoleRope;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

@VelvetRope(venue = Venue.EMPLOYEE_WORKER_ROLE)
public class EmployeeWorkerRoleDoorman implements Doorman<EmployeeWorkerRoleRope> {

	@Override
	public void welcome(final Guest guest, final EmployeeWorkerRoleRope rope) {
		if (guest == null || rope == null) {
			return;
		}
		rope.enter();
	}
}
