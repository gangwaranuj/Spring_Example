package com.workmarket.domains.model.realtime;

import com.workmarket.thrift.services.realtime.RealtimeUser;

public class RealtimeOwnerDecorator extends RealtimeUser implements IRealtimeUser {

	public RealtimeOwnerDecorator(RealtimeUser owner) {
		//TODO: I think we can just use the TSerializer or something
		if (owner.isSetFirstName()) {
			this.setFirstName(owner.getFirstName());
		}
		if (owner.isSetLastName()) {
			this.setLastName(owner.getLastName());
		}
		if (owner.isSetUserId()) {
			this.setUserId(owner.getUserId());
		}
		if (owner.isSetUserNumber()) {
			this.setUserNumber(owner.getUserNumber());
		}
	}

	private static final long serialVersionUID = -3240344119875430059L;

}
