package com.workmarket.domains.velvetrope.rope;

import com.workmarket.domains.velvetrope.model.EmployeeWorkerRoleAdmitted;
import com.workmarket.velvetrope.Rope;

public class EmployeeWorkerRoleRope implements Rope {

	EmployeeWorkerRoleAdmitted admitted;

	public EmployeeWorkerRoleRope(EmployeeWorkerRoleAdmitted admitted) {
		this.admitted = admitted;
	}

	@Override
	public void enter() {
		if (admitted == null) {
			return;
		}
		admitted.setAdmitted(true);
	}
}
