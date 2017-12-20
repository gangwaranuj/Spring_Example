package com.workmarket.test.mock.answer.defaults;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Optional;
import com.workmarket.domains.model.Maintenance;

public class DefaultOptionalMaintenanceAnswer implements Answer<Optional<Maintenance>> {
	private Optional<Maintenance> optionalMaintenance;

	public DefaultOptionalMaintenanceAnswer() {
		if(this.optionalMaintenance == null) {
			this.optionalMaintenance = Optional.fromNullable(new Maintenance());
		}
	}
	
	@Override
	public Optional<Maintenance> answer(InvocationOnMock invocation) throws Throwable {
		return optionalMaintenance;
	}

}
