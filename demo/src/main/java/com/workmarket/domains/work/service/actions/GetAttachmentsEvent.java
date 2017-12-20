package com.workmarket.domains.work.service.actions;

import com.workmarket.domains.model.User;

import java.util.List;

public class GetAttachmentsEvent extends AbstractWorkEvent {

	private static final long serialVersionUID = -2511264703334464120L;

	public static class Builder extends AbstractWorkEvent.Builder{

		public Builder(List<String> workNumbers, User user, String actionName, String messageKey) {
			super(workNumbers, user, actionName, messageKey);
		}

		@Override
		public GetAttachmentsEvent build(){
			return new GetAttachmentsEvent(this);
		}
	}

	private GetAttachmentsEvent(Builder builder){
		super(builder);
	}
}
