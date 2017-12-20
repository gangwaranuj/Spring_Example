package com.workmarket.domains.work.service.actions;

import com.workmarket.domains.model.User;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class AddNotesWorkEvent extends AbstractWorkEvent {

	private static final long serialVersionUID = -777326813448471735L;
	final private String content;
	final private boolean isPrivate;

	public static class Builder extends AbstractWorkEvent.Builder{
		String content;
		boolean isPrivate = true;

		public Builder(List<String> workNumbers, User user, String actionName, String messageKey,String content, boolean isPrivate) {
			super(workNumbers, user, actionName, messageKey);
			this.content = content;
			this.isPrivate = isPrivate;
		}

		@Override
		public AddNotesWorkEvent build(){
			return new AddNotesWorkEvent(this);
		}
	}

	private AddNotesWorkEvent(Builder builder) {
		super(builder);
		this.content = builder.content;
		this.isPrivate = builder.isPrivate;
	}

	public String getContent() {
		return content;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public boolean isValid(){
		return StringUtils.isNotEmpty(content) && super.isValid();
	}


}
