package com.workmarket.domains.velvetrope.rope;

import com.workmarket.domains.velvetrope.model.InternalPrivateNetworkAdmitted;

public class InternalPrivateNetworkAdmittedRope extends InternalPrivateNetworkRope {
	InternalPrivateNetworkAdmitted admitted;

	public InternalPrivateNetworkAdmittedRope(InternalPrivateNetworkAdmitted admitted) {
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


