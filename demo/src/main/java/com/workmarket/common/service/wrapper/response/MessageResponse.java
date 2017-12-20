package com.workmarket.common.service.wrapper.response;

import com.google.common.collect.Lists;
import com.workmarket.common.service.status.ResponseStatus;

import java.io.Serializable;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

public class MessageResponse extends BaseResponse implements Serializable {

	protected static final long serialVersionUID = 7526472295622776147L;

	protected List<String> messages;

	protected MessageResponse() {
		super();
		messages = Lists.newArrayList();
	}

	public MessageResponse(ResponseStatus status) {
		super(status);
	}

	public MessageResponse(ResponseStatus status, String message) {
		this(status);
		addMessage(message);
	}

	public MessageResponse(ResponseStatus status, List<String> messages) {
		this(status);
		this.messages = Lists.newArrayList(messages);
	}

	public List<String> getMessages() {
		return messages;
	}

	public boolean hasMessages() {
		return !isEmpty(messages);
	}

	public void addMessage(String message) {
		if (messages == null) {
			messages = Lists.newArrayList();
		}
		messages.add(message);
	}

	public void addAllMessages(List<String> messages) {
		if (this.messages == null) {
			this.messages = Lists.newArrayList();
		}
		this.messages.addAll(messages);
	}

	@Override
	public boolean isSuccessful() {
		return status != null && status.isSuccessful();
	}

	public boolean isFailure() {
		return status != null && status.isFailure();
	}

}
