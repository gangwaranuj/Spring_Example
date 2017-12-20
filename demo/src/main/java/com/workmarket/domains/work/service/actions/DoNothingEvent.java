package com.workmarket.domains.work.service.actions;

import com.workmarket.domains.model.User;

import java.util.List;

public class DoNothingEvent extends AbstractWorkEvent {

	private static final long serialVersionUID = -8490502880789514162L;

	public static class Builder extends AbstractWorkEvent.Builder{

		public Builder(List<String> workNumbers, User user, String actionName, String messageKey) {
			super(workNumbers, user, actionName, messageKey);
		}

		@Override
		public DoNothingEvent build(){
			return new DoNothingEvent(this);
		}
	}

	private DoNothingEvent(Builder builder){
		super(builder);
	}

}
