package com.workmarket.domains.work.service.actions;

import com.workmarket.domains.model.User;

import java.util.List;

public class ApproveForPaymentWorkEvent extends AbstractWorkEvent {

	private static final long serialVersionUID = -2747308154666652990L;

	public static class Builder extends AbstractWorkEvent.Builder{

		public Builder(List<String> workNumbers, User user, String actionName, String messageKey) {
			super(workNumbers, user, actionName, messageKey);
		}

		@Override
		public ApproveForPaymentWorkEvent build(){
			return new ApproveForPaymentWorkEvent(this);
		}
	}

	private ApproveForPaymentWorkEvent(Builder builder){
		super(builder);
	}
}
